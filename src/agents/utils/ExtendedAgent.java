package agents.utils;

import jade.core.Agent;

import static java.lang.Thread.sleep;

/**
 * Created by pbielicki on 03.05.2016.
 */
public class ExtendedAgent extends Agent {
    public void println(String str) {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(getLocalName() + ": " + str);
    }

    @Override
    protected void takeDown() {
        println("ends");
    }
}
