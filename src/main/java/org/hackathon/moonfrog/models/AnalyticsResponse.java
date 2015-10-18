package org.hackathon.moonfrog.models;

import java.util.List;

import lombok.Data;

@Data
public class AnalyticsResponse {
	private List<Review> reviewStats;
	private NegativeSentiments negativeSentiments;
	
	@Data
	public static class Review {
		public String date;
		public String positive;
		public String negative;
		public String neutral;
	}

	
}
