package com.SolutionChallenge.ReCloset.app.repository;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    // 예시: 이메일을 기준으로 기부 정보를 찾기
    List<Reward> findByEmail(String email);
}
