package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

import java.util.HashMap;
import java.util.Set;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.modelio.api.module.IModule;

import cz.cvut.kbss.modelio.Utils;

public class SGovCompactProcessor extends SGovProcessor {
	public SGovCompactProcessor(IModule module) {
		super(module);
	}

	@Override
	protected void addTropeType(OntModel model, Resource s, Resource o) {
		model.add(s, RDFS.subPropertyOf, o);
	}

	@Override
	protected void postProcess(OntModel m) {
	}

	@Override
	protected void postProcess(OntModel m, int i, HashMap<OntResource, Set<OntResource>> map) {
		map.entrySet().forEach(e -> {
			if (!e.getKey().listRDFTypes(true).filterKeep(t -> t.getURI().contentEquals(ZSGoV.typVlastnosti)).toList().isEmpty()) {
				final DatatypeProperty p = m.createDatatypeProperty(e.getKey().getURI());
				if (i == 1) {			
					p.setDomain(Utils.setExpression(m, e));
					p.setRange(RDFS.Literal);
				}
			} else {
				final ObjectProperty p = m.createObjectProperty(e.getKey().getURI());
				if (i == 1) {			
					p.setDomain(Utils.setExpression(m, e));
				} else if (i == 2) {
					p.setRange(Utils.setExpression(m, e));
				}				
			}
		});
	}
}
