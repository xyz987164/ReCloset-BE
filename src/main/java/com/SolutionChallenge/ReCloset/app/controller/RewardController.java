package com.SolutionChallenge.ReCloset.app.controller;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import com.SolutionChallenge.ReCloset.app.domain.RoleType;
import com.SolutionChallenge.ReCloset.app.dto.RewardRequestDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardSummaryDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardUpdateDto;
import com.SolutionChallenge.ReCloset.app.service.RewardService;
import com.SolutionChallenge.ReCloset.global.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Autowired
    private TokenService tokenService; // TokenService 추가

    @PostMapping("/request")
    public ResponseEntity<Reward> createReward(@RequestBody RewardRequestDto rewardRequestDto,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        // Authorization 헤더에서 액세스 토큰 추출
        String token = authorizationHeader.substring(7); // "Bearer " 제거

        // 액세스 토큰에서 이메일 추출
        Optional<String> emailOptional = tokenService.extractEmail(token);

        // 이메일이 없을 경우 예외 처리
        if (emailOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = emailOptional.get(); // Optional에서 값을 추출

        Reward reward = rewardService.createReward(email, rewardRequestDto);
        return ResponseEntity.ok(reward);
    }

    @GetMapping("/list")
    public ResponseEntity<List<RewardSummaryDto>> getUserRewards(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);

        // 이메일과 역할 추출
        Optional<String> emailOptional = tokenService.extractEmail(token);
        Optional<RoleType> roleOptional = tokenService.extractRole(token); // RoleType 추출

        if (emailOptional.isEmpty() || roleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String email = emailOptional.get();
        RoleType role = roleOptional.get(); // Enum으로 역할 추출

        List<Reward> rewards = rewardService.getUserRewards(email);

        // DTO 변환 후 반환 (역할 정보 추가)
        List<RewardSummaryDto> rewardSummaries = rewards.stream()
                .map(reward -> RewardSummaryDto.fromEntity(reward, role.name())) // role을 문자열로 전달
                .collect(Collectors.toList());

        return ResponseEntity.ok(rewardSummaries);
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<String> getRewardById(@PathVariable Long id,
                                                @RequestHeader("Authorization") String authorizationHeader) {
        // Authorization 헤더에서 액세스 토큰 추출
        String token = authorizationHeader.substring(7); // "Bearer " 제거

        // 액세스 토큰에서 role 추출
        Optional<RoleType> roleOptional = tokenService.extractRole(token);

        if (roleOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        RoleType role = roleOptional.get();
        String redirectUrl = null;

        if (role == RoleType.ADMIN) {
            // 관리자라면 /update/{id}로 리디렉션
            redirectUrl = "/update/" + id;
        } else if (role == RoleType.USER) {
            // 일반 사용자는 /list/{id}로 리디렉션
            redirectUrl = "/list/" + id;
        }

        if (redirectUrl != null) {
            return ResponseEntity.status(HttpStatus.FOUND) // 302 리디렉션 응답
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Forbidden");
        }
    }
}

