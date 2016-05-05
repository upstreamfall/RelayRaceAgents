package agents.runner;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class WaitForSignalToGetLocationsBehaviour extends Behaviour {
    private boolean receiveSignal;

    @Override
    public void action() {
        MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("GET_LOCATIONS");
        ACLMessage msg = myAgent.receive(messageTemplate);
        if (msg != null) {
            ((RunnerAgent)myAgent).judgeAgent = msg.getSender();
            ((RunnerAgent)myAgent).addBehaviour(new GetTeamMemberLocationsBehaviour());
            receiveSignal = true;
        }else {
            block();
        }
    }

    @Override
    public boolean done() {
        return receiveSignal;
    }
}
