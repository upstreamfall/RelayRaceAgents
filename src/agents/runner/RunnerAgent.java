package agents.runner;

import agents.utils.ExtendedAgent;
import jade.core.AID;
import jade.core.Location;
import jade.lang.acl.ACLMessage;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class RunnerAgent extends ExtendedAgent {
    String _name;
    private RunnerType type;
    private int P, K, N;
    private int currentContainer;
    private Location[] containers;
    AID judgeAgent;
    private int currentStep;

    protected void setup() {
        _name = getLocalName();

        Object[] args = getArguments();
        if (args != null && args.length>0) {
            P = Integer.parseInt(String.valueOf(args[0]));
            K = Integer.parseInt(String.valueOf(args[1]));
            N = Integer.parseInt(String.valueOf(args[2]));
            currentContainer = Integer.parseInt(String.valueOf(args[3]));

            type = (getAgentNumber() == 1 ? RunnerType.RunnerAgent : RunnerType.LocalAgent);
            containers = new Location[P+1];

//            println(String.valueOf(type));
//            println(String.valueOf(P + " " + K + " " + N + " " + currentContainer));

            addBehaviour(new WaitForSignalToGetLocationsBehaviour());
            addBehaviour(new WaitNextMoveBehaviour());
            addBehaviour(new WaitStartRaceBehaviour());
            notifyReadiness();
        }else {
            doDelete();
        }
    }

    private void notifyReadiness() {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.setConversationId("READY");
        message.addReceiver(new AID("t0", AID.ISLOCALNAME));
        send(message);
    }

    public int getMachinesNumber(){
        return P;
    }

    public int getTeamNumber() {
        return K;
    }

    public int getAgentNumber() {
        return Integer.parseInt(getLocalName().split("_")[2]);
    }

    public void addLocation(int agentNumber, Location location) {
//        println("agentNumber: " + agentNumber + ", id: " + location.getID());
//        println("name: " + location.getName().split("-")[1]);
//        println("address: " + location.getAddress());
        containers[agentNumber] = location;
    }

    public void confirmGettingLocations() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId("GET_LOCATIONS");
        msg.addReceiver(judgeAgent);
        send(msg);
    }

    public void move() {
        type = RunnerType.RunnerAgent;
        int nextContainerNumber = currentContainer+1;
        if(nextContainerNumber > P) {
            currentContainer = 1;
        }else {
            currentContainer += 1;
        }
        println("next container: " + currentContainer);
        Location newLocation = containers[currentContainer];
        println("move to " + newLocation.getName());
        doMove(newLocation);
    }

    private void finishRace() {
        if(getAgentNumber() == P){
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setConversationId("FINISH");
            message.setContent(String.valueOf(K));
            message.addReceiver(judgeAgent);
            send(message);
        }
        doDelete();
        println("after doDelete");
    }

    @Override
    protected void afterMove() {
        super.afterMove();
        currentStep++;
        informLocalAgent();
        if (currentStep == N) {
            finishRace();
        }
    }

    private void informLocalAgent() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setConversationId("NEXT_MOVE");
        int nextAgentNumber = (getAgentNumber()+1) > P ? 0 : (getAgentNumber()+1);
        msg.addReceiver(new AID("runner_" + getTeamNumber() + "_" + nextAgentNumber, AID.ISLOCALNAME));

        send(msg);
        type = RunnerType.LocalAgent;
    }

    public void startRace() {
        if(getAgentNumber() == 1) {
            move();
        }
    }
}
