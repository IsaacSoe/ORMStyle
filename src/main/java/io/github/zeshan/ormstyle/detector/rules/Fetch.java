package io.github.zeshan.ORMStyle.detector.rules;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.HqlAndContext;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.github.zeshan.ORMStyle.model.HqlAndContext.extractSelectedFields;
import static io.github.zeshan.ORMStyle.parser.EntityParser.findTypeDeclaration;
import static io.github.zeshan.ORMStyle.utils.Const.*;
import static io.github.zeshan.ORMStyle.utils.Utils.cleanHql;public class Fetch extends SmellDetector {

    /**
     * insert smell to psr
     * @param n corresponding node
     * @param eagerFetches result list
     * @param cu compilationunit of file
     */
    public void genSmell(Node n, List eagerFetches, CompilationUnit cu){
        Optional<Node> parentField = n.getParentNode();
        while (parentField.isPresent() && !(parentField.get() instanceof FieldDeclaration)) {
            parentField = parentField.get().getParentNode();
        }
        if (parentField.isPresent()) {
            final Smell smell;
            try {
                String field;
                try {
                    field = parentField.get().findAll(VariableDeclarator.class).get(0).getNameAsString();
                }catch(Exception e){
                    field = parentField.get().toString();
                }
                Declaration parentDeclaration = findTypeDeclaration(cu.getStorage().get().getPath().toString());
                smell = initSmell(parentDeclaration).setComment(field)
                        .setName("Eager Fetch");

                n.getRange().ifPresent(s -> smell.setPosition(s.toString()));
                psr.getSmells().get(parentDeclaration).add(smell);
                eagerFetches.add(smell);
            }catch(Exception e){
                e.printStackTrace();
                return;
            }
        }
    }
    /**
     * find fields annotated with eager fetches in entites
     * @param cus scope of detected files
     * @return list of smells
     */
    public List<Smell> getEagerFetches(List<CompilationUnit> cus) {
        List<Smell> eagerFetches = new ArrayList<>();
        for (CompilationUnit cu : cus) {
            List<NormalAnnotationExpr> annotations = cu.findAll(NormalAnnotationExpr.class);
            for (NormalAnnotationExpr annotation : annotations) {
                if (annotation.getNameAsString().contains(TO_ONE_ANNOT_EXPR) || annotation.getNameAsString().contains(TO_MANY_ANNOT_EXPR)) {
                    boolean fetchTypeExists = false;
                    for (MemberValuePair mvp : annotation.getPairs()) {
                        // case of @ManyToOne(fetch = {FetchType.EAGER})
                        if(mvp.getNameAsString().equals(FETCH_ANNOT_EXPR)){
                            fetchTypeExists = true;
                        }
                        if (fetchTypeExists && mvp.getValue().toString().contains(EAGER_ANNOT_EXPR)) {
                            genSmell(mvp, eagerFetches, cu);
                        }
                    }
                    if(!fetchTypeExists && annotation.getNameAsString().contains(TO_ONE_ANNOT_EXPR)){
                        // case of @ManyToOne(cascade = { CascadeType.ALL })
                        genSmell(annotation, eagerFetches, cu);
                    }
                }
            }
            List<MarkerAnnotationExpr> annotationsMarker = cu.findAll(MarkerAnnotationExpr.class);
            for (MarkerAnnotationExpr marker : annotationsMarker) {
                if (marker.getNameAsString().contains(TO_ONE_ANNOT_EXPR)) {
                    //Direct marker like @ManyToOne or @OneToOne with default fetch type EAGER
                    genSmell(marker, eagerFetches, cu);
                }
            }
        }
        return eagerFetches;
    }

    /**
     * find hqls affected by Join Fetch smell
     * @param hqls hqls
     * @param eagerFetches detection result of Eager Fetch smell
     * @return list of smells
     */
    public List<Smell> getJoinFetch(List<HqlAndContext> hqls, List<Smell> eagerFetches) {
        List<Smell> joinFetchSmell = new ArrayList<>();
        for (HqlAndContext hqlAndContext : hqls) {
            StringBuilder hql = new StringBuilder();
            for (String hql__ : hqlAndContext.getHql()) {
                hql.append(hql__).append(' ');
            }
            String hql_expr = hql.toString().toLowerCase();
            if (!hql_expr.contains(JOIN_FETCH_EXPR)) {
                String from_entity = null;
                hql_expr = cleanHql(hql_expr);
                if (!hql_expr.startsWith(DELETE_EXPR) && !hql_expr.startsWith(UPDATE_EXPR) && !hql_expr.startsWith(INSERT_EXPR)) {
                    try {
                        from_entity = hql_expr.split( FROM_EXPR+" ")[1].split(" ")[0];
                    } catch (Exception e) {
                        from_entity = hqlAndContext.getReturnType();
                    }
                    if (from_entity != null) {
                        String[] from_entity_arr = from_entity.split("\\.");
                        from_entity = from_entity_arr[from_entity_arr.length - 1];
                        for (Smell eagerFetch : eagerFetches) {
                            // check if from entity in hql is an EAGER fetch smelled entity
                            if (eagerFetch.getClassName().toLowerCase().equals(from_entity)) {
                                Declaration dec = findTypeDeclaration(eagerFetch.getClassName());
                                // check if the selected fields of dec in hql are annotated with FetchType.EAGER
                                Set<String> selected_fields = extractSelectedFields(hql_expr, dec);
                                if(dec!=null) {
                                    if(selected_fields.size()>0 && !selected_fields.contains(eagerFetch.getComment().toLowerCase())){
                                        continue;
                                    }
                                    // this case, eager fields may be selected by specified expr or select *.
                                    Smell smell = new Smell()
                                            .setPosition(hqlAndContext.getCreateQueryPosition())
                                            .setFile(hqlAndContext.getFullPath())
                                            .setComment(eagerFetch.getClassName() + "("+eagerFetch.getComment() +")"+ ">" +hqlAndContext.getTypeName()+"::"+hqlAndContext.getMethodName() + ">" + hql.toString())
                                            .setClassName(eagerFetch.getClassName())
                                            .setName("Lacking Join Fetch");
                                    joinFetchSmell.add(smell);
                                    psr.getSmells().get(dec).add(smell);
                                }
                            }
                        }
                    }
                }
            }
        }
        return joinFetchSmell;
    }

    /**
     * execute detection
     * @return list of smells
     */
    public List<Smell> exec() {
        List<Smell> eagerFetches = getEagerFetches(entities);
        List<Smell> joinFetches = getJoinFetch(hqls, eagerFetches);
        joinFetches.addAll(eagerFetches);
        return joinFetches;
    }
}
