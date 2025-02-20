package io.github.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.collection.CollUtil;
import lombok.NoArgsConstructor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class ProcessorUtil {

    /* default */
    static boolean isDescendantOfTargetInterface(
        final Types typeUtils,
        final TypeElement typeElement,
        final String targetInterfaceQualifiedName
    ) {
        final List<? extends TypeMirror> superInterfaceTypes = typeElement.getInterfaces();

        if (CollUtil.isEmpty(superInterfaceTypes)) {
            return false;
        }

        for (final TypeMirror superInterfaceType : superInterfaceTypes) {
            final TypeElement superInterfaceTypeElement = (TypeElement) typeUtils.asElement(superInterfaceType);
            final String interfaceQualifiedName = superInterfaceTypeElement.getQualifiedName().toString();
            if (interfaceQualifiedName.equals(targetInterfaceQualifiedName)) {
                return true;
            }

            if (isDescendantOfTargetInterface(typeUtils, superInterfaceTypeElement, targetInterfaceQualifiedName)) {
                return true;
            }
        }

        return false;
    }
}
