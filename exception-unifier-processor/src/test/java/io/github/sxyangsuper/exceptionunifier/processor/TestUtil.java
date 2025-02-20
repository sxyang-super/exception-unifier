package io.github.sxyangsuper.exceptionunifier.processor;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInfo;

import java.io.File;
import java.lang.reflect.Method;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestUtil {

    @NotNull
    public static String getPackageCorrespondingTestDirectoryPath(final TestInfo testInfo) {
        final Method testMethod = testInfo
            .getTestMethod()
            .orElseThrow(() -> new RuntimeException("fail to get test method"));

        final String generateFileTargetDirectoryPath = testMethod
            .getDeclaringClass()
            .getName()
            .replace(".", File.separator)
            .replace("$", File.separator);

        return String.join(File.separator, generateFileTargetDirectoryPath, testMethod.getName());
    }
}
