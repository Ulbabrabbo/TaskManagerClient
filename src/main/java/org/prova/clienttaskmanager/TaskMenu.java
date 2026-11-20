package org.prova.clienttaskmanager;

import org.prova.clienttaskmanager.data.Status;
import org.prova.clienttaskmanager.data.Task;
import org.prova.clienttaskmanager.data.TaskReceived;
import org.prova.clienttaskmanager.exception.ApiException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;

@Component
public class TaskMenu implements CommandLineRunner {

    private final TaskService taskService;
    private static final Scanner input = new Scanner(System.in);

    public TaskMenu(TaskService service) {
        this.taskService = service;
    }

    @Override
    public void run(String... args){

        boolean running = true;

        while(running){
            System.out.println("Selezionare la funzione da eseguire:\n" +
                    "1: Creare un nuovo task\n" +
                    "2: Visualizzare tutti i task\n" +
                    "3: Cercare un task tramite ID\n" +
                    "4: Modificare un task esistente\n" +
                    "5: Eliminare un task\n" +
                    "6: Filtrare i task in base allo stato\n" +
                    "7: Uscire\n" );

            try {
                String menuSelection = input.nextLine();

                switch (menuSelection) {
                    case "1" -> createTask();
                    case "2" -> showAllTask();
                    case "3" -> searchTaskById();
                    case "4" -> modifyTask();
                    case "5" -> deleteTask();
                    case "6" -> filterTask();
                    case "7" -> {
                        running = false;
                        System.out.println("Esecuzione terminata");
                    }

                    default -> System.out.println("Scelta non valida\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Inserimento non valido.\n");
                // consumo l'input per non farlo andare avanti all'infinito
                input.nextLine();
            }
        }
    }


    private void createTask() {
        System.out.print("Inserire titolo: ");
        String title = input.nextLine();

        System.out.print("Inserire descrizione: ");
        String description = input.nextLine();

        Status status;
        System.out.print("Inserire stato progetto tra: \n" +
                "1): TODO\n" +
                "2): IN_PROGRESS\n" +
                "3): DONE\n");
        String statusSelection = input.nextLine();
        switch (statusSelection) {
            case "1":
                status = Status.TODO;
                break;
            case "2":
                status = Status.IN_PROGRESS;
                break;
            case "3":
                status = Status.DONE;
                break;
            default:
                System.out.println("Scelta non valida! Impostato su TODO di default.\n");
                status = Status.TODO;
                break;
        }

        try {
            TaskReceived<Task> taskReceived = taskService.createNewTask(title, description, status);
            Task taskCreated = taskReceived.getData();
            System.out.println("\nHttp Status: " + taskReceived.getStatusCode());
            System.out.println("Creato nuovo task: ");
            System.out.println("id: " + taskCreated.getId() + "\n" +
                    "titolo: " + taskCreated.getTitle() + "\n" +
                    "descrizione: " + taskCreated.getDescription() + "\n" +
                    "status: " + taskCreated.getStatus() + "\n");
        } catch (ApiException e) {
            ProblemDetail problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore: " + problem.getDetail());
        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }


    }

    private void showAllTask() {

        try {
            TaskReceived<List<Task>> taskListReceived = taskService.showTaskList();
            List<Task> taskList = taskListReceived.getData();
            if (taskList.isEmpty()) {
                System.out.println("Non ci sono task nella lista.\n");
                return;
            }
            System.out.println("\nHttp Status: " + taskListReceived.getStatusCode());
            System.out.println("Lista di tutti i task: ");
            taskList.stream()
                    .forEach(task -> {
                        System.out.println("id: " + task.getId() + "\n" +
                                "titolo: " + task.getTitle() + "\n" +
                                "descrizione: " + task.getDescription() + "\n" +
                                "status: " + task.getStatus() + "\n");
                    });

        } catch (ApiException e) {
            ProblemDetail problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore: " + problem.getDetail());
        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }


    }

    private void searchTaskById() {
        System.out.print("Inserire Id del task da ricercare: ");
        int idToSearch = input.nextInt();
        input.nextLine();

        try {
            TaskReceived<Task> taskReceived = taskService.searchTask(idToSearch);
            Task taskFound = taskReceived.getData();
            System.out.println("\nHttp Status: " + taskReceived.getStatusCode());
            System.out.println("Trovato task:");
            System.out.println("id: " + taskFound.getId() + "\n" +
                    "titolo: " + taskFound.getTitle() + "\n" +
                    "descrizione: " + taskFound.getDescription() + "\n" +
                    "status: " + taskFound.getStatus() + "\n");

        } catch (ApiException e) {
            ProblemDetail problem;
            problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore :" + problem.getDetail());

        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }
    }


    private void modifyTask() {
        System.out.print("Inserire Id del task da modificare: ");
        int idToSearch = input.nextInt();
        input.nextLine();

        try{
            TaskReceived<Task> taskReceived = taskService.searchTask(idToSearch);
            Task foundTask = taskReceived.getData();

            System.out.println("Selezionare il campo da modificare tra: \n" +
                    "1: Titolo \n" +
                    "2: Descrizione \n" +
                    "3: Status");
            String choice = input.nextLine();

            switch (choice) {
                    case "1":
                        System.out.print("Inserire nuovo titolo: ");
                        String newTitle = input.nextLine();
                        taskService.modifyTask(idToSearch, new Task(
                                newTitle,
                                foundTask.getDescription(),
                                foundTask.getStatus()
                        ));
                        System.out.println("\nHttp Status: " + taskReceived.getStatusCode());
                        System.out.println("Task con id: " + foundTask.getId() + " modificato!");
                        break;

                    case "2":
                        System.out.print("Inserire nuova descrizione: ");
                        String newDescription = input.nextLine();
                        taskService.modifyTask(idToSearch, new Task(
                                foundTask.getTitle(),
                                newDescription,
                                foundTask.getStatus()
                        ));
                        System.out.println("\nHttp Status: " + taskReceived.getStatusCode());
                        System.out.println("Task con id: " + foundTask.getId() + " modificato!");
                        break;

                    case "3":
                        System.out.print("Selezionare il nuova stato tra: \n" +
                                "1): TODO\n" +
                                "2): IN_PROGRESS\n" +
                                "3): DONE\n");

                        Status status;
                        String statusSelection = input.nextLine();
                        switch (statusSelection) {
                            case "1":
                                status = Status.TODO;
                                break;
                            case "2":
                                status = Status.IN_PROGRESS;
                                break;
                            case "3":
                                status = Status.DONE;
                                break;
                            default:
                                System.out.println("Scelta non valida!\n");
                                return;
                        }

                        taskService.modifyTask(idToSearch, new Task(
                                foundTask.getTitle(),
                                foundTask.getDescription(),
                                status
                        ));
                        System.out.println("\nHttp Status: " + taskReceived.getStatusCode());
                        System.out.println("Task con id: " + foundTask.getId() + " modificato!");
                        break;

                    default:
                        System.out.println("Scelta non valida\n");

            }

        } catch (ApiException e) {
            ProblemDetail problem;
            problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore :" + problem.getDetail());

        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }

    }


    private void deleteTask() {
        System.out.print("Inserire Id del task da eliminare: ");
        int idToDelete = input.nextInt();
        input.nextLine();

        try {
            TaskReceived <Void> taskReceived = taskService.deleteTask(idToDelete);
            System.out.println("\nHttp status: " + taskReceived.getStatusCode());
            System.out.println("Task con id: " + idToDelete + " eliminato!");

        } catch (ApiException e) {
            ProblemDetail problem;
            problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore :" + problem.getDetail());

        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }
    }


    private void filterTask() {
        System.out.println("Selezionare lo status secondo cui filtrare tra: \n" +
                "1): TODO\n" +
                "2): IN_PROGRESS\n" +
                "3): DONE\n");

        Status status;
        String statusSelection = input.nextLine();
        TaskReceived<List<Task>> taskListReceived;
        try {
            switch (statusSelection) {
                case "1":
                    status = Status.TODO;
                    taskListReceived = taskService.filterTask(status);
                    break;
                case "2":
                    status = Status.IN_PROGRESS;
                    taskListReceived = taskService.filterTask(status);
                    break;
                case "3":
                    status = Status.DONE;
                    taskListReceived = taskService.filterTask(status);
                    break;
                default:
                    System.out.println("Scelta non valida!\n");
                    return;
            }

            if(taskListReceived.getData().isEmpty())  {
                System.out.println("\nHttp Status: " + taskListReceived.getStatusCode());
                System.out.println("La lista è vuota\n");
            }
            else {
                System.out.println("\nHttp Status: " + taskListReceived.getStatusCode());
                System.out.println("Lista filtrata dei task: ");
                taskListReceived.getData().stream()
                        .forEach(task -> {
                            System.out.println("id: " + task.getId() + "\n" +
                                    "titolo: " + task.getTitle() + "\n" +
                                    "descrizione: " + task.getDescription() + "\n" +
                                    "status: " + task.getStatus() + "\n");
                        });
            }

        } catch (ApiException e) {
            ProblemDetail problem;
            problem = e.getProblemDetail();
            System.out.println("Http Status: " + problem.getStatus());
            System.out.println("errore :" + problem.getDetail());

        } catch (ResourceAccessException e) {
            System.out.println("Errore: impossibile raggiungere il server.");
        }


    }
}
