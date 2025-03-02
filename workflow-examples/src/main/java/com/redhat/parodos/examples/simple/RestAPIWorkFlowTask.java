/*
 * Copyright (c) 2022 Red Hat Developer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.parodos.examples.simple;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.redhat.parodos.workflow.task.WorkFlowTaskOutput;
import com.redhat.parodos.workflow.task.infrastructure.BaseInfrastructureWorkFlowTask;
import com.redhat.parodos.workflow.task.parameter.WorkFlowTaskParameter;
import com.redhat.parodos.workflow.task.parameter.WorkFlowTaskParameterType;
import com.redhat.parodos.workflows.work.DefaultWorkReport;
import com.redhat.parodos.workflows.work.WorkContext;
import com.redhat.parodos.workflows.work.WorkReport;
import com.redhat.parodos.workflows.work.WorkStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * rest api task execution
 *
 * @author Luke Shannon (Github: lshannon)
 * @author Richard Wang (Github: richardw98)
 * @author Annel Ketcha (Github: anludke)
 */

@Slf4j
public class RestAPIWorkFlowTask extends BaseInfrastructureWorkFlowTask {

	private static final String PAYLOAD_PASSED_IN_FROM_SERVICE = "PAYLOAD_PASSED_IN_FROM_SERVICE";

	private String urlDefinedAtTaskCreation;

	public RestAPIWorkFlowTask(String urlDefinedAtTaskCreation) {
		super();
		this.urlDefinedAtTaskCreation = urlDefinedAtTaskCreation;
	}

	/**
	 * Executed by the InfrastructureTask engine as part of the Workflow
	 */
	public WorkReport execute(WorkContext workContext) {
		try {
			String urlString = getParameterValue(workContext, urlDefinedAtTaskCreation);
			String payload = getParameterValue(workContext, PAYLOAD_PASSED_IN_FROM_SERVICE);
			log.info("Running Task REST API Call: urlString: {} payload: {} ", urlString, payload);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> result = restTemplate.postForEntity(urlString, payload, String.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				log.info("Rest call completed: {}", result.getBody());
				return new DefaultWorkReport(WorkStatus.COMPLETED, workContext);
			}
			log.error("Call to the API was not successful. Response: {}", result.getStatusCode());
		}
		catch (Exception e) {
			log.error("There was an issue with the REST call: {}", e.getMessage());

		}
		return new DefaultWorkReport(WorkStatus.FAILED, workContext);
	}

	@Override
	public List<WorkFlowTaskParameter> getWorkFlowTaskParameters() {
		return List.of(
				WorkFlowTaskParameter.builder().key(urlDefinedAtTaskCreation)
						.description("The Url of the service (ie: https://httpbin.org/post").optional(false)
						.type(WorkFlowTaskParameterType.URL).build(),
				WorkFlowTaskParameter.builder().key(PAYLOAD_PASSED_IN_FROM_SERVICE)
						.description("Json of what to provide for data. (ie: 'Hello!')").optional(false)
						.type(WorkFlowTaskParameterType.PASSWORD).build());
	}

	public List<WorkFlowTaskOutput> getWorkFlowTaskOutputs() {
		return List.of(WorkFlowTaskOutput.HTTP2XX, WorkFlowTaskOutput.OTHER);
	}

}
