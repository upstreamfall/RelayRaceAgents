package agents.testmanager;

import agents.utils.ExtendedAgent;
import com.csvreader.CsvWriter;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by pbielicki on 05.05.2016.
 */
public class WaitForResultBehaviour extends CyclicBehaviour {
    private int stage;

    String outputFile;
    protected CsvWriter csvOutput;

    @Override
    public void action() {
        switch (stage){
            case 0:
                outputFile = "results.csv";
                writeHeaderToFile();
                stage++;
                break;
            case 1:
                MessageTemplate messageTemplate = MessageTemplate.MatchConversationId("RESULT");
                ACLMessage message = myAgent.receive(messageTemplate);
                if (message !=null) {
                    try {
                        csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
                        ((ExtendedAgent)myAgent).println("result: " + message.getContent());
                        csvOutput.write(String.valueOf(message.getContent()));
                        csvOutput.endRecord();
                        csvOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ((TestManagerAgent)myAgent).nextTest();
                }else {
                    block();
                }
                break;
        }
    }

    public void writeHeaderToFile() {
        try {
            csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
            csvOutput.write("P");
            csvOutput.write("K");
            csvOutput.write("N");
            csvOutput.write("Time");
            csvOutput.endRecord();
            csvOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
