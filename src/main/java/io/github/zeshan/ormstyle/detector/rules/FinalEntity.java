package io.github.zeshan.ORMStyle.detector.rules;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;public class FinalEntity extends SmellDetector {

    /**
     * detection methods
     * @param classes Entity Declarations
     * @return results
     */
    public List<Smell> noFinalClassRule(Set<Declaration> classes) {
        List<Smell> smells = new ArrayList<>();
        for (Declaration clazz : classes) {
            ClassOrInterfaceDeclaration cid = clazz.getClassDeclr();
            if (cid == null) continue;
            if (cid.getModifiers() != null && cid.getModifiers().contains(Modifier.finalModifier())) {
                Smell smell = initSmell(clazz).setName("Final Entity");
                psr.getSmells().get(clazz).add(smell);
                smells.add(smell);
            }
        }
        return smells;
    }

    /**
     * execute detection
     * @return list of smells
     */
    public List<Smell> exec() {
        return noFinalClassRule(entityDeclarations);
    }

}
