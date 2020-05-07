package cz.cvut.kbss.modelio.ontouml.Import;


import org.modelio.api.modelio.Modelio;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.List;

public class ImportFromOwl extends DefaultModuleCommandHandler {

    public ImportFromOwl() {
        super();
    }

    @Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        return selectedElements.size() == 1
                && selectedElements.iterator().next() instanceof Package;
    }

    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
        final ILogService logService = module.getModuleContext().getLogService();

        logService.info("import - actionPerformed( Import concepts from ttl )");
        new Importer(module).run((Package) selectedElements.iterator().next());
    }

}

