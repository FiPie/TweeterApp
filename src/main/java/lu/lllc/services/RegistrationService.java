package lu.lllc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.lllc.dto.RegistrationDto;
import lu.lllc.entities.User;
import lu.lllc.entities.UserRole;
import lu.lllc.repositories.UserRepository;
import lu.lllc.repositories.UserRoleRepository;

@Service
@Transactional
public class RegistrationService {

    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean register(RegistrationDto form) {
        User user = new User();
        if (!form.getPassword().equals( form.getConfirmedPassword() )) {
            return false;
        } else {
            user.setEmail( form.getEmail() );
            user.setFirstName( form.getFirstName() );
            user.setLastName( form.getLastName() );
            String encodedPassword = passwordEncoder.encode( form.getPassword() );
            user.setPassword( encodedPassword );
            user = userRepository.save( user );

            UserRole userRole = new UserRole();
            userRole.setUser( user );
            userRole.setRole( "ROLE_USER" );
            userRole = userRoleRepository.save( userRole );

            return true;
        }
    }

}
