package knowledge.representation.transe;

import javafx.util.Pair;
import knowledge.representation.prepare.Train;
import xyz.taosue.utils.enumeration.path;

import java.io.*;
import java.util.ArrayList;

/**
 * TransE算法实现思考
 * 1. 读取获得的文件中的数据,分别将实体和向量使用id标号
 * 1.1 文件内容分析
 * entity2id —— 存储的是所有的实体以及对应的id号
 * relation2id —— 存储的是所有的关系以及与之对应的id号
 * train.txt —— 训练数据集,头实体-h、尾实体-t、关系-r
 * test.txt —— 测试数据集
 * valid.txt —— 有效数据集
 * <p>
 * 2. 初始化——为每个实体和向量随机分配初始向量
 * <p>
 * 3. 采用TransE算法进行训练
 * TransE 算法实现
 * 3.1
 * <p>
 * 4. 将训练结果存入指定结果文件中
 */
public class TrainTransE extends Train {
    private ArrayList<ArrayList<Double>> entityVector, entityVectorCopy;           // 实体向量
    private ArrayList<ArrayList<Double>> relationVector, relationVectorCopy;       // 关系向量
    // 结果集路径
    private String entityVectorPath = path.entityVectorPath.getPath();
    private String relationVectorPath = path.relationVectorPath.getPath();

    // 训练参数
    private int dimension = 100;
    private double learnRate = 0.001;
    private double margin = 1;
    private int batchs = 100;                                   // 批处理个数
    private int rounds = 1000;                                  // 训练轮数
    private int batch_size = 0;                                 // 批处理大小
    // 评分函数值 - 损失函数值
    private double criterion = 0.0;

    {
        entityVector = new ArrayList<>();
        relationVector = new ArrayList<>();
    }


    public void run() {
        prepare();
        init();
        MBGD();
        export();
    }

    /**
     * Mini-batch gradient descent 随机批处理优化
     */
    private void MBGD() {
        batch_size = train_head.size() / batchs;

        for (int round = 0; round < rounds; ++round) {
            criterion = 0.0;
            long start = System.currentTimeMillis();
            for (int batch = 0; batch < batchs; ++batch) {
                entityVectorCopy = (ArrayList<ArrayList<Double>>) entityVector.clone();
                relationVectorCopy = (ArrayList<ArrayList<Double>>) relationVector.clone();
                for (int k = 0; k < batch_size; ++k) {
                    train();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("round : " + round +
                    "; use_time : " + (end - start) / 1000 +
                    "s; criterion : " + criterion);
        }
    }

    /**
     * 训练过程:
     * 1. 采用 Mini-Batch gradient descent为整体流程框架
     * 2. 获取训练三元组与错误三元组
     * 3. 训练
     */
    private void train() {
        // 随机取一个训练集中的三元组
        int i = rand(0, train_head.size());
        // 在实体集中随机取一个实体
        int j = rand(0, entityNum);

        int head = train_head.get(i);
        int relation = train_relation.get(i);
        int tail = train_tail.get(i);

        if (headOrTail(relation)) {
            // 替换头实体
            while (in_train.contains(new Pair<>(new Pair<>(j, train_relation.get(i)), train_tail.get(i)))) {
                j = rand(0, entityNum);
            }
            train(head, relation, tail, train_head.get(j), relation, tail);
        } else {
            // 替换尾实体
            while (in_train.contains(new Pair<>(new Pair<>(train_head.get(i), train_relation.get(i)), j))) {
                j = rand(0, entityNum);
            }
            train(head, relation, tail, head, relation, train_tail.get(j));
        }

    }

    /**
     * 训练过程 - 正确三元组 - 错误三元组
     *
     * @param head
     * @param relation
     * @param tail
     * @param c_head
     * @param c_relation
     * @param c_tail
     */
    private void train(int head, int relation, int tail,
                       int c_head, int c_relation, int c_tail) {
        // 计算正确三元组的范数
        double d_i = distance(head, relation, tail);
        // 计算错误三元组的范数
        double d_j = distance(c_head, c_relation, c_tail);
        if (d_i + margin > d_j) {
            criterion += margin + d_i - d_j;
            gradient(head, relation, tail, c_head, c_relation, c_tail);
        }
    }

    /**
     * 梯度下降优化
     *
     * @param head
     * @param relation
     * @param tail
     * @param c_head
     * @param c_relation
     * @param c_tail
     */
    private void gradient(int head, int relation, int tail,
                          int c_head, int c_relation, int c_tail) {
        for (int i = 0; i < dimension; ++i) {
            // 若采用 2-范数 的下降梯度
            double gradient = 2 * (entityVectorCopy.get(head).get(i) + relationVectorCopy.get(relation).get(i) - entityVectorCopy.get(tail).get(i));

            // 若采用 1-范数 的下降梯度
            if (!L_norm) {
                if (gradient > 0) {
                    gradient = 1;
                } else {
                    gradient = -1;
                }
            }

            entityVector.get(head).set(i, entityVector.get(head).get(i) - learnRate * gradient);
            relationVector.get(relation).set(i, relationVector.get(relation).get(i) - learnRate * gradient);
            entityVector.get(tail).set(i, entityVector.get(tail).get(i) + learnRate * gradient);
            gradient = 2 * (entityVectorCopy.get(c_head).get(i) + relationVectorCopy.get(c_relation).get(i) - entityVectorCopy.get(c_tail).get(i));
            // 若采用 1-范数 的下降梯度
            if (!L_norm) {
                if (gradient > 0) {
                    gradient = 1;
                } else {
                    gradient = -1;
                }
            }
            entityVector.get(c_head).set(i, entityVector.get(c_head).get(i) + learnRate * gradient);
            relationVector.get(c_relation).set(i, relationVector.get(c_relation).get(i) + learnRate * gradient);
            entityVector.get(c_tail).set(i, entityVector.get(c_tail).get(i) - learnRate * gradient);

        }
        normalized(entityVector.get(head));
        normalized(entityVector.get(c_head));
        normalized(entityVector.get(tail));
        normalized(entityVector.get(c_tail));
        normalized(relationVector.get(relation));
        normalized(relationVector.get(c_relation));
    }

    /**
     * 随机选择替换头实体还是尾实体
     * true;  选择头实体
     * false; 选择尾实体
     *
     * @param relation
     * @return
     */
    private boolean headOrTail(int relation) {
        if (hasAverage) {
            double head_average = headTailAverage.get(relation).getKey();
            double tail_average = headTailAverage.get(relation).getValue();
            double proportion = 1000 * head_average / (head_average + tail_average);
            return Math.abs(random.nextInt()) % 1000 > proportion;
        } else {
            int i = rand(0, train_head.size());
            return i < train_head.size() / 2;
        }
    }

    /**
     * 向量初始化
     */
    private void init() {
        hasAverage = getHeadTailAverage();
        if (initVector()) {
            return;
        }
        random.setSeed(System.currentTimeMillis());
        for (int i = 0; i < entityNum; ++i) {
            ArrayList<Double> e_vector = new ArrayList<>();
            for (int j = 0; j < dimension; ++j) {
                e_vector.add(uniform(0, 1 / dimension, -6 / Math.sqrt(dimension), 6 / Math.sqrt(dimension)));
            }
            normalized(e_vector);
            entityVector.add(e_vector);
        }
        for (int i = 0; i < relationNum; ++i) {
            ArrayList<Double> r_vector = new ArrayList<>();
            for (int j = 0; j < dimension; ++j) {
                r_vector.add(uniform(0, 1 / dimension, -6 / Math.sqrt(dimension), 6 / Math.sqrt(dimension)));
            }
            normalized(r_vector);
            relationVector.add(r_vector);
        }
    }

    /**
     * 计算1-范数 与 2-范数
     *
     * @param head
     * @param relation
     * @param tail
     * @return
     */
    private double distance(int head, int relation, int tail) {
        double result = 0;
        for (int i = 0; i < dimension; ++i) {
            if (L_norm) {
                result += Math.pow(entityVector.get(head).get(i) + relationVector.get(relation).get(i) - entityVector.get(tail).get(i), 2.0);
            } else {
                result += Math.abs(entityVector.get(head).get(i) + relationVector.get(relation).get(i) - entityVector.get(tail).get(i));
            }
        }

        return result;
    }

    /**
     * 将结果写入文件
     */
    private void export() {
        try {
            File entity_file = new File(relationVectorPath);
            File relation_file = new File(relationVectorPath);
            if (!entity_file.exists()) {
                entity_file.createNewFile();
            }
            if (!relation_file.exists()) {
                relation_file.createNewFile();
            }
            BufferedWriter entityWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(entityVectorPath), "utf-8"));
            BufferedWriter relationWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(relationVectorPath), "utf-8"));
            entityWriter.write(entityVector.toString().replace("[", "").replaceAll("](,|])", "\n").replaceAll(" +", "").replace(",", sperator));
            relationWriter.write(relationVector.toString().replace("[", "").replaceAll("](,|])", "\n").replaceAll(" +", "").replace(",", sperator));
            entityWriter.close();
            relationWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化向量
     *
     * @return
     */
    private boolean initVector() {
        if (!onTheShoulder) {
            return false;
        }
        try {
            File entityFile = new File(entityVectorPath);
            File relationFile = new File(relationVectorPath);
            if (!entityFile.exists() || !relationFile.exists()) {
                return false;
            }
            // 实体-向量
            BufferedReader entityVecReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(entityFile), "utf-8"));
            // 关系-向量
            BufferedReader relationVecReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(relationFile), "utf-8"));

            String line;

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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param vector
     * @param record
     */
    private void addVector(ArrayList<Double> vector, String record) {
        String[] values = record.split(sperator);
        for (String value : values) {
            vector.add(new Double(value));
        }
    }
}