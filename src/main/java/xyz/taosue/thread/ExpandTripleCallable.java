package xyz.taosue.thread;

import xyz.taosue.entity.Triple;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 知识扩充
 *
 * @author tao
 */
public class ExpandTripleCallable implements Callable<List<Triple>> {
    private List<Triple> tripleList;

    public ExpandTripleCallable(List<Triple> tripleList) {
        this.tripleList = tripleList;
    }

    @Override
    public List<Triple> call() throws Exception {
        return tripleList;
    }
}
