package server;

import java.io.Serializable;

/**
 * Created by Mikaël on 2016-10-22.
 */

public class PollCounter implements Serializable {

    private static int counter;
    //Critical proportion must be between 0 and 100
    private static final int criticalProportion = 90;

    static {
        counter = 0;
    }

    private int numTexton, numReg;
    private int a, b, c, d;
    private PollController pollController;

    public PollCounter(int numTexton, PollController pollController) {
        System.out.println("Constructeur du pollCounter.");
        this.numTexton = numTexton;
        this.pollController = pollController;
        incrementCounter();
    }

    public static int getCounter() {
        return counter;
    }

    private static void incrementCounter() {
        counter++;
    }

    public static int getCriticalProportion() {
        return criticalProportion;
    }

    public int getNumTexton() {
        return numTexton;
    }

    private void reset(int numTexton) {
        this.numTexton = numTexton;
        a = 0;
        b = 0;
        c = 0;
        d = 0;

    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public ControlCode getMax(StringBuilder sb) {
        /*Cette méthode est adaptée de Jason (nom d'utilisateur). 2014. Réponse à « Find the Variable with biggest int
        value ». Stack Overflow. 10 juin. Consulté le 1 janvier 2017.
        <http://stackoverflow.com/questions/24132819/find-the-variable-with-biggest-int-value>*/
        ControlCode highestVote = ControlCode.A;
        if (b > a) {
            highestVote = ControlCode.B;
        }
        if (c > b) {
            highestVote = ControlCode.C;
        }
        if (d > c) {
            highestVote = ControlCode.D;
        }
        if (sb != null) {
            sb.append(getNumFromVote(highestVote));
        }
        return highestVote;
    }

    public ControlCode getMax() {
        return getMax(null);
    }

    public int getNumReg() {
        return numReg;
    }

    public ControlCode updateVotes(ControlCode cmd) {
        //If criticalProportion has been reached, return ControlCode A, B, C or D.
        ControlCode returnCc = ControlCode.UPDATE;
        if (!cmd.equals(ControlCode.BLANK))
            System.out.println("PollCounter received CC " + cmd.toString());
        switch (cmd) {
            case APLUS:
                a++;
                break;
            case AMINUS:
                a--;
                break;
            case BPLUS:
                b++;
                break;
            case BMINUS:
                b--;
                break;
            case CPLUS:
                c++;
                break;
            case CMINUS:
                c--;
                break;
            case DPLUS:
                d++;
                break;
            case DMINUS:
                d--;
                break;
            case REG:
                numReg++;
                break;
            case UNREG:
                numReg--;
                break;
            case RESET:
                reset(pollController.getController().getCurTexton().getNumTexton());
            default:
                //If ControlCode does not pertain to vote counting
                return cmd;
        }

        //if criticalProportion has been reached
        if (numReg * criticalProportion / 100 < a) {
            return ControlCode.A;
        }
        if (numReg * criticalProportion / 100 < b) {
            return ControlCode.B;
        }
        if (numReg * criticalProportion / 100 < c) {
            return ControlCode.C;
        }
        if (numReg * criticalProportion / 100 < d) {
            return ControlCode.D;
        }

        //if votes have been counted, but criticalProportion hasn't been reached
        return returnCc;
    }

    private int getNumFromVote(ControlCode vote) {
        switch (vote) {
            case A:
                return getA();
            case B:
                return getB();
            case C:
                return getC();
            case D:
                return getD();
            default:
                return 0;
        }
    }
}
