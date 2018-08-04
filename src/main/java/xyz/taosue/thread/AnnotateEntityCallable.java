package xyz.taosue.thread;

import xyz.taosue.entity.Triple;
import xyz.taosue.utils.ProbaseUtil;

import java.util.List;
import java.util.concurrent.Callable;

public class AnnotateEntityCallable implements Callable<List<Triple>> {
    private List<Triple> tripleList;

    public AnnotateEntityCallable(List<Triple> tripleList) {
        this.tripleList = tripleList;
    }

    @Override
    public List<Triple> call() throws Exception {
        System.out.println("=========开始实体关系标注=========");
        tripleList.forEach(triple -> {
            triple = ProbaseUtil.judgeEntity(triple, 10);
            System.out.println("{sub:" + triple.getSubj() + ",pred:" + triple.getPred() + ",obj:" + triple.getObj() + ",type:" + triple.getType() + ",subClasses:" + triple.getSubClasses() +",objClasses:" + triple.getObjClasses()+ "}");
        });
        System.out.println("=========结束实体关系标注=========");
        return tripleList;
    }
}
