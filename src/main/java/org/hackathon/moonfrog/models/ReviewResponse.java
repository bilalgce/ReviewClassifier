package org.hackathon.moonfrog.models;

import lombok.Data;

@Data
public class ReviewResponse {
	private SentimentAnalysisResponse sentimentAnalysis;
	private String error;
}
