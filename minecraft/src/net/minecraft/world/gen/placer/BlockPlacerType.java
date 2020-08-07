package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer> {
	public static final BlockPlacerType<SimpleBlockPlacer> field_21223 = register("simple_block_placer", SimpleBlockPlacer.CODEC);
	public static final BlockPlacerType<DoublePlantPlacer> field_21224 = register("double_plant_placer", DoublePlantPlacer.field_24868);
	public static final BlockPlacerType<ColumnPlacer> field_21225 = register("column_placer", ColumnPlacer.CODEC);
	private final Codec<P> field_24866;

	private static <P extends BlockPlacer> BlockPlacerType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.BLOCK_PLACER_TYPE, id, new BlockPlacerType<>(codec));
	}

	private BlockPlacerType(Codec<P> codec) {
		this.field_24866 = codec;
	}

	public Codec<P> method_28674() {
		return this.field_24866;
	}
}
