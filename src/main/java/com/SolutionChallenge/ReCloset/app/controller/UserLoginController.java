package com.SolutionChallenge.ReCloset.app.controller;


import com.SolutionChallenge.ReCloset.global.dto.ApiResponseTemplete;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserLoginController {


    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";


    private final TokenService tokenService;

    /**
     * 로그아웃 API
     */
    @Operation(summary = "로그아웃 API (Access 토큰 필요)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseTemplete<String>> logout(HttpServletRequest request, HttpServletResponse response) {

        // 요청에서 액세스 토큰 추출
        String accessToken = tokenService.extractAccessToken(request).orElse(null);

        if (accessToken == null) {
            return ResponseEntity.status(401).body(
                    ApiResponseTemplete.<String>builder()
                            .status(401)
                            .success(false)
                            .message("인증되지 않은 사용자입니다. (액세스 토큰 없음)")
                            .data(null)
                            .build()
            );
        }

        // 토큰 유효성 검사 (토큰이 만료되었어도 로그아웃은 가능해야 함)
        boolean isValid = tokenService.validateToken(accessToken);

        // 만료된 토큰에서도 로그아웃 처리 가능하도록 수정
        tokenService.extractEmail(accessToken).ifPresent(tokenService::removeRefreshToken);

        // 클라이언트 쿠키/헤더에서 토큰 제거
        response.setHeader("Authorization", "");
        response.setHeader("Refresh-Token", "");

        return ResponseEntity.ok(
                ApiResponseTemplete.<String>builder()
                        .status(200)
                        .success(true)
                        .message("로그아웃 성공")
                        .data(null)
                        .build()
        );
    }

    @GetMapping("/login/google")
    public RedirectView redirectToGoogle() {
        // 올바른 scope 설정
        String scope = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
        String responseType = "code";

        // ✅ prompt=select_account 파라미터 추가
        String url = String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&access_type=offline&prompt=select_account",
                AUTH_URL, clientId, redirectUri, responseType, scope);

        return new RedirectView(url);
    }

}