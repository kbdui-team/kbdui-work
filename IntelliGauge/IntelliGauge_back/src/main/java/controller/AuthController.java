package controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.transform.Result;

public class AuthController {
    @PostMapping("/login")
    public String login(@RequestBody String request) {
        return "success";
    }
}
