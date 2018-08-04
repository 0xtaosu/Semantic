package xyz.taosue.thread;

import xyz.taosue.entity.Schema;
import xyz.taosue.entity.Triple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author tao
 */
public class ConvertSchemaCallable implements Callable<Set<Schema>> {
    private List<Triple> tripleList;

    public ConvertSchemaCallable(List<Triple> tripleList) {
        this.tripleList = tripleList;
    }

    @Override
    public Set<Schema> call() throws Exception {
        System.out.println("==开始构建RDFS===");
        Set<Schema> schemaSet = new HashSet<>();
        List<String> schemaNameList = new ArrayList<>();
        tripleList.forEach(triple -> schemaNameList.add(triple.getSubClasses()));
        Set<String> schemaNameSet = new HashSet<>(schemaNameList);
        schemaNameSet.forEach((String schemaName) -> {
            Schema schema = new Schema();
            Schema nextSchema = new Schema();
            schema.setName(schemaName);
            List<String> propertyList = new ArrayList<>();
            tripleList.forEach(triple -> {
                if (triple.getSubClasses() == schemaName) {
                    if (triple.getType() == "1") {
                        propertyList.add(triple.getPred() + " " + triple.getObj());
                    } else if (triple.getType() == "0") {
                        propertyList.add(triple.getPred() + " " + triple.getObjClasses());
                        schema.setRelation(triple.getObjClasses());
                        nextSchema.setName(triple.getObjClasses());
                        nextSchema.setProperty(new HashSet<>());
                        nextSchema.setRelation(schemaName);
                        schemaSet.add(nextSchema);
                    }
                }
            });
            Set<String> propertySet = new HashSet<>(propertyList);
            schema.setProperty(propertySet);
            schemaSet.add(schema);
        });
        schemaSet.forEach(schema -> System.out.println("{name:" + schema.getName() + ",property:" + schema.getProperty().toString()+ ",relation:" + (schema.getRelation() != null ? schema.getRelation() : "") + "}"));
        System.out.println("==结束构建RDFS===");
        return schemaSet;
    }
}
