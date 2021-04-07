/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;
import org.jetbrains.annotations.NotNull;

/**
 * Specifies that all methods in the annotated package/class return nonnull
 * values unless nullability is specified with a separate annotation.
 * 
 * <p>While this annotation is meant to be a package/class annotation, it can
 * be applied to anything as it does not restrict its targets.
 * 
 * @see ClientMethodsReturnNonnullByDefault
 * @see MathMethodsReturnNonnullByDefault
 */
@NotNull
@TypeQualifierDefault(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface MethodsReturnNonnullByDefault {
}

