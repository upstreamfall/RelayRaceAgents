package agents.testmanager;

import agents.testmanager.TestManagerAgent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pbielicki on 05.05.2016.
 */
public class WaitForCreateAgentsBehaviour extends Behaviour {
    private int stage;
    private int counter;
    private boolean isDone;

    @Override
    public void action() {
        switch (stage) {
            case 0:
                counter = ((TestManagerAgent)myAgent).getAgentsNumber(); //liczba kontenerow
                stage++;
                break;
            case 1:
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("READY");
                ACLMessage message = myAgent.receive(messageTemplate);
                if(message != null) {
                    if(--counter == 0){
                        stage++;
                    }
                }else {
                    block();
                }
                break;
            case 2:
                ((TestManagerAgent)myAgent).startTest();
                isDone = true;
                break;
        }
    }

    @Override
    public boolean done() {
        return isDone;
    }
}
