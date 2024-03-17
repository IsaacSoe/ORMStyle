package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.ParametreOrField;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;public class MissingNoArgumentConstructor extends SmellDetector {


    /**
     * detection methods
     * @param classes Entity Declarations
     * @return results
     */
    public final List<Smell> noArgumentConstructorRule(Set<Declaration> classes) {
        List<Smell> smells = new ArrayList<>();
        for (Declaration clazz : classes) {

            // Checks the class and the inherited methods from the super class
            List<Declaration> constructors = clazz.getConstructors();
            boolean smelly = true;

            if (constructors != null) {
                if(constructors.size()<1){
                    // java will implement default no arg constructor if no constructor is specified
                    smelly = false;
                }
                for (Declaration methodNode : constructors) {
                    List<ParametreOrField> parameters = methodNode.getParametres();
                    if (parameters.isEmpty()) {
                        smelly = false;
                        break;
                    }
                }
            }else{
                smelly = false;
            }

            if (smelly) {
                Smell smell = initSmell(clazz).setName("MissingNoArgumentConstructor");
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
        return noArgumentConstructorRule(entityDeclarations);
    }

}
