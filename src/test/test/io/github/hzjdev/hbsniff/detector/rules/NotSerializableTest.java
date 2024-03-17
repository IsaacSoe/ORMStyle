package io.github.zeshan.ORMStyle.detector.rules;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.output.ProjectSmellReport;
import io.github.zeshan.ORMStyle.model.output.Smell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * GetterSetter Tester.
 */
public class NotSerializableTest {
    Set<Declaration> toInput;
    ProjectSmellReport psr = new ProjectSmellReport();
    NotSerializable c;
    List<CompilationUnit> cus = new ArrayList<>();

    @Before
    public void before() throws FileNotFoundException {
        String rootPath = "src/test/resources/entities/";
        Declaration notSerializableEntity = Declaration.fromPath(rootPath + "InCompleteGetterSetterEntity.java");
        Declaration serializableEntity = Declaration.fromPath(rootPath + "SerializableEntity.java");
        cus.add(StaticJavaParser.parse(new File(rootPath + "InCompleteGetterSetterEntity.java")));
        cus.add(StaticJavaParser.parse(new File(rootPath + "SerializableEntity.java")));

        toInput = new HashSet<>();
        toInput.add(notSerializableEntity);
        toInput.add(serializableEntity);

        psr.getSmells().put(notSerializableEntity, new ArrayList<>());
        psr.getSmells().put(serializableEntity, new ArrayList<>());

        c = (NotSerializable) new NotSerializable().populateContext(cus, null, null, psr);
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void checkRule() {
        List<Smell> s = c.checkRule(toInput);
        assertEquals(s.size(), 1);
        assertEquals(s.get(0).getClassName(), "InCompleteGetterSetterEntity");
    }

    /**
     * Method: exec()
     */
    @Test
    public void testExec() {
        c.setEntityDeclarations((HashSet<Declaration>) toInput);
        assertEquals(c.exec().size(), c.checkRule(toInput).size());
        assertEquals(c.exec().size(), 1);
        assertEquals(c.exec().get(0).getClassName(), c.checkRule(toInput).get(0).getClassName());

    }

} 
