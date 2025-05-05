package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.service.ImageAnalysisService;
import com.SolutionChallenge.ReCloset.app.service.ImageService;
import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Tag(name = "이미지 분석 API", description = "이미지를 업로드하고 분석하는 API")
@RestController
@RequestMapping("/api/image")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    @Autowired
    public ImageAnalysisController(ImageAnalysisService imageAnalysisService) {
        this.imageAnalysisService = imageAnalysisService;
    }

    @Operation(
            summary = "이미지 업로드 및 분석",
            description = "이미지를 업로드한 후 오염 여부 및 기부 가능 여부를 분석합니다."
    )
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseTemplete<Map<String, Object>>> analyzeImage(
            @Parameter(
                    description = "업로드할 이미지 파일",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("image") MultipartFile imageFile) {

        // 실제 이미지 파일을 바로 분석 서비스로 전달
        return imageAnalysisService.processImageAnalysis(imageFile);
    }
}
