package com.tfg.packagetracking;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Architecture tests to enforce hexagonal architecture and clean code practices.
 */
public class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.tfg.packagetracking");

    /**
     * Validates layered hexagonal architecture rules adapted to real package structure.
     */
    @Test
    void shouldRespectHexagonalLayeredArchitecture() {
        Architectures.LayeredArchitecture rule = Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()

                .layer("Controller").definedBy("..application.controllers..")
                .layer("ServiceApp").definedBy("..application.services..")
                .layer("Mapper").definedBy("..application.mappers..")
                .layer("DTO").definedBy("..application.dto..")
                .layer("Utils").definedBy("..application.utils..")
                .layer("Domain").definedBy("..domain..")
                .layer("Ports").definedBy("..domain.ports..")
                .layer("Infrastructure").definedBy("..infrastructure..")

                .whereLayer("Controller").mayOnlyAccessLayers("ServiceApp", "DTO", "Mapper", "Utils", "Domain")
                .whereLayer("ServiceApp").mayOnlyAccessLayers("Domain", "DTO", "Mapper")
                .whereLayer("Mapper").mayOnlyAccessLayers("Domain", "DTO")
                .whereLayer("DTO").mayOnlyAccessLayers("Domain")
                .whereLayer("Utils").mayOnlyAccessLayers("Domain", "DTO")
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Ports").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure")
                .mayOnlyAccessLayers("Domain", "Ports")
                // Allowing specific exceptions for infrastructure dependencies for code reuse
                .ignoreDependency(
                        "com.tfg.packagetracking.infrastructure.adapters.PackageEventKafkaAdapter",
                        "com.tfg.packagetracking.application.mappers.PackageMapper"
                );

        rule.check(importedClasses);
    }

    /**
     * Prevents cyclic dependencies between top-level packages.
     */
    @Test
    void shouldHaveNoCyclicDependencies() {
        ArchRule rule = slices().matching("com.tfg.packagetracking.(*)..")
                .should().beFreeOfCycles();

        rule.check(importedClasses);
    }

    /**
     * Ensures that application services follow naming and location conventions.
     */
    @Test
    void applicationServicesShouldBeAnnotatedAndInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Service")
                .and().resideInAPackage("..application.services..")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

        rule.check(importedClasses);
    }

    /**
     * Ensures that controllers are annotated and located properly.
     */
    @Test
    void controllersShouldBeAnnotatedAndInControllerPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..application.controllers..")
                .andShould().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);

        rule.check(importedClasses);
    }

    /**
     * Ensures that DTOs do not depend on infrastructure layer.
     */
    @Test
    void dtosShouldNotDependOnInfrastructure() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.dto..")
                .should().onlyDependOnClassesThat().resideOutsideOfPackages("..infrastructure..");

        rule.check(importedClasses);
    }

    /**
     * Ensures domain model does not depend on Jakarta or Spring. Exceptions are made for Pageable and Page.
     */
    @Test
    void domainShouldBeFreeOfSpringAndJakartaExceptPageables() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain..")
                .should().onlyDependOnClassesThat(
                        JavaClass.Predicates.resideOutsideOfPackages(
                                "org.springframework..",
                                "jakarta..",
                                "javax.."
                        ).or(JavaClass.Predicates.belongToAnyOf(
                                org.springframework.data.domain.Pageable.class,
                                org.springframework.data.domain.Page.class
                        ))
                );

        rule.check(importedClasses);
    }

    /**
     * Enforces the use of constructor or setter injection instead of field injection.
     */
    @Test
    void shouldAvoidFieldInjection() {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(importedClasses);
    }

    /**
     * Prevents use of System.out or System.err in production code.
     */
    @Test
    void shouldAvoidUsingSystemOutErr() {
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(importedClasses);
    }

    /**
     * Encourages specific exceptions by disallowing use of RuntimeException directly.
     */
    @Test
    void shouldAvoidThrowingGenericExceptions() {
        GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(importedClasses);
    }
}
