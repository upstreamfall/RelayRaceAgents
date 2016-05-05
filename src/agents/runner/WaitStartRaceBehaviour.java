package agents.runner;

import agents.runner.RunnerAgent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pbielicki on 04.05.2016.
 */
public class WaitStartRaceBehaviour extends Behaviour {
    private int stage;

    @Override
    public void action() {
        switch (stage){
            case 0:
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("START");
                ACLMessage msg = myAgent.receive(messageTemplate);
                if (msg != null) {
                    ((RunnerAgent)myAgent).startRace();
                    stage++;
                }else {
                    block();
                }
                break;
        }
    }

    @Override
    public boolean done() {
        return stage == 1;
    }
}
