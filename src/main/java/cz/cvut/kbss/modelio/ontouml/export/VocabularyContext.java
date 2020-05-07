package cz.cvut.kbss.modelio.ontouml.export;

import org.modelio.metamodel.uml.infrastructure.UmlModelElement;

public interface VocabularyContext {

	Vocabulary getVocabulary(UmlModelElement mt); 
}
