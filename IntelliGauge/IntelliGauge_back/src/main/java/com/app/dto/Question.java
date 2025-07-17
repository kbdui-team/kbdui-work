package com.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String question;
    private List<String> options;
    private String answer; // A/B/C/D
}
