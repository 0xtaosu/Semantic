package xyz.taosue.thread;


import xyz.taosue.entity.Schema;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author tao
 */
public class CreateSchemaCallable implements Callable<Set<String>> {
    private Set<Schema> schemaSet;

    public CreateSchemaCallable(Set<Schema> schemaSet) {
        this.schemaSet = schemaSet;
    }

    @Override
    public Set<String> call() throws Exception {
        System.out.println("==开始建表===");
        Set<String> createSQLSet = new HashSet<>();
        for (Schema schema : schemaSet) {
            //如果属性不为空，也就是关系为空
            StringBuilder propertyString = new StringBuilder();
            for (String property : schema.getProperty()) {
                property = "`" + property + "`";
                propertyString.append(property).append(" bit NULL DEFAULT b'0' ,");
            }
            String createSQL = "CREATE TABLE `" + schema.getName() + "`(`id` int NOT NULL AUTO_INCREMENT," + propertyString.toString() + "PRIMARY KEY (`id`));";
            System.out.println(createSQL);
            createSQLSet.add(createSQL);
        }
        System.out.println("==结束建表===");
        return createSQLSet;
    }
}
