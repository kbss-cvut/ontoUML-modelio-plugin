package cz.cvut.kbss.modelio.ontouml.metadata.term;

import cz.cvut.kbss.modelio.Utils;
import cz.cvut.kbss.modelio.ontouml.metadata.GeneralMetadata;
import java.util.Map;
import org.modelio.api.module.IModule;
import org.modelio.metamodel.uml.infrastructure.ModelElement;

public class TermMetadata extends GeneralMetadata<TermTagType> {

	private TermMetadata(final Map<TermTagType, String> tagValues) {
		super(tagValues);
	}

	public String getNazev() {
		return get(TermTagType.TERM_NAME);
	}

	public String getKontextualniNazev() {
		return get(TermTagType.TERM_CONTEXT_BASED_NAME);
	}

	public String getVyhledavaciIndex() {
		return get(TermTagType.TERM_SEARCH_INDEX);
	}

	public String getDefinice() {
		return get(TermTagType.TERM_MEANING_DEFINITION);
	}

	public String getVymezeniVyznamuPojmu() {
		return get(TermTagType.TERM_MEANING_CONSTRAINT);
	}

	public String[] getOdkazNaVymezeniVyznamuVLegislative() {
		return Utils
			.parseCommaSeparatedStrings(get(TermTagType.TERM_MEANING_CONSTRAINT_SOURCE));
	}

	public String[] getOdkazNaVybraneMistoVLegislative() {
		return Utils
			.parseCommaSeparatedStrings(get(TermTagType.TERM_MEANING_DEFINITION_SOURCE));
	}

	public String[] getSpecializujeExterniPojmy() {
		return Utils
			.parseCommaSeparatedStrings(get(TermTagType.TERM_SPECIALIZES_EXTERNAL_TERMS));
	}

	public static TermMetadata create(final ModelElement root, final IModule module) {
		return create(GeneralMetadata.create(root,module,TermTagType.class));
	}

	static TermMetadata create(Map<TermTagType,String> tagTypeMap) {
		return new TermMetadata(tagTypeMap);
	}
}
