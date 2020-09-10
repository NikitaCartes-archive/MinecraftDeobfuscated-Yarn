/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.ActionResult;

public class TypedActionResult<T> {
    private final ActionResult result;
    private final T value;

    public TypedActionResult(ActionResult result, T value) {
        this.result = result;
        this.value = value;
    }

    public ActionResult getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }

    public static <T> TypedActionResult<T> success(T data) {
        return new TypedActionResult<T>(ActionResult.SUCCESS, data);
    }

    public static <T> TypedActionResult<T> consume(T data) {
        return new TypedActionResult<T>(ActionResult.CONSUME, data);
    }

    public static <T> TypedActionResult<T> pass(T data) {
        return new TypedActionResult<T>(ActionResult.PASS, data);
    }

    public static <T> TypedActionResult<T> fail(T data) {
        return new TypedActionResult<T>(ActionResult.FAIL, data);
    }

    public static <T> TypedActionResult<T> success(T data, boolean swingHand) {
        return swingHand ? TypedActionResult.success(data) : TypedActionResult.consume(data);
    }
}

