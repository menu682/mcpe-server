package ua.pp.mcpe.server.service.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.pp.mcpe.server.config.security.UserDetailsImpl;
import ua.pp.mcpe.server.config.security.jwt.JwtUtils;

import ua.pp.mcpe.server.dto.EResponseMessage;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.security.JwtResponseDto;
import ua.pp.mcpe.server.dto.security.LoginRequestDto;
import ua.pp.mcpe.server.dto.security.SignupRequestDto;
import ua.pp.mcpe.server.exeptions.BadDataRequestException;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.ERole;
import ua.pp.mcpe.server.persistance.EUserStatus;
import ua.pp.mcpe.server.persistance.entity.security.RoleEntity;
import ua.pp.mcpe.server.persistance.entity.security.UserEntity;
import ua.pp.mcpe.server.persistance.repository.security.RoleRepository;
import ua.pp.mcpe.server.persistance.repository.security.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder encoder,
                       JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponseDto loginUser(LoginRequestDto loginRequestDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getName(),
                        loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshJwt = jwtUtils.generateJwtRefreshToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponseDto(
                jwt,
                refreshJwt,
                userDetails.getId(),
                userDetails.getUsername(),
//                userDetails.getEmail(),
                roles
        );
    }


    public MessageResponseDto registerUser(SignupRequestDto signupRequestDTO) {

        if (signupRequestDTO.getName().isEmpty()
                || signupRequestDTO.getPassword().isEmpty()) {
            throw new BadDataRequestException(EExceptionMessage.FIELDS_MUST_NOT_BE_EMPTY.getMessage());
        }

        if (Boolean.TRUE.equals(userRepository.existsByName(signupRequestDTO.getName()))) {
            throw new BadDataRequestException(EExceptionMessage.NAME_IS_ALREADY_TAKEN.getMessage());
        }



        UserEntity userEntity = new UserEntity();
        userEntity.setName(signupRequestDTO.getName());
        userEntity.setPassword(encoder.encode(signupRequestDTO.getPassword()));
        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new BadDataRequestException(EExceptionMessage.NO_SUCH_ROLE.getMessage()));

        roles.add(userRole);

        userEntity.setStatus(EUserStatus.ACTIVE);
        userEntity.setRoles(roles);
        userRepository.save(userEntity);
        return new MessageResponseDto(EResponseMessage.REGISTER_SUCCESSFULLY.getMessage());

    }

}
