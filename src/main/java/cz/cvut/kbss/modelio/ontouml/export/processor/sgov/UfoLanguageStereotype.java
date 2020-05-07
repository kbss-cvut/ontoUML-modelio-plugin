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

    ROLEMIXIN("ontouml.rolemixin"),

    QUALITY("ontouml.quality"),

    MODE("ontouml.mode"),

    RELATOR("ontouml.relator"),

    EVENTTYPE("ontoumlplus.event-type"),

    OBJECTTYPE("ontoumlplus.object-type"),

    INTRINSICTROPETYPE("ontoumlplus.intrinsic-trope-type");

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
