package com.sxyangsuper.exceptionunifier.processor;

import com.sun.tools.javac.tree.JCTree;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ExceptionCodeExpressions {
    private JCTree.JCLiteral codeExpression;
    private JCTree.JCLiteral messageExpression;
}
