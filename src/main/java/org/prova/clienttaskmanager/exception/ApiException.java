package org.prova.clienttaskmanager.exception;

import org.springframework.http.ProblemDetail;

public class ApiException extends RuntimeException {

    private final ProblemDetail problemDetail;

    public ApiException(ProblemDetail problemDetail) {
        super(problemDetail.getDetail());
        this.problemDetail = problemDetail;
    }

    public ProblemDetail getProblemDetail() {
        return problemDetail;
    }

}
