# OntoUML+ Modelio Module

OntoUML+ Modelio Module is a plugin into the Modelio system that allows to create UML models compliant with the Unified Foundational Ontology (UFO) and express it in the Web Ontology Language (OWL) version of UFO.

OntoUML+ is an extension of OntoUML - a simple language based on UML and UFO - towards modeling of other parts of models, not only structural models in UFO-A.

## Installation

1) run `mvn install`
2) open Modelio 3.6 and pass the jmdac file in `target` into `Configuration -> Modules catalog... -> Add a module to the catalog...`
3) add OntoUML module into project by `Configuration -> Modules -> Add ...`

## Usage

### Leveraging UML into OntoUML

The Module contains a palette of stereotypes that can be used to annotate UML artifacts. 

### Exporting to OWL

## Development

### Replacing OntoUML plugin in opened Modelio project
Replacing OntoUML plugin in opened running Modelio project can be done by navigating to context menu of a root model element:
`$ModelRoot/Administration/Deploy a module directly from a jmdac file` ... and choose the jmdac file from `target` directory.

### Binding to a SPARQL Endpoint

//import cz.cvut.kbss.ontouml.modelio.impl.OntoUmlModuleModule;
//String queryString = "SELECT ?iri ?label { ?iri a <http://www.w3.org/2004/02/skos/core#Concept> ; <http://www.w3.org/2000/01/rdf-schema#label> ?label } ";
//String endpoint = "http://onto.fel.cvut.cz/rdf4j-server/repositories/Urban_Planning_Thesaurus_RDFS_core";

Icons made by [[http://www.freepik.com]] from [[https://www.flaticon.com/]] are licensed by [[http://creativecommons.org/licenses/by/3.0/]](Creative Commons BY 3.0)