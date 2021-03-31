package net.minecraft.obfuscate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * This annotation applies to some unobfuscated elements within the Minecraft
 * source code.
 * 
 * <p>Its behavior appears as follows:
 * <ul>
 * <li>The annotation itself is not obfuscated.</li>
 * <li>If a class is annotated, it is not obfuscated. It's not yet clear if its
 * members will always become deobfuscated as well.</li>
 * <li>If a member is annotated, it and its containing class is not obfuscated,
 * but other members in the same class may stay obfuscated.</li>
 * </ul>
 * 
 * <p>Visit the use page for the usage of this annotation.
 * 
 * <p>In addition, single-abstract-method interfaces used as lambda expressions
 * always have their single abstract method unobfuscated per proguard behavior.
 * 
 * <p>This annotation is not {@link java.lang.annotation.Documented}, and hence
 * will not appear in the generated javadoc for annotated elements.
 * 
 * @see net.minecraft.util.CubicSampler.RgbFetcher
 * @see net.minecraft.world.level.ColorResolver#getColor
 */
@TypeQualifierDefault({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface DontObfuscate {
}
