package ca.mcgill.ecse321.gameorganizer.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ca.mcgill.ecse321.gameorganizer.TestJwtConfig;
import ca.mcgill.ecse321.gameorganizer.config.TestSecurityConfig;
import ca.mcgill.ecse321.gameorganizer.models.LegoSet;
import ca.mcgill.ecse321.gameorganizer.models.RetirementStatus;
import ca.mcgill.ecse321.gameorganizer.repositories.LegoSetRepository;

@SpringBootTest
@ActiveProfiles("test")
@Import({ TestSecurityConfig.class, TestJwtConfig.class })
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:flyway_hibernate_validate;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.flyway.enabled=true",
        "spring.flyway.locations=classpath:db/migration",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect"
})
public class FlywaySchemaValidationTest {

    @Autowired
    private LegoSetRepository legoSetRepository;

    @Test
    public void testFlywayMigrationsMatchHibernateEntities() {
        assertEquals(12, legoSetRepository.count(), "V2 should seed 12 LEGO sets");

        Optional<LegoSet> markets = legoSetRepository.findBySetNumber("71799");
        assertTrue(markets.isPresent());
        assertEquals("NINJAGO City Markets", markets.get().getName());
        assertEquals("Ninjago", markets.get().getTheme());
        assertEquals(RetirementStatus.ACTIVE, markets.get().getRetirementStatus());

        Optional<LegoSet> mino = legoSetRepository.findBySetNumber("71719");
        assertTrue(mino.isPresent());
        assertEquals(RetirementStatus.RETIRED, mino.get().getRetirementStatus());
    }
}
