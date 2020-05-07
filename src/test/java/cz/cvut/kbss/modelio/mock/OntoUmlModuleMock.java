package cz.cvut.kbss.modelio.mock;

import javax.script.ScriptEngine;

import org.modelio.api.modelio.IModelioContext;
import org.modelio.api.modelio.IModelioServices;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.configuration.IModuleAPIConfiguration;
import org.modelio.api.module.context.configuration.IModuleUserConfiguration;
import org.modelio.api.module.context.i18n.I18nSupport;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.api.module.context.project.IProjectStructure;
import org.modelio.metamodel.mda.ModuleComponent;

import cz.cvut.kbss.modelio.ontouml.impl.OntoUmlModule;

public class OntoUmlModuleMock extends OntoUmlModule {
	
	public OntoUmlModuleMock(final IModuleContext c) {	
		super(c);
	}
	
	public static IModule get() {
		return new OntoUmlModuleMock(new IModuleContext() {
			
			@Override
			public void setModule(IModule arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public IProjectStructure getProjectStructure() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IModuleAPIConfiguration getPeerConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IModelioServices getModelioServices() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IModelioContext getModelioContext() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IModelingSession getModelingSession() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ModuleComponent getModel() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ILogService getLogService() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ScriptEngine getJythonEngine() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public I18nSupport getI18nSupport() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public IModuleUserConfiguration getConfiguration() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}	
}
