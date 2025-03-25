package com.SolutionChallenge.ReCloset.app.service;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import com.SolutionChallenge.ReCloset.app.dto.RewardRequestDto;
import com.SolutionChallenge.ReCloset.app.dto.RewardUpdateDto;
import com.SolutionChallenge.ReCloset.app.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Transactional
    public Reward createReward(String email, RewardRequestDto rewardRequestDto) {
        // 리워드 객체 생성 시 기본값으로 status를 "PENDING"으로 설정
        Reward reward = Reward.builder()
                .email(email)
                .donationSite(rewardRequestDto.getDonationSite())
                .donationPhoto(rewardRequestDto.getDonationPhoto())
                .status("PENDING")  // 기본 상태를 "PENDING"으로 설정
                .createdAt(LocalDateTime.now()) // 현재 시간으로 createdAt 설정
                .build();

        return rewardRepository.save(reward);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updateRewardStatus(Long id, RewardUpdateDto rewardUpdateDto) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리워드가 존재하지 않습니다."));

        // 상태 값 변경
        reward.setStatus(rewardUpdateDto.getStatus());
        reward.setAcceptedAt(LocalDateTime.now());

        // 거절 사유 및 리워드 지급 여부는 null 허용
        reward.setRejectionReason(rewardUpdateDto.getRejectionReason());
        reward.setRewardGranted(rewardUpdateDto.getRewardGranted());

        rewardRepository.save(reward);
    }

    public List<Reward> getUserRewards(String email) {
        return rewardRepository.findByEmail(email);
    }

    public Optional<Reward> getRewardById(Long id) {
        return rewardRepository.findById(id);
    }

}
