/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.block.Block;

/**
 * A supplier of a block state JSON definition.
 */
public interface BlockStateSupplier
extends Supplier<JsonElement> {
    public Block getBlock();
}

