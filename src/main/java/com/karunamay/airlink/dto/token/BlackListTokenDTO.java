package com.karunamay.airlink.dto.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karunamay.airlink.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlackListTokenDTO {

    private Long id;
    private String tokenId;
    private User user;
    private LocalDateTime expiry;
}
