package cz.cvut.kbss.modelio.vocterm;


public class VocabularyTerm {
    private String iri;
    private String label;
    private String source;

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getIri() {
        return iri;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return label + " ("+iri+")";
    }
}
