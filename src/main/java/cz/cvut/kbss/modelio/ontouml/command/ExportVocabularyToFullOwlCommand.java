package cz.cvut.kbss.modelio.ontouml.command;

import cz.cvut.kbss.modelio.ontouml.export.ExportType;
import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import cz.cvut.kbss.modelio.ontouml.export.VocabularyExporter;

/**
 * Implementation of the IModuleContextualCommand interface.
 * <br>The module contextual commands are displayed in the contextual menu and in the specific toolbar of each module property page.
 * <br>The developer may inherit the DefaultModuleContextualCommand class which contains a default standard contextual command implementation.
 *
 */
public class ExportVocabularyToFullOwlCommand extends AbstractExportVocabularyCommand {
	
    /**
     * @see org.modelio.api.module.command.DefaultModuleCommandHandler#actionPerformed(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    @Override
    public void _actionPerformed(List<MObject> selectedElements, IModule module) {
        new VocabularyExporter(module).exportVocabulary((Package) selectedElements.iterator().next(), ExportType.FULL);
    }
}
