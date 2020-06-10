package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.statik.NameSpace;

import cz.cvut.kbss.modelio.Utils;

class UfoLanguageResolverUtils {
	private final IModule module;

	private UfoLanguageResolverUtils(final IModule module) {
		this.module = module;
	}

	private boolean hasStereotypeInHierarchy(final NameSpace c, final UfoLanguageStereotype... stereotypes) {
		return Arrays.stream(stereotypes).anyMatch(s -> Utils.hasStereotypeInHierarchy(c, module, s.getId()));
	}

	static boolean isRecognizedEventType(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).isRecognizedEventType(c);
	}

	private boolean isRecognizedEventType(final NameSpace c) {
		return hasStereotypeInHierarchy(c, UfoLanguageStereotype.EVENT_TYPE);
	}

	static boolean isRecognizedRelator(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).isRecognizedRelator(c);
	}
 
	private boolean isRecognizedRelator(final NameSpace c) {
		return hasStereotypeInHierarchy(c, UfoLanguageStereotype.RELATOR_TYPE);
	}

	private static Map<UfoLanguageStereotype,String> getIntrinsicTropeTypeMap() {
		final Map<UfoLanguageStereotype,String> map = new EnumMap<>(UfoLanguageStereotype.class);
		map.put(UfoLanguageStereotype.QUALITYTYPE, ZSGoV.get("typ-kvalitativní-vlastnosti"));
		map.put(UfoLanguageStereotype.MODETYPE, ZSGoV.get("typ-modální-vlastnosti"));
		map.put(UfoLanguageStereotype.ASPECTTYPE, ZSGoV.get("typ-vlastnosti"));

		return map;
	}
	
	
	static boolean isRecognizedIntrinsicTropeType(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).hasStereotypeInHierarchy(c, getIntrinsicTropeTypeMap().keySet().toArray(new UfoLanguageStereotype[0]));
	}
	
	static List<String> getRecognizedIntrinsicTropeTypes(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).getRecognizedTypes(c,getIntrinsicTropeTypeMap());
	}
	
	private static Map<UfoLanguageStereotype,String> getObjectTypeMap() {
		final Map<UfoLanguageStereotype,String> map = new EnumMap<>(UfoLanguageStereotype.class);
		map.put(UfoLanguageStereotype.KIND, ZSGoV.get("druh"));
		map.put(UfoLanguageStereotype.SUBKIND, ZSGoV.get("poddruh"));
		map.put(UfoLanguageStereotype.MIXIN, ZSGoV.get("mixin"));
		map.put(UfoLanguageStereotype.ROLE_MIXIN, ZSGoV.get("mixin-rolí"));
		map.put(UfoLanguageStereotype.PHASE_MIXIN, ZSGoV.get("mixin-fází"));
		map.put(UfoLanguageStereotype.ROLE, ZSGoV.get("role"));
		map.put(UfoLanguageStereotype.CATEGORY, ZSGoV.get("kategorie"));
		map.put(UfoLanguageStereotype.PHASE, ZSGoV.get("fáze"));
		map.put(UfoLanguageStereotype.OBJECT_TYPE, ZSGoV.get("typ-objektu"));
		return map;
	}
	
	static boolean isRecognizedObjectType(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).hasStereotypeInHierarchy(c, getObjectTypeMap().keySet().toArray(new UfoLanguageStereotype[0]));
	}
	
	static List<String> getRecognizedObjectTypes(final IModule module, final NameSpace c) {
		return new UfoLanguageResolverUtils(module).getRecognizedTypes(c,getObjectTypeMap());
	}

	private List<String> getRecognizedTypes(final NameSpace c, final Map<UfoLanguageStereotype,String> map) {
		final List<String> list = new ArrayList<>();
		
		for(final UfoLanguageStereotype stereotype : map.keySet()) {
			if (c.getStereotype(module.getName(), stereotype.getId()) != null) {
				list.add(map.get(stereotype));
			}
		}
		
		return list;
	}
}
