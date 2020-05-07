package cz.cvut.kbss.modelio.ontouml.impl;

import cz.cvut.kbss.modelio.ontouml.metadata.vocabulary.VocabularyTagType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.modelio.api.modelio.IModelioServices;
import org.modelio.api.modelio.meta.IMetamodelService;
import org.modelio.api.modelio.model.IMetamodelExtensions;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.metamodel.uml.infrastructure.TagType;
import org.modelio.vcore.smkernel.mapi.MClass;
import org.modelio.vcore.smkernel.mapi.MMetamodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestOntoUmlModuleModelComponentContributor {

    @Test void testExportsAllVocabularyTagTypes() {
        final IModuleContext ctx = mock(IModuleContext.class);
        final IModelioServices svcs = mock(IModelioServices.class);
        final IMetamodelService svc = mock(IMetamodelService.class);
        final MMetamodel mm = mock(MMetamodel.class);
        when(ctx.getModelioServices()).thenReturn(svcs);
        when(svcs.getMetamodelService()).thenReturn(svc);
        when(svc.getMetamodel()).thenReturn(mm);
        when(mm.getMClass(any(String.class))).thenReturn(mock(MClass.class));

        final IModelingSession ms = mock(IModelingSession.class);
        when(ctx.getModelingSession()).thenReturn(ms);

        final IMetamodelExtensions mme = mock(IMetamodelExtensions.class);
        when(ms.getMetamodelExtensions()).thenReturn(mme);

        final List<TagType> ttList = Arrays.stream(VocabularyTagType.values()).map(vtt -> {
            final TagType tt = mock(TagType.class);
            when(tt.getLabelKey()).thenReturn(vtt.getId());
            return tt;
        }).collect(Collectors.toList());
        when(mme.findTagTypes(any(),any())).thenReturn(ttList);

        // add another unspecified tag type
        final TagType tt = mock(TagType.class);
        ttList.add(tt);
        when(mme.findTagTypes(any(),any())).thenReturn(ttList);

        final OntoUmlModuleModelComponentContributor m = new OntoUmlModuleModelComponentContributor(ctx);
        final List<String> tagNames = m.getTagTypes().stream()
                                       .map(TagType::getLabelKey).collect(Collectors.toList());

        assertEquals(VocabularyTagType.values().length, Arrays.stream(VocabularyTagType.values())
                                                              .filter( vt ->
                                                                  tagNames.contains(vt.getId())).count()
        );
    }
}
