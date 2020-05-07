package cz.cvut.kbss.modelio.ontouml.export.processor;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.export.Processor;
import cz.cvut.kbss.modelio.ontouml.export.Vocabulary;
import cz.cvut.kbss.modelio.ontouml.export.VocabularyContext;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Classifier;

public class PlainProcessor implements Processor {
    private final ILogService logService;
    private final IModule module;

    public PlainProcessor(final IModule module) {
        this.logService = module.getModuleContext().getLogService();
        this.module = module;
    }

    @Override public void processClass(final Class e, final VocabularyContext context) {
        logService.info("CLASS: " + e.getName());
        addGlossaryTerm(e, context.getVocabulary(e));
        final OntModel model = context.getVocabulary(e).getModel();
        final OntClass cCurrent = Utils.getClass(getId(e, context), model);
        final Vocabulary eVoc = context.getVocabulary(e);

        e.getParent().forEach(p -> {
            final OntClass sub = Utils.getClass(getId(p.getSubType(), context), model);
            final OntClass sup = Utils.getClass(getId(p.getSuperType(), context), model);
            model.add(sub, RDFS.subClassOf, sup);
            eVoc.getGlossary().add(sub, SKOS.broader, sup);
        });
        e.getOwnedEnd().forEach(a -> {
            addGlossaryTerm(a.getAssociation(), eVoc);
            final OntProperty prop =
                Utils.getObjectProperty(getIdentifier(eVoc, a.getAssociation()), model);

            final OntResource domain = prop.getDomain();
            prop.setDomain(Utils.createUnionResource(cCurrent, domain, model));

            final Classifier opposite = a.getOpposite().getOwner();
            final OntClass cOpposite =
                Utils.getClass(getIdentifier(context.getVocabulary(opposite), opposite), model);
            prop.setRange(Utils.createUnionResource(cOpposite, prop.getRange(), model));

            // expected that outgoing associations from a class have unique names
            cCurrent.addSuperClass(model.createAllValuesFromRestriction(null, prop, cOpposite));

            addMultiplicity(cCurrent, OWL2.minQualifiedCardinality, a.getMultiplicityMin(), prop,
                cOpposite, model);
            addMultiplicity(cCurrent, OWL2.maxQualifiedCardinality, a.getMultiplicityMax(), prop,
                cOpposite, model);
        });
        logService.info("	- done. ");
    }

    private void addMultiplicity(final OntClass cCurrent, final Property cardinalityType,
                                 final String sCardinality, final OntProperty onProp,
                                 final OntClass onClass, final OntModel model) {
        try {
            int cardinality = Integer.parseInt(sCardinality);
            if (cardinality < 0) {
                throw new NumberFormatException();
            }
            final Resource restriction = model.createResource();
            restriction.addProperty(RDF.type, OWL.Restriction);
            restriction.addLiteral(cardinalityType, cardinality);
            restriction.addProperty(OWL.onProperty, onProp);
            restriction.addProperty(OWL2.onClass, onClass);
            cCurrent.addSuperClass(restriction);
        } catch (NumberFormatException ee) {
            logService.info(String
                .format(" - sCardinality %s of type %s has not been transformed", sCardinality,
                    cardinalityType));
        }
    }

    @Override public IModule getModule() {
        return module;
    }

    @Override public void postprocess(OntModel m) {
        // NOTHING TO DO
    }
}
