package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import com.SolutionChallenge.ReCloset.app.domain.RoleType;
import com.SolutionChallenge.ReCloset.app.domain.Status;
import com.SolutionChallenge.ReCloset.app.dto.RewardDetailDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardRequestDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardSummaryDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardUpdateDto;
import com.SolutionChallenge.ReCloset.app.service.RewardService;
import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import com.SolutionChallenge.ReCloset.global.exception.SuccessCode;
import com.SolutionChallenge.ReCloset.global.exception.model.CustomException;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.SolutionChallenge.ReCloset.app.domain.Status.fromValue;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Autowired
    private TokenService tokenService;

    @Value("${imgbb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    @PostMapping(value = "/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리워드 요청 (Access 토큰 필요) / USER, ADMIN")
    public ResponseEntity<ApiResponseTemplete<Reward>> createReward(
            @RequestPart("donationSite") String donationSite,
            @RequestPart("donationPhoto") MultipartFile donationPhoto,
            @RequestHeader("Authorization") String authorizationHeader) {

        // 1. 입력 유효성 검사
        if (donationSite == null || donationSite.isBlank() || donationPhoto == null || donationPhoto.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, null);
        }

        // 2. 토큰에서 이메일 추출
        String token = authorizationHeader.substring(7);
        Optional<String> emailOptional = tokenService.extractEmail(token);
        if (emailOptional.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, null);
        }
        String email = emailOptional.get();

        try {
            // 3. imgbb API 호출하여 이미지 URL 가져오기
            String imageUrl = uploadToImgbb(donationPhoto);

            // 4. RewardRequestDto 생성 후 서비스 호출
            RewardRequestDto rewardRequestDto = new RewardRequestDto();
            rewardRequestDto.setDonationSite(donationSite);
            rewardRequestDto.setDonationPhoto(imageUrl); // URL만 저장

            Reward reward = rewardService.createReward(email, rewardRequestDto);
            return ApiResponseTemplete.success(SuccessCode.REWARD_CREATED, reward);

        } catch (Exception e) {
            return ApiResponseTemplete.error(ErrorCode.IMAGE_UPLOAD_ERROR, null);
        }
    }

    private String uploadToImgbb(MultipartFile file) throws IOException {
        String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.path("data").path("url").asText(); // 이미지 URL
    }


    @GetMapping("/list")
    @Operation(summary = "리워드 목록 조회 (Access 토큰 필요) / USER-본인것만, ADMIN-전부다")
    public ResponseEntity<ApiResponseTemplete<List<RewardSummaryDto>>> getRewards(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Optional<RoleType> roleOptional = tokenService.extractRole(token);
        Optional<String> emailOptional = tokenService.extractEmail(token);

        if (roleOptional.isEmpty() || emailOptional.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, null);
        }

        RoleType role = roleOptional.get();
        String email = emailOptional.get();

        List<Reward> rewards = (role == RoleType.ADMIN) ? rewardService.getAllRewards() : rewardService.getUserRewards(email);

        if (rewards.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.RESOURCE_NOT_FOUND, null);
        }

        List<RewardSummaryDto> rewardSummaryDtos = rewards.stream()
                .map(reward -> RewardSummaryDto.fromEntity(reward, role.name()))
                .collect(Collectors.toList());

        return ApiResponseTemplete.success(SuccessCode.REWARD_LIST_RETRIEVED, rewardSummaryDtos);
    }

    @GetMapping("/list/{id}")
    @Operation(summary = "리워드 세부사항 조회 (Access 토큰 필요) / USER, ADMIN")
    public ResponseEntity<ApiResponseTemplete<RewardDetailDto>> getRewardById(@PathVariable Long id,
                                                                              @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Optional<RoleType> roleOptional = tokenService.extractRole(token);
        Optional<String> emailOptional = tokenService.extractEmail(token);

        if (roleOptional.isEmpty()) {
            return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, null);
        }

        RoleType role = roleOptional.get();
        Reward reward;

        if (role == RoleType.ADMIN) {
            reward = rewardService.getRewardById(id)
                    .orElse(null);
        } else {
            if (emailOptional.isEmpty()) {
                return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, null);
            }
            String email = emailOptional.get();
            reward = rewardService.getUserRewardById(email, id)
                    .orElse(null);
        }

        if (reward == null) {
            return ApiResponseTemplete.error(ErrorCode.RESOURCE_NOT_FOUND, null);
        }

        RewardDetailDto rewardDetailDto = RewardDetailDto.fromEntity(reward, role.name());
        return ApiResponseTemplete.success(SuccessCode.REWARD_DETAIL_RETRIEVED, rewardDetailDto);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "리워드 상태 수정 (Access 토큰 필요) / ADMIN ONLY")
    public ResponseEntity<ApiResponseTemplete<RewardDetailDto>> updateRewardStatus(
            @PathVariable Long id,
            @RequestBody RewardUpdateDto rewardUpdateDto,
            @RequestHeader("Authorization") String authorizationHeader) {

        // 인증 정보에서 토큰을 추출하고 권한을 확인하는 로직
        String token = authorizationHeader.substring(7);
        Optional<RoleType> roleOptional = tokenService.extractRole(token);

        if (roleOptional.isEmpty() || !roleOptional.get().equals(RoleType.ADMIN)) {
            return ApiResponseTemplete.error(ErrorCode.UNAUTHORIZED_EXCEPTION, null);
        }

        try {
            // 필수 요소 공백 처리
            if (rewardUpdateDto.getStatus() == null) {
                return ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, null);
            }

            // status 값을 Enum으로 변환 (잘못된 값에 대한 처리)
            Status status = rewardUpdateDto.getStatus();

            // 필수 필드가 비어있는지 확인
            if (rewardUpdateDto.getRewardGranted() == null) {
                return ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, null);
            }

            // 상태 업데이트 서비스 호출
            rewardUpdateDto.setStatus(status); // Enum을 사용하여 상태 업데이트
            rewardService.updateRewardStatus(id, rewardUpdateDto);

            // 수정된 Reward 객체 조회 (수정된 값 반환)
            Reward updatedReward = rewardService.getRewardById(id)
                    .orElseThrow(() -> new IllegalArgumentException("The reward does not exist"));

            // 수정된 Reward를 DTO로 변환하여 반환
            RewardDetailDto rewardDetailDto = RewardDetailDto.fromEntity(updatedReward, RoleType.ADMIN.name());
            return ApiResponseTemplete.success(SuccessCode.REWARD_STATUS_UPDATED, rewardDetailDto);
        } catch (IllegalArgumentException e) {
            // 예외 처리 - 리소스가 존재하지 않음
            return ApiResponseTemplete.error(ErrorCode.RESOURCE_NOT_FOUND, null);
        } catch (HttpMessageNotReadableException e) {
            // 잘못된 Enum 값 처리
            return ApiResponseTemplete.error(ErrorCode.INVALID_ENUM_VALUE, null);
        } catch (Exception e) {
            // 그 외 모든 예외 처리
            return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, null);
        }
    }
}