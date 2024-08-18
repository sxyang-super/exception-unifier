package com.sxyangsuper.exceptionunifier.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static com.sxyangsuper.exceptionunifier.processor.Consts.JC_TREE_PREFIX_ENUM_VARIABLE;
import static com.sxyangsuper.exceptionunifier.processor.Consts.JC_VARIABLE_NAME_CODE;
import static com.sxyangsuper.exceptionunifier.processor.ProcessorUtil.getCodeIndex;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ProcessorUtilTest {

    @Nested
    class TestGetCodeIndex {

        @Test
        void should_throw_exception_given_jc_class_decl_defs_is_empty() {
            JCClassDecl jcClassDecl = Mockito.mock(JCClassDecl.class);
            final com.sun.tools.javac.util.Name jcClassDeclSimpleName = Mockito.mock(com.sun.tools.javac.util.Name.class);
            //noinspection unchecked
            jcClassDecl.defs = Mockito.mock(com.sun.tools.javac.util.List.class);

            when(jcClassDecl.getSimpleName()).thenReturn(jcClassDeclSimpleName);
            when(jcClassDeclSimpleName.toString()).thenReturn("Sample");
            when(jcClassDecl.defs.stream()).thenReturn(Stream.of());

            assertThrows(ExUnifierProcessException.class, () -> getCodeIndex(jcClassDecl));
        }

        @Test
        void should_throw_exception_given_jc_class_decl_defs_all_are_not_variable() {
            JCClassDecl jcClassDecl = Mockito.mock(JCClassDecl.class);
            final com.sun.tools.javac.util.Name jcClassDeclSimpleName = Mockito.mock(com.sun.tools.javac.util.Name.class);
            //noinspection unchecked
            jcClassDecl.defs = Mockito.mock(com.sun.tools.javac.util.List.class);
            JCTree def = Mockito.mock(JCTree.class);

            when(jcClassDecl.getSimpleName()).thenReturn(jcClassDeclSimpleName);
            when(jcClassDeclSimpleName.toString()).thenReturn("Sample");
            when(jcClassDecl.defs.stream()).thenReturn(Stream.of(def));
            when(def.getKind()).thenReturn(Tree.Kind.ENUM);

            assertThrows(ExUnifierProcessException.class, () -> getCodeIndex(jcClassDecl));
        }

        @Test
        void should_throw_exception_given_jc_class_decl_defs_all_are_enum_variable() {
            JCClassDecl jcClassDecl = Mockito.mock(JCClassDecl.class);
            final com.sun.tools.javac.util.Name jcClassDeclSimpleName = Mockito.mock(com.sun.tools.javac.util.Name.class);
            //noinspection unchecked
            jcClassDecl.defs = Mockito.mock(com.sun.tools.javac.util.List.class);
            JCTree def = Mockito.mock(JCTree.class);

            when(jcClassDecl.getSimpleName()).thenReturn(jcClassDeclSimpleName);
            when(jcClassDeclSimpleName.toString()).thenReturn("Sample");
            when(jcClassDecl.defs.stream()).thenReturn(Stream.of(def));
            when(def.getKind()).thenReturn(Tree.Kind.VARIABLE);
            when(def.toString()).thenReturn(JC_TREE_PREFIX_ENUM_VARIABLE);

            assertThrows(ExUnifierProcessException.class, () -> getCodeIndex(jcClassDecl));
        }

        @Test
        void should_throw_exception_given_jc_class_decl_defs_all_are_not_code() {
            JCClassDecl jcClassDecl = Mockito.mock(JCClassDecl.class);
            final com.sun.tools.javac.util.Name jcClassDeclSimpleName = Mockito.mock(com.sun.tools.javac.util.Name.class);
            //noinspection unchecked
            jcClassDecl.defs = Mockito.mock(com.sun.tools.javac.util.List.class);
            JCTree.JCVariableDecl def = Mockito.mock(JCTree.JCVariableDecl.class);
            def.name = Mockito.mock(com.sun.tools.javac.util.Name.class);

            when(jcClassDecl.getSimpleName()).thenReturn(jcClassDeclSimpleName);
            when(jcClassDeclSimpleName.toString()).thenReturn("Sample");
            when(jcClassDecl.defs.stream()).thenReturn(Stream.of(def));
            when(def.getKind()).thenReturn(Tree.Kind.VARIABLE);
            when(def.toString()).thenReturn("");
            when(def.name.toString()).thenReturn("");

            assertThrows(ExUnifierProcessException.class, () -> getCodeIndex(jcClassDecl));
        }

        @Test
        void should_return_0_given_jc_class_decl_first_def_is_code_field() {
            JCClassDecl jcClassDecl = Mockito.mock(JCClassDecl.class);
            final com.sun.tools.javac.util.Name jcClassDeclSimpleName = Mockito.mock(com.sun.tools.javac.util.Name.class);
            //noinspection unchecked
            jcClassDecl.defs = Mockito.mock(com.sun.tools.javac.util.List.class);
            JCTree.JCVariableDecl def = Mockito.mock(JCTree.JCVariableDecl.class);
            def.name = Mockito.mock(com.sun.tools.javac.util.Name.class);

            when(jcClassDecl.getSimpleName()).thenReturn(jcClassDeclSimpleName);
            when(jcClassDeclSimpleName.toString()).thenReturn("Sample");
            when(jcClassDecl.defs.stream()).thenReturn(Stream.of(def));
            when(def.getKind()).thenReturn(Tree.Kind.VARIABLE);
            when(def.toString()).thenReturn("");
            when(def.name.toString()).thenReturn(JC_VARIABLE_NAME_CODE);

            final int codeIndex = getCodeIndex(jcClassDecl);

            assertSame(codeIndex, 0);
        }
    }
}
