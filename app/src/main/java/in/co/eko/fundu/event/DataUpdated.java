package in.co.eko.fundu.event;

/**
 * Created by zartha on 8/17/17.
 */

public class DataUpdated {
    public DataUpdatedType type;

    public enum DataUpdatedType{
      InvitationIncentive,
        UserContacts
    }
}
