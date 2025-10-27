package com.jobboard.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostedEvent implements Serializable {
    private Long jobId;
    private String title;
    private String companyName;
    private String location;
    private String category;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String postedByUsername;
    private String postedByEmail;
}