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
/*
@Service
public class ImageAnalysisService {

    private final RestTemplate restTemplate;
    private final String IMAGE_SERVER_URL;
    private final String LLM_SERVER_URL;
    private final ImageService imageService;

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

            // 임시 파일 삭제
            tempFile.delete();

            if (imageResponse.getStatusCode() != HttpStatus.OK || imageResponse.getBody() == null) {
                return ApiResponseTemplete.error(ErrorCode.IMAGE_SERVER_ERROR, Map.of("error", "이미지 분석 서버 오류"));
            }

            Map<String, Object> imageResult = imageResponse.getBody();
            Double confidence = ((Number) imageResult.get("confidence")).doubleValue();

            // confidence 0.3 이상이면 → LLM 서버로 전달
            if (confidence >= 0.3) {
                HttpHeaders llmHeaders = new HttpHeaders();
                llmHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> llmRequest = new HttpEntity<>(imageResult, llmHeaders);

                ResponseEntity<Map> llmResponse = restTemplate.postForEntity(LLM_SERVER_URL, llmRequest, Map.class);

                if (llmResponse.getStatusCode() != HttpStatus.OK || llmResponse.getBody() == null) {
                    return ApiResponseTemplete.error(ErrorCode.LLM_SERVER_ERROR, Map.of("error", "LLM 서버 오류"));
                }

                Map<String, Object> llmResult = llmResponse.getBody();

                // resultType: false (기부 불가)
                llmResult.put("resultType", false);

                return ApiResponseTemplete.success(SuccessCode.ANALYSIS_SUCCESS, llmResult);
            }

            // confidence < 0.3 → 기부 가능 처리
            Map<String, Object> result = new HashMap<>();
            result.put("status", 200);
            result.put("prediction", imageResult.get("prediction"));
            result.put("confidence", confidence);
            result.put("resultType", true); // 기부 가능

            return ApiResponseTemplete.success(SuccessCode.ANALYSIS_SUCCESS, result);

        } catch (Exception e) {
            return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }
*/

@Service
public class ImageAnalysisService {

    private final RestTemplate restTemplate;
    private final String IMAGE_SERVER_URL;
    private final ImageService imageService;

    @Autowired
    public ImageAnalysisService(RestTemplate restTemplate,
                                @Value("${image.server.url}") String imageServerUrl,
                                ImageService imageService) {
        this.restTemplate = restTemplate;
        this.IMAGE_SERVER_URL = imageServerUrl;
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

            // 임시 파일 삭제
            tempFile.delete();

            if (imageResponse.getStatusCode() != HttpStatus.OK || imageResponse.getBody() == null) {
                return ApiResponseTemplete.error(ErrorCode.IMAGE_SERVER_ERROR, Map.of("error", "이미지 분석 서버 오류"));
            }

            Map<String, Object> imageResult = imageResponse.getBody();
            Double confidence = ((Number) imageResult.get("confidence")).doubleValue();

            // 이미지 분석 서버의 응답 출력
            System.out.println("이미지 분석 결과: " + imageResult);

            // 응답 반환
            Map<String, Object> result = new HashMap<>();
            result.put("status", 200);
            result.put("prediction", imageResult.get("prediction"));
            result.put("confidence", confidence);

            return ApiResponseTemplete.success(SuccessCode.ANALYSIS_SUCCESS, result);

        } catch (Exception e) {
            return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, Map.of("error", "서버 오류: " + e.getMessage()));
        }
    }
}
