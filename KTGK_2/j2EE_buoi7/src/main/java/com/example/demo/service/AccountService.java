package com.example.demo.service;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByLoginName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String storedPassword = account.getPassword();
        if (!isBcryptHash(storedPassword)) {
            // Support existing plaintext passwords in DB and migrate them to BCrypt.
            storedPassword = bcryptPasswordEncoder.encode(storedPassword);
            account.setPassword(storedPassword);
            accountRepository.save(account);
        }

        return new User(
                account.getLoginName(),
                storedPassword,
                mapRolesToAuthorities(account)
        );
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Account account) {
        return account.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }
}
