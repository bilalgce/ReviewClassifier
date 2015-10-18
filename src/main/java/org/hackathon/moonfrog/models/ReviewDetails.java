package org.hackathon.moonfrog.models;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewDetails {

	private String reviewerName;
	private String reviewDate;
	private int rating;
	private String review;

}
