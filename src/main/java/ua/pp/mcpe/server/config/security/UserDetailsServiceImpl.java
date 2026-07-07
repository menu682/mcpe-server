package ua.pp.mcpe.server.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.pp.mcpe.server.exeptions.EExceptionMessage;
import ua.pp.mcpe.server.persistance.entity.security.UserEntity;
import ua.pp.mcpe.server.persistance.repository.security.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByName(name)
                .orElseThrow(() ->
                        new UsernameNotFoundException(EExceptionMessage.USER_NOT_FOUND.getMessage() + name));

        return UserDetailsImpl.build(user);
    }
}
