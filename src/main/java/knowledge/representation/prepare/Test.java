package knowledge.representation.prepare;


import javafx.util.Pair;
import xyz.taosue.utils.enumeration.path;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class Test extends Prepare {

    // 文件路径设置
    protected String testPath = path.testPath.getPath();
    protected String validPath = path.validPath.getPath();

    // 测试集
    protected ArrayList<Integer> test_head, test_tail, test_relation;
    protected int testNum = 0;
    // head - relation - tail
    // 有效集
    protected HashSet<Pair<Pair<Integer, Integer>, Integer>> effectiveSet;
    // 分类链接预测参数设置
    // 未分类    0
    // 1 to 1   1
    // 1 to N   2
    // N to 1   3
    // N to N   4
    // head-relation
    private HashSet<Pair<Integer, Integer>> relationTail;    // 关系对应尾部个数
    private double relationSum = 0.0;
    // relation-tail
    private HashSet<Pair<Integer, Integer>> relationHead;    // 关系对应头部个数
    // 是否分过类
    protected boolean classified = false;
    // 1 to N
    private int oneToN = 0;
    private int oneToOne = 0;
    private int nToOne = 0;
    private int nToN = 0;

    {
        test_head = new ArrayList<>();
        test_tail = new ArrayList<>();
        test_relation = new ArrayList<>();
        effectiveSet = new HashSet<>();
        relationTail = new HashSet<>();
        relationHead = new HashSet<>();
    }

    // 原始数据读取 —— 作为有效数据集的判定总集
    // entity2id.txt
    // realtion2id.txt
    // test.txt.txt
    // train.txt
    // valid.txt
    // 对关系进行分类
    @Override
    protected void prepare() {
        super.prepare();
        try {
            // 测试数据集
            BufferedReader testReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(testPath), "utf-8"));
            // 训练数据集
            BufferedReader trainReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(trainPath), "utf-8"));
            // 验证数据集
            BufferedReader validReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(validPath), "utf-8"));

            // 初始化 实体-id 、id-实体映射表
            String record = "";

            // 初始化 测试集 和 有效验证集
            while ((record = testReader.readLine()) != null) {
                addTest(record);
            }

            // 向有效验证集中添加 训练集
            while ((record = trainReader.readLine()) != null) {
                addEffective(record);
            }

            // 向有效验证集中添加 验证集
            while ((record = validReader.readLine()) != null) {
                addEffective(record);
            }

            testReader.close();
            trainReader.close();
            validReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param record
     */
    private void addTest(String record) {
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
        add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), true);
    }

    /**
     * @param head
     * @param relation
     * @param tail
     * @param test
     */
    private void add(int head, int relation, int tail, boolean test) {
        if (test) {
            test_head.add(head);
            test_tail.add(tail);
            test_relation.add(relation);
            ++testNum;
        }
        effectiveSet.add(new Pair<>(new Pair<>(head, relation), tail));
    }

    /**
     * @param record
     */
    private void addEffective(String record) {
        String head = record.split(sperator)[0];
        String tail = record.split(sperator)[1];
        String relation = record.split(sperator)[2];
        add(entity2id.get(head), relation2id.get(relation), entity2id.get(tail), false);
    }

    /**
     * 判断 head relation tail 是否为有效集
     *
     * @param head
     * @param relation
     * @param tail
     * @return
     */
    protected boolean effective(int head, int relation, int tail) {
        return effectiveSet.contains(new Pair<>(new Pair<>(head, relation), tail));
    }

    /**
     * 关系类型统计
     */
    public void classified() {
        if (getClassifications()) return;
        for (int i = 0; i < relationNum; ++i) {
            for (Pair<Pair<Integer, Integer>, Integer> effective : effectiveSet) {
                int relation = effective.getKey().getValue();
                if (relation == i) {
                    int tail = effective.getValue();
                    Pair<Integer, Integer> head_relation = effective.getKey();
                    Pair<Integer, Integer> tail_relation = new Pair<>(tail, relation);
                    ++relationSum;
                    relationHead.add(head_relation);
                    relationTail.add(tail_relation);
                }
            }

            relationType.add(classfication());
        }
        System.out.println("1 To 1 : " + oneToOne / (double) relationNum + " \n" +
                "1 To N : " + oneToN / (double) relationNum + " \n" +
                "N To 1 : " + nToOne / (double) relationNum + " \n" +
                "N To N : " + nToN / (double) relationNum);
        classified = true;
        restoreClassification();

    }

    /**
     * @return
     */
    private Integer classfication() {
        int type = 0;
        double average_tail = relationSum / relationHead.size();
        double average_head = relationSum / relationTail.size();
        headTailAverage.add(new Pair<>(average_head, average_tail));
        // 1 to 1
        if (average_head <= 1.5 && average_tail <= 1.5) {
            ++oneToOne;
            type = 1;
        }
        // 1 to N
        if (average_head <= 1.5 && average_tail > 1.5) {
            ++oneToN;
            type = 2;
        }
        // N to 1
        if (average_head > 1.5 && average_tail <= 1.5) {
            ++nToOne;
            type = 3;
        }
        // N to N
        if (average_head > 1.5 && average_tail > 1.5) {
            ++nToN;
            type = 4;
        }
        relationSum = 0;
        relationHead.clear();
        relationTail.clear();
        return type;
    }

    /**
     * 存储分类结果
     */
    private void restoreClassification() {
        try {
            File file = new File(classificationPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter classficationWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file), "utf-8"));
            for (Integer type : relationType) {
                classficationWriter.write(type + "\n");
            }
            classficationWriter.close();

            file = new File(headAndTailAveragePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter headAndTailWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file), "utf-8"));
            for (Pair<Double, Double> head_tail : headTailAverage) {
                headAndTailWriter.write(head_tail.getKey() + "\t" + head_tail.getValue() + "\n");
            }
            headAndTailWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}