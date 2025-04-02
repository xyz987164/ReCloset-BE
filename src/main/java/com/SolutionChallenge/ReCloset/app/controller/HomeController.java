package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.domain.RoleType;
import com.SolutionChallenge.ReCloset.app.dto.HomeResponseDto;
import com.SolutionChallenge.ReCloset.app.service.RewardService;
import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import com.SolutionChallenge.ReCloset.global.exception.SuccessCode;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
    private final TokenService tokenService;
    private final RewardService rewardService;

    @Operation(summary = "홈 화면 정보 조회 (Access 토큰 필요) ")
    @GetMapping
    public ResponseEntity<ApiResponseTemplete<HomeResponseDto>> getHomeData(@RequestHeader("Authorization") String authorizationHeader) {
        // JWT 토큰 추출
        String token = authorizationHeader.substring(7);
        Optional<String> emailOptional = tokenService.extractEmail(token);

        String email = emailOptional.get();

        // 리워드 총합 조회
        Integer totalReward = rewardService.getTotalRewardGranted(email);

        // DTO 생성
        HomeResponseDto responseDto = new HomeResponseDto(email, totalReward);

        return ApiResponseTemplete.success(SuccessCode.HOME_DATA_RETRIEVED, responseDto);
    }
}
