package com.dynatrace.HystrixMonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.dynatrace.diagnostics.pdk.Status;


public class Overview {
	
	//private String Type;
	String tempValue = "0";
	
	//Command Metrics
	boolean HystrixStreamSuccess = true;
	boolean CircuitBreakerOpen = false;
	int CircuitInt = 0;
	long ErrorPercentage = 0;
	long ErrorCount = 0;
	long RequestCount = 0;
	long RollingCountCollapsedRequests = 0;
	long RollingCountExceptionsThrown = 0;
	long RollingCountFailure = 0;
	long RollingCountFallbackFailure = 0;
	long RollingCountFallbackRejection = 0;
	long RollingCountFallbackSuccess = 0;
	long RollingCountResponsesFromCache = 0;
	long RollingCountSemaphoreRejected = 0;
	long RollingCountShortCircuited = 0;
	long RollingCountSuccess = 0;
	long RollingCountThreadPoolRejected = 0;
	long RollingCountTimeout = 0;
	long CurrentConcurrentExecutionCount = 0;
	long LatencyExecute_mean = 0;
	//Array latencyExecute
	long LatencyTotal_mean = 0;
	//Array latencyTotal
	long PropertyValue_circuitBreakerRequestVolumeThreshold = 0;
	long PropertyValue_circuitBreakerSleepWindowInMilliseconds = 0;
	long PropertyValue_circuitBreakerErrorThresholdPercentage = 0;
	boolean PropertyValue_circuitBreakerForceOpen = false;
	boolean PropertyValue_circuitBreakerForceClosed = false;
	boolean PropertyValue_circuitBreakerEnabled = false;
	//String PropertyValue_executionIsolationStrategy = "";
	long PropertyValue_executionIsolationThreadTimeoutInMilliseconds = 0;
	boolean PropertyValue_executionIsolationThreadInterruptOnTimeout = false;
	long PropertyValue_executionIsolationThreadPoolKeyOverride = 0;
	long PropertyValue_executionIsolationSemaphoreMaxConcurrentRequests = 0;
	long PropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests = 0;
	long PropertyValue_metricsRollingStatisticalWindowInMilliseconds = 0;
	boolean PropertyValue_requestCacheEnabled = false;
	boolean PropertyValue_requestLogEnabled = false;
	long ReportingHosts = 0;
	
	//Thread Pool Metrics
	long CurrentActiveCount = 0;
	long CurrentCompletedTaskCount = 0;
	long CurrentCorePoolSize = 0;
	long CurrentLargestPoolSize = 0;
	long CurrentMaximumPoolSize = 0;
	long CurrentPoolSize = 0;
	long CurrentQueueSize = 0;
	long CurrentTaskCount = 0;
	long RollingCountThreadsExecuted = 0;
	long RollingMaxActiveThreads = 0;
	long PropertyValue_queueSizeRejectionThreshold = 0;
	long Thread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds = 0;
	long Thread_ReportingHosts = 0;
	
	private static final Logger log = Logger.getLogger(Overview.class.getName());
	
	public Overview(String server, String port, final String Username, final String Password) throws IOException, ParseException{
		
		log.info("Making call for JSON");
		
		/*Authenticator.setDefault (new Authenticator() {
    	    protected PasswordAuthentication getPasswordAuthentication() {
    	        return new PasswordAuthentication (Username, Password.toCharArray());
    	    }
    	});*/
		JSONParser parser = new JSONParser();
		//String hystrixURL = "http://" + server + ":" + port + "/hystrix.stream";
		String hystrixURL = "http://" + server + "/hystrix.stream";
        String inputLine="";
        String inputLineSub="";
        String inputLineType="";
        int inputLineLength=0;
        String pingLine = "";
        String commandLine = "";
        String threadLine = "";
        String Type = "";
		log.info("URL for Hystrix Stream: " + hystrixURL);
		URL hystrix = new URL(hystrixURL);
		//log.info("Trying to open Hystrix Stream...");
		BufferedReader in = null;
		HttpURLConnection hystrixConn = null;
		//InputStreamReader hystrixStream = new InputStreamReader(hystrix);
		//BufferedReader in = new BufferedReader(new InputStreamReader(hystrix.openStream()));
		//log.info("Hystrix Stream Opened");
        try {
        	//begin new code
        	log.info("Connection timeout set to 10 seconds");
        	log.info("Trying to open Hystrix Stream...");
        	hystrixConn = (HttpURLConnection) hystrix.openConnection();
        	HttpURLConnection.setFollowRedirects(false);
        	hystrixConn.setConnectTimeout(10000);
        	hystrixConn.setReadTimeout(10000);
        	hystrixConn.connect();
        	//InputStream input = hystrixConn.getInputStream();
        	InputStreamReader input = new InputStreamReader(hystrixConn.getInputStream());
        	in = new BufferedReader(input);
        	//begin old code
        	//log.info("Trying to open Hystrix Stream...");
        	//in = new BufferedReader(new InputStreamReader(hystrix.openStream()));
        	log.info("Hystrix Stream Opened");
        	//begin read logic
        	//if(in.ready()) {
        		log.info("Retrieving Hystrix Stream Data");
	        	for (int i=1; i<6; i++){
		        	inputLine=in.readLine();
		        	inputLineLength = inputLine.length();
		        	log.info("inputLineLength: " + inputLineLength);
		        	if(inputLineLength > 4)
		        	{
		        		inputLineSub = inputLine.substring(0,5);
		        		log.info("inputLineSub: " + inputLineSub);
			        	switch (inputLineSub) {
				        	case "ping:":
				        		pingLine = inputLine;
				        		break;
				        	case "data:":
				        		inputLineType = inputLine.substring(22,29);
				        		log.info("inputLineType: " + inputLineType);
				        		if (inputLineType.equals("Command")) {
				        			commandLine = inputLine;
					        		commandLine = commandLine.startsWith("data: ") ? commandLine.substring(6) : commandLine;
				        		}
				        		else {
					        		threadLine = inputLine;
					        		threadLine = threadLine.startsWith("data: ") ? threadLine.substring(6) : threadLine;
				        		}
				        		break;
			        	}
		        	}
	        	}	
	        /*}
        	else {
        		log.severe("The Hystrix Stream is not ready.  Please run the monitor again.");
        		Status error = new Status(Status.StatusCode.ErrorInfrastructure);
        		error.setMessage("The Hystrix Stream is not ready.  Please run the monitor again.");
        		//return error;
        		HystrixStreamSuccess = false;
        		return;
        	}*/
        }
		catch (Exception exp) {
			log.severe("The URL did not return the expected Hystrix Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
			log.log(Level.SEVERE, exp.getMessage(), exp);
			Status error = new Status(Status.StatusCode.ErrorInfrastructure);
    		error.setMessage("The URL did not return the expected Hystrix Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
    		//Close Stream
        	in.close();
            log.info("Hystrix Stream Closed");
    		HystrixStreamSuccess = false;
    		//return error;
    		return;
		}
        finally {
        	//Close Stream
        	if (in != null) {
        		try {in.close();} catch (Exception exp) {log.log(Level.SEVERE, exp.getMessage(), exp);}
        		log.info("Hystrix Stream Closed");
        	}
        	//Close URL Connection
        	if (hystrixConn != null) {
        		try {hystrixConn.disconnect();} catch (Exception exp) {log.log(Level.SEVERE, exp.getMessage(), exp);}
        		log.info("URL Connection Closed");
        	}	
        }
        
        log.info("Result of Hystrix Stream Ping JSON: " + pingLine);
        log.info("Result of Hystrix Stream Command JSON: " + commandLine);
        log.info("Result of Hystrix Stream Thread JSON: " + threadLine);
        
        JSONObject commandObj = new JSONObject();
        JSONObject threadObj = new JSONObject();
        
        try {
        	commandObj = (JSONObject) parser.parse(commandLine);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp );
        }
        
        try {
        	threadObj = (JSONObject) parser.parse(threadLine);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp );
        }
        
        //Retrieve isCurcuitBreakerOpen object from JSON
        try {
        	//Type = (String) obj.get("type");
        	Type = (String) commandObj.get("type");
        	log.info("Message Type: " + Type);
        } catch (Exception exp){
        	log.log(Level.SEVERE, exp.getMessage(), exp);
        }
        

        //if(Type=="HystrixCommand") {
        //Hystrix Command Metrics
	        //Retrieve isCurcuitBreakerOpen object from JSON
	        try {
	        	CircuitBreakerOpen = (boolean) commandObj.get("isCircuitBreakerOpen");
	        	log.info("isCircuitBreakerOpen JSON Value: " + CircuitBreakerOpen);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve ErrorPercentage object from JSON
	        try {
	        	ErrorPercentage = (long) commandObj.get("errorPercentage");
	        	log.info("errorPercentage JSON Value: " + ErrorPercentage);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve errorCount object from JSON
	        try {
	        	ErrorCount = (long) commandObj.get("errorCount");
	        	log.info("errorCount JSON Value: " + ErrorCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve requestCount object from JSON
	        try {
	        	RequestCount = (long) commandObj.get("requestCount");
	        	log.info("requestCount JSON Value: " + RequestCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountCollapsedRequests object from JSON
	        try {
	        	RollingCountCollapsedRequests = (long) commandObj.get("rollingCountCollapsedRequests");
	        	log.info("rollingCountCollapsedRequests JSON Value: " + RollingCountCollapsedRequests);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountExceptionsThrown object from JSON
	        try {
	        	RollingCountExceptionsThrown = (long) commandObj.get("rollingCountExceptionsThrown");
	        	log.info("rollingCountExceptionsThrown JSON Value: " + RollingCountExceptionsThrown);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountFailure object from JSON
	        try {
	        	RollingCountFailure = (long) commandObj.get("rollingCountFailure");
	        	log.info("rollingCountFailure JSON Value: " + RollingCountFailure);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountFallbackFailure object from JSON
	        try {
	        	RollingCountFallbackFailure = (long) commandObj.get("rollingCountFallbackFailure");
	        	log.info("rollingCountFallbackFailure JSON Value: " + RollingCountFallbackFailure);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountFallbackRejection object from JSON
	        try {
	        	RollingCountFallbackRejection = (long) commandObj.get("rollingCountFallbackRejection");
	        	log.info("rollingCountFallbackRejection JSON Value: " + RollingCountFallbackRejection);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountFallbackSuccess object from JSON
	        try {
	        	RollingCountFallbackSuccess = (long) commandObj.get("rollingCountFallbackSuccess");
	        	log.info("rollingCountFallbackSuccess JSON Value: " + RollingCountFallbackSuccess);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountResponsesFromCache object from JSON
	        try {
	        	RollingCountResponsesFromCache = (long) commandObj.get("rollingCountResponsesFromCache");
	        	log.info("rollingCountResponsesFromCache JSON Value: " + RollingCountResponsesFromCache);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountSemaphoreRejected object from JSON
	        try {
	        	RollingCountSemaphoreRejected = (long) commandObj.get("rollingCountSemaphoreRejected");
	        	log.info("rollingCountSemaphoreRejected JSON Value: " + RollingCountSemaphoreRejected);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountShortCircuited object from JSON
	        try {
	        	RollingCountShortCircuited = (long) commandObj.get("rollingCountShortCircuited");
	        	log.info("rollingCountShortCircuited JSON Value: " + RollingCountShortCircuited);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountSuccess object from JSON
	        try {
	        	RollingCountSuccess = (long) commandObj.get("rollingCountSuccess");
	        	log.info("rollingCountSuccess JSON Value: " + RollingCountSuccess);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountThreadPoolRejected object from JSON
	        try {
	        	RollingCountThreadPoolRejected = (long) commandObj.get("rollingCountThreadPoolRejected");
	        	log.info("rollingCountThreadPoolRejected JSON Value: " + RollingCountThreadPoolRejected);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountTimeout object from JSON
	        try {
	        	RollingCountTimeout = (long) commandObj.get("rollingCountTimeout");
	        	log.info("rollingCountTimeout JSON Value: " + RollingCountTimeout);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentConcurrentExecutionCount object from JSON
	        try {
	        	CurrentConcurrentExecutionCount = (long) commandObj.get("currentConcurrentExecutionCount");
	        	log.info("currentConcurrentExecutionCount JSON Value: " + CurrentConcurrentExecutionCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve latencyExecute_mean object from JSON
	        try {
	        	LatencyExecute_mean = (long) commandObj.get("latencyExecute_mean");
	        	log.info("latencyExecute_mean JSON Value: " + LatencyExecute_mean);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve latencyTotal_mean object from JSON
	        try {
	        	LatencyTotal_mean = (long) commandObj.get("latencyTotal_mean");
	        	log.info("latencyTotal_mean JSON Value: " + LatencyTotal_mean);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerRequestVolumeThreshold object from JSON
	        try {
	        	PropertyValue_circuitBreakerRequestVolumeThreshold = (long) commandObj.get("propertyValue_circuitBreakerRequestVolumeThreshold");
	        	log.info("propertyValue_circuitBreakerRequestVolumeThreshold JSON Value: " + PropertyValue_circuitBreakerRequestVolumeThreshold);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerSleepWindowInMilliseconds object from JSON
	        try {
	        	PropertyValue_circuitBreakerSleepWindowInMilliseconds = (long) commandObj.get("propertyValue_circuitBreakerSleepWindowInMilliseconds");
	        	log.info("propertyValue_circuitBreakerSleepWindowInMilliseconds JSON Value: " + PropertyValue_circuitBreakerSleepWindowInMilliseconds);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerErrorThresholdPercentage object from JSON
	        try {
	        	PropertyValue_circuitBreakerErrorThresholdPercentage = (long) commandObj.get("propertyValue_circuitBreakerErrorThresholdPercentage");
	        	log.info("propertyValue_circuitBreakerErrorThresholdPercentage JSON Value: " + PropertyValue_circuitBreakerErrorThresholdPercentage);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerForceOpen object from JSON
	        try {
	        	PropertyValue_circuitBreakerForceOpen = (boolean) commandObj.get("propertyValue_circuitBreakerForceOpen");
	        	log.info("propertyValue_circuitBreakerForceOpen JSON Value: " + PropertyValue_circuitBreakerForceOpen);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerForceClosed object from JSON
	        try {
	        	PropertyValue_circuitBreakerForceClosed = (boolean) commandObj.get("propertyValue_circuitBreakerForceClosed");
	        	log.info("propertyValue_circuitBreakerForceClosed JSON Value: " + PropertyValue_circuitBreakerForceClosed);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_circuitBreakerEnabled object from JSON
	        try {
	        	PropertyValue_circuitBreakerEnabled = (boolean) commandObj.get("propertyValue_circuitBreakerEnabled");
	        	log.info("propertyValue_circuitBreakerEnabled JSON Value: " + PropertyValue_circuitBreakerEnabled);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_executionIsolationThreadTimeoutInMilliseconds object from JSON
	        try {
	        	PropertyValue_executionIsolationThreadTimeoutInMilliseconds = (long) commandObj.get("propertyValue_executionIsolationThreadTimeoutInMilliseconds");
	        	log.info("propertyValue_executionIsolationThreadTimeoutInMilliseconds JSON Value: " + PropertyValue_executionIsolationThreadTimeoutInMilliseconds);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_executionIsolationThreadInterruptOnTimeout object from JSON
	        try {
	        	PropertyValue_executionIsolationThreadInterruptOnTimeout = (boolean) commandObj.get("propertyValue_executionIsolationThreadInterruptOnTimeout");
	        	log.info("propertyValue_executionIsolationThreadInterruptOnTimeout JSON Value: " + PropertyValue_executionIsolationThreadInterruptOnTimeout);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_executionIsolationThreadPoolKeyOverride object from JSON
	        try {
	        	tempValue = (String) commandObj.get("propertyValue_executionIsolationThreadPoolKeyOverride");
	        	if (tempValue != null) {
	        		PropertyValue_executionIsolationThreadPoolKeyOverride = (long) commandObj.get("propertyValue_executionIsolationThreadPoolKeyOverride");
	        		log.info("propertyValue_executionIsolationThreadPoolKeyOverride JSON Value: " + PropertyValue_executionIsolationThreadPoolKeyOverride);
	        	}
	        	else {
	        		log.info("propertyValue_executionIsolationThreadPoolKeyOverride JSON Value: null");
	        	}
	        	
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_executionIsolationSemaphoreMaxConcurrentRequests object from JSON
	        try {
	        	PropertyValue_executionIsolationSemaphoreMaxConcurrentRequests = (long) commandObj.get("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests");
	        	log.info("propertyValue_executionIsolationSemaphoreMaxConcurrentRequests JSON Value: " + PropertyValue_executionIsolationSemaphoreMaxConcurrentRequests);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests object from JSON
	        try {
	        	PropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests = (long) commandObj.get("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests");
	        	log.info("propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests JSON Value: " + PropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_metricsRollingStatisticalWindowInMilliseconds object from JSON
	        try {
	        	PropertyValue_metricsRollingStatisticalWindowInMilliseconds = (long) commandObj.get("propertyValue_metricsRollingStatisticalWindowInMilliseconds");
	        	log.info("propertyValue_metricsRollingStatisticalWindowInMilliseconds JSON Value: " + PropertyValue_metricsRollingStatisticalWindowInMilliseconds);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_requestCacheEnabled object from JSON
	        try {
	        	PropertyValue_requestCacheEnabled = (boolean) commandObj.get("propertyValue_requestCacheEnabled");
	        	log.info("propertyValue_requestCacheEnabled JSON Value: " + PropertyValue_requestCacheEnabled);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_requestLogEnabled object from JSON
	        try {
	        	PropertyValue_requestLogEnabled = (boolean) commandObj.get("propertyValue_requestLogEnabled");
	        	log.info("propertyValue_requestLogEnabled JSON Value: " + PropertyValue_requestLogEnabled);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve reportingHosts object from JSON
	        try {
	        	ReportingHosts = (long) commandObj.get("reportingHosts");
	        	log.info("reportingHosts JSON Value: " + ReportingHosts);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
       //Retrieve metrics from Hystrix Thread Pool JSON
	        //Retrieve currentActiveCount object from JSON
	        try {
	        	CurrentActiveCount = (long) threadObj.get("currentActiveCount");
	        	log.info("currentActiveCount JSON Value: " + CurrentActiveCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentCompletedTaskCount object from JSON
	        try {
	        	CurrentCompletedTaskCount = (long) threadObj.get("currentCompletedTaskCount");
	        	log.info("currentCompletedTaskCount JSON Value: " + CurrentCompletedTaskCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentCorePoolSize object from JSON
	        try {
	        	CurrentCorePoolSize = (long) threadObj.get("currentCorePoolSize");
	        	log.info("currentCorePoolSize JSON Value: " + CurrentCorePoolSize);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentLargestPoolSize object from JSON
	        try {
	        	CurrentLargestPoolSize = (long) threadObj.get("currentLargestPoolSize");
	        	log.info("currentLargestPoolSize JSON Value: " + CurrentLargestPoolSize);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentMaximumPoolSize object from JSON
	        try {
	        	CurrentMaximumPoolSize = (long) threadObj.get("currentMaximumPoolSize");
	        	log.info("currentMaximumPoolSize JSON Value: " + CurrentMaximumPoolSize);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentPoolSize object from JSON
	        try {
	        	CurrentPoolSize = (long) threadObj.get("currentPoolSize");
	        	log.info("currentPoolSize JSON Value: " + CurrentPoolSize);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentQueueSize object from JSON
	        try {
	        	CurrentQueueSize = (long) threadObj.get("currentQueueSize");
	        	log.info("currentQueueSize JSON Value: " + CurrentQueueSize);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve currentTaskCount object from JSON
	        try {
	        	CurrentTaskCount = (long) threadObj.get("currentTaskCount");
	        	log.info("currentTaskCount JSON Value: " + CurrentTaskCount);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingCountThreadsExecuted object from JSON
	        try {
	        	RollingCountThreadsExecuted = (long) threadObj.get("rollingCountThreadsExecuted");
	        	log.info("rollingCountThreadsExecuted JSON Value: " + RollingCountThreadsExecuted);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve rollingMaxActiveThreads object from JSON
	        try {
	        	RollingMaxActiveThreads = (long) threadObj.get("rollingMaxActiveThreads");
	        	log.info("rollingMaxActiveThreads JSON Value: " + RollingMaxActiveThreads);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_queueSizeRejectionThreshold object from JSON
	        try {
	        	PropertyValue_queueSizeRejectionThreshold = (long) threadObj.get("propertyValue_queueSizeRejectionThreshold");
	        	log.info("propertyValue_queueSizeRejectionThreshold JSON Value: " + PropertyValue_queueSizeRejectionThreshold);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve propertyValue_metricsRollingStatisticalWindowInMilliseconds object from JSON
	        try {
	        	Thread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds = (long) threadObj.get("propertyValue_metricsRollingStatisticalWindowInMilliseconds");
	        	log.info("propertyValue_metricsRollingStatisticalWindowInMilliseconds JSON Value: " + Thread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	        //Retrieve reportingHosts object from JSON
	        try {
	        	Thread_ReportingHosts = (long) threadObj.get("reportingHosts");
	        	log.info("reportingHosts JSON Value: " + Thread_ReportingHosts);
	        } catch (Exception exp){
	        	log.log(Level.SEVERE, exp.getMessage(), exp);
	        }
	        
	
	}
	
	//Retrieve JSON metrics for local use
	public int getCircuitBreakerOpen()
	{
		CircuitInt = (CircuitBreakerOpen) ? 1 : 0;
		//log.info("CircuitInt get value: " + CircuitInt);
		return CircuitInt;
	}
	
	public long getErrorPercentage()
	{
		return ErrorPercentage;
	}
	
	public long getErrorCount()
	{
		return ErrorCount;
	}
	
	public long getRequestCount()
	{
		return RequestCount;
	}
	
	public long getRollingCountCollapsedRequests()
	{
		return RollingCountCollapsedRequests;
	}
	
	public long getRollingCountExceptionsThrown()
	{
		return RollingCountExceptionsThrown;
	}
	
	public long getRollingCountFailure()
	{
		return RollingCountFailure;
	}
	
	public long getRollingCountFallbackFailure()
	{
		return RollingCountFallbackFailure;
	}
	
	public long getRollingCountFallbackRejection()
	{
		return RollingCountFallbackRejection;
	}
	
	public long getRollingCountFallbackSuccess()
	{
		return RollingCountFallbackSuccess;
	}
	
	public long getRollingCountResponsesFromCache()
	{
		return RollingCountResponsesFromCache;
	}
	
	public long getRollingCountSemaphoreRejected()
	{
		return RollingCountSemaphoreRejected;
	}
	
	public long getRollingCountShortCircuited()
	{
		return RollingCountShortCircuited;
	}
	
	public long getRollingCountSuccess()
	{
		return RollingCountSuccess;
	}
	
	public long getRollingCountThreadPoolRejected()
	{
		return RollingCountThreadPoolRejected;
	}
	
	public long getRollingCountTimeout()
	{
		return RollingCountTimeout;
	}
	
	public long getCurrentConcurrentExecutionCount()
	{
		return CurrentConcurrentExecutionCount;
	}
	
	public long getLatencyExecute_mean()
	{
		return LatencyExecute_mean;
	}
	
	public long getLatencyTotal_mean()
	{
		return LatencyTotal_mean;
	}
	
	public long getPropertyValue_circuitBreakerRequestVolumeThreshold()
	{
		return PropertyValue_circuitBreakerRequestVolumeThreshold;
	}
	
	public long getPropertyValue_circuitBreakerSleepWindowInMilliseconds()
	{
		return PropertyValue_circuitBreakerSleepWindowInMilliseconds;
	}
	
	public long getPropertyValue_circuitBreakerErrorThresholdPercentage()
	{
		return PropertyValue_circuitBreakerErrorThresholdPercentage;
	}
	
	public int getPropertyValue_circuitBreakerForceOpen()
	{
		int PVForceOpenInt = (PropertyValue_circuitBreakerForceOpen) ? 1 : 0;
		return PVForceOpenInt;
	}	
	
	public int getPropertyValue_circuitBreakerForceClosed()
	{
		int PVForceCloseInt = (PropertyValue_circuitBreakerForceClosed) ? 1 : 0;
		return PVForceCloseInt;
	}
	
	public int getPropertyValue_circuitBreakerEnabled()
	{
		int PVCircuitEnabledInt = (PropertyValue_circuitBreakerEnabled) ? 1 : 0;
		return PVCircuitEnabledInt;
	}
	
	public long getPropertyValue_executionIsolationThreadTimeoutInMilliseconds()
	{
		return PropertyValue_executionIsolationThreadTimeoutInMilliseconds;
	}
	
	public int getPropertyValue_executionIsolationThreadInterruptOnTimeout()
	{
		int PVThreadInterruptInt = (PropertyValue_executionIsolationThreadInterruptOnTimeout) ? 1 : 0;
		return PVThreadInterruptInt;
	}
	
	public long getPropertyValue_executionIsolationThreadPoolKeyOverride()
	{
		return PropertyValue_executionIsolationThreadPoolKeyOverride;
	}
	
	public long getPropertyValue_executionIsolationSemaphoreMaxConcurrentRequests()
	{
		return PropertyValue_executionIsolationSemaphoreMaxConcurrentRequests;
	}
	
	public long getPropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests()
	{
		return PropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests;
	}
	
	public long getPropertyValue_metricsRollingStatisticalWindowInMilliseconds()
	{
		return PropertyValue_metricsRollingStatisticalWindowInMilliseconds;
	}
	
	public int getPropertyValue_requestCacheEnabled()
	{
		int PVCacheEnabledInt = (PropertyValue_requestCacheEnabled) ? 1 : 0;
		return PVCacheEnabledInt;
	}
	
	public int getPropertyValue_requestLogEnabled()
	{
		int PVRequestLogEnabledInt = (PropertyValue_requestLogEnabled) ? 1 : 0;
		return PVRequestLogEnabledInt;
	}
	
	public long getReportingHosts()
	{
		return ReportingHosts;
	}
	
	public long getCurrentActiveCount()
	{
		return CurrentActiveCount;
	}
	
	public long getCurrentCompletedTaskCount()
	{
		return CurrentCompletedTaskCount;
	}
	
	public long getCurrentCorePoolSize()
	{
		return CurrentCorePoolSize;
	}
	
	public long getCurrentLargestPoolSize()
	{
		return CurrentLargestPoolSize;
	}
	
	public long getCurrentMaximumPoolSize()
	{
		return CurrentMaximumPoolSize;
	}
	
	public long getCurrentPoolSize()
	{
		return CurrentPoolSize;
	}
	
	public long getCurrentQueueSize()
	{
		return CurrentQueueSize;
	}
	
	public long getCurrentTaskCount()
	{
		return CurrentTaskCount;
	}
	
	public long getRollingCountThreadsExecuted()
	{
		return RollingCountThreadsExecuted;
	}
	
	public long getRollingMaxActiveThreads()
	{
		return RollingMaxActiveThreads;
	}
	
	public long getPropertyValue_queueSizeRejectionThreshold()
	{
		return PropertyValue_queueSizeRejectionThreshold;
	}
	
	public long getThread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds()
	{
		return Thread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds;
	}
	
	public long getThread_ReportingHosts()
	{
		return Thread_ReportingHosts;
	}
	
	//Retrieve Hystrix Stream Success
	public boolean getHystrixStreamSuccess()
	{
		return HystrixStreamSuccess;
	}
	
}