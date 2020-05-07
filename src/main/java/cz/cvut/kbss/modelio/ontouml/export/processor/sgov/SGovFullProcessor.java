package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

import java.util.HashMap;
import java.util.Set;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.modelio.api.module.IModule;

import cz.cvut.kbss.modelio.Utils;

public class SGovFullProcessor extends SGovProcessor {
	public SGovFullProcessor(IModule module) {
		super(module);
	}

	@Override
	protected void addTropeType(OntModel model, Resource s, Resource o) {
		model.add(s, RDFS.subClassOf, o);
	}
	
	@Override
	protected void postProcess(OntModel m) {
		final OntProperty pMaVztazenyPrvek = Utils.getObjectProperty(ZSGoV.maVztazenyPrvek,m);
		pMaVztazenyPrvek.addInverseOf(Utils.getObjectProperty(ZSGoV.jeVeVztahu, m));
		
		final OntProperty pJeVlastnosti = Utils.getObjectProperty(ZSGoV.jeVlastnosti, m);
		pJeVlastnosti.addInverseOf(Utils.getObjectProperty(ZSGoV.maVlastnost, m));							
	}
	
	@Override
	protected void postProcess(OntModel m, int arity, HashMap<OntResource, Set<OntResource>> map) {
		final int x = arity;
		info("**************************** " + x + " : " + map.entrySet());
		map.entrySet().forEach(e -> {
			if (!e.getKey().listRDFTypes(true).filterKeep(t -> t.getURI().contentEquals(ZSGoV.typVlastnosti)).toList().isEmpty()) {
				m.add(m.createClass(e.getKey().getURI()), RDFS.subClassOf, m.createAllValuesFromRestriction(
						null, Utils.getObjectProperty(ZSGoV.jeVlastnosti, m), Utils.setExpression(m, e)));
				m.add(m.createClass(e.getKey().getURI()), RDFS.subClassOf, m.createSomeValuesFromRestriction(
						null, Utils.getObjectProperty(ZSGoV.jeVlastnosti, m), Utils.setExpression(m, e)));
			} else {
				final OntProperty p = Utils.getObjectProperty(ZSGoV.getMaVztazenyPrvek(x), m);
				final OntProperty pp = Utils.getObjectProperty(ZSGoV.maVztazenyPrvek,m);
				pp.addSubProperty(p);
				
				m.add(m.createClass(e.getKey().getURI()), RDFS.subClassOf,
						m.createAllValuesFromRestriction(null,
								p,
								Utils.setExpression(m, e)));
				m.add(m.createClass(e.getKey().getURI()), RDFS.subClassOf,
						m.createSomeValuesFromRestriction(null,
								p,
								Utils.setExpression(m, e)));
			}
		});
	}
}
