package cz.cvut.kbss.modelio.ontouml.export;

import cz.cvut.kbss.modelio.ontouml.export.processor.PlainProcessor;
import cz.cvut.kbss.modelio.ontouml.export.processor.sgov.SGovCompactProcessor;
import cz.cvut.kbss.modelio.ontouml.export.processor.sgov.SGovFullProcessor;
import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyMetadata;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.ClassDiagram;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Instance;
import org.modelio.metamodel.uml.statik.Package;

import cz.cvut.kbss.modelio.Utils;

public class VocabularyExporter {
	private static final String VOCABULARY = "type.vocabulary";

	private final IModule module;

	private Set<Vocabulary> exportedVocabularies = new HashSet<>();
	private Map<ModelTree, Vocabulary> vocabularies = new HashMap<>();

	private Processor exporter;
	
	public VocabularyExporter(final IModule module) {
		this.module = module;
	}

	private void info(final String log) {
		logService().info("[SGoV] " + log);
	}

	private ILogService logService() {
		return module.getModuleContext().getLogService();
	}
	
	private boolean isVocabulary(final ModelTree p) {
		return p.getStereotype(module.getName(), VOCABULARY) != null;
	}
	
	public void exportVocabulary(final Package vocabulary, final ExportType exportType) {
		info("Exporting " + vocabulary);
		
		switch (exportType) {
			case PLAIN : exporter = new PlainProcessor( module ); break;
			case COMPACT : exporter = new SGovCompactProcessor( module ); break;
			case FULL : exporter = new SGovFullProcessor( module ); break;
		}

		final Path path = module.getModuleContext().getProjectStructure().getPath();

		processPackage(vocabulary);
		info("Saving " + vocabulary);
		for (final Vocabulary v : exportedVocabularies) {
			final VocabularyMetadata metadata = v.getMetadata();
			info("	- postprocessing " + v.hashCode());
			exporter.postprocess(v.getModel());
			info(" 	- saving glossary");
			Utils.saveOntology(logService(), path, v.getGlossary(), metadata.getPrefix() + "-glosář");
			info(" 	- saving model");
			Utils.saveOntology(logService(), path, v.getModel(), metadata.getPrefix() + "-model");
			info(" 	- saving vocabulary");
			Utils.saveOntology(logService(), path, v.getVocabulary(),
					metadata.getPrefix() + "-slovník");
			info(" 	- saving diagrams metadata");
			Utils.saveOntology(logService(), path, v.getDiagram(),metadata.getPrefix() + "-diagram");
		}
	}

	private void processPackage(final Package root) {
		info("Processing package: " + root.getName());
		if (isVocabulary(root)) {
			exportedVocabularies.add(getVocabularyForPackage(root));
		}
		final VocabularyContext vc = e -> {
            ModelTree mt;
            if (e instanceof ModelTree) {
                mt = (ModelTree) e;
            } else if (e instanceof Instance) {
                mt = ((Instance) e).getOwner();
            } else {
                throw new IllegalArgumentException("Unknown type of element " + e);
            }

            return getVocabularyForPackage(Utils.getAncestorByStereotype(mt, module, VOCABULARY));
        };
		
		root.getOwnedElement().forEach((e) -> {
			if (e instanceof Package) {
				processPackage((Package) e);
			} else if (e instanceof Instance) {
				exporter.processInstance((Instance) e,vc);
			} else if (e instanceof Class) {
				exporter.processClass((Class) e, vc);
			}
		});

		root.getProduct().forEach((p) -> {
		    if (p instanceof ClassDiagram) {
                exporter.processDiagram((ClassDiagram) p, vc);
            }
		});
	}

	private Vocabulary getVocabularyForPackage(final ModelTree pkg) {
		if (!this.vocabularies.containsKey(pkg)) {
			info("Registering new vocabulary for package " + pkg);
			try {
				this.vocabularies.put(pkg, new Vocabulary(VocabularyMetadata.create(pkg, module)));
			} catch (InvalidMetadataException e) {
				e.printStackTrace();
				return null;
			}
		}
		return this.vocabularies.get(pkg);
	}
}