package com.example.telegrambot.service;

import com.example.telegrambot.dto.VacancyDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class vacancyService {
    // save vacancies from various inputs
    // key in the hashmap => String id, value => the vacancy itself
    private final Map<String, VacancyDto> vacancies = new HashMap<>();

    @PostConstruct
    public void init() {
        VacancyDto juniorMaDeveloper = new VacancyDto();
        juniorMaDeveloper.setId("1");
        juniorMaDeveloper.setTitle("Junior Dev at MA");
        juniorMaDeveloper.setShortDescription("Java Core is required!");
        vacancies.put("1", juniorMaDeveloper);

        VacancyDto middle = new VacancyDto();
        middle.setId("2");
        middle.setTitle("Middle Java Dev");
        middle.setShortDescription("Join our awesome company!");
        vacancies.put("2", middle);
    }
}
