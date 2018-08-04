package xyz.taosue.utils;

import edu.stanford.nlp.simple.Document;
import xyz.taosue.entity.Triple;

import java.util.ArrayList;
import java.util.List;


/**
 * @author tao
 */
public class StanfordCoreNLPUtil {

    /**
     * 提取三元组
     *
     * @param text
     * @return
     */
    public static List<Triple> extractTriple(String text) {
        Document doc = new Document(text);
        List<Triple> list = new ArrayList<>();
        doc.sentences().forEach(sentence -> sentence.openieTriples().forEach(triple -> {
            Triple t = new Triple();
            t.setSubj(triple.subjectLemmaGloss());
            t.setPred(triple.relationLemmaGloss());
            t.setObj(triple.objectLemmaGloss());
            list.add(t);
            System.out.println("{sub:" + t.getSubj() + ",pred:" + t.getPred() + ",obj:" + t.getObj() + "}");
        }));
        return list;
    }
}
