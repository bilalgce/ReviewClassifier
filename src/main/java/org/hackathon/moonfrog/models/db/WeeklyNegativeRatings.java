package org.hackathon.moonfrog.models.db;

import lombok.Data;

@Data
public class WeeklyNegativeRatings {
	private int id;
	private int week;
	private int rating;
	private String negativeReviews;
}
