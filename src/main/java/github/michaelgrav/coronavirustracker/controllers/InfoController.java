package github.michaelgrav.coronavirustracker.controllers;

import github.michaelgrav.coronavirustracker.models.LocationStats;
import github.michaelgrav.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.NumberFormat;
import java.util.*;

@Controller
public class InfoController {

    @GetMapping("/info") // root url
    public String info() {
        return "info"; // Maps to the info html file
    }
}
