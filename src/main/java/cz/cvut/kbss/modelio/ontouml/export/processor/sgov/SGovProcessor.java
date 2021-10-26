package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

import cz.cvut.kbss.modelio.ontouml.export.Processor;
import cz.cvut.kbss.modelio.ontouml.export.VocabularyContext;
import cz.cvut.kbss.modelio.ontouml.language.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.SKOS;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.Class;

import cz.cvut.kbss.modelio.Utils;

public abstract class SGovProcessor implements Processor {

	private final IModule module;

//	private ontoUmlRelators ontoUML = new ontoUmlRelators();

	private Map<OntModel, List<HashMap<OntResource, Set<OntResource>>>> aritySetMap = new HashMap<>();

	SGovProcessor(final IModule module) {
		this.module = module;
	}

	void info(final String log) {
		module.getModuleContext().getLogService().info("[SGoV] " + log);
	}

	private void warning(final String log) {
		module.getModuleContext().getLogService().warning("[SGoV] " + log);
	}

	@Override
	public void processClass(Class e, final VocabularyContext context) {
		info("************************************");
		info("Processing UML Class: " + e.getName());
		addGlossaryTerm(e, context.getVocabulary(e));
		if (UfoLanguageResolverUtils.isRecognizedRelator(module, e)) {
			processRelatorType(e, true, context);
		} else if (UfoLanguageResolverUtils.isRecognizedIntrinsicTropeType(module, e)) {
			processIntrinsicTropeType(e, true, context);
		} else if (UfoLanguageResolverUtils.isRecognizedObjectType(module, e)) {
			processObjectType(e, true, context);
		} else if (UfoLanguageResolverUtils.isRecognizedEventType(module, e)) {
			processEventType(e, true, context);
		} else {
			warning("Class " + e + ", has not defined a type, considering just hierachies");
		}
		info("	- done. ");
	}



	private void processObjectType(final Class e, final boolean asserted, final VocabularyContext context) {
		info("************************************");
		info(" * processing Object type " + e);
		final OntModel model = context.getVocabulary(e).getModel();
		final OntClass p = Utils.getClass(getId(e, context), model);
		UfoLanguageResolverUtils.getRecognizedObjectTypes(module, e).forEach(t -> Utils
			.addType(context.getVocabulary(e), p, ResourceFactory.createResource(t)));
		final OntModel g = context.getVocabulary(e).getGlossary();
		final Resource gRes = ResourceFactory.createResource(context.getVocabulary(e).getMetadata().getBase().toString()+"glosář");

		Utils.addType(context.getVocabulary(e), p, ResourceFactory.createResource(ZSGoV.typObjektu));

		if (e.getParent().isEmpty()) {
			if (asserted && !(ZSGoV.typObjektu.contentEquals(p.getURI()))) {
				final Resource rx = ResourceFactory.createResource(ZSGoV.objekt);
				model.add(p, RDFS.subClassOf, rx);
				g.add(p, SKOS.broader, rx);
				g.add(gRes, SKOS.hasTopConcept, p);
				info("- processing " + p + " as a subclass of " + rx);
			}
		} else {
			e.getParent().forEach(supP -> {
				final Resource ry = Utils.getClass(getId(supP.getSuperType(), context), model);
				model.add(p, RDFS.subClassOf, ry);
				g.add(p, SKOS.broader, ry);
				processObjectType((Class) supP.getSuperType(), false, context);

				if (!(ry.getNameSpace().equals(p.getNameSpace()))) {
					g.add(gRes, SKOS.hasTopConcept, p);
				}

                info("- processing " + p + " as a subclass of " + ry);
			});
		}


        // TODO: add "má část"
		// there has to be some mereological property from Z-SGov, that not yet exists
		// looking for associations on targeting end of class e
		// e zsgov:hasPart a
//		e.getTargetingEnd().forEach(a -> {
//			if (a.getAssociation().getName().equals(ontoUmlRelators.maCast)) {
//                info("- processing incoming hasPart association " + a.getAssociation().getName());
//                // find from and relator
//                model.add(from, hasPart, to);
//            }
//		});

	}

	private void processRelatorType(final Class e, final boolean asserted, final VocabularyContext context) {
		info("************************************");
		info(" * processing Relator " + e);
		final OntModel model = context.getVocabulary(e).getModel();
		final OntResource p = Utils.getResource(getId(e, context), model);
		Utils.addType(context.getVocabulary(e), p, ResourceFactory.createResource(ZSGoV.typVztahu));
		final OntModel g = context.getVocabulary(e).getGlossary();
		final Resource gRes = ResourceFactory.createResource(context.getVocabulary(e).getMetadata().getBase().toString()+"glosář");

		if (e.getParent().isEmpty()) {
			if (asserted && !(ZSGoV.typVztahu.contentEquals(p.getURI()))) {
				final Resource r = ResourceFactory.createResource(ZSGoV.vztah);
				addTropeType(model, p, r);
				g.add(p, SKOS.broader, r);
				g.add(gRes, SKOS.hasTopConcept, p);
			}
		} else {
			e.getParent().forEach(supP -> {
				final OntResource ct = Utils.getResource(getId(supP.getSuperType(), context), model);
				addTropeType(model, p, ct);
				g.add(p, SKOS.broader, ct);
				processRelatorType((Class) supP.getSuperType(), false, context);
				if (!(ct.getNameSpace().equals(p.getNameSpace()))) {
					g.add(gRes, SKOS.hasTopConcept, p);
				}
			});
		}

		e.getOwnedEnd().forEach(a -> {
			final Pattern pattern1 = Pattern.compile("má vztažený prvek ([\\d+])");
			final Matcher matcher1 = pattern1.matcher(a.getAssociation().getName());
			if (matcher1.find()) {
				final Integer i = Integer.parseInt(matcher1.group(1));
				final OntClass oppositeClass = Utils.getClass(getId(a.getOpposite().getOwner(), context), model);
				if (!aritySetMap.containsKey(model)) {
					aritySetMap.put(model, new ArrayList<>());
				}

				info(i + " - " + e + " : " + oppositeClass);
				Utils.updateSet(Collections.singleton(Utils.getResource(getId(e, context), model)), oppositeClass,
						Utils.getSet(i, p, aritySetMap.get(model)));
			} //has inverseOf relation
			else if (a.getAssociation().getName().equals(OntoUmlRelators.maInverzniPrvek)) {
                info("- processing association " + a.getAssociation().getName());
                final OntResource source = p;
                final OntClass target = Utils.getClass(getId(a.getOpposite().getOwner(), context), model);
                model.add(source, OWL.inverseOf, target);
            } else {
				warning("	link " + a.getAssociation().getName() + " not supported.");
			}
		});
	}

	private void processIntrinsicTropeType(final Class e, final boolean asserted, final VocabularyContext context) {
		info("************************************");
		info(" * Processing Intrinsic Trope " + e);
		final OntModel model = context.getVocabulary(e).getModel();
		final OntResource p = Utils.getResource(getId(e, context), model);
		Utils.addType(context.getVocabulary(e), p, ResourceFactory.createResource(ZSGoV.typVlastnosti));
		UfoLanguageResolverUtils.getRecognizedIntrinsicTropeTypes(module, e).forEach(t -> Utils
			.addType(context.getVocabulary(e), p, ResourceFactory.createResource(t)));
		final OntModel g = context.getVocabulary(e).getGlossary();
		final Resource gRes = ResourceFactory.createResource(context.getVocabulary(e).getMetadata().getBase().toString()+"glosář");

		if (e.getParent().isEmpty()) {
			if (asserted && !(ZSGoV.typVlastnosti.contentEquals(p.getURI()))) {
				final Resource r = ResourceFactory.createResource(ZSGoV.vlastnost);
				addTropeType(model, p, r);
				g.add(p, SKOS.broader, r);
				g.add(gRes, SKOS.hasTopConcept, p);
			}
		} else {
			e.getParent().forEach(supP -> {
				final Resource r = Utils.getDataProperty(getId(supP.getSuperType(), context), model);
				addTropeType(model, p, r);
				g.add(p, SKOS.broader, r);
				processIntrinsicTropeType((Class) supP.getSuperType(), false, context);
				if (!(r.getNameSpace().equals(p.getNameSpace()))) {
					g.add(gRes, SKOS.hasTopConcept, p);
				}
			});
		}
		e.getTargetingEnd().forEach(a -> {
			info("- processing incoming association " + a.getAssociation().getName());
			if (a.getAssociation().getName().equals("má vlastnost")) {
				final OntResource from = Utils.getResource(p.getURI(), model);
				final OntModel toModel = context.getVocabulary(a.getOwner()).getModel();
				final OntResource to = Utils.getResource(getId(a.getOwner(), context), toModel);								
				update(model, to, from);
			} else {
				warning("	link " + a.getAssociation().getName() + " not supported.");
			}
		});
		e.getOwnedEnd().forEach(a -> {
			info("- processing outgoing association " + a.getAssociation().getName());
			if (a.getAssociation().getName().equals("je vlastností")) {
				final OntModel fromModel = context.getVocabulary(a.getTarget()).getModel();
				final OntResource from = Utils.getResource(getId(a.getTarget(), context), fromModel);
				final OntResource to = Utils.getResource(p.getURI(), model);
				update(model, from, to);
			} else {
				warning("	link " + a.getAssociation().getName() + " not supported.");
			}
		});
	}

	private void update(final OntModel toModel, final OntResource from, final OntResource to) {
		if (!aritySetMap.containsKey(toModel)) {
			aritySetMap.put(toModel, new ArrayList<>());
		}
		
		Utils.updateSet(Collections.singleton(to), from,
				Utils.getSet(1, to, aritySetMap.get(toModel)));
	}
	
	private void processEventType(final Class e, final boolean asserted, final VocabularyContext context) {
		info("Processing Event type " + e);
		final OntModel model = context.getVocabulary(e).getModel();
		final OntClass p = Utils.getClass(getId(e, context), model);
		Utils.addType(context.getVocabulary(e), p, ResourceFactory.createResource(ZSGoV.typUdalosti));

		if (e.getParent().isEmpty()) {
			if (asserted && !(ZSGoV.typUdalosti.contentEquals(p.getURI()))) {
				model.add(p, RDFS.subClassOf, ResourceFactory.createResource(ZSGoV.udalost));
			}
		} else {
			e.getParent().forEach(supP -> {
				model.add(p, RDFS.subClassOf, Utils.getClass(getId(supP.getSuperType(), context), model));
				processEventType((Class) supP.getSuperType(), false, context);
			});
		}
	}

	public void postprocess(final OntModel m) {
		postProcess(m);
		final List<HashMap<OntResource, Set<OntResource>>> aritySets = aritySetMap.get(m);
		if (aritySets != null) {
			for (int i = 1; i < aritySets.size() + 1; i++) {
				final HashMap<OntResource, Set<OntResource>> map = aritySets.get(i - 1);
				postProcess(m, i, map);
			}
		}
	}

	public IModule getModule() {
	    return module;
    }

	protected abstract void addTropeType(final OntModel model, final Resource s, final Resource o);

	protected abstract void postProcess(OntModel m);

	protected abstract void postProcess(OntModel m, int arity, HashMap<OntResource, Set<OntResource>> map);
}
