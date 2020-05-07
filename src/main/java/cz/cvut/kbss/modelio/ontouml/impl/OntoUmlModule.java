package cz.cvut.kbss.modelio.ontouml.impl;

import cz.cvut.kbss.modelio.Utils;
import org.modelio.api.module.AbstractJavaModule;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.api.module.lifecycle.IModuleLifeCycleHandler;
import org.modelio.api.module.parameter.IParameterEditionModel;
import org.modelio.gproject.ramc.core.model.IModelComponent;
import org.modelio.gproject.ramc.core.packaging.IModelComponentContributor;

/**
 * Implementation of the IModule interface.
 * <br>All Modelio java modules should inherit from this class.
 */
public class OntoUmlModule extends AbstractJavaModule {

	private OntoUmlModulePeerModule peerModule;

	private OntoUmlModuleLifeCycleHandler lifeCycleHandler;

	@Override
	public OntoUmlModulePeerModule getPeerModule() {
		return this.peerModule;
	}

	/**
	 * Return the lifecycle handler attached to the current module.
	 * <p>
	 * <p>
	 * This handler is used to manage the module lifecycle by declaring the
	 * desired implementation on start, select... methods.
	 */
	@Override
	public IModuleLifeCycleHandler getLifeCycleHandler() {
		return this.lifeCycleHandler;
	}

	/**
	 * Method automatically called just after the creation of the module.
	 * <p>
	 * <p>
	 * The module is automatically instantiated at the beginning of the MDA
	 * lifecycle and constructor implementation is not accessible to the module
	 * developer.
	 * <p>
	 * <p>
	 * The <code>init</code> method allows the developer to execute the desired initialization code at this step. For
     * example, this is the perfect place to register any IViewpoint this module provides.
	 *
	 *
	 * @see org.modelio.api.module.AbstractJavaModule#init()
	 */
	@Override
	public void init() {
	    super.init();
	    final ILogService log = this.getModuleContext().getLogService();
		log.info(String
            .format("Initializing %s", Utils.getModuleIdentification(this)));
		this.getModuleContext().getModelingSession().addModelHandler((arg0, arg1) ->
			log.info(String.format("Handle model change %s : %s", arg1.getCreationEvents(),
                arg1.getUpdateEvents()))
		);
		this.getModuleContext().getModelingSession().addModelHandler(new UpdateTermNameHandler(this));
	}

    /**
     * Method automatically called just before the disposal of the module.
     * <p>
     * <p>
     * 
     * 
     * The <code>uninit</code> method allows the developer to execute the desired un-initialization code at this step.
     * For example, if IViewpoints have been registered in the {@link #init()} method, this method is the perfect place
     * to remove them.
     * <p>
     * <p>
     * 
     * This method should never be called by the developer because it is already invoked by the tool.
     * 
     * @see org.modelio.api.module.AbstractJavaModule#uninit()
     */
    @Override
    public void uninit() {
        this.getModuleContext().getLogService().info(
            String.format("Unitializing %s", Utils.getModuleIdentification(this)));
        super.uninit();
    }
    
	/**
	 * Builds a new module.
	 * <p>
	 * <p>
	 * This constructor must not be called by the user. It is automatically
	 * invoked by Modelio when the module is installed, selected or started.
	 * @param moduleContext context of the module, needed to access Modelio features.
	 */
	public OntoUmlModule(IModuleContext moduleContext) {
		super(moduleContext);
		this.lifeCycleHandler = new OntoUmlModuleLifeCycleHandler(this);
		this.peerModule = new OntoUmlModulePeerModule(this, moduleContext.getPeerConfiguration());
	}

	/**
	 * @see org.modelio.api.module.AbstractJavaModule#getParametersEditionModel()
	 */
	@Override
	public IParameterEditionModel getParametersEditionModel() {
	    if (this.parameterEditionModel == null) {
	        this.parameterEditionModel = super.getParametersEditionModel();
	    }
		return this.parameterEditionModel;
	}

	@Override
    public String getModuleImagePath() {
        return "/res/icons/module_16.png";
    }

	public IModelComponentContributor getModelComponentContributor(IModelComponent mc)
	{
		return new OntoUmlModuleModelComponentContributor(this.getModuleContext());
	}
}
