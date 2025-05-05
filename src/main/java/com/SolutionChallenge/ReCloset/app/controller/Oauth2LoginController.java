package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.domain.AuthResponse;
import com.SolutionChallenge.ReCloset.app.domain.User;
import com.SolutionChallenge.ReCloset.app.service.Oauth2LoginService;
import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.*;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import com.SolutionChallenge.ReCloset.global.exception.SuccessCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class Oauth2LoginController {

    private final Oauth2LoginService oauth2LoginService;
    private final TokenService tokenService; // TokenService 추가

    public Oauth2LoginController(Oauth2LoginService oauth2LoginService, TokenService tokenService) {
        this.oauth2LoginService = oauth2LoginService;
        this.tokenService = tokenService; // TokenService 의존성 주입
    }

    @GetMapping("/code/{registrationId}")
    public ResponseEntity<ApiResponseTemplete<Map<String, String>>> googleLogin(@RequestParam(required = false) String code,
                                                                                @PathVariable String registrationId) {
        // code 파라미터가 없으면 로그인 실패 처리
        if (code == null) {
            Map<String, String> errorData = new HashMap<>();
            errorData.put("error", "Required request parameter 'code' for method parameter type String is not present");
            return ApiResponseTemplete.error(ErrorCode.LOGIN_USER_FAILED, errorData);
        }

        try {
            // 로그인 성공 처리
            User user = oauth2LoginService.socialLogin(code, registrationId);

            // JWT 토큰 생성
            String accessToken = tokenService.createAccessToken(user.getEmail());
            Map<String, String> data = new HashMap<>();
            data.put("accessToken", accessToken);
            data.put("email", user.getEmail());

            // 성공적인 응답 반환
            return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, data);

        } catch (Exception e) {
            // 로그인 처리 중 예외 발생 시
            Map<String, String> errorData = new HashMap<>();
            errorData.put("error", "Internal error: " + e.getMessage());
            return ApiResponseTemplete.error(ErrorCode.INTERNAL_SERVER_ERROR, errorData);
        }
    }
}
