package net.minecraft.util.annotation;

/**
 * An annotation, mostly on side-effect-free methods.
 * 
 * <p>Most methods annotated are getters that don't cause side-effects. Some of
 * those methods also may execute dry-runs.
 * 
 * <p>However, it is present on {@link
 * net.minecraft.entity.passive.BeeEntity.MoveToHiveGoal}, and its purpose in that
 * case is not yet clear.
 * 
 * <p>This annotation has class retention and can be applied to a wide range of
 * targets.
 * 
 * <p>This annotation is not {@link java.lang.annotation.Documented}, and hence
 * will not appear in the generated javadoc for annotated elements.
 */
public @interface Debug {
}
