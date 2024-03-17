package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.ParametreOrField;
import io.github.zeshan.ORMStyle.model.output.Smell;
import io.github.zeshan.ORMStyle.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;public class CollectionField extends SmellDetector {


    /**
     * check if entities use set as interfaces
     * @param allModelClasses entities
     * @return list of smells
     */
    public List<Smell> useSetCollectionRule(Set<Declaration> allModelClasses) {
        List<Smell> smells = new ArrayList<>();

        for (Declaration entityNode : allModelClasses) {

            List<ParametreOrField> declaredFields = entityNode.getFields();
            boolean smelly = false;
            StringBuilder comment = new StringBuilder();

            for (ParametreOrField fieldNode : declaredFields) {
                String type = fieldNode.getType();
                if (type.contains("<")) {
                    // List<Type>
                    type = type.split("<")[0];
                }
                if (Utils.isCollection(type) && !Utils.isSet(type)) {
                    comment.append(fieldNode.getName());
                    smelly = true;
                }
            }

            if (smelly) {
                Smell smell = initSmell(entityNode).setName("CollectionField").setComment(comment.toString());
                psr.getSmells().get(entityNode).add(smell);
                smells.add(smell);
            }
        }
        return smells;
    }

    /**
     * check if entities use set or list as interfaces
     * @param entities entities
     * @return list of smells
     */
    @Deprecated
    public List<Smell> useInterfaceSetOrListRule(Set<Declaration> entities) {
        List<Smell> smells = new ArrayList<>();

        for (Declaration entity : entities) {

            List<ParametreOrField> declaredFields = entity.getFields();
            boolean smelly = false;
            StringBuilder comment = new StringBuilder();
            for (ParametreOrField fieldNode : declaredFields) {
                String type = fieldNode.getType();
                if (type.contains("<")) {
                    type = type.split("<")[0];
                }
                if (Utils.isCollection(type) && !type.equals(Utils.SET_NAME)
                        && !type.equals(Utils.LIST_NAME)) {
                    comment.append("The field <").append(fieldNode.getName()).append("> of the class <").append(entity.getName()).append("> implements interface Collection but it ").append("doesn't implements interface Set or interface List.\n");
                    smelly = true;
                }
            }

            if (smelly) {
                Smell smell = initSmell(entity).setName("CollectionField").setComment(comment.toString());
                psr.getSmells().get(entity).add(smell);
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
        List<Smell> interfaces = useSetCollectionRule(entityDeclarations);
//        interfaces.addAll(useSetCollectionRule(entityDeclarations));
        return interfaces;
    }

}
