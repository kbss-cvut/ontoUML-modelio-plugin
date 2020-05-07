package cz.cvut.kbss.modelio;

import cz.cvut.kbss.modelio.ontouml.export.Vocabulary;
import cz.cvut.kbss.modelio.ontouml.metadata.term.TermMetadata;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.*;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.NameSpace;

public class Utils {

	public static String getModuleIdentification(final IModule module) {
		return module.getName() + " " +  module.getVersion();
	}

	public static ModelTree getAncestorByStereotype(final ModelTree root, final IModule module, final String stereotypeName) {
		if ( root == null ) {
			return null;
		}

		final Stereotype s = root.getStereotype(module.getName(), stereotypeName);
		if (s != null) {
			return root;
		}

		return getAncestorByStereotype(root.getOwner(), module, stereotypeName);
	}

	public static boolean hasStereotypeInHierarchy(final NameSpace c, final IModule module, final String stereotype) {
		if (c.getStereotype(module.getName(), stereotype) != null) {
			return true;
		}

		if (c.getParent().isEmpty()) {
			return false;
		}

		final AtomicBoolean b = new AtomicBoolean(false);

		c.getParent().forEach(p -> b.set(b.get() || hasStereotypeInHierarchy(p.getSuperType(), module, stereotype)));
		return b.get();
	}

	public static void updateSet(final Set<OntResource> parents, final OntResource c, final Set<OntResource> set) {
		Set<OntResource> set2 = new HashSet<>(set);

		Set<OntResource> parents2 = new HashSet<>(parents);

		int size1 = parents.size();
		int size2 = set.size();
		parents2.removeAll(set);
		set2.removeAll(parents);
		if (parents2.size() == size1) {
			set.add(c);
		} else if (set2.size() < size2) {
			set.remove(c);
		}
	}

	public static void saveOntology(final ILogService logService, final Path path, final OntModel m, final String ontologyLocalName) {
		try {
			final File f = path.resolve(ontologyLocalName + ".ttl").toFile();
			logService.info("	- "+f.getAbsolutePath());
			m.write(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), "turtle");
		} catch (IOException e) {
			logService.error(e);
		}
	}

	private static <T extends OntResource> T getResource(String id, Function<String,T> createF, Function<String,T> getF) {
		T c = getF.apply(id);
		if (c == null) {
			c = createF.apply(id);
		}
		return c;
	}

	public static OntResource getResource(String idx, final OntModel model) {
		return getResource(idx, model::createOntResource, model::getOntResource);
	}

	public static OntClass getClass(String idx, final OntModel model) {
		return getResource(idx, model::createClass, model::getOntClass);
	}

	public static OntProperty getObjectProperty(String idx, final OntModel model) {
		return getResource(idx, model::createObjectProperty, model::getObjectProperty);
	}

	public static OntProperty getDataProperty(String idx, final OntModel model) {
		return getResource(idx, model::createDatatypeProperty, model::getDatatypeProperty);
	}

	public static Resource setExpression(final OntModel m, final java.util.Map.Entry<OntResource, Set<OntResource>> e) {
		if (e.getValue().size() == 1) {
			return e.getValue().iterator().next();
		} else {
			return m.createUnionClass(null, m.createList(e.getValue().iterator()));
		}
	}

	public static OntResource createUnionResource(final OntResource notNullClass,
											final OntResource potentiallyNullClass,
											final OntModel model) {
		if (potentiallyNullClass == null) {
			return notNullClass;
		} else {
			return model.createUnionClass(null,
				model.createList(new RDFNode[] {notNullClass, potentiallyNullClass}));
		}
	}

	public static Set<OntResource> getSet(int order, OntResource p, List<HashMap<OntResource, Set<OntResource>>> lst) {
		while ( lst.size() < order ) {
			lst.add(new HashMap<>());
		}
		HashMap<OntResource, Set<OntResource>> map = lst.get(order-1);

		return map.computeIfAbsent(p, k -> new HashSet<>());
	}

	public static String[] parseCommaSeparatedStrings(final String value) {
		if ( value != null) {
			return Arrays.stream(value.split("(\\s)*,(\\s)*")).map(String::trim).toArray(String[]::new);
		} else {
			return new String[]{};
		}
	}

	public static Map<String,String> parseLangString(final String value, final String... defaultLangs) {
		final Map<String,String> map = new HashMap<>();

		Pattern p = Pattern.compile("\"([^@]*)\"@([a-z]+)");
		Matcher mValue = p.matcher(value);
		boolean found = false;
		while (mValue.find()) {
			found = true;
			map.put(mValue.group(2), mValue.group(1));
		}
		if (!found) {
			for(String lang : defaultLangs) {
				if (!map.containsKey(lang)) {
					map.put(lang,value);
				}
			}
		}

		return map;
	}

	public static String buildLangString(final Map<String,String> langToValue) {
			return langToValue.keySet().stream()
					.sorted(Comparator.naturalOrder()).
							map(l -> "\"" + langToValue.get(l) + "\"" + "@" + l)
					.collect(Collectors.joining(","));
	}

	static Map<String,String> buildLangStringMap(final Map<String,String> langToValue, final Map<String,String> langToSuffix) {
		final Map<String,String> map = new HashMap<>();

		for(final String lang : langToValue.keySet()) {
			map.put(lang,langToValue.get(lang) + (langToSuffix.getOrDefault(lang, "")));
		}

		return map;
	}

	/**
	 * Assigns to the resource "ind" all comma-separated IRIs from the list "value".
	 *
	 * @param value
	 * @param prp
	 * @param ind
	 */
	public static void assignIRIs(final String[] value, final Property prp, final Resource ind) {
		for(String s: value) {
			ind.addProperty(prp, ResourceFactory.createResource(s));
		}
	}

	/**
	 * Assigns to the resource "ind" all comma-separated literals from the list "value".
	 *
	 * @param value
	 * @param prp
	 * @param ind
	 */
	public static void assignStringLiterals(final String value, final Property prp, final Resource ind) {
		assignStringLiterals(value, prp, ind,"");
	}

    private static void assignStringLiterals(final String value, final Property prp, final Resource i, final String suffix) {
		final Map<String,String> langToValue = parseLangString(value, "cs");
		final Map<String,String> langToSuffix = parseLangString(suffix, langToValue.keySet().toArray(new String[0]));
		assignStringLiterals(langToValue, prp, i, langToSuffix);
	}

	public static void assignStringLiterals(final Map<String,String> langToValue, final Property prp, final Resource i, final Map<String,String> langToSuffix) {
		final Map<String,String> map = buildLangStringMap(langToValue, langToSuffix);

		for(final String lang : map.keySet()) {
			i.addProperty(prp, ResourceFactory.createLangLiteral(map.get(lang), lang));
		}
	}

	public static String normalizeName(final String name) {
		return name.replace(" ", "-").replace(",", "-nebo-").toLowerCase();
	}

	public static OntResource createSkosConcept(
			final OntModel m,
			final String iri,
			final TermMetadata termMetadata) {
		final OntResource i = m.createIndividual(iri, SKOS.Concept);
		i.addProperty(SKOS.inScheme, m.listOntologies().toList().get(0));
		if (termMetadata != null) {
			if (termMetadata.getNazev() != null) {
				assignStringLiterals(termMetadata.getNazev(), SKOS.prefLabel, i);
			}
			if (termMetadata.getKontextualniNazev() != null) {
				assignStringLiterals(termMetadata.getKontextualniNazev(), SKOS.altLabel, i);
			}
			if (termMetadata.getVyhledavaciIndex() != null) {
				assignStringLiterals(termMetadata.getVyhledavaciIndex(), SKOS.hiddenLabel, i);
			}
			if (termMetadata.getOdkazNaVybraneMistoVLegislative() != null) {
				assignIRIs(termMetadata.getOdkazNaVybraneMistoVLegislative(), DCTerms.source, i);
			}
			if (termMetadata.getOdkazNaVymezeniVyznamuVLegislative() != null) {
				assignIRIs(termMetadata.getOdkazNaVymezeniVyznamuVLegislative(), DCTerms.relation, i);
			}
			if (termMetadata.getDefinice() != null) {
				assignStringLiterals(termMetadata.getDefinice(), SKOS.definition, i);
			}
			if (termMetadata.getVymezeniVyznamuPojmu() != null) {
				assignStringLiterals(termMetadata.getVymezeniVyznamuPojmu(), SKOS.scopeNote, i);
			}
			if (termMetadata.getSpecializujeExterniPojmy() != null) {
				assignIRIs(termMetadata.getSpecializujeExterniPojmy(), RDFS.subClassOf, i);
			}
		}
		return i;
	}

	public static void addType(final Vocabulary v, final Resource sub, final Resource sup) {
		v.getModel().add(sub, RDF.type,sup);
		v.getGlossary().add(sub, SKOS.broader, sup);
	}
}
