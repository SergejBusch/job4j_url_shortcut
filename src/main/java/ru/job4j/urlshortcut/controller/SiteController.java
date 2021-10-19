package ru.job4j.urlshortcut.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.urlshortcut.model.dto.DTOLink;
import ru.job4j.urlshortcut.model.dto.DTOSite;
import ru.job4j.urlshortcut.model.dto.DTOStatistic;
import ru.job4j.urlshortcut.service.LinkService;
import ru.job4j.urlshortcut.service.SiteService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SiteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiteController.class.getSimpleName());

    private SiteService siteService;
    private LinkService linkService;
    private final ObjectMapper objectMapper;

    public SiteController(SiteService siteService, LinkService linkService, ObjectMapper objectMapper) {
        this.siteService = siteService;
        this.linkService = linkService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public DTOSite register(@RequestBody Map<String, String> site) {
        return siteService.register(site);
    }

    @PostMapping(value = "/convert")
    public DTOLink convert(@RequestBody Map<String, String> link) throws URISyntaxException {
        return linkService.convert(link);
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code) {
        var link = linkService.redirect(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(link));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/statistic")
    public List<DTOStatistic> statistic() {
        return linkService.statistic();
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }
}
