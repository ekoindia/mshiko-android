package in.co.eko.fundu.event;

/**
 * Created by pallavi on 6/9/17.
 */

public class OtpReceiverEvent {

    private String otp;

    public OtpReceiverEvent(String message) {
        this.otp = message;
    }

    public String getOtp() {
        return otp;
    }
}


