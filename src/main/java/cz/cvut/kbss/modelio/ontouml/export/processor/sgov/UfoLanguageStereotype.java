package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

import java.util.Arrays;
import java.util.Optional;

public enum UfoLanguageStereotype {

    KIND("ontouml.kind"),

    SUBKIND("ontouml.subkind"),

    ROLE("ontouml.role"),

    PHASE("ontouml.phase"),

    CATEGORY("ontouml.category"),

    MIXIN("ontouml.mixin"),

    ROLE_MIXIN("ontouml.role-mixin"),

    PHASE_MIXIN("ontouml.phase-mixin"),

    QUALITYTYPE("ontouml.quality-type"),

    MODETYPE("ontouml.mode-type"),

    RELATOR_TYPE("ontouml.relator-type"),

    EVENT_TYPE("ontoumlplus.event-type"),

    OBJECT_TYPE("ontoumlplus.object-type"),

    ASPECTTYPE("ontoumlplus.aspect-type");

    private String id;

    UfoLanguageStereotype(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public UfoLanguageStereotype byId(final String id) {
        Optional<UfoLanguageStereotype>
            s = Arrays.stream(UfoLanguageStereotype.values()).filter(v -> v.getId().equals(id)).findAny();
        return s.orElse(null);
    }
}
