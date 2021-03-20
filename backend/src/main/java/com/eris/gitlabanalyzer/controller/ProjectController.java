package com.eris.gitlabanalyzer.controller;

import com.eris.gitlabanalyzer.model.AnalyticsProgress;
import com.eris.gitlabanalyzer.model.Project;
import com.eris.gitlabanalyzer.model.RawTimeLineProjectData;
import com.eris.gitlabanalyzer.service.AnalyticsService;
import com.eris.gitlabanalyzer.service.AuthService;
import com.eris.gitlabanalyzer.service.MessageService;
import com.eris.gitlabanalyzer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final AnalyticsService analyticsService;
    private final AuthService authService;

    @Autowired
    public ProjectController(ProjectService projectService, AnalyticsService analyticsService, AuthService authService, MessageService messageService){
        this.projectService = projectService;
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    @GetMapping(path = "/{projectId}/rawdata")
    public RawTimeLineProjectData analyzeProject(
            @PathVariable("projectId") Long projectId,
            @RequestParam("startDateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTime,
            @RequestParam("endDateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTime) {
        return projectService.getTimeLineProjectData(projectId, startDateTime, endDateTime);
    }

    @GetMapping
    public List<Project> getProjects(){
        return projectService.getProjects();
    }

    @PostMapping(path = "/analytics")
    public List<Long> saveAllFromGitlab(
            Principal principal,
            @RequestBody List<Long> projectIdList,
            @RequestParam("startDateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTime,
            @RequestParam("endDateTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTime){
        var user = authService.getLoggedInUser(principal);
        return analyticsService.saveAllFromGitlab(user, projectIdList, startDateTime, endDateTime);
    }

    @GetMapping(path = "/analytics/progress/{userId}")
    public AnalyticsProgress getProgress(@PathVariable("userId") Long userId){
        return analyticsService.getProgress(userId);
    }
}
