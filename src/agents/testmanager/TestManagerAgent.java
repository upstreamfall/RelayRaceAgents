package agents.testmanager;

import agents.utils.ExtendedAgent;
import agents.utils.TestParams;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

/**
 * Created by pawel.bielicki on 2016-05-05.
 */
public class TestManagerAgent extends ExtendedAgent {
    private int P;
    private int minK, stepK, maxK, actualK;
    private int minN, stepN, maxN, actualN;
    private int testMax, testIterator;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args!= null && args.length>1) {
            testMax = Integer.parseInt(String.valueOf(args[0]));
            P = Integer.parseInt(String.valueOf(args[1]));
            minK = Integer.parseInt(String.valueOf(args[2]));
            stepK = Integer.parseInt(String.valueOf(args[3]));
            maxK = Integer.parseInt(String.valueOf(args[4]));
            minN = Integer.parseInt(String.valueOf(args[5]));
            stepN = Integer.parseInt(String.valueOf(args[6]));
            maxN = Integer.parseInt(String.valueOf(args[7]));

            runTests();
        }else {
            doDelete();
        }
    }

    private void runTests() {
        addBehaviour(new WaitForResultBehaviour());

        actualK = minK;
        actualN = minN;
        runSingleTest(P, actualK, actualN);
    }

    private void runSingleTest(int p, int k, int n) {
        addBehaviour(new WaitForCreateAgentsBehaviour());
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setConversationId("PREPARE_TEST");
        message.addReceiver(new AID("j0", AID.ISLOCALNAME));
        for (int i = 1; i <= p; i++) {
            message.addReceiver(new AID("creator_" + i, AID.ISLOCALNAME));
            try {
                message.setContentObject(new TestParams(p, k, n));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        send(message);
    }

    public void startTest() {
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.setConversationId("START_TEST");
        message.addReceiver(new AID("j0", AID.ISLOCALNAME));
        try {
            message.setContentObject(new TestParams(P, actualK, actualN));
        } catch (IOException e) {
            e.printStackTrace();
        }
        send(message);
    }

    public int getAgentsNumber() {
        return P * actualK;
    }

    public void nextTest() {
        testIterator++;
        if(testIterator >= testMax) {
            testIterator = 0;
            if (actualK <= maxK) {
                if(actualN < maxN) {
                    actualN += stepN;
                }else {
                    actualK += stepK;
                    actualN = minN;
                }
            }else {
                doDelete();
            }
        }
        runSingleTest(P, actualK, actualN);
    }
}
