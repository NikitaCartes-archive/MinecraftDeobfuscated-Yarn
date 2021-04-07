package net.minecraft.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * Specifies that all methods in the annotated package/class return nonnull
 * values unless nullability is specified with a separate annotation.
 * 
 * <p>While this annotation is meant to be a package/class annotation, it can
 * be applied to anything as it does not restrict its targets.
 * 
 * <p>This is pretty much identical to {@link MethodsReturnNonnullByDefault}, but
 * this is only used by the package info for a few math classes.
 * 
 * @see MethodsReturnNonnullByDefault
 */
@Nonnull
@TypeQualifierDefault({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MathMethodsReturnNonnullByDefault {
}
