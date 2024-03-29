package io.github.zeshan.ORMStyle.detector.rules;

import io.github.zeshan.ORMStyle.detector.SmellDetector;
import io.github.zeshan.ORMStyle.model.Declaration;
import io.github.zeshan.ORMStyle.model.ParametreOrField;
import io.github.zeshan.ORMStyle.model.output.Smell;
import io.github.zeshan.ORMStyle.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static io.github.zeshan.ORMStyle.parser.EntityParser.getSuperClassDeclarations;
import static io.github.zeshan.ORMStyle.utils.Const.*;public class GetterSetter extends SmellDetector {


    /**
     * Verifies if exists set method for the field.
     *
     * @param fieldNode  The field contained in the class.
     * @param entityNode Entity that contains the field.
     * @return True if exists set method in the entity for the field.
     */
    protected final boolean hasSetMethod(final ParametreOrField fieldNode, final Declaration entityNode) {
        String fieldName = fieldNode.getName();
        String type = fieldNode.getType();

        String setName = SETTER_METHOD_PREFIX + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        Declaration methodNode = entityNode.findDeclaration(setName);

        if (methodNode == null) {
            return false;
        }

        if ((methodNode.getParametres() != null) && !(methodNode.getParametres().size() == 1 && methodNode.getParametres().get(0).getType().equals(type))) {
            return false;
        }

        String methodName = methodNode.getName();
        String methodType = methodNode.getReturnTypeName();

        return methodName.equals(setName) && (methodType.equals(VOID_TYPE_EXPR) || methodType.equals(entityNode.getName()));
    }

    /**
     * Verifies if exists get method for the field.
     *
     * @param fieldNode  The field contained in the class.
     * @param entityNode Entity that contains the field.
     * @return True if exists get method in the entity for the field.
     */
    protected final boolean hasGetMethod(final ParametreOrField fieldNode, final Declaration entityNode) {

        String shortFieldName = fieldNode.getName();
        String type = fieldNode.getType();
        String strGetOrIs;

        if (type != null && (type.equals(Utils.BOOLEAN_PRIMITIVE) || type.equals(Utils.BOOLEAN_CLASS))) {
            strGetOrIs = GETTER_METHOD_PREFIX_BOOL;
        } else {
            strGetOrIs = GETTER_METHOD_PREFIX_NORMAL;
        }

        String methodGetField = strGetOrIs + shortFieldName.substring(0, 1).toUpperCase()
                + shortFieldName.substring(1);

        Declaration methodNode = entityNode.findDeclaration(methodGetField);

        if (methodNode == null || (methodNode.getParametres() != null && methodNode.getParametres().size() > 0)) {
            // for example, a getter for boolean field may be straightly the name of the field.
            methodNode = entityNode.findDeclaration(fieldNode.getName());
            if(methodNode == null){
                return false;
            }else{
                methodGetField = methodNode.getName();
            }
        }

        String methodName = methodNode.getName();
        String methodType = methodNode.getReturnTypeName();

        return methodName.equals(methodGetField) && methodType.equals(type);
    }


    /**
     * detection methods
     * @param classes Entity Declarations
     * @return results
     */
    public final List<Smell> provideGetsSetsFieldsRule(Set<Declaration> classes) {

        List<Smell> smells = new ArrayList<>();
        boolean annotationGetter = false;
        boolean annotationSetter = false;
        boolean annotationData = false;

        for (Declaration entityNode : classes) {
            // lombok annotations
            annotationData = entityNode.annotationIncludes(DATA_ANNOT_EXPR);
            if(annotationData) continue;
            annotationGetter = entityNode.annotationIncludes(GETTER_ANNOT_EXPR);
            annotationSetter = entityNode.annotationIncludes(SETTER_ANNOT_EXPR);

            StringBuilder comment = new StringBuilder();
            List<ParametreOrField> declaredFields = entityNode.getFields();

            boolean passedGetter = false;
            boolean passedSetter = false;

            for (ParametreOrField fieldNode : declaredFields) {

                // ignore serial version uid field, static fields, and fields annotated with @Transient
                if (fieldNode.isStatic() || fieldNode.getName().equals(SERIAL_VERSION_UID) || fieldNode.getAnnotations().contains(TRANSIENT_ANNOT_EXPR)) {
                    continue;
                }

                passedGetter = annotationGetter || hasGetMethod(fieldNode, entityNode);
                passedSetter = annotationSetter || hasSetMethod(fieldNode, entityNode);

                if(!passedGetter || !passedSetter){
                    List<Declaration> superClasses = getSuperClassDeclarations(entityNode);
                    for(Declaration superClass: superClasses){
                        if (!passedGetter && hasGetMethod(fieldNode, superClass)) {
                            passedGetter = true;
                        }
                        if (!passedSetter && hasSetMethod(fieldNode, superClass)) {
                            passedSetter = true;
                        }
                    }
                }

                if (!passedGetter) {
                        comment.append("Missing Getter of <").append(fieldNode.getName()).append(">. ");
                }
                if (!passedSetter) {
                    comment.append("Missing Setter of <").append(fieldNode.getName()).append(">. ");
                }
            }

            if (!passedGetter || !passedSetter) {
                Smell smell = initSmell(entityNode).setName("MissingGetterSetter").setComment(comment.toString());
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
        return provideGetsSetsFieldsRule(entityDeclarations);
    }

}
