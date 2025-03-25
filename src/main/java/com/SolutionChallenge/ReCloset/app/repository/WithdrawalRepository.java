package com.SolutionChallenge.ReCloset.app.repository;

import com.SolutionChallenge.ReCloset.app.domain.WithDrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<WithDrawal, Long> {
}
