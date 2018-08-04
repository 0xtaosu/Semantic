package xyz.taosue.entity;

import java.util.Set;

/**
 * @author tao
 */
public class Schema {
    private String name;
    private Set<String> property;
    private String relation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getProperty() {
        return property;
    }

    public void setProperty(Set<String> property) {
        this.property = property;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "name='" + name + '\'' +
                ", property=" + property +
                '}';
    }
}
