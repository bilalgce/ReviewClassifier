package org.hackathon.moonfrog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hackathon.moonfrog.models.ReviewDetails;
import org.hackathon.moonfrog.models.SentimentAnalysisRequest;
import org.hackathon.moonfrog.models.SentimentAnalysisResponse;
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
	private static final String havenOnDemandUrl = "https://api.havenondemand.com/1/api/sync/analyzesentiment/v1";
	private static final String apiKey = "058efc09-6378-45d9-b5ac-52ff4c758e90";
	private static final Logger logger = Logger
			.getLogger(RatingController.class.getName());

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<SentimentAnalysisResponse> getType(
			@RequestBody ReviewDetails reviewDetails) {

		SentimentAnalysisResponse serviceResponse = new SentimentAnalysisResponse();

		// Make a call to HavenOnDemand for sentiment analysis
		SentimentAnalysisRequest request = new SentimentAnalysisRequest();
		request.setApiKey(apiKey);
		request.setText(reviewDetails.getReview());

		HttpClientUtil httpClientUtil = new HttpClientUtil();
		Map<String, String> headers = new HashMap<String, String>();
		String response;
		try {
			response = httpClientUtil.callPostMethod(havenOnDemandUrl, headers,
					httpClientUtil.writeValueAsString(request),
					"application/json", 200);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception while calling havenOnDemand",
					e);
			return new ResponseEntity<SentimentAnalysisResponse>(
					serviceResponse, HttpStatus.PARTIAL_CONTENT);
		}

		try {
			serviceResponse = (SentimentAnalysisResponse) httpClientUtil
					.getObject(response,
							"org.hackathon.moonfrog.models.SentimentAnalysisResponse");
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"Exception while mapping havenOnDemand response", e);
			return new ResponseEntity<SentimentAnalysisResponse>(
					serviceResponse, HttpStatus.PARTIAL_CONTENT);
		}

		logger.info("Reached end");

		return new ResponseEntity<SentimentAnalysisResponse>(serviceResponse,
				HttpStatus.OK);

	}
}
