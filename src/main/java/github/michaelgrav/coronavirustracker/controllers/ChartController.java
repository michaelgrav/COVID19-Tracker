package github.michaelgrav.coronavirustracker.controllers;

import github.michaelgrav.coronavirustracker.models.LocationStats;
import github.michaelgrav.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.*;

@Controller
public class ChartController {

    @Autowired
    CoronavirusDataService coronavirusDataService;

    @GetMapping("/charts") // root url
    public String charts(Model model) {
        model.addAttribute("chartData", coronavirusDataService.getDataForGraph());
        model.addAttribute("chartDataForTop10", coronavirusDataService.getTop10ForGraph());

        return "charts"; // Maps to the chart html file
    }
}
