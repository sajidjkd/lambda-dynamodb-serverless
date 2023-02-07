package com.aws.dynnamoappsample.lambda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.dynamoappsample.dao.MusicDao;
import com.aws.dynamoappsample.entity.Music;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.http.SdkHttpMethod;

public class LambdaRequestHandler implements RequestStreamHandler {

	private MusicDao musicDao;

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		System.out.println("Injected objectMapper:::" + mapper);

		BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.US_ASCII));
		AwsProxyRequest request = mapper.readValue(reader, AwsProxyRequest.class);

		try {
			AwsProxyResponse awsProxyResponse = new AwsProxyResponse();
			awsProxyResponse.setStatusCode(HttpStatusCode.OK);

			System.out.println("request.getHttpMethod() ::" + request.getHttpMethod());
			System.out.println("SdkHttpMethod.POST.name()::" + SdkHttpMethod.POST.name());

			System.out.println("request::body->" + request.getBody());

			System.out.println("PathParameters:::" + request.getPathParameters());

			// Invoke Handler Via Route Manager
			// awsProxyResponse.setBody((String) routeManager.invoke(request));

			musicDao = new MusicDao();

			if (request.getHttpMethod().equalsIgnoreCase(SdkHttpMethod.POST.name())) {
				System.out.println("Inside POST...");
				Music music = mapper.readValue(request.getBody(), Music.class);
				musicDao.persistMusic(music);
			} else if (request.getHttpMethod().equalsIgnoreCase(SdkHttpMethod.GET.name())) {
				System.out.println("Inside GET...");
				System.out.println("PathParameters- artist:::" + request.getPathParameters().get("artist"));
				System.out.println("PathParameters- songTitle:::" + request.getPathParameters().get("songTitle"));
				String pk = request.getPathParameters().get("artist");
				String sk = request.getPathParameters().get("songTitle");
				musicDao.getItem(pk, sk);
			}

			output.write(mapper.writeValueAsString(awsProxyResponse).getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
