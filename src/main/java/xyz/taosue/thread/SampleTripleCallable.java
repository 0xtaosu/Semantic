package xyz.taosue.thread;

import xyz.taosue.entity.Triple;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author tao
 */
public class SampleTripleCallable implements Callable<Map<String, List<Triple>>> {
    private List<Triple> tripleList;

    /**
     * @param tripleList
     */
    public SampleTripleCallable(List<Triple> tripleList) {
        this.tripleList = tripleList;
    }

    @Override
    public Map<String, List<Triple>> call() throws Exception {
        Map<String, List<Triple>> map = new HashMap<>();
        int cut= (int) Math.floor(tripleList.size()*0.8);
        Collections.shuffle(tripleList);
        //train
        List<Triple> train = tripleList.subList(0, cut);
        map.put("train", train);
        //test
        List<Triple> test = tripleList.subList(cut, tripleList.size());
        map.put("test", test);
        return map;
    }
}
