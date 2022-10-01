package github.michaelgrav.coronavirustracker.controllers;

import github.michaelgrav.coronavirustracker.models.LocationStats;
import github.michaelgrav.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    @Autowired
    CoronavirusDataService coronavirusDataService;

    @GetMapping("/") // root url
    public String home(Model model) {
        model.addAttribute("locationStats", coronavirusDataService.getAllStats());

        // Format the numbers to include commas
        model.addAttribute("totalReportedCases", NumberFormat.getNumberInstance(Locale.US).format(coronavirusDataService.getTotalReportedCases()));
        model.addAttribute("totalNewCasesFormatted", NumberFormat.getNumberInstance(Locale.US).format(coronavirusDataService.getTotalNewCases()));

        model.addAttribute("totalNewCases", coronavirusDataService.getTotalNewCases());
        model.addAttribute("maxDiffLocation", coronavirusDataService.getMaxDiffLocation());
        model.addAttribute("maxDiffLocationCasesFormatted", NumberFormat.getNumberInstance(Locale.US).format(coronavirusDataService.getMaxDiffLocation().getDiffFromPreviousDay()));

        return "home"; // Maps to the home html file
    }
}
