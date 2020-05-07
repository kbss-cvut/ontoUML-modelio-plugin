package cz.cvut.kbss.modelio.ontouml.export.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.cvut.kbss.modelio.ontouml.export.InvalidMetadataException;
import cz.cvut.kbss.modelio.ontouml.export.Processor;
import cz.cvut.kbss.modelio.ontouml.export.Vocabulary;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.statik.Association;
import org.modelio.metamodel.uml.statik.AssociationEnd;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Instance;

import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyMetadata;

@ExtendWith(MockitoExtension.class)
public class TestPlainProcessor {

	private Processor processor;
	
	private Vocabulary vocabulary;
	
	public static Class getIsolatedClass() {
		final Class c = mock(Class.class);
		when(c.getName()).thenReturn("Test C");
		when(c.getParent()).thenReturn(mock(EList.class));
		when(c.getOwnedEnd()).thenReturn(mock(EList.class));
		return c;
	}
	
	public static Instance getSingleInstance() {
		final Instance i = mock(Instance.class);
		when(i.getName()).thenReturn("Test I");	
		final Class c = mock(Class.class);
		when(c.getName()).thenReturn("Test C");
		when(i.getBase()).thenReturn(c);		
		return i;
	}
	
	public static Class getSingleAssociationClass() {
		final Class c = mock(Class.class);
		when(c.getName()).thenReturn("Test C");
		when(c.getParent()).thenReturn(mock(EList.class));
		
		final Class c2 = mock(Class.class);
		when(c2.getName()).thenReturn("Test C2");

		final EList<AssociationEnd> list = new BasicEList<AssociationEnd>();
		final AssociationEnd a = mock(AssociationEnd.class);
		list.add(a);
		when(c.getOwnedEnd()).thenReturn(list);
		
		final AssociationEnd oppositeAE = mock(AssociationEnd.class);

		final Association as = mock(Association.class);
		when(a.getAssociation()).thenReturn(as);
		when(a.getOpposite()).thenReturn(oppositeAE);
		when(oppositeAE.getOwner()).thenReturn(c2);
		when(as.getName()).thenReturn("Test P");		
		
		return c;
	}

	@BeforeEach
	public void init() throws InvalidMetadataException {
		final IModule module = mock(IModule.class);
		final IModuleContext moduleContext = mock(IModuleContext.class);
		when(module.getModuleContext()).thenReturn(moduleContext);
		when(moduleContext.getLogService()).thenReturn(mock(ILogService.class));
		
		processor = new PlainProcessor(module);		
		
		final VocabularyMetadata metadata = mock(VocabularyMetadata.class);
		when(metadata.getImports()).thenReturn(new String[] {"http://test.org/testimport"});
		when(metadata.getAuthors()).thenReturn("");
		when(metadata.getBase()).thenReturn("http://test.org/test/");
		when(metadata.getDescription()).thenReturn("Test description");
		when(metadata.getLicense()).thenReturn("Test License");
		when(metadata.getPrefix()).thenReturn("tst");
		when(metadata.getTitle()).thenReturn("Test title");
		when(metadata.getVersion()).thenReturn("Test version");
		
		vocabulary = new Vocabulary(metadata);
	}

	@Test
	public void testGlossaryContainsConceptForSingleClass() {
		final Class c = getIsolatedClass();
		processor.processClass(c, (mt) -> vocabulary);
		Assertions.assertEquals(1,vocabulary.getGlossary().listResourcesWithProperty(RDF.type,
			SKOS.Concept).toList().size());
	}

	@Test
	public void testGlossaryContainsConceptsForSingleAssociationAndItsSource() {
		final Class c = getSingleAssociationClass();
		processor.processClass(c, (mt) -> vocabulary);
		Assertions.assertEquals(2,vocabulary.getGlossary().listResourcesWithProperty(RDF.type,
			SKOS.Concept).toList().size());
	}

	@Test
	public void testGlossaryContainsConceptForSingleIndividual() {
		final Instance c = getSingleInstance();
		processor.processInstance(c, (mt) -> vocabulary);
		Assertions.assertEquals(1,vocabulary.getGlossary().listResourcesWithProperty(RDF.type,
			SKOS.Concept).toList().size());
	}

	@Test
	public void testModelContainsIsolatedClass() {
		processor.processClass(getIsolatedClass(), (mt) -> vocabulary);		
		Assertions.assertEquals(1,vocabulary.getModel().listClasses().toList().size());
		Assertions.assertEquals(0,vocabulary.getModel().listObjectProperties().toList().size());		
	}	
	
	@Test
	public void testModelContainsSingleAssociation() {
		processor.processClass(getSingleAssociationClass(), (mt) -> vocabulary);
		// 2 classes, 1 value restriction
		Assertions.assertEquals(2,vocabulary.getModel().listClasses().filterKeep(RDFNode
			::isURIResource).toList().size());
		Assertions.assertEquals(1,vocabulary.getModel().listObjectProperties().toList().size());		
	}

	@Test
	public void testModelContainsSingleInstance() {
		processor.processInstance(getSingleInstance(), (mt) -> vocabulary);		
		Assertions.assertEquals(1,vocabulary.getModel().listClasses().filterKeep(RDFNode
			::isURIResource).toList().size());
		Assertions.assertEquals(1,vocabulary.getModel().listIndividuals().toList().size());		
	}
}
