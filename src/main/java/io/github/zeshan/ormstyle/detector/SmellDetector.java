package io.github.zeshan.ORMStyle.detector;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.HqlAndContext;
import io.github.zeshan.ORMStyle.model.output.ProjectSmellReport;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.zeshan.ORMStyle.parser.EntityParser.findTypeDeclaration;


/**
 * This module contain some transferred code from https://github.com/tacianosilva/designtests
 * M. Silva, D. Serey, J. Figueiredo, J. Brunet. Automated design tests to check Hibernate design recommendations. SBES 2019
 */
public abstract class SmellDetector {

    public ProjectSmellReport psr;

    public List<CompilationUnit> cus;

    public List<HqlAndContext> hqls;

    public List<CompilationUnit> entities;

    public HashSet<Declaration> declarations;

    public Set<Declaration> entityDeclarations;

    protected static Smell initSmell(Declaration entity) {
        return new Smell().setClassName(entity.getName()).setFile(entity.getFullPath()).setPosition(entity.getPosition());
    }

    public void setEntityDeclarations(Set<Declaration> entityDeclarations) {
        this.entityDeclarations = entityDeclarations;
    }

    /**
     * initialize
     * @param cus compilation units
     * @param hqls hqls
     * @param entities entity compilation units
     * @param psr project smell report
     * @return intialized smell detector
     */
    public SmellDetector populateContext(List<CompilationUnit> cus, List<HqlAndContext> hqls, List<CompilationUnit> entities, ProjectSmellReport psr) {
        this.psr = psr;
        this.cus = cus;
        this.hqls = hqls;
        this.entities = entities;
        if (cus != null) {
            declarations = new HashSet<>();
            entityDeclarations = new HashSet<>();
            for (CompilationUnit cu : cus) {
                for (TypeDeclaration td : cu.getTypes()) {
                    String fullPath = null;
                    if(cu.getStorage().isPresent()){
                        fullPath = cu.getStorage().get().getPath().toString();
                    }
                    Declaration d = findTypeDeclaration(td.getNameAsString(), fullPath, cus, 1);
                    if (d != null) {
                        declarations.add(d);
                        if (entities != null && entities.contains(cu)) {
                            entityDeclarations.add(d);
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * locate Declaration from CompilationUnits
     * @param path declaration path
     * @return result Declaration
     */
    public Declaration findDeclarationFromPath(String path) {
        for (CompilationUnit cu : cus) {
            String cuPath;
            if (cu.getStorage().isPresent()) {
                cuPath = cu.getStorage().get().getPath().toString();
                if (path.equals(cuPath)) {
                    return new Declaration(cu);
                }
            }
        }
        return null;
    }

    /**
     * execute detection
     * @return list of results
     */
    public abstract List<Smell> exec();

}
