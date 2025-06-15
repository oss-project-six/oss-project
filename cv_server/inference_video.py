import os
import cv2
import numpy as np
import torch
import torchvision
from ultralytics import YOLO
import utils
from test import SixDRepNet360
import math
from math import cos, sin, radians
import datetime
import json
import time
from sklearn.cluster import DBSCAN

# ─────────────────────────────────────────────────────────────────────────────
# 하드코딩된 모델 경로
YOLO_FACE_WEIGHTS   = r"C:\Users\gud66\Desktop\6DRepNet360-master\sixdrepnet360\yolov8n-face.pt"
POSE_WEIGHTS        = r"C:\Users\gud66\Desktop\ossp_gaze2\ossp_gaze\6DRepNet360_Full-Rotation_300W_LP+Panoptic.pth"
# MiDaS 설정
MIDAS_WEIGHTS       = "DPT_Large"
MIDAS_TRANSFORMS    = "DPT_Large"

# 디바이스 설정
DEVICE              = 'cuda:0' if torch.cuda.is_available() else 'cpu'

# 상수 정의
DEPTH_THRESHOLD     = 0.65
FIXED_AXIS_SIZE     = 5000
HEAT_ALPHA          = 0.4
HEAT_COLOR_ON_DEPTH = (255, 0, 255)
ROI_BOX             = (0, 0, 467, 1058)
FRONT_THRESHOLD_DEG = 4.26
MIN_LOOK_FRAMES     = 10
DBSCAN_EPS          = 20
DBSCAN_MIN_SAMPLES  = 1
# ─────────────────────────────────────────────────────────────────────────────

# 1) YOLOv8-face 모델 로드
_face_detector = None

def init_face_detector(weights_path=YOLO_FACE_WEIGHTS):
    global _face_detector
    if _face_detector is None:
        _face_detector = YOLO(weights_path)
    return _face_detector

# 2) 6DRepNet360 모델 로드
_pose_model = None

def init_pose_model(weights_path=POSE_WEIGHTS, device=DEVICE):
    global _pose_model
    if _pose_model is None:
        model = SixDRepNet360(torchvision.models.resnet.Bottleneck, [3,4,6,3], 1)
        state = torch.load(weights_path, map_location='cpu')
        model.load_state_dict(state.get('model_state_dict', state))
        model.to(device).eval()
        _pose_model = model
    return _pose_model

# 3) MiDaS 모델 로드
_midas_model = None
_midas_transform = None

def init_midas(device=DEVICE):
    global _midas_model, _midas_transform
    if _midas_model is None or _midas_transform is None:
        _midas_model = torch.hub.load("intel-isl/MiDaS", MIDAS_WEIGHTS).to(device).eval()
        midas_transforms = torch.hub.load("intel-isl/MiDaS", "transforms")
        _midas_transform = midas_transforms.default_transform
    return _midas_model, _midas_transform

# 4) Depth map 추정

def estimate_depth(frame, midas_model, midas_transform, device=DEVICE):
    img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    inp = midas_transform(img).to(device)
    batch = inp.unsqueeze(0) if inp.ndim == 3 else inp
    with torch.no_grad():
        pred = midas_model(batch)
        if pred.ndim == 3:
            pred = pred.unsqueeze(1)
        pred = torch.nn.functional.interpolate(pred,
            size=frame.shape[:2], mode="bicubic", align_corners=False)
        pred = pred.squeeze(1)
    return pred.cpu().numpy()[0]

# 5) 얼굴 전처리

def preprocess_face(face_img):
    import torchvision.transforms as T
    tf = T.Compose([
        T.ToPILImage(), T.Resize(256), T.CenterCrop(224),
        T.ToTensor(),
        T.Normalize([0.485,0.456,0.406],[0.229,0.224,0.225])
    ])
    img = cv2.cvtColor(face_img, cv2.COLOR_BGR2RGB)
    return tf(img).unsqueeze(0)

# 6) 바운딩박스 확장

def expand_box(box, scale=1.3, img_shape=None):
    x1,y1,x2,y2 = box
    w,h = x2-x1, y2-y1
    cx,cy = x1 + w/2, y1 + h/2
    nw,nh = w*scale, h*scale
    nx1 = int(max(cx-nw/2,0)); ny1 = int(max(cy-nh/2,0))
    nx2 = int(min(cx+nw/2, img_shape[1]-1)) if img_shape else int(cx+nw/2)
    ny2 = int(min(cy+nh/2, img_shape[0]-1)) if img_shape else int(cy+nh/2)
    return [nx1, ny1, nx2, ny2]

# 7) IoU 계산

def compute_iou(boxA, boxB):
    xA,yA,xB,yB = max(boxA[0],boxB[0]), max(boxA[1],boxB[1]), min(boxA[2],boxB[2]), min(boxA[3],boxB[3])
    interW = max(0, xB-xA+1); interH = max(0, yB-yA+1)
    interArea = interW * interH
    areaA = (boxA[2]-boxA[0]+1)*(boxA[3]-boxA[1]+1)
    areaB = (boxB[2]-boxB[0]+1)*(boxB[3]-boxB[1]+1)
    return interArea/float(areaA+areaB-interArea) if (areaA+areaB-interArea)>0 else 0

# 8) Face detection + Head-pose + Depth + DBSCAN Annotate

def annotate_image_with_heat(frame, face_detector, pose_model, midas_model, midas_transform, depth_threshold=DEPTH_THRESHOLD):
    annotated = frame.copy()
    h,w = frame.shape[:2]
    depth_map = estimate_depth(frame, midas_model, midas_transform)

    results = face_detector.predict(source=frame, conf=0.5, imgsz=max(h,w))[0]
    boxes = results.boxes.xyxy.cpu().numpy()
    classes = results.boxes.cls.cpu().numpy()
    names = face_detector.model.names

    face_infos = []
    for box, cls in zip(boxes, classes):
        if names[int(cls)] != 'face':
            continue
        x1,y1,x2,y2 = map(int, expand_box(box,2.0,frame.shape))
        x1,y1 = max(x1,0), max(y1,0)
        x2,y2 = min(x2,w-1), min(y2,h-1)

        face_img = frame[y1:y2, x1:x2]
        if face_img.size == 0:
            continue
        face_t = preprocess_face(face_img).to(DEVICE)
        with torch.no_grad():
            R = pose_model(face_t)
        euler = utils.compute_euler_angles_from_rotation_matrices(R)[0] * 180 / math.pi
        pitch,yaw,roll = euler.cpu().numpy()

        cx,cy = (x1+x2)//2, (y1+y2)//2
        depth_center = float(depth_map[cy, cx])

        # FOV mask points
        fov, seg = 15, 30
        pr, yr = radians(pitch), -radians(yaw)
        polygon = [(cx,cy)]
        for i in range(seg+1):
            angle = -radians(fov)/2 + i*(radians(fov)/seg)
            xx = FIXED_AXIS_SIZE * sin(yr+angle) + cx
            yy = FIXED_AXIS_SIZE * (-cos(yr+angle)*sin(pr)) + cy
            polygon.append((int(np.clip(xx,0,w-1)), int(np.clip(yy,0,h-1))))
        mask = np.zeros((h,w), dtype=np.uint8)
        cv2.fillPoly(mask, [np.array(polygon, dtype=np.int32)], 1)

        # collect heat candidates
        pts = []
        ys,xs = np.where(mask==1)
        for px,py in zip(xs,ys):
            if x1<=px<=x2 and y1<=py<=y2: continue
            if abs(depth_map[py,px] - depth_center) <= depth_threshold:
                pts.append((px,py))

        # DBSCAN clustering
        center = None
        if len(pts) >= DBSCAN_MIN_SAMPLES:
            arr = np.array(pts)
            cl = DBSCAN(eps=DBSCAN_EPS, min_samples=DBSCAN_MIN_SAMPLES).fit(arr)
            lbl, cnt = np.unique(cl.labels_, return_counts=True)
            valid = [(l,c) for l,c in zip(lbl,cnt) if l!=-1]
            if valid:
                best = max(valid, key=lambda x:x[1])[0]
                sel = arr[cl.labels_==best]
                cm = sel.mean(axis=0).astype(int)
                center = (int(cm[0]), int(cm[1]))

        # annotate original frame
        cv2.rectangle(annotated, (x1,y1), (x2,y2), (0,255,255), 2)
        cv2.circle(annotated, (cx,cy), 5, (0,255,0), -1)
        cv2.putText(annotated, f"P{pitch:.1f},Y{yaw:.1f},R{roll:.1f}", (x1,y1-6),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.45, (255,255,255), 1)
        overlay = annotated.copy()
        radius = max(10, (x2-x1)//4)
        if center: cv2.circle(overlay, center, radius, (0,0,255), -1)
        else:    
            for px,py in pts: cv2.circle(overlay, (px,py), radius, (0,0,255), -1)
        annotated = cv2.addWeighted(overlay, HEAT_ALPHA, annotated, 1-HEAT_ALPHA, 0)
        utils.draw_axis(annotated, yaw, pitch, roll, tdx=cx, tdy=cy, size=FIXED_AXIS_SIZE)

        face_infos.append({'id':None,'box':(x1,y1,x2,y2),'pitch':pitch,'yaw':yaw,'roll':roll,'heat_center':center,'heat_points':pts})

    # depth colormap + plot clustered centers
    depth_norm = cv2.normalize(depth_map,None,0,255,cv2.NORM_MINMAX).astype(np.uint8)
    depth_cm   = cv2.applyColorMap(depth_norm,cv2.COLORMAP_JET)
    for info in face_infos:
        if info['heat_center']: cv2.circle(depth_cm, info['heat_center'], max(10,(info['box'][2]-info['box'][0])//4), HEAT_COLOR_ON_DEPTH, -1)

    return annotated, depth_cm, face_infos

def run_inference(input_video_path: str, output_annot_path: str = None, output_depth_path: str = None):
    print('running...')
    start = time.time()

    # 1) 입·출력 경로 셋업: 같은 폴더에 생성
    dir_name  = os.path.dirname(input_video_path)
    base_name = os.path.splitext(os.path.basename(input_video_path))[0]
    ext       = os.path.splitext(input_video_path)[1]

    if output_annot_path is None:
        output_annot_path = os.path.join(dir_name, f"{base_name}_annotated{ext}")
    if output_depth_path is None:
        output_depth_path = os.path.join(dir_name, f"{base_name}_depth{ext}")

    # 2) 모델 초기화
    midas_model, midas_transform = init_midas(DEVICE)
    face_detector = init_face_detector()
    pose_model    = init_pose_model()

    # 3) 비디오 IO 설정
    cap = cv2.VideoCapture(input_video_path)
    if not cap.isOpened():
        raise FileNotFoundError(f"Cannot open video: {input_video_path}")

    width  = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    fps    = cap.get(cv2.CAP_PROP_FPS)
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')

    out_annot = cv2.VideoWriter(output_annot_path, fourcc, fps, (width, height))
    out_depth = cv2.VideoWriter(output_depth_path, fourcc, fps, (width, height))

    tracked_people, next_id, total_frames = [], 0, 0

    # 4) 프레임별 처리
    while True:
        ret, frame = cap.read()
        if not ret: break
        total_frames += 1
        annotated_frame, depth_frame, face_infos = annotate_image_with_heat(frame, face_detector, pose_model, midas_model, midas_transform)
        for face in face_infos:
            best_iou,best_id=0,None
            for p in tracked_people:
                iou=compute_iou(face['box'],p['last_box'])
                if iou>best_iou and iou>0.5: best_iou,best_id=iou,p['id']
            if best_id is not None:
                for p in tracked_people:
                    if p['id']==best_id:
                        p['last_box']=face['box']
                        p['frames_seen']+=1
                        cx,cy=face['heat_center'] or (None,None)
                        if cx is not None and ROI_BOX[0]<=cx<=ROI_BOX[2] and ROI_BOX[1]<=cy<=ROI_BOX[3]: p['frames_looking']+=1
                        if abs(face['pitch'])<FRONT_THRESHOLD_DEG and abs(face['yaw'])<FRONT_THRESHOLD_DEG: p['frames_facing']+=1
                        face['id']=best_id
                        break
            else:
                face['id']=next_id
                inside = False
                if face['heat_center']:
                    cx,cy=face['heat_center']
                    inside = ROI_BOX[0]<=cx<=ROI_BOX[2] and ROI_BOX[1]<=cy<=ROI_BOX[3]
                tracked_people.append({'id':next_id,'last_box':face['box'],'frames_seen':1,'frames_looking':1 if inside else 0,'frames_facing':1 if (abs(face['pitch'])<FRONT_THRESHOLD_DEG and abs(face['yaw'])<FRONT_THRESHOLD_DEG) else 0,'pitch':face['pitch'],'yaw':face['yaw'],'roll':face['roll']})
                next_id+=1
        cv2.rectangle(annotated_frame,(ROI_BOX[0],ROI_BOX[1]),(ROI_BOX[2],ROI_BOX[3]),(0,0,255),2)
        for face in face_infos: x1,y1,_,_=face['box']; cv2.putText(annotated_frame,f"ID{face['id']}",(x1,y1-10),cv2.FONT_HERSHEY_SIMPLEX,0.6,(0,255,0),2)
        out_annot.write(annotated_frame); out_depth.write(depth_frame)
    cap.release(); out_annot.release(); out_depth.release()
    arr = np.array([p['frames_looking'] for p in tracked_people if p['frames_looking']>=MIN_LOOK_FRAMES]) if any(p['frames_looking']>=MIN_LOOK_FRAMES for p in tracked_people) else np.array([])
    face_group = [p['frames_facing'] for p in tracked_people if p['frames_facing']>=MIN_LOOK_FRAMES]
    result = {
        "cv_info_time_stamp": datetime.datetime.now(datetime.timezone(datetime.timedelta(hours=9))).isoformat(),
        "cv_info_mid_time": round(arr.mean()/30,2) if arr.size else None,
        "cv_info_min_time": round(arr.min()/30,2) if arr.size else None,
        "cv_info_q1_time": round(np.percentile(arr,25)/30,2) if arr.size else None,
        "cv_info_q3_time": round(np.percentile(arr,75)/30,2) if arr.size else None,
        "cv_info_max_time": round(arr.max()/30,2) if arr.size else None,
        "cv_info_exposure_score": round(len(tracked_people)*len(face_group)*(arr.mean()/30),2) if arr.size and face_group else None,
        "cv_info_attention_ration": round(float(np.mean(face_group)),2) if face_group else None,
        "cv_info_view_count": int(arr.size)
    }
    print(json.dumps(result, ensure_ascii=False, indent=2))
    return result

if __name__ == "__main__":
    import argparse
    p=argparse.ArgumentParser()
    p.add_argument("input_video")
    p.add_argument("--annot","-a",default=None)
    p.add_argument("--depth","-d",default=None)
    args=p.parse_args()
    run_inference(args.input_video,args.annot,args.depth)