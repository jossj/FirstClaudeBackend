package com.example.firstclaudebackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class RacingApiService {

    private final RestTemplate restTemplate;

    @Value("${racing-api.base-url:https://api.theracingapi.com/v1}")
    private String baseUrl;

    public RacingApiService(RestTemplate racingApiRestTemplate) {
        this.restTemplate = racingApiRestTemplate;
    }

    public Object getRacecards(String date, String region, String course) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/racecards/pro");
        if (date != null && !date.isBlank()) builder.queryParam("date", date);
        if (region != null && !region.isBlank()) builder.queryParam("region", region);
        if (course != null && !course.isBlank()) builder.queryParam("course", course);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }

    public Object getResults(String startDate, String endDate, String region, String course, Integer limit, Integer skip) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/results");
        if (startDate != null && !startDate.isBlank()) builder.queryParam("start_date", startDate);
        if (endDate != null && !endDate.isBlank()) builder.queryParam("end_date", endDate);
        if (region != null && !region.isBlank()) builder.queryParam("region", region);
        if (course != null && !course.isBlank()) builder.queryParam("course", course);
        if (limit != null) builder.queryParam("limit", limit);
        if (skip != null) builder.queryParam("skip", skip);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }

    public Object searchHorses(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/horses/search")
                .queryParam("name", name)
                .toUriString();
        return restTemplate.getForObject(url, Object.class);
    }

    public Object getHorseResults(String horseId, String startDate, String endDate) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/horses/" + horseId + "/results");
        if (startDate != null && !startDate.isBlank()) builder.queryParam("start_date", startDate);
        if (endDate != null && !endDate.isBlank()) builder.queryParam("end_date", endDate);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }

    public Object searchJockeys(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/jockeys/search")
                .queryParam("name", name)
                .toUriString();
        return restTemplate.getForObject(url, Object.class);
    }

    public Object getJockeyResults(String jockeyId, String startDate, String endDate) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/jockeys/" + jockeyId + "/results");
        if (startDate != null && !startDate.isBlank()) builder.queryParam("start_date", startDate);
        if (endDate != null && !endDate.isBlank()) builder.queryParam("end_date", endDate);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }

    public Object searchTrainers(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/trainers/search")
                .queryParam("name", name)
                .toUriString();
        return restTemplate.getForObject(url, Object.class);
    }

    public Object getTrainerResults(String trainerId, String startDate, String endDate) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/trainers/" + trainerId + "/results");
        if (startDate != null && !startDate.isBlank()) builder.queryParam("start_date", startDate);
        if (endDate != null && !endDate.isBlank()) builder.queryParam("end_date", endDate);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }

    public Object getCourses(String region) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/courses");
        if (region != null && !region.isBlank()) builder.queryParam("region", region);
        return restTemplate.getForObject(builder.toUriString(), Object.class);
    }
}
