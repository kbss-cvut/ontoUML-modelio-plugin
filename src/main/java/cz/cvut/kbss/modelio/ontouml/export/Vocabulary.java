package cz.cvut.kbss.modelio.ontouml.export;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyMetadata;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.*;


public class Vocabulary {


	//  deprecated
//	private final String VEREJNY_SEKTOR_POJEM = "https://slovník.gov.cz/veřejný-sektor/pojem/";

	private final String POPIS_DAT_POJEM = "http://onto.fel.cvut.cz/ontologies/slovník/agendový/popis-dat/pojem/";

	private final OntModel model;

	private final OntModel glossary;

	private final OntModel vocabulary;

	private final OntModel diagram;

	private final VocabularyMetadata metadata;

	public Vocabulary(final VocabularyMetadata metadata) {
		check(metadata);
		this.metadata = metadata;
		this.model = createModel();
		this.glossary = createGlossary();
		this.vocabulary = createVocabulary();
		this.diagram = createDiagram();
	}

	private void check(final VocabularyMetadata metadata) {
		if (metadata.getTitle() == null) {
			throw new InvalidMetadataException(metadata);
		}
	}
	
	private OntModel createBlankModel(final String iri, final Map<String, String> langToLabelSuffix,
			final Consumer<Ontology> processOntology) {
		final OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		m.setNsPrefix(metadata.getPrefix(), metadata.getBase());
		m.setNsPrefix(getTermPrefix(), getTermBase());
		final Ontology o = m.createOntology(iri);
		Utils.assignStringLiterals(Utils.parseLangString(metadata.getTitle()), DCTerms.title, o.asResource(),
				langToLabelSuffix);
		addWidocoMetadata(o, metadata, langToLabelSuffix);
		processOntology.accept(o);
		return m;
	}

	private void addImport(final Ontology o, final String iri, final String property) {
		final Resource glossary = ResourceFactory.createResource(iri);
		o.addImport(glossary);
		o.addProperty(o.getModel().getProperty(property), glossary);
	}

	private OntModel createVocabulary() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("cs", " - slovník");
		map.put("en", " - vocabulary");
		return createBlankModel(metadata.getBase().substring(0, metadata.getBase().length()-1) , map, (Ontology o) -> {
			o.addProperty(RDF.type, ResourceFactory.createResource( POPIS_DAT_POJEM + "slovník"));
			addImport(o, getGlossaryIri(metadata.getBase()), POPIS_DAT_POJEM+"má-glosář");
			addImport(o, getModelIri(metadata.getBase()), POPIS_DAT_POJEM+"má-model");
		});
	}

	private OntModel createGlossary() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("cs", " - glosář");
		map.put("en", " - glossary");
		return createBlankModel(getGlossaryIri(metadata.getBase()), map, (Ontology o) -> {
			Arrays.asList(metadata.getImports())
					.forEach(a -> o.addImport(ResourceFactory.createResource(a + "/glosář")));
			o.addProperty(RDF.type, SKOS.ConceptScheme);
			o.addProperty(RDF.type, ResourceFactory.createResource(POPIS_DAT_POJEM +"glosář"));
			o.getModel().add(SKOS.prefLabel, RDFS.subPropertyOf, RDFS.label);
		});
	}

	private OntModel createModel() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("cs", " - model");
		map.put("en", " - model");
		return createBlankModel(getModelIri(metadata.getBase()), map, (Ontology o) -> {
			Arrays.asList(metadata.getImports())
					.forEach(a -> o.addImport(ResourceFactory.createResource(a + "/model")));
			o.addProperty(RDF.type, ResourceFactory.createResource(POPIS_DAT_POJEM +"model"));
			o.addImport(ResourceFactory.createResource(getGlossaryIri(metadata.getBase())));
		});
	}

	private OntModel createDiagram() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("cs", " - diagram");
		map.put("en", " - diagram");
		return createBlankModel(getDiagramIri(metadata.getBase()), map, (Ontology o) -> {
			Arrays.asList(metadata.getImports())
					.forEach(a -> o.addImport(ResourceFactory.createResource(a + "/diagram")));
			o.addProperty(RDF.type, ResourceFactory.createResource(POPIS_DAT_POJEM +"diagram"));
			o.addImport(ResourceFactory.createResource(getGlossaryIri(metadata.getBase())));
		});
	}

	private void addWidocoMetadata(final Ontology o, final VocabularyMetadata metadata, final Map<String, String> langToLabelSuffix) {
		final String VOC_NS = "http://purl.org/vocab/vann/preferredNamespaceUri";
		final Property pNs = ResourceFactory.createProperty(VOC_NS);
		o.addProperty(pNs, getTermBase());

		final String VOC_PR = "http://purl.org/vocab/vann/preferredNamespacePrefix";
		final Property pPr = ResourceFactory.createProperty(VOC_PR);
		o.addProperty(pPr, getTermPrefix());

		if (metadata.getTitle() != null) {
			final Property pT = ResourceFactory.createProperty(DCTerms.title.getURI());
			Utils.assignStringLiterals(Utils.parseLangString(metadata.getTitle()), pT, o, langToLabelSuffix);
		}

		if (metadata.getDescription() != null) {
			final Property pD = ResourceFactory.createProperty(DCTerms.description.getURI());
			Utils.assignStringLiterals(metadata.getDescription(), pD, o);
		}
//
//		if (o.getURI().substring(o.getURI().length()-1) == "/"){
//			o.addProperty(OWL2.versionIRI, ResourceFactory.createResource(o.getURI() + "verze/" + metadata.getVersion()));
//		}
//		else{
//			o.addProperty(OWL2.versionIRI, ResourceFactory.createResource(o.getURI() + "/verze/" + metadata.getVersion()));
//		}
		o.addProperty(OWL2.versionIRI, ResourceFactory.createResource(o.getURI() + "/verze/" + metadata.getVersion()));

		final String VOC_S = "http://purl.org/ontology/bibo/status";
		final Property pS = ResourceFactory.createProperty(VOC_S);
		Utils.assignStringLiterals("\"Specifikace\"@cs,\"Specification\"@en", pS, o);

		final Property pDC = ResourceFactory.createProperty(DCTerms.created.getURI());

		LocalDateTime ldt = LocalDateTime.now();
		DateTimeFormatter formmat1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter formatISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		o.addProperty(pDC, ResourceFactory.createPlainLiteral(formatISO.format(ldt)));

		final Property pC = ResourceFactory.createProperty(DCTerms.creator.getURI());
		final String creators = metadata.getAuthors();
		if (creators != null) {
			for (String s : creators.split(",")) {
				o.addProperty(pC, s);
			}
		}

		if (metadata.getLicense() != null) {
			final Property pR = ResourceFactory.createProperty(DCTerms.rights.getURI());
			o.addProperty(pR, ResourceFactory.createResource(metadata.getLicense()));
		}
	}

	public String getTermBase() {
		return metadata.getBase() + "pojem/";
	}

	private String getTermPrefix() {
		return metadata.getPrefix() + "-pojem";
	}

	private String getModelIri(final String base) {
		return base + "model";
	}

	private String getGlossaryIri(final String base) {
		return base + "glosář";
	}

	private String getDiagramIri(final String base) {
		return base + "diagram";
	}

	public VocabularyMetadata getMetadata() {
		return metadata;
	}

	public OntModel getGlossary() {
		return glossary;
	}

	public OntModel getModel() {
		return model;
	}

	public OntModel getVocabulary() {
		return vocabulary;
	}

	public OntModel getDiagram() {
		return diagram;
	}
}
