package agents.runner;

import agents.runner.RunnerAgent;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Location;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.domain.mobility.MobilityOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Iterator;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class GetTeamMemberLocationsBehaviour extends Behaviour {
    private int teamMembers;
    private int counter;
    private boolean isDone;

    private void getLocations() {
        ContentManager contentManager = myAgent.getContentManager();
        contentManager.registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        contentManager.registerOntology(MobilityOntology.getInstance());

        int teamNumber = ((RunnerAgent)myAgent).getTeamNumber();
        int agentNumber = ((RunnerAgent)myAgent).getAgentNumber();
        for (int i = 0; i < teamMembers; i++) {
//            if ((i+1) == agentNumber) continue;
            sendRequestToAMSForMember("runner_" + teamNumber + "_" + (i+1));
//            ((RunnerAgent) myAgent).println("ask for runner_" + teamNumber + "_" + (i+1));
        }
    }

    private void sendRequestToAMSForMember(String agentName) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(myAgent.getAMS());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        request.setOntology(MobilityOntology.NAME);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

        Action act = new Action();
        act.setActor(myAgent.getAMS());

        WhereIsAgentAction action = new WhereIsAgentAction();
        action.setAgentIdentifier(new AID(agentName, AID.ISLOCALNAME));
        act.setAction(action);

        try {
            myAgent.getContentManager().fillContent(request, act);
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }

        myAgent.send(request);
    }

    private int stage;
    @Override
    public void action() {
        switch (stage){
            case 0:
                teamMembers = ((RunnerAgent)myAgent).getMachinesNumber();
                counter = 0;

                getLocations();
                stage=1;
                break;
            case 1:
                MessageTemplate messageTemplate = MessageTemplate.MatchReceiver(new AID[]{myAgent.getAMS()});
//                ACLMessage msg = myAgent.receive(messageTemplate);
                ACLMessage msg = myAgent.receive();
                if(msg != null) {
//                    ((RunnerAgent)myAgent).println("sender: " + msg.getSender());
                    Location location = parseAMSResponse(msg);
                    addLocation(location);

                    counter++;
                    if(counter == teamMembers) {
                        stage = 2;
                    }
                } else {
                    block();
                }
                break;
            case 2:
                ((RunnerAgent)myAgent).confirmGettingLocations();
                isDone = true;
                break;
        }
    }

    private void addLocation(Location location) {
        ((RunnerAgent)myAgent).addLocation(location);
    }

    private Location parseAMSResponse(ACLMessage msg) {
        Location location = null;

        try {
            Result results = (Result) myAgent.getContentManager().extractContent(msg);
            Iterator iterator = results.getItems().iterator();
            if(iterator.hasNext()) {
                location = (Location) iterator.next();
            }
        } catch (Codec.CodecException e) {
            e.printStackTrace();
        } catch (OntologyException e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public boolean done() {
        return isDone;
    }
}
