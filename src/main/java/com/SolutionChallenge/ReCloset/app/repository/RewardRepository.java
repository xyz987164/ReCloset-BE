package com.SolutionChallenge.ReCloset.app.repository;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    // 예시: 이메일을 기준으로 기부 정보를 찾기
    List<Reward> findByEmail(String email);
    Optional<Reward> findByEmailAndId(String email, Long id);  // 이메일과 ID로 리워드 조회

    @Query("SELECT COALESCE(SUM(r.rewardGranted), 0) FROM Reward r WHERE r.email = :email")
    Integer getTotalRewardGrantedByEmail(@Param("email") String email);
}
