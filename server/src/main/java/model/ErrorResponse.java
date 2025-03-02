package model;

public class ErrorResponse {
    private String error;
    private int statusCode;

    public ErrorResponse(String error, int statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
