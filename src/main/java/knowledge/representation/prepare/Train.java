package knowledge.representation.prepare;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author tao
 */
public abstract class Train extends Prepare {

    protected ArrayList<Integer> train_head, train_tail, train_relation;
    /**
     * head - relation - tail
     */
    protected HashSet<Pair<Pair<Integer, Integer>, Integer>> in_train;
    /**
     * 判断选择头实体与尾实体的参数准备
     */
    protected boolean hasAverage = false;

    {
        train_head = new ArrayList<>();
        train_tail = new ArrayList<>();
        train_relation = new ArrayList<>();
        in_train = new HashSet<>();
    }

    /**
     * 原始数据读取
     * entity2id.txt
     * relation2id.txt
     * train.txt
     */
    @Override
    protected void prepare() {
        super.prepare();
        try {
            // 训练数据集
            BufferedReader trainReader = new BufferedReader(new InputStreamReader(new FileInputStream(trainPath), "utf-8"));
            // 初始化 实体-id 、id-实体映射表
            String record;
            // 初始化训练集
            while ((record = trainReader.readLine()) != null) {
                addTrain(record);
            }
            trainReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化训练集
     *
     * @param record
     */
    private void addTrain(String record) {
        String head = record.split(sperator)[0];
        String tail = record.split(sperator)[1];
        String relation = record.split(sperator)[2];

        if (!entity2id.containsKey(head)) {
            System.out.println("miss entity");
        }

        if (!entity2id.containsKey(tail)) {
            System.out.println("miss entity");
        }

        if (!relation2id.containsKey(relation)) {
            relation2id.put(relation, relationNum);
            ++relationNum;
        }
        add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail));
    }

    /**
     * @param head
     * @param relation
     * @param tail
     */
    private void add(int head, int relation, int tail) {
        train_head.add(head);
        train_tail.add(tail);
        train_relation.add(relation);
        in_train.add(new Pair<>(new Pair<>(head, relation), tail));
    }
}