package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.Opcodes;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Consts {
    public static final int ASM_API_VERSION = Opcodes.ASM8;
    public static final String METHOD_NAME_INIT = "<init>";
    public static final String METHOD_NAME_CLINIT = "<clinit>";
}
