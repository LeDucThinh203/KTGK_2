package com.example.demo.config;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(AccountRepository accountRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Account> accounts = accountRepository.findAll();
        boolean changed = false;

        for (Account account : accounts) {
            String password = account.getPassword();
            if (!isBcryptHash(password)) {
                account.setPassword(passwordEncoder.encode(password));
                changed = true;
            }
        }

        if (changed) {
            accountRepository.saveAll(accounts);
        }

        // Đăng ký tài khoản admin3 và user3 nếu chưa tồn tại
        createAccountIfNotExists("admin3", "123456Aa@", "ADMIN");
        createAccountIfNotExists("user3", "123456Aa@", "ROLE_USER");
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }

    private void createAccountIfNotExists(String loginName, String rawPassword, String roleName) {
        if (accountRepository.findByLoginName(loginName).isPresent()) {
            return;
        }
        var roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            System.err.println("Role '" + roleName + "' not found. Please seed roles first.");
            return;
        }
        var account = new Account();
        account.setLoginName(loginName);
        account.setPassword(passwordEncoder.encode(rawPassword));
        var roles = new java.util.HashSet<com.example.demo.model.Role>();
        roles.add(roleOpt.get());
        account.setRoles(roles);
        accountRepository.save(account);
        System.out.println("Created account: " + loginName + " with role " + roleName);
    }
}
