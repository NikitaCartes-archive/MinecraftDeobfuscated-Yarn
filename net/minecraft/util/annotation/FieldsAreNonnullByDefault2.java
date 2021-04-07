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
 * Specifies that all fields in the annotated package/class are nonnull
 * unless nullability is specified with a separate annotation.
 * 
 * <p>While this annotation is meant to be a package/class annotation, it can
 * be applied to anything as it does not restrict its targets.
 * 
 * <p>This is pretty much identical to {@link FieldsAreNonnullByDefault} that they
 * are used on different package info files and never coexist.
 * 
 * @see ClientFieldsAreNonnullByDefault
 * @see FieldsAreNonnullByDefault
 */
@NotNull
@TypeQualifierDefault(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface FieldsAreNonnullByDefault2 {
}

