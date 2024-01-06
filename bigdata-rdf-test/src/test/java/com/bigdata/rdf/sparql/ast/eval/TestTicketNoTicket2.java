package com.bigdata.rdf.sparql.ast.eval;

import junit.framework.AssertionFailedError;

/**
 * 8-digit unicode escape sequences (\UXXXXXXXX) in SPARQL queries raised an error.
 * Also N-Triples parser treated it's input as US-ASCII resulting in 0xFF characters.
 *
 * https://www.w3.org/TR/sparql11-query/#codepointEscape
 */
public class TestTicketNoTicket2 extends AbstractDataDrivenSPARQLTestCase {

    public TestTicketNoTicket2() {
    }

    public TestTicketNoTicket2(String name) {
        super(name);
    }

    public void test_ticket_no_ticket_2() throws Exception {
        new TestHelper(
            "ticket_no_ticket_2",
            "ticket_no_ticket_2.rq",
            "ticket_no_ticket_2.nt",
            "ticket_no_ticket_2.srx"
        ).runTest();
    }

    public void test_ticket_no_ticket_2_unescaped() throws Exception {
        new TestHelper(
            "ticket_no_ticket_2",
            "ticket_no_ticket_2.rq",
            "ticket_no_ticket_2_unescaped.nt",
            "ticket_no_ticket_2.srx"
        ).runTest();
    }

    public void test_ticket_no_ticket_2_unescaped_unescaped() throws Exception {
        new TestHelper(
            "ticket_no_ticket_2",
            "ticket_no_ticket_2_unescaped.rq",
            "ticket_no_ticket_2_unescaped.nt",
            "ticket_no_ticket_2.srx"
        ).runTest();
    }
}
