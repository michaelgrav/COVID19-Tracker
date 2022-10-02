package github.michaelgrav.coronavirustracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InfoController {

    @GetMapping("/info") // root url
    public String info() {
        return "info"; // Maps to the info html file
    }
}
