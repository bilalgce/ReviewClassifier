package org.hackathon.moonfrog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hackathon.moonfrog.models.HavenApiRequest;
import org.hackathon.moonfrog.models.LanguageIdentificationResponse;
import org.hackathon.moonfrog.models.ReviewDetails;
import org.hackathon.moonfrog.models.ReviewResponse;
import org.hackathon.moonfrog.models.SentimentAnalysisResponse;
import org.hackathon.moonfrog.models.SentimentAnalysisResponse.Sentiment;
import org.hackathon.moonfrog.utilities.DBUtil;
import org.hackathon.moonfrog.utilities.HttpClientUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rating")
public class RatingController {
	private String havenOnDemandUrl = "https://api.havenondemand.com/1/api/sync/%s/v1";
	private static final String apiKey = "058efc09-6378-45d9-b5ac-52ff4c758e90";
	private static final Logger logger = Logger
			.getLogger(RatingController.class.getName());

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ReviewResponse> analyzeReview(
			@RequestBody ReviewDetails reviewDetails) {

		ReviewResponse serviceResponse = new ReviewResponse();

		// Make a call to HavenOnDemand
		HavenApiRequest request = new HavenApiRequest();
		request.setApiKey(apiKey);
		request.setText(reviewDetails.getReview());

		HttpClientUtil httpClientUtil = new HttpClientUtil();
		Map<String, String> headers = new HashMap<String, String>();
		String response;

		// Call language identification api to figure out if reviews are in
		// english
		try {
			logger.info("Calling havenOnDemand language identification api");
			response = httpClientUtil.callPostMethod(
					String.format(havenOnDemandUrl, "identifylanguage"),
					headers, httpClientUtil.writeValueAsString(request),
					"application/json", 200);

		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception while calling language identification api", e);
			serviceResponse
					.setError("Exception while calling languageIdentification api");
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}

		LanguageIdentificationResponse languageResponse;
		try {
			languageResponse = (LanguageIdentificationResponse) httpClientUtil
					.getObject(response,
							"org.hackathon.moonfrog.models.LanguageIdentificationResponse");

		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception while mapping language identification response",
					e);
			serviceResponse
					.setError("Exception while mapping languageIdentification api");
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}

		// If review language is not english return
		if (languageResponse.getLanguage().equals("hindi")) {
			logger.log(Level.INFO, "Language is not english");
			serviceResponse.setError("Language is "
					+ languageResponse.getLanguage());
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}

		// Call sentiment analysis api
		try {
			logger.info("Calling havenOnDemand sentiment analysis api");
			response = httpClientUtil.callPostMethod(
					String.format(havenOnDemandUrl, "analyzesentiment"),
					headers, httpClientUtil.writeValueAsString(request),
					"application/json", 200);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception while calling havenOnDemand",
					e);
			serviceResponse
					.setError("Exception while calling sentiment analysis api");
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}

		SentimentAnalysisResponse sentimentResponse;
		try {
			sentimentResponse = (SentimentAnalysisResponse) httpClientUtil
					.getObject(response,
							"org.hackathon.moonfrog.models.SentimentAnalysisResponse");
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception while mapping sentiment analysis response", e);
			serviceResponse
					.setError("Exception while mapping sentiment analysis api");
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}
		serviceResponse.setSentimentAnalysis(sentimentResponse);

		SimpleDateFormat sdf = new SimpleDateFormat("mmddyyyy");
		Date reviewDate;
		try {
			reviewDate = sdf.parse(reviewDetails.getReviewDate());
		} catch (ParseException e) {
			logger.log(Level.WARNING, "Exception while mapping review date", e);
			serviceResponse.setError("Exception while mapping review date");
			return new ResponseEntity<ReviewResponse>(serviceResponse,
					HttpStatus.PARTIAL_CONTENT);
		}

		double aggregate = serviceResponse.getSentimentAnalysis()
				.getAggregate().getScore();
		String negativeSentiments = "";
		for (Sentiment sentiment : serviceResponse.getSentimentAnalysis()
				.getNegative()) {
			negativeSentiments += sentiment.getOriginal_text();
			negativeSentiments += ",";
		}

		System.out.println(reviewDetails);
		System.out.println(reviewDate);
		System.out.println(aggregate);
		// Add row to userreviews DB
		DBUtil dbUtil = DBUtil.getInstance();
		dbUtil.start();
		dbUtil.insertUserReview(reviewDetails.getReviewerName(), reviewDate,
				reviewDetails.getRating(), reviewDetails.getReview(),
				aggregate, negativeSentiments);
		dbUtil.close();
		return new ResponseEntity<ReviewResponse>(serviceResponse,
				HttpStatus.OK);

	}

	/*
	 * @RequestMapping(method = RequestMethod.POST)
	 * 
	 * @ResponseStatus(value = HttpStatus.OK) public
	 * ResponseEntity<ReviewResponse> analyzeReview(
	 * 
	 * @RequestBody ReviewDetails reviewDetails) {
	 */

}
