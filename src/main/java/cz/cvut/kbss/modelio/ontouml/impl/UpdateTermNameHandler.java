package cz.cvut.kbss.modelio.ontouml.impl;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.config.ModuleConfig;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.event.IModelChangeEvent;
import org.modelio.api.modelio.model.event.IModelChangeHandler;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.mmextensions.infrastructure.ExtensionNotFoundException;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.vcore.smkernel.mapi.MObject;

import java.util.Map;
import java.util.Optional;

import static cz.cvut.kbss.modelio.ontouml.metadata.term.TermTagType.TERM_NAME;

public class UpdateTermNameHandler implements IModelChangeHandler {

    private ILogService log;
    private IModule module;

    public UpdateTermNameHandler(IModule module) {
        log = module.getModuleContext().getLogService();
        this.module = module;
    }

    @Override
    public void handleModelChange(IModelingSession iModelingSession, IModelChangeEvent iModelChangeEvent) {
        log.info(String.format("Handle model change %s : %s", iModelChangeEvent.getCreationEvents(),
                iModelChangeEvent.getUpdateEvents()));
        iModelChangeEvent.getUpdateEvents().stream()
                .filter(this::isTermLabelHolder)
                .forEach(mo -> updateTermName((ModelElement) mo));
    }

    private boolean isTermLabelHolder(MObject el) {
        return (el instanceof org.modelio.metamodel.uml.statik.Class);
    }

    private void updateTermName(ModelElement me) {
        IModelingSession session = module.getModuleContext().getModelingSession();
        String transactionName = "updating term name of element " + me.getUuid();

        try (ITransaction t = session.createTransaction(transactionName)) {

            String nameTagValue = Optional.ofNullable(
                    me.getTagValue(this.module.getName(), TERM_NAME.getId())
            ).orElse("");
            Map<String, String> nameValues = Utils.parseLangString(nameTagValue);
            String name = Optional.ofNullable(
                    nameValues.get(ModuleConfig.DEFAULT_LANGUAGE)
            ).orElse("");

            if (!name.equals(me.getName())) {
                log.info(String.format(
                        "Updating term label '%s' into '%s'.", name, me.getName())
                );
                nameValues.put(ModuleConfig.DEFAULT_LANGUAGE, me.getName());
                me.putTagValue(this.module.getName(), TERM_NAME.getId(), Utils.buildLangString(nameValues));
            }
            t.commit();
        } catch (ExtensionNotFoundException e) {
            e.printStackTrace();
        }

    }
}
