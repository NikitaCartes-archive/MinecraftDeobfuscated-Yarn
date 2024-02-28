package net.minecraft.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@code GameTest} is used to tell the test framework that the annotated method is a test.
 * 
 * <p>{@code GameTest} methods must take 1 parameter of {@link net.minecraft.test.TestContext}
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTest {
	int tickLimit() default 100;

	String batchId() default "defaultBatch";

	boolean skyAccess() default false;

	int rotation() default 0;

	boolean required() default true;

	boolean manualOnly() default false;

	String templateName() default "";

	long duration() default 0L;

	int maxAttempts() default 1;

	int requiredSuccesses() default 1;
}
