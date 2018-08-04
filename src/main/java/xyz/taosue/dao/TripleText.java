package xyz.taosue.dao;


import xyz.taosue.entity.Triple;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author tao
 */
public class TripleText {
    /**
     * 写入entity2id.txt和relation2id.txt
     *
     * @param entitys
     */
    public void writeTetx(Set<String> entitys, String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (int i = 0; i < entitys.size(); i++) {
                writer.write(entitys.toArray()[i] + "\t" + i + "\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param tripleList
     */
    public void writeTriple(List<Triple> tripleList,String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"));
            for (int i=0;i<tripleList.size();i++){
                Triple triple=tripleList.get(i);
                writer.write(triple.getSubj()+ "\t" +triple.getObj()+"\t"+triple.getPred()+ "\r\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
