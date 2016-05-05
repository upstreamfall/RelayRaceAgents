package agents.judge;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pbielicki on 04.05.2016.
 */
public class WaitRunnersGetLocationsBehaviour extends Behaviour {
    private boolean isDone;
    private int stage;
    private int runnersCounter;

    @Override
    public void action() {
        switch (stage){
            case 0:
                runnersCounter = ((JudgeAgent)myAgent).getRunnersNumber();
                stage++;
                break;
            case 1:
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("GET_LOCATIONS");
                ACLMessage msg = myAgent.receive(messageTemplate);
                if (msg != null){
                    runnersCounter--;
                    if (runnersCounter == 0){
                        stage++;
                    }
                }else {
                    block();
                }
                break;
            case 2:
                isDone = true;
                ((JudgeAgent)myAgent).startRace();
        }
    }

    @Override
    public boolean done() {
        return isDone;
    }
}
