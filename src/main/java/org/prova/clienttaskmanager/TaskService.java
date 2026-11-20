package org.prova.clienttaskmanager;


import org.prova.clienttaskmanager.client.TaskApiClient;
import org.prova.clienttaskmanager.data.Status;
import org.prova.clienttaskmanager.data.Task;
import org.prova.clienttaskmanager.data.TaskReceived;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskApiClient client;

    public TaskService(TaskApiClient taskClient){
        this.client = taskClient;
    }

    public TaskReceived<Task> createNewTask(String title, String description, Status status) {
        return client.createTask(new Task(title, description, status));
    }

    public TaskReceived<List<Task>> showTaskList() {
        return client.getAllTasks();
    }

    public TaskReceived<Task> searchTask(int idToSearch) {
        return client.getTaskById(idToSearch);
    }

    public void modifyTask(int id, Task taskModified) {
        client.updateTask(id, taskModified);
    }

    public TaskReceived<Void> deleteTask(int idToDelete) {
        return client.deleteTask(idToDelete);
    }

    public TaskReceived<List<Task>> filterTask(Status status) {
        return client.getTasksByStatus(status);
    }
}
