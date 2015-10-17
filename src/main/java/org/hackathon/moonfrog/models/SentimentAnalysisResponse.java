package org.hackathon.moonfrog.models;

import java.util.List;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties
public class SentimentAnalysisResponse {

	private List<Sentiment> positive;
	private List<Sentiment> negative;
	private Aggregate aggregate;

	@Data
	@JsonIgnoreProperties
	public static class Sentiment {
		private String sentiment;
		private String topic;
		private double score;
		private String original_text;
		private String original_length;
		private String normalized_text;
		private String normalized_length;

	}

	@Data
	@JsonIgnoreProperties
	public static class Aggregate {
		private String sentiment;
		private double score;
	}
}
