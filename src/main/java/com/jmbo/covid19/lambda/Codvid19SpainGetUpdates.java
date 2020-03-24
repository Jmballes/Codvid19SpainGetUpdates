package com.jmbo.covid19.lambda;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
public class Codvid19SpainGetUpdates  {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
    private AmazonSNS sns= AmazonSNSClientBuilder.standard().build();
    public Codvid19SpainGetUpdates() {}

    //Handler for testing 
    public Codvid19SpainGetUpdates(AmazonS3 s3) {
        this.s3 = s3;
    }

    private static final String bucket_name=System.getenv("BUCKET_NAME");

    private static final String origen="https://covid19.isciii.es/";
    private static final String file_name_web="current_web.txt";
    private static final String file_name_phones="phonelist.txt";
    private static final String file_name_csv="resources/data.csv";
    
    //Make http request to download source web
    private String sendGet(String url) {
    	HttpGet request = new HttpGet(url);


        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result=null;
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                result = EntityUtils.toString(entity);

            }

        }catch (Exception e) {
			e.printStackTrace();
		}
        return result;
    }

    public String handleRequest(Map<String,Object> input, Context context) {
    	

    	  

    	System.out.println("Inicio de metodo handle request");
        String currents3web = s3.getObjectAsString(bucket_name, file_name_web);
        String []phoneList= s3.getObjectAsString(bucket_name, file_name_phones).split(",");
        String currents3bodycsv = s3.getObjectAsString(bucket_name, file_name_csv);

 
        
        String contentweb=sendGet(origen);
        String contentcsv=sendGet(origen+file_name_csv);
        if (!contentweb.equals(currents3web) || !contentcsv.equals(currents3bodycsv)) {
        	s3.putObject(bucket_name, file_name_web, contentweb);
        	s3.putObject(bucket_name, file_name_csv, contentcsv);
        	String content=procesData(contentcsv);
        	for (String phone: phoneList) {
        		PublishRequest pubRequest = new PublishRequest();

        	    pubRequest.setMessage("Actualizada la Situación de COVID-19 en España: "+ content+ " https://covid19.isciii.es/");
        	    pubRequest.setPhoneNumber(phone);
        	    	Map<String, MessageAttributeValue> smsAttributes =
        	    	        new HashMap<String, MessageAttributeValue>();
        	    smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
        	            .withStringValue("Transactional") 
        	            .withDataType("String"));
        	    pubRequest.setMessageAttributes(smsAttributes);
        	    PublishResult pubresult = sns.publish(pubRequest);

        	    System.out.println("pubresult"+pubresult);
        	    System.out.println("response:"+pubresult.toString());
        	    System.out.println("headers:"+pubresult.getSdkHttpMetadata().getHttpHeaders());
        	    System.out.println("sdk:"+pubresult.getSdkHttpMetadata().toString());
        	    System.out.println(pubresult.getSdkResponseMetadata());
        	    System.out.println(pubresult.getSdkResponseMetadata());
        	    
        	}

        }

        return "ok";
    }
    //Generate content to send by sms from source web
    public String procesData(String content){
        String lines[] = content.split("\\r?\\n");
        String header[] = lines[0].split(",");
        String values[] = lines[1].split(",");
       
        StringBuilder stringBuilder= new StringBuilder();
        for (int i=2;i<header.length;i++) {
        	stringBuilder.append(header[i] + ": " + values[i] + ", ");
        	
		}
        String value=values[1] + " de " + values[0] + " "+ stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        value=value.substring(0, value.length()-1);
        System.out.println("data:" +value);
    	return value;
    }
}