package computer.heather.goagnostic.api.command;

public class CommandResult {
    
    private final String message;
    private final Boolean success;

    
    public CommandResult(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }


    public String getMessage() {
        return message;
    }


    public Boolean getSuccess() {
        return success;
    }

}
