package org.prova.clienttaskmanager.client;


import org.prova.clienttaskmanager.data.Status;
import org.prova.clienttaskmanager.data.Task;
import org.prova.clienttaskmanager.exception.TaskNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TaskApiClient {

    private final RestClient restClient;

    public TaskApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // POST /api/tasks
    public Task createTask(Task task) {

        return restClient.post()
                .uri("/api/tasks")
                .body(task)
                .retrieve()
                .body(Task.class);
    }

    // GET /api/tasks
    public List<Task> getAllTasks() {

        return restClient.get()
                .uri("/api/tasks")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});  // deserializza in classe generica
    }

    // GET /api/tasks/{id}
    public Task getTaskById(int id) {

        return restClient.get()
                .uri("/api/tasks/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) ->{
                    throw new TaskNotFoundException(id);
                })
                .body(Task.class);   // deserializza Json in Task
    }

    // PUT /api/tasks/{id}
    public Task updateTask(int id, Task task) {

        return restClient.put()
                .uri("/api/tasks/{id}", id)
                .body(task)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) ->{
                    throw new TaskNotFoundException(id);
                })
                .body(Task.class);
    }

    // DELETE /api/tasks/{id}
    public void deleteTask(int id) {

        restClient.delete()
                .uri("/api/tasks/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) ->{
                    throw new TaskNotFoundException(id);
                })
                .toBodilessEntity();
    }


    // GET /api/tasks?status={status}
    public List<Task> getTasksByStatus(Status status) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/tasks")
                        .queryParam("status", status)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}