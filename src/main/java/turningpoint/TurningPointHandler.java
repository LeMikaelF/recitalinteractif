package turningpoint;

import com.turningtech.responsecardsdk.*;
import com.turningtech.responsecardsdk.presentation.PresentationListener;
import com.turningtech.responsecardsdk.receiver.Receiver;
import com.turningtech.responsecardsdk.receiver.ReceiverManager;
import server.Vote;
import server.VoteCollector;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mikaël on 2017-10-03.
 */
public class TurningPointHandler implements VoteCollector, PresentationListener {

    InputStream resourceAsStream = getClass().getResourceAsStream("/license/license.properties");
    Properties licenseProperties = new Properties();
    License license;
    ReceiverManager receiverManager;
    List<Receiver> receivers;
    Map<Device, Vote> map = new HashMap<>();

    public TurningPointHandler() throws IOException {
        licenseProperties.load(resourceAsStream);
        License license =
                new License(licenseProperties.getProperty("salt"),
                        licenseProperties.getProperty("license"), licenseProperties.getProperty("checksum"));
        receiverManager = ReceiverManager.getInstance(license);
        receivers = receiverManager.getAllReceivers();
    }

    @Override
    public void setNumTextonCourant(int numTextonCourant) {

    }

    @Override
    public void startBroadcasting() {

    }

    @Override
    public void resetVotes() {

    }

    @Override
    public void responseReceivedEvent(Response response) {
        Vote vote = 
        map.put(response.getDevice(), response.getResponse());
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
