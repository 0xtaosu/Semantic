package xyz.taosue.thread;

import xyz.taosue.entity.Triple;
import xyz.taosue.utils.StanfordCoreNLPUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 实体关系抽取
 *
 * @author tao
 */
public class ExtractTripleCallable implements Callable<List<Triple>> {
    /**
     * 输入文本
     */
    private List<String> recordList;

    public ExtractTripleCallable(List<String> recordList) {
        this.recordList = recordList;
    }


    /**
     * 输出三元组
     *
     * @return 三元组
     * @throws Exception
     */
    @Override
    public List<Triple> call() throws Exception {
        System.out.println("=========开始实体关系抽取=========");
        List<Triple> tripleList = new ArrayList<>();
        if (recordList != null) {
            //实体关系抽取
            recordList.forEach(record -> tripleList.addAll(StanfordCoreNLPUtil.extractTriple(record)));
        }
        System.out.println("=========结束实体关系抽取=========");
        return tripleList;
    }
}
