package knowledge.representation.transe;

import javafx.util.Pair;
import knowledge.representation.prepare.Test;
import xyz.taosue.utils.enumeration.path;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class TestTransE extends Test {
    // 实体向量
    private ArrayList<ArrayList<Double>> entityVector;
    // 关系向量
    private ArrayList<ArrayList<Double>> relationVector;
    private String entityVectorPath = path.entityVectorPath.getPath();
    private String relationVectorPath = path.relationVectorPath.getPath();
    private String testResultPath = path.testResultPath.getPath();
    private String classifiedPath = path.classifiedPath.getPath();
    private int dimension = 100;
    // 链接预测参数 设置
    private double rankREHead = 0.0, rankREHeadWithFilter = 0.0;
    private double rankRETail = 0.0, rankRETailWithFilter = 0.0;
    private ArrayList<Pair<Integer, Double>> energys;

    // 0 未分类
    // 1 1 to 1
    // 2 1 to n
    // 3 n to 1
    // 4 n to n
    // 过滤前 Top 10排名
    private ArrayList<Double> topTenREH;
    private ArrayList<Double> topTenRET;
    // 过滤后 Top 10排名
    private ArrayList<Double> topTenREHWithF;
    private ArrayList<Double> topTenRETWithF;
    // 过滤计数器
    private ArrayList<Double> filters;
    // 分类测试计数器
    private ArrayList<Double> sum;

    // 排序器
    private Comparator<Pair<Integer, Double>> comparator = (o1, o2) -> {
        if ((o1.getValue() - o2.getValue()) > 0) {
            return 1;
        }
        if ((o1.getValue() - o2.getValue()) == 0) {
            return 0;
        }
        return -1;
    };

    {
        entityVector = new ArrayList<>();
        relationVector = new ArrayList<>();
        energys = new ArrayList<>();
        filters = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        topTenREH = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        topTenRET = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        topTenREHWithF = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        topTenRETWithF = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
        sum = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0));
    }

    public void run() {
        prepare();
        init();
        classified();
        prediction();
    }

    // 链接预测 进行验证
    private void prediction() {
        long start = System.currentTimeMillis();

        for (int test = 0; test < testNum; ++test) {
            int head = test_head.get(test);
            int relation = test_relation.get(test);
            int tail = test_tail.get(test);
            int type = relationType.get(relation);
            sum.set(type, sum.get(type) + 1);
            resetFilter();
            energys.clear();
            // 替换头实体
            for (int i = 0; i < entityNum; ++i) {
                energys.add(new Pair<>(i, distance(i, relation, tail)));
            }
            energys.sort(comparator);
            for (int i = 0; i < entityNum; ++i) {
                // 记录非有效三元组个数
                if (!effective(energys.get(i).getKey(), relation, tail)) {
                    filters.set(0, filters.get(0) + 1);
                    if (type >= 1) {
                        filters.set(type, filters.get(type) + 1);
                    }
                }
                if (energys.get(i).getKey() == head) {
                    rankREHead += i + 1;                              // 过滤前排名
                    rankREHeadWithFilter += filters.get(0) + 1;     // 过滤后排名
                    // 记录过滤前 top 10 个数
                    if (i + 1 <= 10) {
                        topTenREH.set(0, topTenREH.get(0) + 1);
                        if (type >= 1) {
                            topTenREH.set(type, topTenREH.get(type) + 1);
                        }
                    }
                    // 记录过滤后 top 10 个数
                    if (filters.get(0) + 1 <= 10) {
                        topTenREHWithF.set(0, topTenREHWithF.get(0) + 1);
                        if (type >= 1) {
                            topTenREHWithF.set(type, topTenREHWithF.get(type) + 1);
                        }
                    }
                    break;
                }
            }

            resetFilter();
            energys.clear();
            // 替换尾实体
            for (int i = 0; i < entityNum; ++i) {
                energys.add(new Pair<>(i, distance(head, relation, i)));
            }
            energys.sort(comparator);
            for (int i = 0; i < entityNum; ++i) {
                // 记录非有效三元组个数
                if (!effective(head, relation, energys.get(i).getKey())) {
                    filters.set(0, filters.get(0) + 1);
                    if (type >= 1) {
                        filters.set(type, filters.get(type) + 1);
                    }
                }
                if (energys.get(i).getKey() == tail) {
                    rankRETail += i + 1;                              // 过滤前排名
                    rankRETailWithFilter += filters.get(0) + 1;     // 过滤后排名
                    // 记录过滤前 top 10 个数
                    if (i + 1 <= 10) {
                        topTenRET.set(0, topTenRET.get(0) + 1);
                        if (type >= 1) {
                            topTenRET.set(type, topTenRET.get(type) + 1);
                        }
                    }
                    // 记录过滤后 top 10 个数
                    if (filters.get(0) + 1 <= 10) {
                        topTenRETWithF.set(0, topTenRETWithF.get(0) + 1);
                        if (type >= 1) {
                            topTenRETWithF.set(type, topTenRETWithF.get(type) + 1);
                        }
                    }
                    break;
                }
            }

            if (test % 1000 == 0) {
                long end = System.currentTimeMillis();
                System.out.println("test.txt : " + test + " ; use time : " + (end - start) + "ms" + " " + energys.size());
                start = end;
            }
        }

        output();

    }

    // 重置过滤计数器
    private void resetFilter() {
        for (int i = 0; i < filters.size(); ++i) {
            filters.set(i, 0.0);
        }
    }

    // 输出测试结果
    private void output() {
        try {
            File test_log = new File(testResultPath);
            if (!test_log.exists()) {
                test_log.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(test_log), "utf-8"));
            bufferedWriter.write("Replace the head entity : \n");
            bufferedWriter.write("mean_rank : \t" + rankREHead / testNum + " ,\ttop_10 : \t " + topTenREH.get(0) / testNum + " \n");
            bufferedWriter.write("mean_rank_with_f : \t" + rankREHeadWithFilter / testNum + " , \ttop_10_with_filter : \t" + topTenREHWithF.get(0) / testNum + " \n");
            bufferedWriter.write("1_to_1 : \t" + topTenREH.get(1) / sum.get(1) + " ;\t1_to_1_with_filter : \t" + topTenREHWithF.get(1) / sum.get(1) + "\n");
            bufferedWriter.write("1_to_N : \t" + topTenREH.get(2) / sum.get(2) + " ;\t1_to_N_with_filter : \t" + topTenREHWithF.get(2) / sum.get(2) + "\n");
            bufferedWriter.write("N_to_1 : \t" + topTenREH.get(3) / sum.get(3) + " ;\tN_to_1_with_filter : \t" + topTenREHWithF.get(3) / sum.get(3) + "\n");
            bufferedWriter.write("N_to_N : \t" + topTenREH.get(4) / sum.get(4) + " ;\tN_to_N_with_filter : \t" + topTenREHWithF.get(4) / sum.get(4) + "\n");


            bufferedWriter.write("\nReplace the tail entity : \n");
            bufferedWriter.write("mean_rank : \t" + rankRETail / testNum + " ;\ttop_10 : \t " + topTenRET.get(0) / testNum + " \n");
            bufferedWriter.write("mean_rank_with_f : \t" + rankRETailWithFilter / testNum + " ;\ttop_10_with_filter : \t" + topTenRETWithF.get(0) / testNum + " \n");
            bufferedWriter.write("1_to_1 : \t" + topTenRET.get(1) / sum.get(1) + " ;\t1_to_1_with_filter : \t" + topTenRETWithF.get(1) / sum.get(1) + "\n");
            bufferedWriter.write("1_to_N : \t" + topTenRET.get(2) / sum.get(2) + " ;\t1_to_N_with_filter : \t" + topTenRETWithF.get(2) / sum.get(2) + "\n");
            bufferedWriter.write("N_to_1 : \t" + topTenRET.get(3) / sum.get(3) + " ;\tN_to_1_with_filter : \t" + topTenRETWithF.get(3) / sum.get(3) + "\n");
            bufferedWriter.write("N_to_N : \t" + topTenRET.get(4) / sum.get(4) + " ;\tN_to_N_with_filter : \t" + topTenRETWithF.get(4) / sum.get(4) + "\n");

            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化 实体向量 和 关系向量
    private void init() {
        try {
            // 实体-向量
            BufferedReader entityVecReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(entityVectorPath), "utf-8"));
            // 关系-向量
            BufferedReader relationVecReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(relationVectorPath), "utf-8"));

            String line = "";

            // 初始化 实体向量
            while ((line = entityVecReader.readLine()) != null) {
                ArrayList<Double> vector = new ArrayList<>();
                addVector(vector, line);
                entityVector.add(vector);
            }

            // 初始化 关系向量
            while ((line = relationVecReader.readLine()) != null) {
                ArrayList<Double> vector = new ArrayList<>();
                addVector(vector, line);
                relationVector.add(vector);
            }

            entityVecReader.close();
            relationVecReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addVector(ArrayList<Double> vector, String record) {
        String[] values = record.split(sperator);
        for (String value : values) {
            vector.add(new Double(value));
        }
    }

    // 计算1-范数 与 2-范数
    private double distance(int head, int relation, int tail) {
        double result = 0;

        for (int i = 0; i < dimension; ++i) {
            if (L_norm) {
                result += Math.pow(entityVector.get(head).get(i) +
                        relationVector.get(relation).get(i) -
                        entityVector.get(tail).get(i), 2.0);

            } else {
                result += Math.abs(entityVector.get(head).get(i) +
                        relationVector.get(relation).get(i) -
                        entityVector.get(tail).get(i));
            }
        }

        return result;
    }
}