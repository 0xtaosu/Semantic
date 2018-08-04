package xyz.taosue.app;

import xyz.taosue.dao.RecordDao;
import xyz.taosue.dao.SchemaDao;
import xyz.taosue.entity.Schema;
import xyz.taosue.entity.Triple;
import xyz.taosue.thread.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static xyz.taosue.dao.TripleDao.insertTriple;

public class Build {
    private static ExecutorService pool = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        //1.获得评论数据
        List<String> recordList = RecordDao.selectRecord();
        try {
            //2.基于CoreNLP的实体关系抽取
            List<Triple> tripleList = pool.submit(new ExtractTripleCallable(recordList)).get();
            pool.awaitTermination(2, TimeUnit.SECONDS);
//            insertTriple(tripleList);
            //3.基于Probase的实体标注
            tripleList = pool.submit(new AnnotateEntityCallable(tripleList)).get();
            pool.awaitTermination(2, TimeUnit.SECONDS);
            //4.基于Wikidata的知识扩充
            //tripleList = pool.submit(new ExpandTripleCallable(tripleList)).get();
            //pool.awaitTermination(2, TimeUnit.SECONDS);
            //5.开始构建RDFS
            Set<Schema> schemaSet = pool.submit(new ConvertSchemaCallable(tripleList)).get();
            pool.awaitTermination(2, TimeUnit.SECONDS);
            //6.开始建表
            Set<String> createSQLSet = pool.submit(new CreateSchemaCallable(schemaSet)).get();
            pool.awaitTermination(2, TimeUnit.SECONDS);
            SchemaDao.createTable(createSQLSet);
//            //7.抽样
//            Map<String, List<Triple>> sample = pool.submit(new SampleTripleCallable(tripleList)).get();
//            pool.awaitTermination(2, TimeUnit.SECONDS);
//            //8.写入
//            pool.submit(new TextWriterThread(tripleList, sample));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
