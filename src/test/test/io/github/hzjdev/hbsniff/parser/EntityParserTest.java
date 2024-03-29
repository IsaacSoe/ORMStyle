package io.github.zeshan.ORMStyle.parser;

import com.github.javaparser.ast.CompilationUnit;
import io.github.zeshan.ORMStyle.model.Declaration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;public class EntityParserTest { 

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: parseFromDir(String dirPath, List<CompilationUnit> results)
    *
    */
    @Test
    public void testParseFromDir() throws Exception {
        String rootPath = "src/test/resources/entities/parser/parseFromDir/";
        List<CompilationUnit> results = EntityParser.parseFromDir(rootPath);
        assertEquals(results.size(), 3);
    }

    /**
    *
    * Method: genDeclarationsFromCompilationUnits(List<CompilationUnit> cus)
    *
    */
    @Test
    public void testGenDeclarationsFromCompilationUnits() throws Exception {
        String rootPath = "src/test/resources/entities/parser/parseFromDir/";
        List<CompilationUnit> cus = EntityParser.parseFromDir(rootPath);
        List<Declaration> declarations = EntityParser.genDeclarationsFromCompilationUnits(cus);
        assertEquals(declarations.size(), 3);
        List<String> names = declarations.stream().map(Declaration::getName).collect(Collectors.toList());
        assertTrue(names.contains("Entity1"));
        assertTrue(names.contains("Entity2"));
        assertTrue(names.contains("Normal"));

    }

    /**
    *
    * Method: getIdentifierProperty(final Declaration entity)
    *
    */
    @Test
    public void testGetIdentifierProperty() throws Exception {
        String rootPath1 = "src/test/resources/entities/parser/parseFromDir/";
        String rootPath2 = "src/test/resources/entities/parser/getIdentifierProperty/";

        List<CompilationUnit> cus1 = EntityParser.parseFromDir(rootPath1);
        EntityParser.setCusCache(cus1);
        Declaration idDeclaredInField = EntityParser.findTypeDeclaration("Entity1");
        Declaration idDeclaredInGetter = EntityParser.findTypeDeclaration("Entity2");
        Declaration withoutId = EntityParser.findTypeDeclaration("Normal");
        assertEquals("id", EntityParser.getIdentifierProperty(idDeclaredInField).getName());
        assertEquals("id", EntityParser.getIdentifierProperty(idDeclaredInGetter).getName());
        assertNull(EntityParser.getIdentifierProperty(withoutId));

        List<CompilationUnit> cus2 = EntityParser.parseFromDir(rootPath2);
        EntityParser.setCusCache(cus2);
        Declaration entityChild = EntityParser.findTypeDeclaration("EntityChild");
        Declaration entityChild2 = EntityParser.findTypeDeclaration("EntityChildChild");

        assertEquals("id", EntityParser.getIdentifierProperty(entityChild).getName());
        assertEquals("id", EntityParser.getIdentifierProperty(entityChild2).getName());


    }

    /**
    *
    * Method: getSuperClassDeclarations(Declaration dec)
    *
    */
    @Test
    public void testGetSuperClassDeclarations() throws Exception {
        String rootPath = "src/test/resources/entities/parser/getIdentifierProperty/";
        List<CompilationUnit> cus = EntityParser.parseFromDir(rootPath);
        EntityParser.setCusCache(cus);
        Declaration entityChild = EntityParser.findTypeDeclaration("EntityChild");
        Declaration entityChild2 = EntityParser.findTypeDeclaration("EntityChildChild");

        assertEquals(1, EntityParser.getSuperClassDeclarations(entityChild).size());
        assertEquals("EntityParent", EntityParser.getSuperClassDeclarations(entityChild).get(0).getName());
        assertEquals(2, EntityParser.getSuperClassDeclarations(entityChild2).size());

    }


    /**
    *
    * Method: findCalledIn(String methodName, String typeName, List<CompilationUnit> cus)
    *
    */
    @Test
    public void testFindCalledInForMethodNameTypeNameCus() throws Exception {
        String rootPath1 = "src/test/resources/entities/metric/";

        List<CompilationUnit> cus1 = EntityParser.parseFromDir(rootPath1);

        EntityParser.setCusCache(cus1);

        List<Declaration> resultDeprecated = EntityParser.findCalledIn("findStudents", cus1);
        assertEquals(resultDeprecated.size(),2);
        assertEquals(resultDeprecated.get(0).getName(),"students");
        assertEquals(resultDeprecated.get(1).getName(),"students");


    }

    /**
    *
    * Method: findTypeDeclaration(String toFind, List<CompilationUnit> cus, Integer level)
    *
    */
    @Test
    public void testFindTypeDeclaration() throws Exception {
        String rootPath1 = "src/test/resources/entities/parser/parseFromDir/";

        List<CompilationUnit> cus1 = EntityParser.parseFromDir(rootPath1);
        EntityParser.setCusCache(cus1);

        Declaration idDeclaredInField = EntityParser.findTypeDeclaration("Entity1");
        Declaration idDeclaredInGetter = EntityParser.findTypeDeclaration("Entity2");

        assertEquals(idDeclaredInField.getName(),"Entity1");
        assertEquals(idDeclaredInGetter.getName(),"Entity2");

    }



} 


