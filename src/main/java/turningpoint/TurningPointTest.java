package turningpoint;

import com.turningtech.responsecardsdk.Device;
import com.turningtech.responsecardsdk.Participant;
import com.turningtech.responsecardsdk.PresenterCardCommand;
import com.turningtech.responsecardsdk.Response;
import com.turningtech.responsecardsdk.presentation.PresentationListener;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class TurningPointTest implements PresentationListener {
    @Override
    public void responseReceivedEvent(Response response) {

    }

    @Override
    public void participantMessageReceivedEvent(Device device, String s) {

    }

    @Override
    public void presenterCardCommandReceivedEvent(Device device, PresenterCardCommand presenterCardCommand) {

    }

    @Override
    public boolean isValidParticipantLogin(Device device, Participant participant) {
        return false;
    }
}
