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
    private final boolean field_20683;

    public TypedActionResult(ActionResult actionResult, T object, boolean bl) {
        this.result = actionResult;
        this.value = object;
        this.field_20683 = bl;
    }

    public ActionResult getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_22429() {
        return this.field_20683;
    }

    public static <T> TypedActionResult<T> method_22427(T object) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, object, true);
    }

    public static <T> TypedActionResult<T> method_22428(T object) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, object, false);
    }

    public static <T> TypedActionResult<T> method_22430(@Nullable T object) {
        return new TypedActionResult<T>(ActionResult.PASS, object, false);
    }

    public static <T> TypedActionResult<T> method_22431(@Nullable T object) {
        return new TypedActionResult<T>(ActionResult.FAIL, object, false);
    }
}

