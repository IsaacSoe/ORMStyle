package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.output.ProjectSmellReport;
import io.github.zeshan.ORMStyle.model.output.Smell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * GetterSetter Tester.
 */
public class GetterSetterTest {
    Set<Declaration> toInput;
    ProjectSmellReport psr = new ProjectSmellReport();
    GetterSetter c;

    @Before
    public void before() {
        String rootPath = "src/test/resources/entities/";
        Declaration incompleteGetterSetterEntity = Declaration.fromPath(rootPath + "InCompleteGetterSetterEntity.java");

        toInput = new HashSet<>();
        toInput.add(incompleteGetterSetterEntity);

        psr.getSmells().put(incompleteGetterSetterEntity, new ArrayList<>());

        c = (GetterSetter) new GetterSetter().populateContext(null, null, null, psr);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testProvideGetsSetsFieldsRule() {
        List<Smell> s = c.provideGetsSetsFieldsRule(toInput);
        assertEquals(s.size(), 1);
        assertEquals(s.get(0).getClassName(), "InCompleteGetterSetterEntity");
        List<String> fields = Arrays.asList(s.get(0).getComment().split("\n"));
        for (String field : fields) {
            assertTrue(field.contains("missingGetterSetterField") && (field.contains("Getter") || field.contains("Setter")));
        }
        assertEquals(fields.size(), 1);
    }

    /**
     * Method: exec()
     */
    @Test
    public void testExec() {
        c.setEntityDeclarations((HashSet<Declaration>) toInput);
        assertEquals(c.exec().size(), c.provideGetsSetsFieldsRule(toInput).size());
        assertEquals(c.exec().size(), 1);
        assertEquals(c.exec().get(0).getClassName(), c.provideGetsSetsFieldsRule(toInput).get(0).getClassName());

    }

} 
