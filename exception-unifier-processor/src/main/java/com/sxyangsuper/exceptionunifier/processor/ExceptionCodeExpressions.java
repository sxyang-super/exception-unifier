package com.sxyangsuper.exceptionunifier.processor;

import com.sun.tools.javac.tree.JCTree;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class ExceptionCodeExpressions {
    private final JCTree.JCLiteral codeExpression;
    private final JCTree.JCLiteral messageExpression;

    public String getCodeExpressionValue() {
        return (String)codeExpression.value;
    }

    public void setCodeExpressionValue(final String value) {
        this.codeExpression.value = value;
    }

    public String getMessageExpressionValue() {
        return (String)messageExpression.value;
    }
}
