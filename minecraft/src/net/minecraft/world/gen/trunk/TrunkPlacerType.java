package net.minecraft.world.gen.trunk;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class TrunkPlacerType<P extends TrunkPlacer> {
	public static final TrunkPlacerType<StraightTrunkPlacer> field_23763 = register("straight_trunk_placer", StraightTrunkPlacer.CODEC);
	public static final TrunkPlacerType<ForkingTrunkPlacer> field_23764 = register("forking_trunk_placer", ForkingTrunkPlacer.CODEC);
	public static final TrunkPlacerType<GiantTrunkPlacer> field_24171 = register("giant_trunk_placer", GiantTrunkPlacer.CODEC);
	public static final TrunkPlacerType<MegaJungleTrunkPlacer> field_24172 = register("mega_jungle_trunk_placer", MegaJungleTrunkPlacer.CODEC);
	public static final TrunkPlacerType<DarkOakTrunkPlacer> field_24173 = register("dark_oak_trunk_placer", DarkOakTrunkPlacer.CODEC);
	public static final TrunkPlacerType<LargeOakTrunkPlacer> field_24174 = register("fancy_trunk_placer", LargeOakTrunkPlacer.CODEC);
	private final Codec<P> codec;

	private static <P extends TrunkPlacer> TrunkPlacerType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.TRUNK_PLACER_TYPE, id, new TrunkPlacerType<>(codec));
	}

	private TrunkPlacerType(Codec<P> codec) {
		this.codec = codec;
	}

	public Codec<P> getCodec() {
		return this.codec;
	}
}
