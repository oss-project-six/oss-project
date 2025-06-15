from fastapi import FastAPI, UploadFile, File, Form, BackgroundTasks, Request
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from pydantic import BaseModel
from datetime import datetime
import os, shutil
import mysql.connector

# inference 모듈 import
from inference_video import run_inference

# FastAPI 앱 초기화
app = FastAPI()

# 정적 파일 및 템플릿 설정
app.mount("/videos", StaticFiles(directory="videos"), name="videos")
templates = Jinja2Templates(directory="templates")

# 디렉토리 없으면 생성
os.makedirs("videos", exist_ok=True)

# 데이터 모델 정의
class CVInfo(BaseModel):
    cv_info_time_stamp: datetime
    cv_info_mid_time: float
    cv_info_min_time: float
    cv_info_q1_time: float
    cv_info_q3_time: float
    cv_info_max_time: float
    cv_info_exposure_score: float
    cv_info_attention_ration: float
    cv_info_view_count: int
    ad_slot_id: int

# DB 연결 함수
def get_db_connection():
    return mysql.connector.connect(
        host="oss-project-six-team.cfa8quc488hi.ap-southeast-2.rds.amazonaws.com",
        port=3306,
        user="admin",
        password="wjddbgus123",
        database="oss_project_db"
    )

# Inference 및 DB 저장 처리
async def process_and_save(video_path: str, ad_slot_id: int):
    # 1) 출력 파일 경로를 원본과 같은 폴더에 만든다
    base, ext = os.path.splitext(video_path)
    output_annot = f"{base}_annotated{ext}"
    output_depth = f"{base}_depth{ext}"
    print(video_path)

    # 2) 추론 함수 호출 (얘가 두 영상 파일을 disk에 씁니다)
    #    return 값은 JSON 결과(dict) 이고,
    #    파일은 output_annot, output_depth 에 저장됩니다.
    result: dict = run_inference(video_path, output_annot, output_depth)

    # 3) 이후 DB INSERT 처리 (이전과 동일)
    ts_str = result.pop("cv_info_time_stamp")
    ts = datetime.fromisoformat(ts_str)

    data = CVInfo(**result, ad_slot_id=ad_slot_id, cv_info_time_stamp=ts)
    bucket = ts.replace(minute=0, second=0, microsecond=0, hour=(ts.hour // 2) * 2)

    conn = get_db_connection()
    cur = conn.cursor()
    sql = """
    INSERT INTO cv_info (
        cv_info_time_stamp, cv_info_mid_time, cv_info_min_time,
        cv_info_q1_time, cv_info_q3_time, cv_info_max_time,
        cv_info_exposure_score, cv_info_attention_ration,
        cv_info_view_count, ad_slot_id
    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """
    vals = (
        bucket,
        data.cv_info_mid_time,
        data.cv_info_min_time,
        data.cv_info_q1_time,
        data.cv_info_q3_time,
        data.cv_info_max_time,
        data.cv_info_exposure_score,
        data.cv_info_attention_ration,
        data.cv_info_view_count,
        data.ad_slot_id
    )
    cur.execute(sql, vals)
    conn.commit()
    cur.close()
    conn.close()

# 🔹 루트 경로: 업로드 폼 제공
@app.get("/", response_class=HTMLResponse)
async def main(request: Request):
    return templates.TemplateResponse("upload_form.html", {"request": request})

# 🔹 파일 업로드 및 비동기 inference
@app.post("/upload", response_class=HTMLResponse)
async def upload_video(
    request: Request,
    file: UploadFile = File(...),
    ad_slot_id: int = Form(...),
    background_tasks: BackgroundTasks = None,
):
    save_path = os.path.join("videos", file.filename)
    with open(save_path, "wb") as buf:
        shutil.copyfileobj(file.file, buf)

    background_tasks.add_task(process_and_save, save_path, ad_slot_id)

    return templates.TemplateResponse("upload_done.html", {
        "request": request,
        "filename": file.filename,
        "url": f"/videos/{file.filename}"
    })