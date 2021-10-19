package ru.job4j.urlshortcut.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DTOStatistic {
    private String url;
    private int count;
}
