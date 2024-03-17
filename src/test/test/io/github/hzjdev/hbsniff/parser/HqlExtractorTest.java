package io.github.zeshan.ORMStyle.parser;

import com.github.javaparser.ast.CompilationUnit;
import io.github.zeshan.ORMStyle.model.HqlAndContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.zeshan.ORMStyle.parser.HqlExtractor.getHqlNodes;
import static org.junit.Assert.assertEquals;public class HqlExtractorTest {

    List<CompilationUnit> cus;
    @Before
    public void before() throws Exception {
        String rootPath = "src/test/resources/hqls/";
        cus = EntityParser.parseFromDir(rootPath);
    }

    private List<CompilationUnit> extractFromCus(String name){
        return cus.stream().filter(i->i.getPrimaryTypeName().get().equals(name)).collect(Collectors.toList());
    }
    @After
    public void after() throws Exception {
    }

    @Test
    public void testGetHqlNodes1() throws Exception {
        List<CompilationUnit> input = extractFromCus("HQL1");
        List<HqlAndContext> result = getHqlNodes(input);
        assertEquals(result.get(0).getHql().get(0),"SELECT ac from AdCategory ac");
    }

    @Test
    public void testGetHqlNodes2() throws Exception {
        List<CompilationUnit> input = extractFromCus("HQL2");
        List<HqlAndContext> result = getHqlNodes(input);
        assertEquals(result.get(0).getHql().size(),4);
    }


    @Test
    public void testGetHqlNodes3() throws Exception {
        List<CompilationUnit> input = extractFromCus("HQL3");
        List<HqlAndContext> result = getHqlNodes(input);
        assertEquals(result.get(0).getHql().size(),3);
    }



    @Test
    public void testGetHqlNodes4() throws Exception {
        List<CompilationUnit> input = extractFromCus("HQL4");
        List<HqlAndContext> result = getHqlNodes(input);
        assertEquals(result.get(0).getHql().get(0),"SELECT x FROM Feedback x WHERE 1=1");
    }

} 


