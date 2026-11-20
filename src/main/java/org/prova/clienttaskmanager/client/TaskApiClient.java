package org.prova.clienttaskmanager.client;


import org.prova.clienttaskmanager.data.Status;
import org.prova.clienttaskmanager.data.Task;
import org.prova.clienttaskmanager.data.TaskReceived;
import org.prova.clienttaskmanager.exception.ApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
public class TaskApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TaskApiClient(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    private void handleApiError(
            org.springframework.http.HttpRequest request,
            ClientHttpResponse response) throws IOException {

        byte[] body = response.getBody().readAllBytes();
        ProblemDetail problemDetail;

        try {
            if (body.length > 0) {
                problemDetail = objectMapper.readValue(
                        body,
                        ProblemDetail.class
                );
            } else {
                problemDetail = ProblemDetail.forStatusAndDetail(
                        response.getStatusCode(),
                        "The server returned an error without a response body."
                );
            }
        } catch (Exception e) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                    response.getStatusCode(),
                    "The server returned an invalid ProblemDetail response."
            );
        }

        // Use the actual HTTP status if the body doesn't contain it.
        if (problemDetail.getStatus() == 0) {
            problemDetail.setStatus(response.getStatusCode().value());
        }

        throw new ApiException(problemDetail);
    }


    // POST /api/tasks
    public TaskReceived<Task> createTask(Task task) {

        ResponseEntity<Task> response = restClient.post()
                .uri("/api/tasks")
                .body(task)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .toEntity(Task.class);

        return new TaskReceived<>(
                response.getBody(),
                response.getStatusCode().value(),
                response.getHeaders()
        );
    }

    // GET /api/tasks
    public TaskReceived<List<Task>> getAllTasks() {

        ResponseEntity<List<Task>> response = restClient.get()
                .uri("/api/tasks")
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .toEntity(new ParameterizedTypeReference<>() {});  // deserializza in classe generica

        return new TaskReceived<>(
                response.getBody(),
                response.getStatusCode().value(),
                response.getHeaders()
        );
    }

    // GET /api/tasks/{id}
    public TaskReceived<Task> getTaskById(int id) {

        ResponseEntity<Task> response = restClient.get()
                .uri("/api/tasks/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .toEntity(Task.class);

        return new TaskReceived<>(
                response.getBody(),
                response.getStatusCode().value(),
                response.getHeaders()
        );
    }

    // PUT /api/tasks/{id}
    public Task updateTask(int id, Task task) {

        return restClient.put()
                .uri("/api/tasks/{id}", id)
                .body(task)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .body(Task.class);
    }

    // DELETE /api/tasks/{id}
    public TaskReceived<Void> deleteTask(int id) {

        ResponseEntity<Void> response = restClient.delete()
                .uri("/api/tasks/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .toBodilessEntity();

        return new TaskReceived<>(
                response.getBody(),
                response.getStatusCode().value(),
                response.getHeaders()
        );
    }


    // GET /api/tasks?status={status}
    public TaskReceived<List<Task>> getTasksByStatus(Status myStatus) {

        ResponseEntity<List<Task>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/tasks")
                        .queryParam("status", myStatus)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleApiError)
                .toEntity(new ParameterizedTypeReference<>() {});

        return new TaskReceived<>(
                response.getBody(),
                response.getStatusCode().value(),
                response.getHeaders()
        );
    }
}