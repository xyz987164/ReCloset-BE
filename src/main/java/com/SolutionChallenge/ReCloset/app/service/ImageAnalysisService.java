package com.SolutionChallenge.ReCloset.app.service;


import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import com.SolutionChallenge.ReCloset.global.exception.SuccessCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
@Service
public class ImageAnalysisService {

    private final RestTemplate restTemplate;
    private final String IMAGE_SERVER_URL;
    private final String LLM_SERVER_URL;
    private final ImageService imageService;

    private static final Map<String, String> DAMAGE_TYPE_CODE_MAP = Map.of(
            "Large tear", "1",
            "Wear / Small tear", "2",
            "Shrinkage / Stretching / Wrinkling", "3",
            "Buckle / Button / Zipper damage", "4",
            "Oil / Food / Chemical stain", "5",
            "Ink", "6",
            "Mold", "7"
    );

    @Autowired
    public ImageAnalysisService(RestTemplate restTemplate,
                                @Value("${image.server.url}") String imageServerUrl,
                                @Value("${llm.server.url}") String llmServerUrl,
                                ImageService imageService) {
        this.restTemplate = restTemplate;
        this.IMAGE_SERVER_URL = imageServerUrl;
        this.LLM_SERVER_URL = llmServerUrl;
        this.imageService = imageService;
    }

    public ResponseEntity<ApiResponseTemplete<Map<String, Object>>> processImageAnalysis(MultipartFile imageFile) {
        try {
            // 1. MultipartFile -> File 변환
            File tempFile = File.createTempFile("upload", imageFile.getOriginalFilename());
            imageFile.transferTo(tempFile);

            // 2. multipart/form-data 요청 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 3. 이미지 분석 서버 호출
            ResponseEntity<Map> imageResponse = restTemplate.postForEntity(IMAGE_SERVER_URL, requestEntity, Map.class);
            tempFile.delete(); // 임시 파일 삭제

            if (imageResponse.getStatusCode() != HttpStatus.OK || imageResponse.getBody() == null) {
                return ApiResponseTemplete.error(ErrorCode.IMAGE_SERVER_ERROR, Map.of("error", "이미지 분석 서버 오류"));
            }

            Map<String, Object> imageResult = imageResponse.getBody();
            String prediction = String.valueOf(imageResult.get("prediction"));
            Double confidence = ((Number) imageResult.get("confidence")).doubleValue();

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("prediction", prediction);
            responseBody.put("confidence", confidence);

            if (confidence >= 0.3) {
                // 4. prediction → damage_type 숫자 코드 매핑
                String mappedCode = DAMAGE_TYPE_CODE_MAP.get(prediction);
                if (mappedCode == null) {
                    return ApiResponseTemplete.error(ErrorCode.LLM_SERVER_ERROR, Map.of("error", "damage_type 매핑 실패: " + prediction));
                }

                Map<String, String> llmRequestBody = Map.of("damage_type", mappedCode);

                HttpHeaders llmHeaders = new HttpHeaders();
                llmHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> llmRequest = new HttpEntity<>(llmRequestBody, llmHeaders);

                ResponseEntity<Map> llmResponse = restTemplate.postForEntity(LLM_SERVER_URL, llmRequest, Map.class);

                if (llmResponse.getStatusCode() != HttpStatus.OK || llmResponse.getBody() == null) {
                    return ApiResponseTemplete.error(ErrorCode.LLM_SERVER_ERROR, Map.of("error", "LLM 서버 응답 오류"));
                }

                Map<String, Object> llmData = llmResponse.getBody();

                responseBody.put("resultType", false);
                responseBody.put("response_data", Map.of(
                        "response", llmData.get("response"),
                        "solution", llmData.get("solution")
                ));
            } else {
                responseBody.put("resultType", true);
            }

            return ApiResponseTemplete.success(SuccessCode.ANALYSIS_SUCCESS, responseBody);

        } catch (Exception e) {
            return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR,
                    Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }
}
