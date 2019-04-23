/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;

@Environment(value=EnvType.CLIENT)
public interface MultipartModelSelector {
    public static final MultipartModelSelector TRUE = stateFactory -> blockState -> true;
    public static final MultipartModelSelector FALSE = stateFactory -> blockState -> false;

    public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> var1);
}

