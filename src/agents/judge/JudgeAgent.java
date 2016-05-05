package agents.judge;

import agents.utils.ExtendedAgent;
import agents.utils.TestParams;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.util.concurrent.TimeUnit;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class JudgeAgent extends ExtendedAgent {
    private int P, K, N;
    private long startTime;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("START_TEST");
                ACLMessage message = receive(messageTemplate);
                if(message != null) {
                    TestParams params = null;
                    try {
                        params = (TestParams) message.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    P = params.P;
                    K = params.K;
                    N = params.N;
                    addBehaviour(new WaitTeamsFinishRaceBehaviour());
                    addBehaviour(new WaitRunnersGetLocationsBehaviour());
                    requestRunnerForLocation();

                }else {
                    block();
                }
            }
        });
    }

    private void requestRunnerForLocation() {
//        println("request for locations");
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setConversationId("GET_LOCATIONS");
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < P; j++) {
                request.addReceiver(new AID("runner_" + (i+1) + "_" + (j+1), AID.ISLOCALNAME));
            }
        }

        send(request);
    }

    public int getRunnersNumber() {
        return P * K;
    }

    public int getTeamNumber(){
        return K;
    }

    public void startRace() {
        sendMessageToRunners("START");
        startTime = System.nanoTime();
    }

    public void printTime() {
        long endTime = System.nanoTime();
        long elapsedTime = (endTime - startTime);
        long milliSeconds = TimeUnit.NANOSECONDS.toMillis(elapsedTime);

//        System.out.println("Time in milliseconds: " + milliSeconds);
//        System.out.println("Time in seconds: " + TimeUnit.NANOSECONDS.toSeconds(elapsedTime));
        sendResultToTestAgent(P + "," + K + "," + N + "," + milliSeconds);
    }

    private void sendResultToTestAgent(String result) {
//        println(result);
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setContent(result);
        message.setConversationId("RESULT");
        message.addReceiver(new AID("t0", AID.ISLOCALNAME));
        send(message);
    }

    private void sendMessageToRunners(String conversationId) {
//        println("conversationId: " + conversationId);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId(conversationId);
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < P; j++) {
                msg.addReceiver(new AID("runner_" + (i+1) + "_" + (j+1), AID.ISLOCALNAME));
            }
        }

        send(msg);
    }
}
