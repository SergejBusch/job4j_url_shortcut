package ru.job4j.urlshortcut.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.job4j.urlshortcut.model.Link;
import ru.job4j.urlshortcut.model.dto.DTOLink;
import ru.job4j.urlshortcut.model.dto.DTOStatistic;
import ru.job4j.urlshortcut.repository.LinkRepository;
import ru.job4j.urlshortcut.repository.SiteRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LinkService {

    private LinkRepository linkRepository;
    private SiteRepository siteRepository;

    public LinkService(LinkRepository linkRepository, SiteRepository siteRepository) {
        this.linkRepository = linkRepository;
        this.siteRepository = siteRepository;
    }

    public DTOLink convert(Map<String, String> request) throws URISyntaxException {
        urlKeyCheck(request);
        String url = request.get("url");
        validURLCheck(url);
        var result = linkRepository.findByUrl(url).orElseGet(() -> {
            var code = getNewCode();
            var link = new Link();
            link.setUrl(url);
            link.setCode(code);
            return linkRepository.save(link);
        });
        return new DTOLink(result.getCode());
    }

    private String getNewCode() {
        AtomicReference<String> code = new AtomicReference<>(RandomStringUtils.randomAlphanumeric(6));
        linkRepository.findByCode(code.get()).ifPresent(s -> code.set(getNewCode()));
        return code.get();
    }

    private void urlKeyCheck(Map<String, String> request) {
        if (!request.containsKey("url")) {
            throw new IllegalArgumentException("Parameter url is not found");
        }
    }

    private void validURLCheck(String url) throws URISyntaxException {
        var host = new URI(url).getHost();
        var site = siteRepository.findByDomain(host);
        if (site.isPresent()
                && url.startsWith(host)
                || url.startsWith("http://" + host)
                || url.startsWith("https://" + host)) {
            return;
        }
        throw new IllegalArgumentException("Invalid URL");
    }

    public String redirect(String code) {
        var link = linkRepository.findByCode(code).orElseThrow(() ->
                new IllegalArgumentException("Code not found"));
        count(link);
        return link.getUrl();
    }

    private void count(Link link) {
        var count = link.getCount();
        link.setCount(++count);
        linkRepository.save(link);
    }

    public List<DTOStatistic> statistic() {
        var links = linkRepository.findAll();
        return links.stream().map(l -> new DTOStatistic(l.getUrl(), l.getCount())).toList();
    }
}
