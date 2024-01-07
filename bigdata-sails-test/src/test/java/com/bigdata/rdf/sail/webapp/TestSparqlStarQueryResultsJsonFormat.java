package com.bigdata.rdf.sail.webapp;

import java.io.IOException;

import junit.framework.Test;

/**
 * Check that triples in results are serialized correctly.
 *
 * https://w3c.github.io/rdf-star/cg-spec/2021-12-17.html#sparql-star-query-results-json-format
 */
public class TestSparqlStarQueryResultsJsonFormat extends AbstractProtocolTest{

	static public Test suite() {
		return ProxySuiteHelper.suiteWhenStandalone(TestSparqlStarQueryResultsJsonFormat.class,"test.*", TestMode.sids);
	}

	public TestSparqlStarQueryResultsJsonFormat(String name)  {
		super(name);
	}

	public void testSelectGetTripleJSON() throws IOException {
		setMethodisPostUrlEncodedData();
		serviceRequest("update", "INSERT DATA { <<<http://s> <http://p> <http://o>>> <http://mp> <http://mo> . }");
		resetDefaultOptions();
		this.setAccept(BigdataRDFServlet.MIME_SPARQL_RESULTS_JSON);
		String response = serviceRequest("query", "SELECT ?triple WHERE { ?triple <http://mp> <http://mo> . }");
		String responseNoWhitespace = response.replaceAll("\\s+", "");
		assertTrue(responseNoWhitespace.contains("""
			"triple":{"type":"triple","value":{"subject":{
		""".trim()));
	}

}
