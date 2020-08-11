/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;

public class BlockPlacerType<P extends BlockPlacer> {
    public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK_PLACER = BlockPlacerType.register("simple_block_placer", SimpleBlockPlacer.CODEC);
    public static final BlockPlacerType<DoublePlantPlacer> DOUBLE_PLANT_PLACER = BlockPlacerType.register("double_plant_placer", DoublePlantPlacer.CODEC);
    public static final BlockPlacerType<ColumnPlacer> COLUMN_PLACER = BlockPlacerType.register("column_placer", ColumnPlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends BlockPlacer> BlockPlacerType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.BLOCK_PLACER_TYPE, id, new BlockPlacerType<P>(codec));
    }

    private BlockPlacerType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

