package cz.cvut.kbss.modelio.ontouml.metadata.vocabulary;

import cz.cvut.kbss.modelio.ontouml.metadata.IdentifiableEnum;

public enum VocabularyTagType implements IdentifiableEnum {

    VOCABULARY_BASE("type.vocabulary.base"),
    VOCABULARY_PREFIX("type.vocabulary.prefix"),
    VOCABULARY_TITLE("type.vocabulary.title"),
    VOCABULARY_DESCRIPTION("type.vocabulary.description"),
    VOCABULARY_VERSION("type.vocabulary.version"),
    VOCABULARY_AUTHORS("type.vocabulary.authors"),
    VOCABULARY_LICENSE("type.vocabulary.license"),
    VOCABULARY_IMPORTS("type.vocabulary.imports");

    private String id;

    VocabularyTagType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
