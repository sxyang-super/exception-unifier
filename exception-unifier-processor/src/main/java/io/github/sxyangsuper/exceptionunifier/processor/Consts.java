package io.github.sxyangsuper.exceptionunifier.processor;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Consts {
    /* Processor */

    /* default */
    static final String LOG_PREFIX = "[EX UNIFIER]";
    /* default */
    static final String JC_TREE_PREFIX_ENUM_VARIABLE = "/*public static final*/";
    /* default */
    static final String JC_VARIABLE_NAME_CODE = "code";
    /* default */
    static final String JC_VARIABLE_NAME_MESSAGE = "message";
    /* default */
    static final String ANNOTATION_PACKAGE_NAME_PREFIX = "io.github.sxyangsuper.exceptionunifier";
    /* default */
    static final String CLASS_QN_NAME_ORG_JETBRAINS_JPS_JAVAC_API_WRAPPERS = "org.jetbrains.jps.javac.APIWrappers";

    /* System properties */

    public static final String PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG = "annotation.processor.debug";
    public static final String PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG = "false";
}
