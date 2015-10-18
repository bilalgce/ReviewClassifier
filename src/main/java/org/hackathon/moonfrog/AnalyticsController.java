package org.hackathon.moonfrog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hackathon.moonfrog.models.AnalyticsRequest;
import org.hackathon.moonfrog.models.AnalyticsResponse;
import org.hackathon.moonfrog.models.NegativeSentiments;
import org.hackathon.moonfrog.models.NegativeSentiments.UserProblems;
import org.hackathon.moonfrog.models.db.Reviews;
import org.hackathon.moonfrog.utilities.DBUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<AnalyticsResponse> analyzeReview(
			@RequestBody AnalyticsRequest analyticsRequest) {

		AnalyticsResponse response = new AnalyticsResponse();
		response.setTotalReviews(getTotalReviews(analyticsRequest
				.getNegativeSentiments().getRating()));
		if (analyticsRequest.getNegativeSentiments() != null) {
			// Call function to get it from db
			Map<Double, UserProblems> negativeSentiments = negativeSentiments(analyticsRequest
					.getNegativeSentiments().getRating());

			// Sort by user sentiment ratings
			List<Double> sortedReviewRatings = new ArrayList<Double>(
					negativeSentiments.keySet());
			sortedReviewRatings.sort(null);

			NegativeSentiments sentimentResponse = new NegativeSentiments();
			sentimentResponse.setRating(analyticsRequest
					.getNegativeSentiments().getRating());

			List<UserProblems> userProblems = new ArrayList<NegativeSentiments.UserProblems>();

			for (Double rating : sortedReviewRatings) {
				userProblems.add(negativeSentiments.get(rating));

			}

			sentimentResponse.setUserProblems(userProblems);
			response.setNegativeSentiments(sentimentResponse);

		}

		return new ResponseEntity<AnalyticsResponse>(response, HttpStatus.OK);

	}

	/**
	 * Negative sentiments for a given rating
	 * 
	 * @param rating
	 * @return
	 */
	public Map<Double, UserProblems> negativeSentiments(int rating) {
		DBUtil dbUtil = DBUtil.getInstance();
		dbUtil.start();
		List<Reviews> reviews = dbUtil.getReviews(rating);
		System.out.println(reviews);
		Map<Double, UserProblems> sentiments = new HashMap<Double, UserProblems>();

		for (Reviews review : reviews) {
			if (review.getNegativeSentiments() == null
					|| review.getNegativeSentiments() == "")
				continue;
			if (sentiments.containsKey(review.getAggregate()))
				sentiments.get(review.getAggregate()).getSentiment()
						.add(review.getNegativeSentiments());
			else {
				UserProblems userProblems = new UserProblems();
				List<String> negatives = new ArrayList<String>();
				negatives.add(review.getNegativeSentiments());
				userProblems.setSentiment(negatives);
				userProblems.setOriginalReview(review.getUserReview());
				userProblems.setReviewDate(dateToMMDDYYYY(review
						.getReviewDate()));
				sentiments.put(review.getAggregate(), userProblems);

			}
		}

		return sentiments;

	}

	public String dateToMMDDYYYY(Date date) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/YYYY");
		String s = outputDateFormat.format(date);
		// Fixing the date format, since it is loaded in DB wrongly
		return s.substring(1, 2) + s.substring(0, 1) + s.substring(2);
	}

	public int getTotalReviews(int rating) {
		DBUtil dbUtil = DBUtil.getInstance();
		dbUtil.start();
		List<Reviews> reviews = dbUtil.getReviews(rating);
		return reviews.size();
	}

}
