package server;

import javafx.concurrent.Task;
import presentation.ContrPres;

import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Mikaël on 2016-10-28.
 */
public class PollController {
    private static PollCounter pollCounter;
    private String formattedDate;
    private boolean dateSet = false;
    final BlockingQueue<ControlCode> queue;
    private final Task pollContTask;
    private final Calendar date;
    private ContrPres controller;

    public PollController(int numTexton, ContrPres controller) {
        this(numTexton);
        this.controller = controller;
    }

    private PollController(int numTexton) {
        queue = new ArrayBlockingQueue<>(500);
        pollCounter = new PollCounter(numTexton, this);

        //Set date on first construction.
        date = Calendar.getInstance();
        if (!dateSet) {
            formattedDate = date.get(Calendar.YEAR) + "/"
                    + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH);
            dateSet = true;
        }

        //pollContThread
        pollContTask = new Task() {
            @Override
            protected Object call() throws Exception {
                System.out.println("startQueueService thread is running.");
                //Premier message à l'initialisation. Pour tests.
                updateMessage(votesToString(pollCounter.getA(), pollCounter.getB(), pollCounter.getC(), pollCounter.getD(), pollCounter.getNumReg()));
                while (!Thread.interrupted()) {
                    try {
                        ControlCode cc = pollCounter.updateVotes(queue.take());
                        //If cc ends up here, it means that the corresponding link has been activated by critical proportion.
                        switch (cc) {
                            case BLANK:
                                break;
                            case A:
                                updateMessage("!A");
                                break;
                            case B:
                                updateMessage("!B");
                                break;
                            case C:
                                updateMessage("!C");
                                break;
                            case D:
                                updateMessage("!D");
                                break;
                            default:
                                System.out.println("Erreur: le ControlCode " + cc.toString()
                                        + " s'est retrouvé dans la Queue.");
                        }
                        //On every cc excent Blank, update message.
                        if (!cc.equals(ControlCode.BLANK)) {
                            System.out.println("Message updated.");
                            updateMessage(votesToString(pollCounter.getA(), pollCounter.getB(), pollCounter.getC(), pollCounter.getD(), pollCounter.getNumReg()));
                        }
                    } catch (InterruptedException e) {
                        System.out.println("dealWithQueue() method of PollController was interrupted.");
                        return null;
                    }
                }
                return null;
            }
        };
        Thread pollContThread = new Thread(pollContTask);
        pollContThread.setDaemon(true);
        pollContThread.start();
    }

    public static int[] StringToVotes(String str) {
        String[] result = str.split(",");
        int[] resInt = new int[result.length];
        for (int i = 0; i < result.length; i++) {
            resInt[i] = Integer.parseInt(result[i]);
        }
        return resInt;
    }

    private static String votesToString(int A, int B, int C, int D, int numEnr) {
        return A + "," + B + "," + C + "," + D + "," + numEnr;
    }

    public PollCounter getPollCounter() {
        return pollCounter;
    }

    public void changePollCounter() {
        archivePollCounter();
        //After archiving, initialize the poll counter.
        System.out.println("Resetting poll counter...");
        try {
            queue.put(ControlCode.RESET);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void archivePollCounter() {
    }

    public Task getPollContTask() {
        return pollContTask;
    }

    public BlockingQueue<ControlCode> getQueue() {
        return queue;
    }

    public ContrPres getController() {
        return controller;
    }
}
