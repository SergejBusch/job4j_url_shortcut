package ru.job4j.urlshortcut.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.job4j.urlshortcut.model.Site;
import ru.job4j.urlshortcut.model.dto.DTOSite;
import ru.job4j.urlshortcut.repository.SiteRepository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SiteService {
    private SiteRepository repository;
    private BCryptPasswordEncoder encoder;

    public SiteService(SiteRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public DTOSite register(Map<String, String> request) {
        if (!request.containsKey("site")) {
            throw new IllegalArgumentException("Parameter site is not found");
        }
        String url = request.get("site");
        var exist = new AtomicBoolean(true);
        AtomicReference<String> pass = new AtomicReference<>("");
        var site = repository.findByDomain(url).orElseGet(() -> {
            exist.set(false);
            var tempSite = generateNewShortcut(url);
            pass.set(tempSite.getPassword());
            tempSite.setPassword(encoder.encode(tempSite.getPassword()));
            return repository.save(tempSite);
        });
        return new DTOSite(exist.get(), site.getLogin(), exist.get() ? "" : pass.get());

    }

    private Site generateNewShortcut(String url) {
        String login = generateString("login");
        String pass = generateString("pass");

        var site = new Site();
        site.setDomain(url);
        site.setLogin(login);
        site.setPassword(pass);
        return site;
    }

    private String generateString(String type) {
        var generated = new AtomicReference<>(RandomStringUtils.randomAlphanumeric(6));
        if ("login".equals(type)) {
            repository.findByLogin(generated.get()).ifPresent(s -> generated.set(generateString(type)));
        } else {
            repository.findByPassword(generated.get()).ifPresent(s -> generated.set(generateString(type)));
        }
        return generated.get();
    }
}
