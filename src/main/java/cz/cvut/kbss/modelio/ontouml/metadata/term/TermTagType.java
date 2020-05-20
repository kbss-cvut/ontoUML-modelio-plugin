package cz.cvut.kbss.modelio.ontouml.metadata.term;

import cz.cvut.kbss.modelio.ontouml.metadata.IdentifiableEnum;

public enum TermTagType implements IdentifiableEnum {

    TERM_NAME("sgov.concept.preferred-name"),
    TERM_CONTEXT_BASED_NAME("sgov.concept.alternative-name"),
    TERM_SEARCH_INDEX("sgov.concept.search-index"),
    TERM_MEANING_CONSTRAINT("sgov.concept.description"),
    TERM_MEANING_DEFINITION("sgov.concept.definition"),
    TERM_MEANING_CONSTRAINT_SOURCE("sgov.concept.source-of-description"),
    TERM_MEANING_DEFINITION_SOURCE("sgov.concept.source-of-definition"),
    TERM_SPECIALIZES_EXTERNAL_TERMS("sgov.concept.specializes");

    private String id;

    TermTagType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
