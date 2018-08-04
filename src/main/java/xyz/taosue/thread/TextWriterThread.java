package xyz.taosue.thread;

import xyz.taosue.dao.TripleText;
import xyz.taosue.entity.Triple;
import xyz.taosue.utils.enumeration.path;

import java.util.*;

/**
 * @author tao
 */
public class TextWriterThread implements Runnable {
    private List<Triple> list;
    private Map<String, List<Triple>> map;

    public TextWriterThread(List<Triple> list, Map<String, List<Triple>> map) {
        this.list = list;
        this.map = map;
    }

    @Override
    public void run() {
        //写入entity2id.txt和relation2id.txt
        List<String> entitys = new ArrayList<>();
        List<String> relations = new ArrayList<>();
        list.forEach(triple -> {
            entitys.add(triple.getSubj());
            entitys.add(triple.getObj());
            relations.add(triple.getPred());
        });
        Set<String> entitySet = new HashSet<>(entitys);
        Set<String> relationSet = new HashSet<>(relations);
        TripleText tripleText = new TripleText();
        tripleText.writeTetx(entitySet, path.entity2idPath.getPath());
        tripleText.writeTetx(relationSet, path.relation2idPath.getPath());
        //写入train.txt test.txt valid.txt
        tripleText.writeTriple(map.get("train"), path.trainPath.getPath());
        tripleText.writeTriple(map.get("test"), path.testPath.getPath());
        tripleText.writeTriple(map.get("valid"), path.validPath.getPath());
    }
}
