package org.prova.clienttaskmanager.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReceived<T> {

    private T data;
    private int statusCode;
    private HttpHeaders headers;

}
