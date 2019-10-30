package net.minecraft.world.gen.placer;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer> {
	public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK_PLACER = register("simple_block_placer", SimpleBlockPlacer::new);
	public static final BlockPlacerType<DoublePlantPlacer> DOUBLE_PLANT_PLACER = register("double_plant_placer", DoublePlantPlacer::new);
	public static final BlockPlacerType<ColumnPlacer> COLUMN_PLACER = register("column_placer", ColumnPlacer::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends BlockPlacer> BlockPlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
		return Registry.register(Registry.BLOCK_PLACER_TYPE, id, new BlockPlacerType<>(deserializer));
	}

	private BlockPlacerType(Function<Dynamic<?>, P> deserializer) {
		this.deserializer = deserializer;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}
}
