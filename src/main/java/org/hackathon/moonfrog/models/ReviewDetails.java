package org.hackathon.moonfrog.models;

import java.util.Date;

import lombok.Data;

@Data
public class ReviewDetails {

	private String reviewerName;
	private Date reviewDate;
	private float rating;
	private String review;

}
