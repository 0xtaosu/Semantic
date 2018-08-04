package xyz.taosue.utils.enumeration;

/**
 * @author tao
 */

public enum path {
    entity2idPath("SemanticChain/resources/entity2id.txt"),
    relation2idPath("SemanticChain/resources/relation2id.txt"),
    trainPath("SemanticChain/resources/train.txt"),
    testPath("SemanticChain/resources/test.txt"),
    validPath("SemanticChain/resources/valid.txt"),
    classificationPath("SemanticChain/resources/classification.txt"),
    headAndTailAveragePath("SemanticChain/resources/head_tail_average.txt"),
    entityVectorPath("SemanticChain/resources/result/entity_vector.transe"),
    relationVectorPath("SemanticChain/resources/result/relation_vector.transe"),
    testResultPath("SemanticChain/resources/result/test_result.transe"),
    classifiedPath("SemanticChain/resources/result/classified.transe");

    private String path;

    path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
