package agents.runner;

import agents.runner.RunnerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pawel.bielicki on 2016-05-05.
 */
public class WaitNextMoveBehaviour extends CyclicBehaviour {
    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("NEXT_MOVE");
        ACLMessage message = myAgent.receive(messageTemplate);
        if(message != null) {
            ((RunnerAgent)myAgent).move();
        }else {
            block();
        }
    }
}
