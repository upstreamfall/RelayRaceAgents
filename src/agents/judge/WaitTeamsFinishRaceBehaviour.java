package agents.judge;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pawel.bielicki on 2016-05-05.
 */
public class WaitTeamsFinishRaceBehaviour extends Behaviour {
    private boolean isDone;
    private int stage;
    private int K;

    @Override
    public void action() {
        switch (stage){
            case 0:
                K =((JudgeAgent)myAgent).getTeamNumber();
                stage++;
                break;
            case 1:
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("FINISH");
                ACLMessage message = myAgent.receive(messageTemplate);
                if(message != null){
                    ((JudgeAgent)myAgent).println("team " + message.getContent() + " finished!");
                    if (--K == 0) {
                        stage++;
                    }
                }else {
                    block();
                }
                break;
            case 2:
                isDone = true;
                ((JudgeAgent)myAgent).printTime();
                break;
        }
    }

    @Override
    public boolean done() {
        return isDone;
    }
}
