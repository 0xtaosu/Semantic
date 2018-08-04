package xyz.taosue.entity;


/**
 * @author tao
 */
public class Triple {
    /**
     * 主体
     */
    private String subj;
    /**
     * 联系
     */
    private String pred;
    /**
     * 客体
     */
    private String obj;
    /**
     * 0.关系
     * 1.属性
     */
    private String type;

    private String subClasses;

    private String objClasses;

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getPred() {
        return pred;
    }

    public void setPred(String pred) {
        this.pred = pred;
    }

    public String getObj() {
        return obj;
    }

    public void setObj(String obj) {
        this.obj = obj;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubClasses() {
        return subClasses;
    }

    public void setSubClasses(String subClasses) {
        this.subClasses = subClasses;
    }

    public String getObjClasses() {
        return objClasses;
    }

    public void setObjClasses(String objClasses) {
        this.objClasses = objClasses;
    }
}
