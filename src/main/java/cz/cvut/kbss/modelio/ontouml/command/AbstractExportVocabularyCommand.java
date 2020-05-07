package cz.cvut.kbss.modelio.ontouml.command;

import java.util.List;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

/**
 * Implementation of the IModuleContextualCommand interface.
 * <br>The module contextual commands are displayed in the contextual menu and in the specific toolbar of each module property page.
 * <br>The developer may inherit the DefaultModuleContextualCommand class which contains a default standard contextual command implementation.
 *
 */
public abstract class AbstractExportVocabularyCommand extends DefaultModuleCommandHandler {

    /**
     * @see org.modelio.api.module.command.DefaultModuleCommandHandler#accept(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
    	if (selectedElements.size() !=1) {
    		return false;
    	}
    	
		return selectedElements.get(0) instanceof Package;
	}

    public abstract void _actionPerformed(List<MObject> selectedElements, IModule module);
    
    /**
     * @see org.modelio.api.module.command.DefaultModuleCommandHandler#actionPerformed(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
    	try {
    		this._actionPerformed(selectedElements, module);
    	} catch(Exception e) {
			module.getModuleContext().getLogService().error(e);
    	}
    }
}
