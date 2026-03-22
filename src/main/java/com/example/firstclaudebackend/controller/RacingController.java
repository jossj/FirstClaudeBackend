package com.example.firstclaudebackend.controller;

import com.example.firstclaudebackend.service.RacingApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/racing")
public class RacingController {

    private final RacingApiService racingApiService;

    public RacingController(RacingApiService racingApiService) {
        this.racingApiService = racingApiService;
    }

    /**
     * Get racecards (upcoming races).
     * Optional filters: date (YYYY-MM-DD), region (e.g. "gb", "ire"), course name.
     */
    @GetMapping("/racecards")
    public Object getRacecards(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String course) {
        return racingApiService.getRacecards(date, region, course);
    }

    /**
     * Get race results.
     * Optional filters: start_date, end_date, region, course, limit, skip.
     */
    @GetMapping("/results")
    public Object getResults(
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer skip) {
        return racingApiService.getResults(start_date, end_date, region, course, limit, skip);
    }

    /**
     * Search horses by name.
     */
    @GetMapping("/horses/search")
    public Object searchHorses(@RequestParam String name) {
        return racingApiService.searchHorses(name);
    }

    /**
     * Get results for a specific horse by ID.
     */
    @GetMapping("/horses/{horseId}/results")
    public Object getHorseResults(
            @PathVariable String horseId,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date) {
        return racingApiService.getHorseResults(horseId, start_date, end_date);
    }

    /**
     * Search jockeys by name.
     */
    @GetMapping("/jockeys/search")
    public Object searchJockeys(@RequestParam String name) {
        return racingApiService.searchJockeys(name);
    }

    /**
     * Get results for a specific jockey by ID.
     */
    @GetMapping("/jockeys/{jockeyId}/results")
    public Object getJockeyResults(
            @PathVariable String jockeyId,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date) {
        return racingApiService.getJockeyResults(jockeyId, start_date, end_date);
    }

    /**
     * Search trainers by name.
     */
    @GetMapping("/trainers/search")
    public Object searchTrainers(@RequestParam String name) {
        return racingApiService.searchTrainers(name);
    }

    /**
     * Get results for a specific trainer by ID.
     */
    @GetMapping("/trainers/{trainerId}/results")
    public Object getTrainerResults(
            @PathVariable String trainerId,
            @RequestParam(required = false) String start_date,
            @RequestParam(required = false) String end_date) {
        return racingApiService.getTrainerResults(trainerId, start_date, end_date);
    }

    /**
     * Get list of courses, optionally filtered by region.
     */
    @GetMapping("/courses")
    public Object getCourses(@RequestParam(required = false) String region) {
        return racingApiService.getCourses(region);
    }
}
