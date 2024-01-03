package com.bigdata.rdf.sparql.ast.eval;


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
        
        new TestHelper(
            "ticket_no_ticket_1",
            "ticket_no_ticket_1.rq",
            "ticket_no_ticket_1.ntx",
            "ticket_no_ticket_1.srx"
        ).runTest();

    }
    
}
