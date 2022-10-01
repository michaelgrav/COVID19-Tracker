package github.michaelgrav.coronavirustracker.controllers;

import github.michaelgrav.coronavirustracker.models.LocationStats;
import github.michaelgrav.coronavirustracker.services.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    @Autowired
    CoronavirusDataService coronavirusDataService;

    @GetMapping("/") // root url
    public String home(Model model) {
        // This could be moved into the covidDatService file
        List<LocationStats> allStats = coronavirusDataService.getAllStats();
        long totalReportedCases = allStats.stream().mapToLong(LocationStats::getLatestTotalCases).sum();
        long totalNewCases = allStats.stream().mapToLong(LocationStats::getDiffFromPreviousDay).sum();

        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", NumberFormat.getNumberInstance(Locale.US).format(totalReportedCases));
        model.addAttribute("totalNewCases", NumberFormat.getNumberInstance(Locale.US).format(totalNewCases));

        return "home"; // Maps to the home html file
    }
}
