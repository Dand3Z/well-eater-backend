package pl.well_eater.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.well_eater.security.model.RoleEnum;
import pl.well_eater.security.model.UserEntity;
import pl.well_eater.security.repository.UserRepository;



@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserEntity registerUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(RoleEnum.ROLE_USER.value);
        return userRepository.save(user);
    }

    // TODO: impl
    public String registerAdmin(UserEntity admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRoles(RoleEnum.ROLE_ADMIN.value + "," + RoleEnum.ROLE_USER.value);
        userRepository.save(admin);
        return "Admin Added Successfully";
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
