package com.task.TaskManager.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProjectFlowDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectManagerName;
}
