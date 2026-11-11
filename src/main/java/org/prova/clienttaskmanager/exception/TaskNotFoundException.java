package org.prova.clienttaskmanager.exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(int id){
        super("Task con id: " + id + " non trovato.");
    }
}
