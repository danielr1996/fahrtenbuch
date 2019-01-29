package de.danielr1996.fahrtenbuch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ArchitectureTest {
    static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter().importPackages("de.danielr1996.fahrtenbuch");
    }


    @Test
    public void noAcessFromDomainToApplication() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("de.danielr1996.fahrtenbuch.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("de.danielr1996.fahrtenbuch.application..");
        rule.check(classes);
    }


    @ParameterizedTest
    @ValueSource(strings = { "android", "location" })
    public void noCrossAccessFromApplicationPackages(String applicationPackage) {
       /* ArchRule rule = noClasses()
                .that().resideInAnyPackage("de.danielr1996.fahrtenbuch.application.location"
//                        "de.danielr1996.fahrtenbuch.application.geojson..",
//                        "de.danielr1996.fahrtenbuch.application.location..",
//                        "de.danielr1996.fahrtenbuch.application.storage..",
//                        "de.danielr1996.fahrtenbuch.application.support.."
                )
                .should().dependOnClassesThat().resideInAnyPackage("de.danielr1996.fahrtenbuch.application.android",
                        "de.danielr1996.fahrtenbuch.application.geojson..",
                        "de.danielr1996.fahrtenbuch.application.location..",
                        "de.danielr1996.fahrtenbuch.application.storage..",
                        "de.danielr1996.fahrtenbuch.application.support..");*/
//        rule.check(classes);
    }
}
