package com.dynatrace.HystrixMonitor;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Monitor;
import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.MonitorMeasure;
import com.dynatrace.diagnostics.pdk.Status;

public class HystrixMonitor implements Monitor {

	private static final String METRIC_GROUP_COMMAND = "Hystrix Command";
	private static final String METRIC_GROUP_THREAD = "Hystrix Thread Pool";
	private static final String MSR_CMD_CIRCUITBREAKEROPEN = "isCircuitBreakerOpen";
	private static final String MSR_CMD_ERRORPERC = "errorPercentage";
	private static final String MSR_CMD_ERRORCNT = "errorCount";
	private static final String MSR_CMD_REQUESTCNT = "requestCount";
	private static final String MSR_CMD_ROLLCNTCOLLAPSEDREQUESTS = "rollingCountCollapsedRequests";
	private static final String MSR_CMD_ROLLCNTEXCEPTTHROWN = "rollingCountExceptionsThrown";
	private static final String MSR_CMD_ROLLCNTFAILURE = "rollingCountFailure";
	private static final String MSR_CMD_ROLLCNTFALLBACKFAILURE = "rollingCountFallbackFailure";
	private static final String MSR_CMD_ROLLCNTFALLBACKREJECT = "rollingCountFallbackRejection";
	private static final String MSR_CMD_ROLLCNTFALLBACKSUCCESS = "rollingCountFallbackSuccess";
	private static final String MSR_CMD_ROLLCNTRESPFROMCACHE = "rollingCountResponsesFromCache";
	private static final String MSR_CMD_ROLLCNTSEMAPHOREREJECT = "rollingCountSemaphoreRejected";
	private static final String MSR_CMD_ROLLCNTSHORTCIRCUIT = "rollingCountShortCircuited";
	private static final String MSR_CMD_ROLLCNTSUCCESS = "rollingCountSuccess";
	private static final String MSR_CMD_ROLLCNTTHREADPOOLREJECT = "rollingCountThreadPoolRejected";
	private static final String MSR_CMD_ROLLCNTTIMEOUT = "rollingCountTimeout";
	private static final String MSR_CMD_CRNTCONCURRENTEXECCNT = "currentConcurrentExecutionCount";
	private static final String MSR_CMD_LATENCYEXECMEAN = "latencyExecute_mean";
	private static final String MSR_CMD_LATENCYTOTALMEAN = "latencyTotal_mean";
	private static final String MSR_CMD_PVCIRCUITBREAKERREQUESTVOLUMETHRESH = "propertyValue_circuitBreakerRequestVolumeThreshold";
	private static final String MSR_CMD_PVCIRCUITBREAKERSLEEPWINDOW = "propertyValue_circuitBreakerSleepWindowInMilliseconds";
	private static final String MSR_CMD_PVCIRCUITBREAKERERRORTHRESH = "propertyValue_circuitBreakerErrorThresholdPercentage";
	private static final String MSR_CMD_PVCIRCUITBREAKERFORCEOPEN = "propertyValue_circuitBreakerForceOpen";
	private static final String MSR_CMD_PVCIRCUITBREAKERFORCECLOSE = "propertyValue_circuitBreakerForceClosed";
	private static final String MSR_CMD_PVCIRCUITBREAKERENABLED = "propertyValue_circuitBreakerEnabled";
	private static final String MSR_CMD_PVEXECISOLATIONTHREADTIMEOUT = "propertyValue_executionIsolationThreadTimeoutInMilliseconds";
	private static final String MSR_CMD_PVEXECISOLATIONTHREADINTERRUPT = "propertyValue_executionIsolationThreadInterruptOnTimeout";
	private static final String MSR_CMD_PVEXECISOLATIONTHREADPOOLOVERRIDE = "propertyValue_executionIsolationThreadPoolKeyOverride";
	private static final String MSR_CMD_PVEXECISOLATIONSEMAPHOREMAXREQ = "propertyValue_executionIsolationSemaphoreMaxConcurrentRequests";
	private static final String MSR_CMD_PVFALLBACKISOLATIONSEMAPHOREMAXREQ = "propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests";
	private static final String MSR_CMD_PVROLLINGSTATWINDOW = "propertyValue_metricsRollingStatisticalWindowInMilliseconds";
	private static final String MSR_CMD_PVREQCACHEENABLED = "propertyValue_requestCacheEnabled";
	private static final String MSR_CMD_PVREQLOGENABLED  = "propertyValue_requestLogEnabled";
	private static final String MSR_CMD_REPORTINGHOSTS = "reportingHosts";
	private static final String MSR_THREAD_CURRENTACTIVECNT = "currentActiveCount";
	private static final String MSR_THREAD_CURRENTCOMPLETETASKCNT = "currentCompletedTaskCount";
	private static final String MSR_THREAD_CURRENTCOREPOOLSIZE = "currentCorePoolSize";
	private static final String MSR_THREAD_CURRENTLARGESTPOOLSIZE = "currentLargestPoolSize";
	private static final String MSR_THREAD_CURRENTMAXPOOLSIZE = "currentMaximumPoolSize";
	private static final String MSR_THREAD_CURRENTPOOLSIZE = "currentPoolSize";
	private static final String MSR_THREAD_CURRENTQUEUESIZE = "currentQueueSize";
	private static final String MSR_THREAD_CURRENTTASKCNT = "currentTaskCount";
	private static final String MSR_THREAD_ROLLCNTTHREADSEXEC = "rollingCountThreadsExecuted";
	private static final String MSR_THREAD_ROLLMAXACTIVETHREADS = "rollingMaxActiveThreads";
	private static final String MSR_THREAD_PVQUEUESIZEREJECTTHRESH = "propertyValue_queueSizeRejectionThreshold";
	private static final String MSR_THREAD_PVROLLSTATWINDOW = "propertyValue_metricsRollingStatisticalWindowInMilliseconds";
	private static final String MSR_THREAD_REPORTINGHOSTS = "reportingHosts";
	
	
	


	private static final Logger log = Logger.getLogger(HystrixMonitor.class
			.getName());

	@Override
	public Status setup(MonitorEnvironment env) throws Exception {
		// TODO
		return new Status(Status.StatusCode.Success);
	}

	@Override
	public Status execute(MonitorEnvironment env) throws Exception {

		try {

			log.info("Starting execute...");

			Boolean hystrixStreamSuccess = true;
			String Server = env.getHost().getAddress();

			log.info("Server : " + Server);

			//String URL = env.getConfigString("URL");

			//log.info("URL : " + Server);

			String Port = env.getConfigString("Port");
			
			log.info("Port : " + Port);

			String User = env.getConfigString("Username");

			log.info("User : " + User);

			String Pass = env.getConfigPassword("Password");

			Overview temp = new Overview(Server, Port, User, Pass);
			hystrixStreamSuccess = temp.getHystrixStreamSuccess();
			if (hystrixStreamSuccess){
				//Nodes temp2 = new Nodes(Server, Port, User, Pass);
				Collection<MonitorMeasure> measures;
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_CIRCUITBREAKEROPEN)) != null) {
					//log.info("Entered retrieval of CircuitBreakerOpen");
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCircuitBreakerOpen());
					}
				}
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ERRORPERC)) != null) {
					//log.info("Entered retrieval of ErrorPercentage");
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getErrorPercentage());
					}
				}
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ERRORCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getErrorCount());
					}
				}
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_REQUESTCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRequestCount());
					}
				}
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTCOLLAPSEDREQUESTS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountCollapsedRequests());
					}
				}
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTEXCEPTTHROWN)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountExceptionsThrown());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTFAILURE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountFailure());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTFALLBACKFAILURE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountFallbackFailure());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTFALLBACKREJECT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountFallbackRejection());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTFALLBACKSUCCESS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountFallbackSuccess());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTRESPFROMCACHE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountResponsesFromCache());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTSEMAPHOREREJECT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountSemaphoreRejected());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTSHORTCIRCUIT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountShortCircuited());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTSUCCESS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountSuccess());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTTHREADPOOLREJECT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountThreadPoolRejected());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_ROLLCNTTIMEOUT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountTimeout());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_CRNTCONCURRENTEXECCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentConcurrentExecutionCount());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_LATENCYEXECMEAN)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getLatencyExecute_mean());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_LATENCYTOTALMEAN)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getLatencyTotal_mean());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERREQUESTVOLUMETHRESH)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerRequestVolumeThreshold());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERSLEEPWINDOW)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerSleepWindowInMilliseconds());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERERRORTHRESH)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerErrorThresholdPercentage());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERFORCEOPEN)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerForceOpen());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERFORCECLOSE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerForceClosed());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVCIRCUITBREAKERENABLED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_circuitBreakerEnabled());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVEXECISOLATIONTHREADTIMEOUT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_executionIsolationThreadTimeoutInMilliseconds());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVEXECISOLATIONTHREADINTERRUPT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_executionIsolationThreadInterruptOnTimeout());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVEXECISOLATIONTHREADPOOLOVERRIDE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_executionIsolationThreadPoolKeyOverride());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVEXECISOLATIONSEMAPHOREMAXREQ)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_executionIsolationSemaphoreMaxConcurrentRequests());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVFALLBACKISOLATIONSEMAPHOREMAXREQ)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVROLLINGSTATWINDOW)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_metricsRollingStatisticalWindowInMilliseconds());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVREQCACHEENABLED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_requestCacheEnabled());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_PVREQLOGENABLED)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_requestLogEnabled());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_COMMAND,
						MSR_CMD_REPORTINGHOSTS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getReportingHosts());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTACTIVECNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentActiveCount());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTCOMPLETETASKCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentCompletedTaskCount());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTCOREPOOLSIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentCorePoolSize());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTLARGESTPOOLSIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentLargestPoolSize());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTMAXPOOLSIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentMaximumPoolSize());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTPOOLSIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentPoolSize());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTQUEUESIZE)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentQueueSize());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_CURRENTTASKCNT)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getCurrentTaskCount());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_ROLLCNTTHREADSEXEC)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingCountThreadsExecuted());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_ROLLMAXACTIVETHREADS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getRollingMaxActiveThreads());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_PVQUEUESIZEREJECTTHRESH)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getPropertyValue_queueSizeRejectionThreshold());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_PVROLLSTATWINDOW)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getThread_PropertyValue_metricsRollingStatisticalWindowInMilliseconds());
					}
				}
				
				if ((measures = env.getMonitorMeasures(METRIC_GROUP_THREAD,
						MSR_THREAD_REPORTINGHOSTS)) != null) {
					for (MonitorMeasure measure : measures) {
						measure.setValue(temp.getThread_ReportingHosts());
					}
				}
			return new Status(Status.StatusCode.Success);
			}
			else {
				Status error = new Status(Status.StatusCode.ErrorInfrastructure);
	    		error.setMessage("The URL did not return the expected Hystrix Stream.  Please double check that the URL is available and that the Monitor configuration is correct.");
	    		return error;
			}
		} catch (Exception exp) {
			log.log(Level.SEVERE, exp.getMessage(), exp);
			throw exp;
		}
	}

	@Override
	public void teardown(MonitorEnvironment env) throws Exception {
		// TODO
	}
}
