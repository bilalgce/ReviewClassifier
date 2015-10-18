package org.hackathon.moonfrog.models.db;

import java.util.Date;

import lombok.Data;

@Data
public class Reviews {
	private int id;
	private String name;
	private Date reviewDate;
	private int rating;
	private String userReview;
	private double aggregate;
	private String negativeSentiments;
}
