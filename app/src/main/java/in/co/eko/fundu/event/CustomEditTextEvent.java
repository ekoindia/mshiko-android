package in.co.eko.fundu.event;

/**
 * Created by pallavi on 18/9/17.
 */

public class CustomEditTextEvent {

    private String  code;

    public CustomEditTextEvent(String message) {
        this.code = message;
    }

    public String getCode() {
        return code;
    }
}
