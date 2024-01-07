package com.bigdata.rdf.sparql.ast.eval;

/**

 */
public class TestTicketNoTicket3 extends AbstractDataDrivenSPARQLTestCase {

    public TestTicketNoTicket3() {
    }

    public TestTicketNoTicket3(String name) {
        super(name);
    }

    public void test_ticket_no_ticket_3() throws Exception {
        new UpdateTestHelper(
            "ticket_no_ticket_3",
            "ticket_no_ticket_3.rq",
            "ticket_no_ticket_3.nt"
        );
    }
}
