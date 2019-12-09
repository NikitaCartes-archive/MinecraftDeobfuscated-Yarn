/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placer;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;

public class BlockPlacerType<P extends BlockPlacer> {
    public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK_PLACER = BlockPlacerType.register("simple_block_placer", SimpleBlockPlacer::new);
    public static final BlockPlacerType<DoublePlantPlacer> DOUBLE_PLANT_PLACER = BlockPlacerType.register("double_plant_placer", DoublePlantPlacer::new);
    public static final BlockPlacerType<ColumnPlacer> COLUMN_PLACER = BlockPlacerType.register("column_placer", ColumnPlacer::new);
    private final Function<Dynamic<?>, P> deserializer;

    private static <P extends BlockPlacer> BlockPlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
        return Registry.register(Registry.BLOCK_PLACER_TYPE, id, new BlockPlacerType<P>(deserializer));
    }

    private BlockPlacerType(Function<Dynamic<?>, P> deserializer) {
        this.deserializer = deserializer;
    }

    public P deserialize(Dynamic<?> dynamic) {
        return (P)((BlockPlacer)this.deserializer.apply(dynamic));
    }
}

