package org.hackathon.moonfrog.models;

import lombok.Data;

@Data
public class AnalyticsResponse {
	private int totalReviews;
	private NegativeSentiments negativeSentiments;
	
	@Data
	public static class Review {
		public String date;
		public String positive;
		public String negative;
		public String neutral;
	}

	
}
