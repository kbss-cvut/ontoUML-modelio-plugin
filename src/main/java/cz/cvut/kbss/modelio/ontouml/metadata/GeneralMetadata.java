package cz.cvut.kbss.modelio.ontouml.metadata;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

public class GeneralMetadata<ENUM extends Enum<ENUM> & IdentifiableEnum> {

    private Map<ENUM, String> tagValues;

    protected GeneralMetadata(final Map<ENUM, String> tagValues) {
        this.tagValues = tagValues;
    }

    protected String get(final ENUM key) {
        return tagValues.get(key) != null ? tagValues.get(key).trim() : null;
    }

    public static <ENUM extends Enum<ENUM> & IdentifiableEnum> Map<ENUM, String> create(
        final ModelElement root, final IModule module, final Class<ENUM> t) {
        final Map<ENUM, String> map = new EnumMap<>(t);
        Arrays.stream(t.getEnumConstants()).forEach(vt -> {
            final String v = root.getTagValue(module.getName(), vt.getId());
            map.put(vt, v == null ? v : v.trim());
        });
        return map;
    }

    @Override public String toString() {
        return String.format("Metadata(%s)", tagValues);
    }
}
