package knowledge.representation.prepare;

import javafx.util.Pair;
import xyz.taosue.utils.enumeration.path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 封装各个算法 所涉及的相同操作
 * 最终目的 : 减少重复代码量,而不是优化效率
 *
 * @author Administrator
 */
public abstract class Prepare {
    // 常量设置
    protected final double PI = 3.1415926535897932384626433832795;
    protected final String sperator = "\t";
    // 设置计算的范数(默认采用2-范数)
    // 用以计算 d(h+r,t)
    // false - 1 范数
    // true -  2 范数
    protected boolean L_norm = false;
    protected Random random = new Random();
    // 基于之前的训练结果
    protected boolean onTheShoulder = false;
    //原始数据集路径
    protected String entity2idPath = path.entity2idPath.getPath();
    protected String relation2idPath = path.relation2idPath.getPath();
    protected String trainPath = path.trainPath.getPath();
    protected String classificationPath = path.classificationPath.getPath();
    protected String headAndTailAveragePath = path.headAndTailAveragePath.getPath();
    // 数据准备
    protected HashMap<String, Integer> entity2id;
    protected HashMap<String, Integer> relation2id;
    protected HashMap<Integer, String> id2entity, id2relation;
    // 关系分类
    // 未分类    0
    // 1 to 1   1
    // 1 to N   2
    // N to 1   3
    // N to N   4
    protected ArrayList<Integer> relationType;      // 关系类型
    protected ArrayList<Pair<Double, Double>> headTailAverage;  // 头实体与尾实体的平均个数

    protected int entityNum = 0;
    protected int relationNum = 0;

    {
        entity2id = new HashMap<>();
        relation2id = new HashMap<>();
        id2entity = new HashMap<>();
        id2relation = new HashMap<>();
        relationType = new ArrayList<>();
        headTailAverage = new ArrayList<>();
    }

    public void setOnTheShoulder(boolean onTheShoulder) {
        this.onTheShoulder = onTheShoulder;
    }

    protected void prepare() {
        try {
            // 实体-id
            BufferedReader entity2idReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(entity2idPath), "utf-8"));
            // 关系-id
            BufferedReader relation2idReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(relation2idPath), "utf-8"));

            // 初始化 实体-id 、id-实体映射表
            String record = "";
            while ((record = entity2idReader.readLine()) != null) {
                addEntity(record);
                ++entityNum;
            }
            // 初始化 关系-id 、id-关系映射表
            while ((record = relation2idReader.readLine()) != null) {
                addRelation(record);
                ++relationNum;
            }
            entity2idReader.close();
            relation2idReader.close();
            System.out.println("entity_num  : " + entityNum);
            System.out.println("relation_num: " + relationNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addEntity(String record) {
        String entity = record.split(sperator)[0];
        Integer id = new Integer(record.split(sperator)[1]);
        entity2id.put(entity, id);
        id2entity.put(id, entity);
    }

    private void addRelation(String record) {
        String relation = record.split(sperator)[0];
        Integer id = new Integer(record.split(sperator)[1]);
        relation2id.put(relation, id);
        id2relation.put(id, relation);
    }

    /**
     * 获取分类结果
     *
     * @return
     */
    protected boolean getClassifications() {
        try {
            File file = new File(classificationPath);
            if (!file.exists()) {
                return false;
            }
            BufferedReader classificationReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "utf-8"));
            String record;
            while ((record = classificationReader.readLine()) != null) {
                if (record.isEmpty()) {
                    continue;
                }
                Integer type = new Integer(record);
                relationType.add(type);
            }
            classificationReader.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取关系的头实体与尾实体的平均个数
     *
     * @return
     */
    protected boolean getHeadTailAverage() {
        try {
            File file = new File(headAndTailAveragePath);
            if (!file.exists()) {
                return false;
            }
            BufferedReader headAndTailAverageReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "utf-8"));
            String record;
            while ((record = headAndTailAverageReader.readLine()) != null) {
                if (record.isEmpty()) {
                    continue;
                }
                Double head = new Double(record.split(sperator)[0]);
                Double tail = new Double(record.split(sperator)[1]);
                headTailAverage.add(new Pair<>(head, tail));
            }
            headAndTailAverageReader.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 产生一个 [min,max] 之间的随机数
     *
     * @param min
     * @param max
     * @return
     */
    protected double rand(double min, double max) {
        return min + random.nextDouble() % (max - min + 1);
    }

    /**
     * 产生一个 [min,max] 之间的随机数
     *
     * @param min
     * @param max
     * @return
     */
    protected int rand(int min, int max) {
        int num = (random.nextInt() * random.nextInt()) % (max - min);
        while (num < 0) {
            num += max - min;
        }
        return num + min;
    }

    /**
     * 正态分布
     *
     * @param x
     * @param miu
     * @param sigma
     * @return
     */
    protected double normal(double x, double miu, double sigma) {
        return 1.0 / Math.sqrt(PI * 2) * Math.exp(-Math.pow(x - miu, 2.0) / (2 * Math.pow(sigma, 2)));
    }

    /**
     * 产生随机正态分布
     *
     * @param miu
     * @param sigma
     * @param min
     * @param max
     * @return
     */
    protected double uniform(double miu, double sigma, double min, double max) {
        double x, y, dScope;
        do {
            x = rand(min, max);
            y = normal(x, miu, sigma);
            dScope = rand(0.0, normal(miu, miu, sigma));
        } while (dScope > y);
        return x;
    }

    /**
     * 向量归一化
     *
     * @param vector
     */
    protected void normalized(ArrayList<Double> vector) {
        double sum = length(vector);
        for (int i = 0; i < vector.size(); ++i) {
            vector.set(i, vector.get(i) / sum);
        }
    }

    /**
     * 向量长度
     *
     * @param vector
     * @return
     */
    private double length(ArrayList<Double> vector) {
        double sum = 0;
        for (Double num : vector) {
            sum += num * num;
        }
        return Math.sqrt(sum);
    }
}