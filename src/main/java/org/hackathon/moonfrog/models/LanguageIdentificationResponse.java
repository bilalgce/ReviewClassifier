package org.hackathon.moonfrog.models;

import lombok.Data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class LanguageIdentificationResponse {

	private String language;

}
