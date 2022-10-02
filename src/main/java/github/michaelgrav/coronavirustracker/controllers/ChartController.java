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
        Map<String, Long> graphData = new TreeMap<>();
        for (LocationStats location: coronavirusDataService.getAllStats()) {
            if (!graphData.containsKey(location.getCountry())) {
                graphData.put(location.getCountry(), location.getLatestTotalCases());
            } else {
                graphData.put(location.getCountry(), graphData.get(location.getCountry()) + location.getLatestTotalCases());
            }
        }
        model.addAttribute("chartData", graphData);

        return "charts"; // Maps to the home html file
    }
}
