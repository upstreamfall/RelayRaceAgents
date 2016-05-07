package agents.runner;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.event.MessageAdapter;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pawel.bielicki on 2016-05-07.
 */
public class WaitEndRaceBehaviour extends CyclicBehaviour {
    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("DELETE");
        ACLMessage message = myAgent.receive(messageTemplate);
        if( message != null) {
            myAgent.doDelete();
        }else {
            block();
        }
    }
}
