package github.michaelgrav.coronavirustracker.services;

import github.michaelgrav.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;


// Mark this as a spring service
@Service
public class CoronavirusDataService {
    private static final String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    // Run after constructing the instance
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *") // Run the method regularly (every day on the first hour)
    public void fetchCSVData() throws IOException, InterruptedException {
        /*
            This list is needed because if someone tries to access the program while updating the data, it would return an error
            What we can do is populate a new list of data and assign the old one to the new one
            Avoid doing this is prod because we don't want to create state in spring services
         */
        List<LocationStats> newStats = new ArrayList<>();

        // Create a client
        HttpClient client = HttpClient.newHttpClient();

        // Create a request to send
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DATA_URL))
                .build();

        // Send the request and save the response
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        // Parse the csv response

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));

            // The latest cases are at the end of the row
            long latestCases = Long.parseLong(record.get(record.size() - 1));
            long previousDayCases = Long.parseLong(record.get(record.size() - 2));

            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPreviousDay(latestCases - previousDayCases);

            newStats.add(locationStat);
        }

        this.allStats = newStats;
    }

    public LocationStats getMaxDiffLocation() {
        LocationStats maxDiffLocation = null;
        long maxDiff = Long.MIN_VALUE;

        // Find the location that had the greatest change in cases
        for (LocationStats location : allStats) {
            if (location.getDiffFromPreviousDay() > maxDiff)  {
                maxDiff = location.getDiffFromPreviousDay();
                maxDiffLocation = location;
            }
        }

        return maxDiffLocation;
    }

    public long getTotalReportedCases() {
        // Add all of the total cases at each location
        return allStats.stream().mapToLong(LocationStats::getLatestTotalCases).sum();
    }

    public long getTotalNewCases() {
        // Add all of the change in cases at each location
        return allStats.stream().mapToLong(LocationStats::getDiffFromPreviousDay).sum();
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public Map<String, Long> getDataForGraph() {
        Map<String, Long> graphData = new TreeMap<>();
        for (LocationStats location: getAllStats()) {
            if (!graphData.containsKey(location.getCountry())) {
                graphData.put(location.getCountry(), location.getLatestTotalCases());
            } else {
                graphData.put(location.getCountry(), graphData.get(location.getCountry()) + location.getLatestTotalCases());
            }
        }

        return graphData;
    }

    public Map<String, Long> getTop10ForGraph() {
        Map<String, Long> topTenData = new TreeMap<>(Collections.reverseOrder());
        for (LocationStats location: getAllStats()) {
            if (!topTenData.containsKey(location.getCountry())) {
                topTenData.put(location.getCountry(), location.getLatestTotalCases());
            } else {
                topTenData.put(location.getCountry(), topTenData.get(location.getCountry()) + location.getLatestTotalCases());
            }
        }

        return topTenData.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
