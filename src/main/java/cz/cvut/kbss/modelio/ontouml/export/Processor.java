package cz.cvut.kbss.modelio.ontouml.export;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.metadata.term.TermMetadata;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.diagrams.ClassDiagram;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.UmlModelElement;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Instance;

import static cz.cvut.kbss.modelio.Utils.assignStringLiterals;

public interface Processor {

	IModule getModule();

	void postprocess(final OntModel m );

	void processClass(final Class e, final VocabularyContext context);

	default void addGlossaryTerm(final ModelElement c, final Vocabulary vocabulary) {
		Utils.createSkosConcept(vocabulary.getGlossary(), getIdentifier(vocabulary, c),
			TermMetadata.create(c, getModule()));
	}

	default void processInstance(final Instance e, final VocabularyContext context) {
		getModule().getModuleContext().getLogService().info("Processing instance: " + e.getName());
		addGlossaryTerm(e, context.getVocabulary(e));
		final OntModel model = context.getVocabulary(e).getModel();
		final OntResource i = Utils.getResource(getId(e, context), model);
		final OntClass c = Utils.getClass(getId(e.getBase(), context), model);
		model.add(i, RDF.type, c);
	}

	default void processDiagram(final ClassDiagram cd, final VocabularyContext context) {
		getModule().getModuleContext().getLogService().info("Processing diagram: " + cd.getName());

		final Vocabulary vocabulary = context.getVocabulary((UmlModelElement) cd.getOrigin());
		final OntModel diagram = vocabulary.getDiagram();
		final String diagramId = vocabulary.getMetadata().getBase() + Utils.normalizeName(cd.getName());
		final OntResource i = Utils.getResource(diagramId, diagram);
		i.addProperty(DCTerms.identifier, ResourceFactory.createStringLiteral(cd.getUuid()));
		assignStringLiterals(cd.getName(), SKOS.prefLabel, i);

		cd.getRepresented().forEach(
				el -> {
					if ((el instanceof Class) ||  (el instanceof Instance)) {
						final OntResource t = Utils.getResource(
								getId((UmlModelElement) el, context),
								diagram
						);
						diagram.add(i, DCTerms.relation, t);
					}
				}
		);
	}

	default String getId(final UmlModelElement mt, final VocabularyContext c) {
		return getIdentifier(c.getVocabulary(mt),mt);
	}
	
	default String getIdentifier(final Vocabulary vocabulary, final ModelElement e) {
		return vocabulary.getTermBase() + Utils.normalizeName(e.getName());
	}
}
