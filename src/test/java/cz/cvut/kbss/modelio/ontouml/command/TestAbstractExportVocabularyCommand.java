package cz.cvut.kbss.modelio.ontouml.command;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class TestAbstractExportVocabularyCommand {

	private AbstractExportVocabularyCommand c;

	@BeforeEach
	void init() {
		c = new AbstractExportVocabularyCommand() {
			@Override public void _actionPerformed(List<MObject> selectedElements, IModule module) {}
		};
	}

	@Test
    void testRejectEmpty() {
		List<MObject> selectedElements = new ArrayList<>();
		assertFalse(c.accept(selectedElements, null));
    }

	@Test
	void testAcceptSingleNonPackage() {
		List<MObject> selectedElements = new ArrayList<>();
		selectedElements.add(mock(MObject.class));
		assertFalse(c.accept(selectedElements, null));
	}

	@Test
	void testAcceptSinglePackage() {
		List<MObject> selectedElements = new ArrayList<>();
		selectedElements.add(mock(Package.class));
		assertTrue(c.accept(selectedElements, null));
	}

	@Test
	void testRejectMultiple() {
		List<MObject> selectedElements = new ArrayList<>();
		selectedElements.add(mock(MObject.class));
		selectedElements.add(mock(MObject.class));
		assertFalse(c.accept(selectedElements, null));
	}
}
