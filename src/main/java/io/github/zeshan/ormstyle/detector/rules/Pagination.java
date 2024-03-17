package io.github.zeshan.ORMStyle.detector.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.HqlAndContext;
import io.github.zeshan.ORMStyle.model.output.Smell;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.List;

import static io.github.zeshan.ORMStyle.utils.Const.*;

/**
 * detecting the misuse of pagination
 */
public class Pagination extends SmellDetector {


    /**
     * main method of detection
     * @param hqls hqls
     * @param cus CompilationUnits
     * @return results
     */
    public List<Smell> getPaged(List<HqlAndContext> hqls, List<CompilationUnit> cus) {
        List<Smell> pagedSmell = new ArrayList<>();
        if (hqls == null || cus == null) return pagedSmell;
        for (HqlAndContext hql : hqls) {
            for (Declaration calledIn : hql.populateCalledIn(cus).getCalledIn()) {
                String body = calledIn.getBody();
                boolean bodyDoesNotContainKeyword = true;
                if (containsPaginationKeyword(calledIn)) {
                    for(String k: PAGINATION_KEYWORDS){
                        bodyDoesNotContainKeyword = bodyDoesNotContainKeyword && !body.contains(k);
                    }
                    if (!hql.getMethodBody().contains("."+SET_FIRST_RESULT_EXPR+"(") && !hql.getMethodBody().contains("."+SET_MAX_RESULTS_EXPR+"(") && bodyDoesNotContainKeyword)  {
                        Declaration parentDeclaration = findDeclarationFromPath(calledIn.getFullPath());
                        if (parentDeclaration != null) {
                            Smell smell = initSmell(parentDeclaration)
                                    .setName("Pagination")
                                    .setPosition(calledIn.getPosition())
                                    .setComment(calledIn.getName());
                            pagedSmell.add(smell);
                            psr.getSmells().get(parentDeclaration).add(smell);
                        }
                    }
                }
            }
        }
        return pagedSmell;
    }

    /**
     * Check if method declaration contains pagination keyword of Integer or Long and their primitive types
     * @param method to check
     * @return true if keyword is found
     */
    private boolean containsPaginationKeyword(Declaration method){
        if(method == null || method.getRawCU() == null) return false;
        for (SimpleName name : method.getRawBD().findAll(SimpleName.class)){
            String name_str = name.asString();
            if(name_str.toLowerCase().contains(LIMIT_EXPR) || name_str.toLowerCase().contains(PAGE_EXPR)){
                Node parent = name;
                while(parent.getParentNode().isPresent() && !(parent.getParentNode().get() instanceof MethodDeclaration) &&  !(parent.getParentNode().get() instanceof TypeDeclaration) && !(parent.getParentNode().get() instanceof ClassOrInterfaceDeclaration)){
                    parent = parent.getParentNode().get();
                    for(Type t :parent.findAll(Type.class)){
                        if(t == null) continue;
                        if (t.isPrimitiveType()){
                            String primitive = t.asPrimitiveType().asString();
                            if(primitive.equals(PRIMITIVE_INT) || primitive.equals(PRIMITIVE_LONG)){
                                return true;
                            }
                        }else{
                            String typeName = t.toString();
                            if(typeName.equals(INTEGER) || typeName.equals(LONG)){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * execute detection
     * @return list of smells
     */
    public List<Smell> exec() {
        return getPaged(hqls, cus);
    }

}
