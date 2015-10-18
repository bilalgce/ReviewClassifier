package org.hackathon.moonfrog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class AnalyticsRequest {
	private boolean sentimentStatistics;
	private NegativeSentiments negativeSentiments;
	
	
}
