package cz.cvut.kbss.modelio.ontouml.metadata.vocabulary;

import cz.cvut.kbss.modelio.ontouml.export.InvalidMetadataException;
import cz.cvut.kbss.modelio.ontouml.export.Vocabulary;
import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyTagType;
import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyMetadata;
import java.util.EnumMap;
import org.apache.jena.ontology.OntModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestVocabulary {
	
	private static final String BASE="http://example.org/voc1/";

	private EnumMap<VocabularyTagType,String> tagValues;

	@BeforeEach
	void init() {
		tagValues = new EnumMap<>(VocabularyTagType.class);
		tagValues.put(VocabularyTagType.VOCABULARY_BASE, BASE);
		tagValues.put(VocabularyTagType.VOCABULARY_PREFIX, "voc1");
		tagValues.put(VocabularyTagType.VOCABULARY_TITLE, "");
		tagValues.put(VocabularyTagType.VOCABULARY_DESCRIPTION, "");
		tagValues.put(VocabularyTagType.VOCABULARY_VERSION, "");
		tagValues.put(VocabularyTagType.VOCABULARY_LICENSE, "");
		tagValues.put(VocabularyTagType.VOCABULARY_AUTHORS, "");
		tagValues.put(VocabularyTagType.VOCABULARY_IMPORTS, "");
	}

	@Test
	void testCreateVocabularyWithInvalidMetadata() {
		assertThrows(InvalidMetadataException.class, () -> {
			final VocabularyMetadata v = mock(VocabularyMetadata.class);
			when(v.getTitle()).thenReturn(null);
			new Vocabulary(v);
		});
	}

	@Test
	void testCreateVocabularyCreatesNonEmptyModel() {
		final VocabularyMetadata metadata = new VocabularyMetadata(tagValues);
		final OntModel model = new Vocabulary(metadata).getModel();
		Assertions.assertNotNull(model.getOntology(BASE+"model"));
	}
	
	@Test
	void testCreateVocabularyCreatesNonEmptyGlossary() {
		final VocabularyMetadata metadata = new VocabularyMetadata(tagValues);
		final OntModel model = new Vocabulary(metadata).getGlossary();
		Assertions.assertNotNull(model.getOntology(BASE+"glosář"));
	}
}
