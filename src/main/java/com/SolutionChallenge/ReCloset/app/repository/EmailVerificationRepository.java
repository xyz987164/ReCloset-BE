package com.SolutionChallenge.ReCloset.app.repository;

import com.SolutionChallenge.ReCloset.app.domain.EmailVerification;
import com.SolutionChallenge.ReCloset.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    /**
     * 특정 User로 EmailVerification을 조회.
     */
    Optional<EmailVerification> findByUser(User user);
    /**
     * 특정 토큰으로 EmailVerification을 조회.
     */
    Optional<EmailVerification> findByToken(String token);
    /**
     * 특정 User의 인증 데이터를 삭제
     */
    @Transactional
    void deleteByUser(User user);
}
