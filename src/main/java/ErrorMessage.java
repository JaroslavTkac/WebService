import lombok.Data;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

@Data
public class ErrorMessage {
    private String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, String... args) {
        this.message = String.format(message, args);
    }

    ErrorMessage(Exception e) {
        this.message = e.getMessage();
    }
}

