package cz.cvut.kbss.modelio.ontouml.impl;

import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyTagType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.gproject.ramc.core.packaging.IModelComponentContributor;
import org.modelio.metamodel.uml.infrastructure.NoteType;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.infrastructure.TagType;
import org.modelio.vcore.smkernel.mapi.MClass;
import org.modelio.vcore.smkernel.mapi.MObject;

class OntoUmlModuleModelComponentContributor implements IModelComponentContributor {

    private final IModuleContext context;

    OntoUmlModuleModelComponentContributor(IModuleContext context) {
        this.context = context;
    }

    @Override public Set<MObject> getElements() {
        return Collections.emptySet();
    }

    @Override public Set<NoteType> getNoteTypes() {
        return Collections.emptySet();
    }

    @Override public Set<TagType> getTagTypes() {
        final MClass vc =
            context.getModelioServices().getMetamodelService().getMetamodel()
                   .getMClass("Package");
        final Set<TagType> tagTypes = new HashSet<>();
        Arrays.stream(VocabularyTagType.values()).forEach(v ->
            tagTypes.addAll(context.getModelingSession().getMetamodelExtensions().findTagTypes(v.getId(),vc)));
        return tagTypes;
    }

    @Override public Set<Stereotype> getDependencyStereotypes() {
        return Collections.emptySet();
    }

    @Override public Set<ExportedFileEntry> getFiles() {
        return Collections.emptySet();
    }
}
