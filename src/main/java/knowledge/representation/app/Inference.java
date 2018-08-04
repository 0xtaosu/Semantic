package knowledge.representation.app;

import knowledge.representation.transe.TestTransE;
import knowledge.representation.transe.TrainTransE;

public class Inference {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        TrainTransE trainTransE = new TrainTransE();
        TestTransE testTransE = new TestTransE();
        trainTransE.setOnTheShoulder(true);
        trainTransE.run();
//      testTransE.run();
        long end = System.currentTimeMillis();
        System.out.println("TakenTime ： " + (end - start));
    }
}