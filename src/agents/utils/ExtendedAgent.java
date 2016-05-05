package agents.utils;

import jade.core.Agent;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class ExtendedAgent extends Agent {
    public void println(String str) {
        System.out.println(getLocalName() + ": " + str);
    }

    @Override
    protected void takeDown() {
        println("ends");
    }
}
