package ru.job4j.urlshortcut.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DTOSite {
    private boolean registration;
    private String login;
    private String password;
}
