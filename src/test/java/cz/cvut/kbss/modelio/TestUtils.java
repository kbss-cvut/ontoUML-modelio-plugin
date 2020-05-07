package cz.cvut.kbss.modelio;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.UnionClass;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelTree;
import org.modelio.metamodel.uml.infrastructure.Stereotype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestUtils {

	@Test
	void testGetAncestorByStereotypeActualNode() {
		final ModelTree root = mock(ModelTree.class);
		final IModule module = mock(IModule.class);
		when(module.getName()).thenReturn("TestModule");

		final Stereotype s = mock(Stereotype.class);
		when(root.getStereotype(module.getName(), "TestStereotype")).thenReturn(s);

		assertEquals(root,Utils.getAncestorByStereotype(root,module,"TestStereotype"));
	}

	@Test
	void testGetAncestorByStereotypeNullRoot() {
		assertNull(Utils.getAncestorByStereotype(null,mock(IModule.class),"TestStereotype"));
	}

	@Test
	void testParseSingleLangString() {
		assertEquals(Collections.singletonMap("cs", "Test"), Utils.parseLangString("Test", "cs"));
	}

	@Test
	void testParseSingleLangQuotedString() {
		assertEquals(Collections.singletonMap("cs", "Test"), Utils.parseLangString("\"Test\"@cs", "cs"));
	}
	
	@Test
	void testParseDoubleLangQuotedString() {
		final Map<String,String> map = new HashMap<>();
		map.put("cs", "TestCs");
		map.put("en", "TestEn");
		assertEquals(map, Utils.parseLangString("\"TestCs\"@cs,\"TestEn\"@en", "cs"));
	}

	@Test
	void testCreateUnionResourceReturnsTheSameResourceIfSingle() {
		final OntModel m = ModelFactory.createOntologyModel();
		final OntResource resource = m.createOntResource("http://test.org/1");
		assertEquals(resource,Utils.createUnionResource(resource, null, null));
	}

	@Test
	void testCreateUnionResourceReturnsANewUnionClass() {
		final OntModel m = ModelFactory.createOntologyModel();
		final OntResource resource = m.createOntResource("http://test.org/1");
		final OntResource resource2 = m.createOntResource("http://test.org/2");
		assertTrue(Utils.createUnionResource(resource, resource2, m).canAs(UnionClass.class));
	}

	@Test
	void testBuildLangStringMap() {
		final Map<String,String> map = new HashMap<>();
		map.put("cs", "TestCs-CsSuffix");
		map.put("en", "TestEn-EnSuffix");
		
		final Map<String,String> langToValue = new HashMap<>();
		langToValue.put("cs", "TestCs");
		langToValue.put("en", "TestEn");
		
		final Map<String,String> langToSuffix = new HashMap<>();
		langToSuffix.put("cs", "-CsSuffix");
		langToSuffix.put("en", "-EnSuffix");
		assertEquals(map, Utils.buildLangStringMap(langToValue, langToSuffix));
	}
	
	@Test
	void testBuildLangStringMapMissingPrefix() {
		final Map<String,String> map = new HashMap<>();
		map.put("cs", "TestCs-CsSuffix");
		
		final Map<String,String> langToValue = new HashMap<>();
		langToValue.put("cs", "TestCs");
		
		final Map<String,String> langToSuffix = new HashMap<>();
		langToSuffix.put("cs", "-CsSuffix");
		langToSuffix.put("en", "-EnSuffix");
		assertEquals(map, Utils.buildLangStringMap(langToValue, langToSuffix));
	}

	@Test
	void testParseCommaSeparatedStringsSimple() {
		assertEquals(Arrays.asList("ahoj","nazdar"),Arrays.asList(Utils.parseCommaSeparatedStrings("ahoj,nazdar")));
	}

	@Test
	void testParseCommaSeparatedStringsWithSpaces() {
		assertEquals(Arrays.asList("ahoj","nazdar"),Arrays.asList(Utils.parseCommaSeparatedStrings("  ahoj ,     nazdar   ")));
	}

	@Test
	void testBuildLangStringMapMissingSuffix() {
		final Map<String,String> map = new HashMap<>();
		map.put("cs", "TestCs-CsSuffix");
		map.put("en", "TestEn");
		
		final Map<String,String> langToValue = new HashMap<>();
		langToValue.put("cs", "TestCs");
		langToValue.put("en", "TestEn");
		
		final Map<String,String> langToSuffix = new HashMap<>();
		langToSuffix.put("cs", "-CsSuffix");
		assertEquals(map, Utils.buildLangStringMap(langToValue, langToSuffix));
	}

	@Test
	void testBuildLangString() {
		String langString = "\"TestCs\"@cs,\"TestEn\"@en";

		final Map<String,String> langToValue = new HashMap<>();
		langToValue.put("cs", "TestCs");
		langToValue.put("en", "TestEn");

		assertEquals(langString, Utils.buildLangString(langToValue));
	}

	@Test
	void testAssignIRIs() {
		final Model m = ModelFactory.createDefaultModel();
		final Property p = m.createProperty("http://test.org/p1");
		final Resource r = m.createResource("http://test.org/i1");
		
		Utils.assignIRIs(new String[]{"http://test.org/t1","http://test.org/t2"}, p, r);
		
		assertEquals(2, r.listProperties(p).toSet().size() );
	}

	@ParameterizedTest
	@MethodSource(value="testNormalizeName")
	void testNormalizeName(final String before, final String after) {
	    assertEquals(after, Utils.normalizeName(before));
	}

	static Stream<Arguments> testNormalizeName() {
		return Stream.of(
	            Arguments.of("test", "test"),
	            Arguments.of("test Class", "test-class")
	            );
	}
}
