package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.zeshan.ORMStyle.utils.Const.SERIALIZABLE_EXPR;public class NotSerializable extends SmellDetector {

    /**
     * detection methods
     * @param classes Entity Declarations
     * @return results
     */
    public final List<Smell> checkRule(Set<Declaration> classes) {
        List<Smell> smells = new ArrayList<>();
        for (Declaration entityNode : classes) {
            boolean clean = false;
            Set<Declaration> toDetect = entityNode.getExtendedOrImplementedTypes();
            for (Declaration superclass : toDetect) {
                for (String i : superclass.getImplementedInterface()) {
                    if (i.contains(SERIALIZABLE_EXPR)) {
                        clean = true;
                        break;
                    }
                }
                for (String i : superclass.getSuperClass()) {
                    if (!clean && i.contains(SERIALIZABLE_EXPR)) {
                        clean = true;
                        break;
                    }
                }
            }
            if (!clean) {
                Smell smell = initSmell(entityNode).setName("NotSerializable");
                psr.getSmells().get(entityNode).add(smell);
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
        return checkRule(entityDeclarations);
    }

}
