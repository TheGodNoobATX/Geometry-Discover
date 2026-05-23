package com.example.demo;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    /**
     * Built-in admin accounts. Anyone logging in with these credentials gets the ADMIN role
     * (which grants access to comment deletion and any other admin-only actions).
     */
    private static final Map<String, String> ADMIN_ACCOUNTS = Map.of(
        "TheGodNoob", "GeometryDiscoverAdmin560!",
        "Takashi",    "GeometryDiscoverAdmin270!",
        "Sam",        "GeometryDiscoverAdmin120!"
    );

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String adminPassword = ADMIN_ACCOUNTS.get(username);
        if (adminPassword != null) {
            return User.withUsername(username)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles("ADMIN")
                    .build();
        }

        com.example.demo.User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public void registerUser(String username, String password, String email) {
        com.example.demo.User user = new com.example.demo.User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        userRepository.save(user);
    }

    public boolean userExists(String username) {
        if (username != null && ADMIN_ACCOUNTS.containsKey(username)) {
            return true;
        }
        return userRepository.findByUsername(username) != null;
    }

    /** Returns true when the username matches one of the built-in admin accounts. */
    public static boolean isAdmin(String username) {
        return username != null && ADMIN_ACCOUNTS.containsKey(username);
    }
}
