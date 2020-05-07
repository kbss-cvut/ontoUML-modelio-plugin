package cz.cvut.kbss.modelio.ontouml.export;

import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyMetadata;

public class InvalidMetadataException extends RuntimeException {
	final VocabularyMetadata m;
	InvalidMetadataException(VocabularyMetadata m) {
		super("The metadata " + m + " are invalid.");
		this.m = m;
	}
}
