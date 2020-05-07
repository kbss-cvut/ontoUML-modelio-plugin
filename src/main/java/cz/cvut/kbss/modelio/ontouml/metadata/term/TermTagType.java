package cz.cvut.kbss.modelio.ontouml.metadata.term;

import cz.cvut.kbss.modelio.ontouml.metadata.IdentifiableEnum;

public enum TermTagType implements IdentifiableEnum {

    TERM_NAME("sgov.term.nazev"),
    TERM_CONTEXT_BASED_NAME("sgov.term.kontextualni-nazev"),
    TERM_SEARCH_INDEX("sgov.term.vyhledavaci-index"),
    TERM_MEANING_CONSTRAINT("sgov.term.vymezeni-vyznamu-pojmu"),
    TERM_MEANING_DEFINITION("sgov.term.definice"),
    TERM_MEANING_CONSTRAINT_SOURCE("sgov.term.odkaz-na-vymezeni-vyznamu-v-legislative"),
    TERM_MEANING_DEFINITION_SOURCE("sgov.term.odkaz-na-vybrane-misto-v-legislative"),
    TERM_SPECIALIZES_EXTERNAL_TERMS("sgov.term.specializuje-externi-pojmy");

    private String id;

    TermTagType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
