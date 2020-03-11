package apaukls;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ASCIIFoldingTest {

    private static final Config driverConfig = Config.build().withoutEncryption().toConfig();
    private ServerControls embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {

        this.embeddedDatabaseServer = TestServerBuilders
                .newInProcessBuilder()
                .withFunction(ASCIIFolding.class)
                .newServer();
    }

    @Test
    public void shouldAllowIndexingAndFindingANode() {

        // This is in a try-block, to make sure we close the driver after the test
        try( Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {

            // When
            String result = session.run( "RETURN apaukls.asciiFolding('Lörem ëripuît') AS result").single().get("result").asString();

            // Then
            assertThat( result).isEqualTo(( "Lorem eripuit" ));
        }
    }
}