/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.util.ActionResult;

public class TypedActionResult<T> {
    private final ActionResult result;
    private final T value;

    public TypedActionResult(ActionResult actionResult, T object) {
        this.result = actionResult;
        this.value = object;
    }

    public ActionResult getResult() {
        return this.result;
    }

    public T getValue() {
        return this.value;
    }
}

