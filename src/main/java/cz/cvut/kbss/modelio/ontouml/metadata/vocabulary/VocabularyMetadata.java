package cz.cvut.kbss.modelio.ontouml.metadata.vocabulary;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.metadata.GeneralMetadata;
import java.util.Map;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

public class VocabularyMetadata extends GeneralMetadata<VocabularyTagType> {

    VocabularyMetadata(final Map<VocabularyTagType, String> tagValues) {
        super(tagValues);
    }

    public String getBase() {
        return get(VocabularyTagType.VOCABULARY_BASE);
    }

    public String getPrefix() {
        return get(VocabularyTagType.VOCABULARY_PREFIX);
    }

    public String[] getImports() {
        return Utils
            .parseCommaSeparatedStrings(get(VocabularyTagType.VOCABULARY_IMPORTS));
    }

    public String getTitle() {
        return get(VocabularyTagType.VOCABULARY_TITLE);
    }

    public String getDescription() {
        return get(VocabularyTagType.VOCABULARY_DESCRIPTION);
    }

    public String getVersion() {
        return get(VocabularyTagType.VOCABULARY_VERSION);
    }

    public String getLicense() {
        return get(VocabularyTagType.VOCABULARY_LICENSE);
    }

    public String getAuthors() {
        return get(VocabularyTagType.VOCABULARY_AUTHORS);
    }

    public static VocabularyMetadata create(final ModelElement root, final IModule module) {
        return new VocabularyMetadata(
            GeneralMetadata.create(root, module, VocabularyTagType.class));
    }
}
