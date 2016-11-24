package cris.connection;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/*
 * This program demonstrates how to convert speech to text 
 * using the Microsoft's Custom Recognition Intelligent Service (CRIS) [www.cris.ai]
 * Contact unicorn@roboy.org to get an access to Roboy's service account
 */

public class RecognitionService {
	
	private static String serviceUri = "https://cc606eadf8ad4417a0c1271ed7a0fa24.api.cris.ai/cris/speech/query?cid=5ae75d7d-fdda-42fd-ba41-88481fd73337";
	private static String shortPhraseSocket = "https://cc606eadf8ad4417a0c1271ed7a0fa24.api.cris.ai/ws/cris/speech/recognize?cid=5ae75d7d-fdda-42fd-ba41-88481fd73337";
	private static String longPhraseSocket = "https://cc606eadf8ad4417a0c1271ed7a0fa24.api.cris.ai/ws/cris/speech/recognize/continuous?cid=5ae75d7d-fdda-42fd-ba41-88481fd73337";
	private static String appId = "31b3d95b-af74-4550-9619-de76fe33f0f0";
	private static String deviceOs = "Windows";
	
	public static void Recognize(String audioFile, String accessToken) throws IOException {
		
        // send an HTTP request to the recognition endpoint with audio file as binary data 
		// use shortPhraseSocket for audio files under 15 seconds
		// use longPhraseSocket for audio files with duration between 16 and 60 seconds
		
		String authToken = "Bearer " + accessToken;

        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        mediaTypes.add(MediaType.APPLICATION_JSON);

        headers.setAccept(mediaTypes);

        headers.add(HttpHeaders.AUTHORIZATION, authToken);
        headers.set(HttpHeaders.CONTENT_TYPE, "audio/wav;samplerate=16000");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<byte[]> entity = new HttpEntity<>(IOUtils.toByteArray(new FileInputStream(audioFile)), headers);

        String requestId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri)
                .queryParam("format", "json")
                .queryParam("http_endpoint_address", shortPhraseSocket)
                .queryParam("appid", appId)
                .queryParam("locale", "en-US")
                .queryParam("device.os", deviceOs)
                .queryParam("version", "3.0")
                .queryParam("maxnbest", "3")
                .queryParam("scenarios", "ulm")
                .queryParam("instanceid",instanceId)//"1d4b6030-9099-11e0-91e4-0800500c9a66")
                .queryParam("requestid", requestId);
        
        // response contains a JSON object with recognized text
        System.out.println("Sending data to the server...");
        ResponseEntity<String> conversion =
                restTemplate.exchange(builder.build().encode().toUri(),
                        HttpMethod.POST, entity, String.class);
        
       System.out.println("Response:");
       System.out.print(conversion.getBody());
    }
}
