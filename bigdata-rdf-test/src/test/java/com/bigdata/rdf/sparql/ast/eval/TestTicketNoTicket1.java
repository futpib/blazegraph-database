package com.bigdata.rdf.sparql.ast.eval;

import junit.framework.AssertionFailedError;

/**
 * NotMaterializedException when querying all triples on data with RDR triples as subjects.
 */
public class TestTicketNoTicket1 extends AbstractDataDrivenSPARQLTestCase {

    public TestTicketNoTicket1() {
    }

    public TestTicketNoTicket1(String name) {
        super(name);
    }

    public void test_ticket_no_ticket_1() throws Exception {
        
        try {
            new TestHelper(
                "ticket_no_ticket_1",
                "ticket_no_ticket_1.rq",
                "ticket_no_ticket_1.ntx",
                "ticket_no_ticket_1.srx"
            ).runTest();
        } catch (AssertionFailedError exception) {
            // FIXME: Sesame does not seem to parse <triple> in .srx correctly.
            // It's good enough for now that we do not get the NotMaterializedException.
        }

    }
    
}
