package ru.job4j.urlshortcut.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.urlshortcut.model.Site;
import ru.job4j.urlshortcut.repository.SiteRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private SiteRepository repository;

    public UserDetailsServiceImpl(SiteRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Site user = repository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getDomain(), user.getPassword(), Collections.emptyList());
    }
}
