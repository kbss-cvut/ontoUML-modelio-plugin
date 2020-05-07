package cz.cvut.kbss.modelio.ontouml.impl;

import org.modelio.api.module.lifecycle.DefaultModuleLifeCycleHandler;
import org.modelio.api.module.lifecycle.ModuleException;

import cz.cvut.kbss.modelio.Utils;

/**
 * Implementation of the IModuleLifeCycleHandler interface.
 * <br>This default implementation may be inherited by the module developers in order to simplify the code writing of the module life cycle handler .
 */
class OntoUmlModuleLifeCycleHandler extends DefaultModuleLifeCycleHandler {

	/**
	 * Constructor.
	 * @param module the Module this life cycle handler is instantiated for.
	 */
	OntoUmlModuleLifeCycleHandler(OntoUmlModule module) {
		super(module);
	}

	@Override
	public boolean start() throws ModuleException {
		this.module.getModuleContext().getLogService().info("Starting " + Utils.getModuleIdentification(this.module));
		return super.start();
	}

	@Override
	public void stop() throws ModuleException {
		this.module.getModuleContext().getLogService().info("Stopping " + Utils.getModuleIdentification(this.module));
		super.stop();
	}

	public static boolean install(String modelioPath, String mdaPath) throws ModuleException {
		return DefaultModuleLifeCycleHandler.install(modelioPath, mdaPath);
	}
}