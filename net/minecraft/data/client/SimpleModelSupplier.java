/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;

public class SimpleModelSupplier
implements Supplier<JsonElement> {
    private final Identifier parent;

    public SimpleModelSupplier(Identifier parent) {
        this.parent = parent;
    }

    @Override
    public JsonElement get() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parent", this.parent.toString());
        return jsonObject;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

