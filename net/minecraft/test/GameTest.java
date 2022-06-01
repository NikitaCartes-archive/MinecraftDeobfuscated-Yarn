/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface GameTest {
    public int tickLimit() default 100;

    public String batchId() default "defaultBatch";

    public int rotation() default 0;

    public boolean required() default true;

    public String templateName() default "";

    public long duration() default 0L;

    public int maxAttempts() default 1;

    public int requiredSuccesses() default 1;
}

