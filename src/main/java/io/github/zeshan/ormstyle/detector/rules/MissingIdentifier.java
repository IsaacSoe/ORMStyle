package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.ParametreOrField;
import io.github.zeshan.ORMStyle.model.output.Smell;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.zeshan.ORMStyle.parser.EntityParser.getIdentifierProperty;public class MissingIdentifier extends SmellDetector {

    /**
     * detection methods
     * @param classes Entity Declarations
     * @return results
     */
    public List<Smell> provideIdentifierPropertyRule(Set<Declaration> classes) {
        List<Smell> smells = new ArrayList<>();
        for (Declaration clazz : classes) {
            ParametreOrField field = getIdentifierProperty(clazz);
            if (field == null) {
                Smell smell = initSmell(clazz).setName("MissingId");
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
        return provideIdentifierPropertyRule(entityDeclarations);
    }


}
