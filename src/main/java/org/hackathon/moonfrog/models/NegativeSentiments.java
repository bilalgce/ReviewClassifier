package org.hackathon.moonfrog.models;

import java.util.Date;
import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties
public class NegativeSentiments {
	private int rating;
	private List<UserProblems> userProblems;

	@Data
	@JsonIgnoreProperties
	public static class UserProblems {
		private List<String> sentiment;
		private String originalReview;
		private String reviewDate;
	}
}