package com.sxyangsuper.exceptionunifier.processor;

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
    static final String ANNOTATION_PACKAGE_NAME_PREFIX="com.sxyangsuper.exceptionunifier";

    /* System properties */

    public static final String PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG = "annotation.processor.debug";
    public static final String PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG = "false";

    /* Processor args */

    /* default */
    static final String PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX = "exceptionCodePrefix";
    /* default */
    static final String PROCESSOR_ARG_NAME_REMOTE_BASE_URL = "exceptionCodeRemoteBaseUrl";
    /* default */
    static final String PROCESSOR_ARG_NAME_MODULE_ID = "exceptionCodeModuleId";

    /* Remote exception code prefix supplier */

    /* default */
    static final String REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX = "/prefix";

    /* default */
    static final String REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS = "/exception-enums/bulk";
    /* default */
    static final String REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID = "moduleId";
}
