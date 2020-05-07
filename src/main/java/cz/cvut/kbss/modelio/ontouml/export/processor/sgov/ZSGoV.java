package cz.cvut.kbss.modelio.ontouml.export.processor.sgov;

class ZSGoV {

	public static String BASE = "https://slovník.gov.cz/základní/";
//	private static String BASE = "https://onto.fel.cvut.cz/ontologies/ufo/";
	private static String BASE_POJEM = BASE+"pojem/";

	static final String typObjektu = get("typ-objektu");

	static final String typVztahu = get("typ-vztahu");
	static final String typVlastnosti = get("typ-vlastnosti");
	static final String typUdalosti = get("typ-události");
	
	static final String udalost = get("událost");
	static final String vlastnost = get("vlastnost");
	static final String vztah = get("vztah");
	static final String objekt = get("objekt");

	static final String maVztazenyPrvek = get("má-vztažený-prvek");
	static final String maVlastnost = get("má-vlastnost");
	static final String jeVlastnosti = get("je-vlastností");
	static final String jeVeVztahu = get("je-ve-vztahu");
	static final String máÚčastníka = get("má-účastníka");

	static final String maInverziPrvek = get("má-inverzní-prvek");

	static final String getMaVztazenyPrvek(final int i) {
		return BASE_POJEM + "má-vztažený-prvek-" + i;
	}

	public static final String get(final String localName) {
		return BASE_POJEM + localName;
	}
}
