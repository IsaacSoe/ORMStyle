package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.output.ProjectSmellReport;
import io.github.zeshan.ORMStyle.model.output.Smell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * FinalEntity Tester.
 */
public class FinalEntityTest {
    Set<Declaration> toInput;
    ProjectSmellReport psr = new ProjectSmellReport();
    FinalEntity c;

    @Before
    public void before() {
        String rootPath = "src/test/resources/entities/";
        Declaration missingNoArgConstructorEntity = Declaration.fromPath(rootPath + "DefaultNoArgConstructorFinalEntity.java");
        Declaration noArgConstructorEntity = Declaration.fromPath(rootPath + "NoArgConstructorEntity.java");

        toInput = new HashSet<>();
        toInput.add(missingNoArgConstructorEntity);
        toInput.add(noArgConstructorEntity);

        psr.getSmells().put(missingNoArgConstructorEntity, new ArrayList<>());
        psr.getSmells().put(noArgConstructorEntity, new ArrayList<>());

        c = (FinalEntity) new FinalEntity().populateContext(null, null, null, psr);
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: noArgumentConstructorRule(Set<Declaration> classes)
     */
    @Test
    public void testFinalClassRule() {
        List<Smell> s = c.noFinalClassRule(toInput);
        assertEquals(s.size(), 1);
        assertEquals(s.get(0).getClassName(), "DefaultNoArgConstructorFinalEntity");
    }

    /**
     * Method: exec()
     */
    @Test
    public void testExec() {
        c.setEntityDeclarations((HashSet<Declaration>) toInput);
        assertEquals(c.exec().size(), c.noFinalClassRule(toInput).size());
        assertEquals(c.exec().size(), 1);
        assertEquals(c.exec().get(0).getClassName(), c.noFinalClassRule(toInput).get(0).getClassName());

    }

} 
