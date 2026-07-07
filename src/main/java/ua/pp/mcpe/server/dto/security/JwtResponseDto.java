package ua.pp.mcpe.server.dto.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtResponseDto {

    private String token;
    private String refreshToken;
    private Long id;
    private String name;
    private List<String> roles;
}
