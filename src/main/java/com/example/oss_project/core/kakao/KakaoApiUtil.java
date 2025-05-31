package com.example.oss_project.core.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Component
@RequiredArgsConstructor
public class KakaoApiUtil {

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] getCoordinatesFromAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        JSONObject body = new JSONObject(response.getBody());
        JSONArray documents = body.getJSONArray("documents");
        if (documents.length() == 0) {
            throw new RuntimeException("주소로부터 좌표를 찾을 수 없습니다.");
        }
        JSONObject first = documents.getJSONObject(0);
        double x = first.getDouble("x"); // 경도
        double y = first.getDouble("y"); // 위도

        return new double[]{x, y};
    }
}