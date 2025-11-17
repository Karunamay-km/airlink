package com.karunamay.airlink.repository.token;

import com.karunamay.airlink.model.token.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {

    Optional<BlackListToken> findByTokenId(String token);
}
