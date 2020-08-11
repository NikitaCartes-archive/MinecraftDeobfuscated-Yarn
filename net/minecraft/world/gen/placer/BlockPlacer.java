/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.placer.BlockPlacerType;

public abstract class BlockPlacer {
    public static final Codec<BlockPlacer> TYPE_CODEC = Registry.BLOCK_PLACER_TYPE.dispatch(BlockPlacer::getType, BlockPlacerType::getCodec);

    public abstract void generate(WorldAccess var1, BlockPos var2, BlockState var3, Random var4);

    protected abstract BlockPlacerType<?> getType();
}

