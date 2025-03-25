package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.domain.AuthResponse;
import com.SolutionChallenge.ReCloset.app.domain.User;
import com.SolutionChallenge.ReCloset.app.service.Oauth2LoginService;
import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import com.SolutionChallenge.ReCloset.global.exception.ErrorCode;
import com.SolutionChallenge.ReCloset.global.exception.SuccessCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
@RequiredArgsConstructor
public class Oauth2LoginController {

    private final Oauth2LoginService oauth2LoginService;
    private final TokenService tokenService;

    @GetMapping("/code/{registrationId}")
    @Operation(summary = "구글 소셜 로그인후 리디렉션 API (토큰 인증 불필요) \n email, accessToken 반환")

    public ResponseEntity<ApiResponseTemplete<AuthResponse>> googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        try {
            // 구글 로그인 코드로 액세스 토큰을 받고, 이를 통해 사용자 정보를 얻음
            User user = oauth2LoginService.socialLogin(code, registrationId);

            // JWT 토큰 생성
            String jwtToken = tokenService.createAccessToken(user.getEmail());

            // AuthResponse 객체 생성
            AuthResponse authResponse = new AuthResponse(user.getEmail(), jwtToken);

            // 로그인 성공 후 사용자 이메일과 JWT 토큰을 응답
            return ApiResponseTemplete.success(SuccessCode.LOGIN_USER_SUCCESS, authResponse);

        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse(null, "로그인 실패: " + e.getMessage());
            return ApiResponseTemplete.error(ErrorCode.LOGIN_USER_FAILED, errorResponse);
        }
    }

}
