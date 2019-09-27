/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class TypedActionResult<T> {
    private final ActionResult result;
    private final T value;
    private final boolean swingArm;

    public TypedActionResult(ActionResult actionResult, T object, boolean bl) {
        this.result = actionResult;
        this.value = object;
        this.swingArm = bl;
    }

    public ActionResult getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldSwingArm() {
        return this.swingArm;
    }

    public static <T> TypedActionResult<T> successWithSwing(T object) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, object, true);
    }

    public static <T> TypedActionResult<T> successWithoutSwing(T object) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, object, false);
    }

    public static <T> TypedActionResult<T> pass(@Nullable T object) {
        return new TypedActionResult<T>(ActionResult.PASS, object, false);
    }

    public static <T> TypedActionResult<T> fail(@Nullable T object) {
        return new TypedActionResult<T>(ActionResult.FAIL, object, false);
    }
}

