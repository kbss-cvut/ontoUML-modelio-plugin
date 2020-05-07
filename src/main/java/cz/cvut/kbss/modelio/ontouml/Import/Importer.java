package cz.cvut.kbss.modelio.ontouml.Import;


import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.config.ModuleConfig;
import cz.cvut.kbss.modelio.vocterm.VocabularyTerm;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RIOT;
import org.apache.jena.util.FileManager;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.mmextensions.infrastructure.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.NameSpace;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static cz.cvut.kbss.modelio.ontouml.metadata.term.TermTagType.TERM_NAME;

public class Importer {
    private final IModule module;

    public Importer(IModule module) {
        this.module = module;
    }

    public final IModule getModule() {
        return this.module;
    }

    private ILogService log() {
        return module.getModuleContext().getLogService();
    }

    public void run(final ModelTree root) {

        final Map<ModelTree, String> missingOntology2Terms = new HashMap<ModelTree, String>();
        final List<String> terms = new ArrayList<String>();
        getAllOntologiesFromSubPackages(root, missingOntology2Terms, terms);

        for (String e : terms) {

            Class Imudl = createTerm(e, (NameSpace) root);// possible to create with stereotype

            this.putValue(Imudl);//puts value of named element into label (probably)
        }
    }

    private void getAllOntologiesFromSubPackages(final ModelTree root,
                                                 final Map<ModelTree, String> missingOntology2Terms,
                                                 final List<String> voc) {
        log().info("getAllOntologiesFromSubPackages: " + root);
        final List<String> listOne = (List<String>) getTermsForOntology(root);
        final List<String> listTwo = (List<String>) getLabeledElements(root);


        Collection<String> similar = new HashSet<String>(listOne);
        Collection<String> different = new HashSet<String>();

        different.addAll(listOne);
        different.addAll(listTwo);

        similar.retainAll(listTwo);
        different.removeAll(similar);
        voc.addAll(different);
        log().info("Missing vocabulary terms " + different);
    }

    private List<String> getLabeledElements(ModelTree root) {
        final List<String> listTwo = new ArrayList<String>();
        for (final ModelTree mt : root.getOwnedElement()) {
            log().info("Labeled element " + mt.getName());
            if (mt.getTagValue(this.module.getName(), TERM_NAME.getId()) != null) {
                listTwo.add(mt.getName());
            }
        }
        return listTwo;
    }

    private Collection<String> getElements(ModelTree root) {
        final Collection<String> ele = new ArrayList<>();
        for (final ModelTree mt : root.getOwnedElement())
            ele.add(mt.getName());
        return ele;
    }


    private Collection<String> getTermsForOntology(ModelTree root) {
        OntModel model = ModelFactory.createOntologyModel();

        final Path path = module.getModuleContext().getProjectStructure().getPath();
//        InputStream in = FileManager.get().open(String.valueOf(path.resolve(root.getTagValue(this.module.getName(), VOCABULARY_PREFIX.getId()) + "-glosář.ttl")));
        InputStream in = FileManager.get().open("/home/michal/modelio/workspace/OntoUMLTestProject/test-glosář.ttl");

        HashMap parents = new HashMap();

        RIOT.init();
        model.read(in, null, "TURTLE");

        String queryString2 = "SELECT  ?iri ?label " +
                "{ ?iri a <http://www.w3.org/2004/02/skos/core#Concept>; " +
                "     <http://www.w3.org/2004/02/skos/core#prefLabel> ?label. FILTER(LANGMATCHES(LANG(?label), \"cs\"))}" +
                "  ";
        //TODO remake query
        // get stereotype as broader object from list
        // source ->         <http://purl.org/dc/terms/source>
        // definition ->     <http://www.w3.org/2004/02/skos/core#definition>
        // language

        String queryString = "SELECT  ?iri ?labelcs ?labelen ?ancestor ?source ?definitioncs ?definitionen " +
                " { ?iri a <http://www.w3.org/2004/02/skos/core#Concept>; " +
                " <http://www.w3.org/2004/02/skos/core#prefLabel> ?labelcs; " +
                " <http://www.w3.org/2004/02/skos/core#prefLabel> ?labelen; " +
                " <http://www.w3.org/2004/02/skos/core#broader> ?ancestor. " +
                " OPTIONAL{?iri <http://purl.org/dc/terms/source> ?source.} " +
                " OPTIONAL{?iri <http://www.w3.org/2004/02/skos/core#definition> ?definitioncs.} " +
                " OPTIONAL{?iri <http://www.w3.org/2004/02/skos/core#definition> ?definitionen.} " +
//            #sem potrebujeme dostat stereotyp, source a definici
                "     FILTER(LANGMATCHES(LANG(?labelcs), \"cs\")) " +
                "     FILTER(LANGMATCHES(LANG(?labelen), \"en\")) " +
                "     FILTER(LANGMATCHES(LANG(?definitioncs), \"cs\")) " +
                "     FILTER(LANGMATCHES(LANG(?definitionen), \"en\"))} " ;



        final Collection<String> terms = new ArrayList<String>();
        Query query = QueryFactory.create(queryString);
        QueryFactory.parse(query, queryString, "", Syntax.syntaxSPARQL_11);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet r = qexec.execSelect();


        while (r.hasNext()) {
            final QuerySolution s = r.next();
            final VocabularyTerm t = new VocabularyTerm();
            t.setLabel(s.get("labelcs").asLiteral().getLexicalForm());
//            + s.get("labelen").asLiteral().getLexicalForm()
            terms.add(t.getLabel());
            log().info("TERMS in ttl file  " + t.getLabel());
        }
        log().info("Retrieved terms from ttl file: " + terms);
        return terms;
    }

    private ModelTree getOntologyForClass(final ModelTree root) {
        if (root == null) {
            return null;
        }

        final Stereotype s = root.getStereotype(this.module.getName(), "ontouml.ontology");
        if (s != null) {
            return root;
        }

        return getOntologyForClass(root.getOwner());
    }

    private Class createTerm(String termLabel, NameSpace nameSpace) {

        IModelingSession session = module.getModuleContext().getModelingSession();
        ITransaction t = session.createTransaction("create a class");
        IUmlModel factory = session.getModel();
        Class Imudl = factory.createClass(termLabel, nameSpace);
        // createClass(termLabel, nameSpace, stereotype)
        t.commit();

        return Imudl;
    }

    private void putValue(ModelElement me) {
        IModelingSession session = module.getModuleContext().getModelingSession();
        try (ITransaction t = session.createTransaction("put a value")) {
            String nameTagValue = Optional.ofNullable(
                    me.getTagValue(this.module.getName(), TERM_NAME.getId())
            ).orElse("");
            Map<String, String> nameValues = Utils.parseLangString(nameTagValue);
            String name = Optional.ofNullable(
                    nameValues.get(ModuleConfig.DEFAULT_LANGUAGE)
            ).orElse("");

            if (!name.equals(me.getName())) {
                log().info(String.format(
                        "Updating term label '%s' into '%s'.", name, me.getName())
                );
                nameValues.put(ModuleConfig.DEFAULT_LANGUAGE, me.getName());
                me.putTagValue(this.module.getName(), TERM_NAME.getId(), Utils.buildLangString(nameValues));
            }
            t.commit();
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }
    }
}

