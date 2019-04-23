/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.Identifier;

public interface CriterionConditions {
    public Identifier getId();

    default public JsonElement toJson() {
        return JsonNull.INSTANCE;
    }
}

