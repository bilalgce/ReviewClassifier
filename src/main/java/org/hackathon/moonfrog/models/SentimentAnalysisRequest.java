package org.hackathon.moonfrog.models;

import lombok.Data;

@Data
public class SentimentAnalysisRequest {
	private String apiKey;
	private String text;

}
