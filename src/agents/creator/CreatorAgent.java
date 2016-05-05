package agents.creator;

import agents.utils.ExtendedAgent;
import agents.utils.TestParams;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class CreatorAgent extends ExtendedAgent {
    private int P, K, N;
    private int containerNumber;

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("PREPARE_TEST");
                ACLMessage message = receive(messageTemplate);
                if(message != null){
                    TestParams params = null;
                    try {
                        params = (TestParams) message.getContentObject();
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                    createAgents(params);
                }else {
                    block();
                }
            }
        });
    }

    private void createAgents(TestParams params) {
        P = params.P;
        K = params.K;
        N = params.N;

        ContainerController containerController = getContainerController();

        try {
//            println("starts creating agents in " + containerController.getContainerName());
//            println(containerController.getContainerName().split("-")[1]);
            containerNumber = Integer.parseInt(containerController.getContainerName().split("-")[1]);
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < K; i++) {
            String agentName = "runner_" + (i+1) + "_" + containerNumber;
            String agentClassName = "agents.runner.RunnerAgent";
            Object[] agentParams = new Object[]{P, (i+1), N, containerNumber};
            try {
                AgentController agent = containerController.createNewAgent(agentName, agentClassName, agentParams);
                agent.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }
}
