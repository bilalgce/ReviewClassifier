package org.hackathon.moonfrog.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("deprecation")
public class HttpClientUtil {
	private HttpClient httpClient;

	public HttpClientUtil() {
		httpClient = new DefaultHttpClient();
	}

	/**
	 * Makes a post method REST call
	 * 
	 * @param fullUrl
	 * @param headers
	 * @param body
	 * @param contentType
	 * @param statusCode
	 * @return
	 * @throws Exception
	 */
	public String callPostMethod(String fullUrl, Map<String, String> headers,
			String body, String contentType, int statusCode) throws Exception {

		HttpPost request = new HttpPost(fullUrl);

		for (String key : headers.keySet()) {
			request.addHeader(key, headers.get(key));
		}

		System.out
				.println("---------------------------------------------------------");
		System.out.println("Headers: " + headers);

		if (!body.equalsIgnoreCase("")) {

			System.out.println("Request: " + body);
			StringEntity bodyEntity = new StringEntity(body);
			bodyEntity.setContentType(contentType);
			request.setEntity(bodyEntity);

		}

		StringBuffer responseBufferString = new StringBuffer();

		long startTime = System.currentTimeMillis();
		HttpResponse response = httpClient.execute(request);
		long endTime = System.currentTimeMillis();

		System.out.println("Time Taken: " + fullUrl + " : "
				+ (endTime - startTime) + "(ms)");

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));

		String line = "";
		while ((line = reader.readLine()) != null) {
			responseBufferString.append(line);
		}

		System.out.println("Response: " + responseBufferString.toString());

		System.out
				.println("---------------------------------------------------------");

		return responseBufferString.toString();

	}

	/**
	 * Converts object into json value Removes null values and retains only
	 * non-null values
	 *
	 * @param value
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public String writeValueAsString(Object value)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.convertValue(value, Map.class);
		return mapper.writeValueAsString(map);
	}

	/**
	 * Takes a string and expected object class name and converts it into actual
	 * object
	 *
	 * @param objectString
	 * @param className
	 * @return
	 */
	public Object getObject(String objectString, String className)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Object responseObj;
		try {
			responseObj = mapper.readValue(objectString,
					Class.forName(className));
		} catch (ClassNotFoundException e) {
			// LOG.error("No class found with name - " + responseClass);
			throw e;
		} catch (JsonParseException e) {
			// LOG.error("Parse exception while reading JSON:" + e);
			throw e;
		} catch (JsonMappingException e) {
			// LOG.error("Excetpion while mapping JSON:" + e);
			throw e;
		} catch (IOException e) {
			// LOG.error("Exception while reading JSON file:" + e);
			throw e;
		}

		return responseObj;
	}

}
