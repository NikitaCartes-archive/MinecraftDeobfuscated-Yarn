package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TrunkPlacerType<P extends TrunkPlacer> {
	public static final TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer::new);
	public static final TrunkPlacerType<ForkingTrunkPlacer> FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkingTrunkPlacer::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends TrunkPlacer> TrunkPlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
		return Registry.register(Registry.TRUNK_PLACER_TYPE, id, new TrunkPlacerType<>(deserializer));
	}

	private TrunkPlacerType(Function<Dynamic<?>, P> deserializer) {
		this.deserializer = deserializer;
	}

	public P deserialize(Dynamic<?> data) {
		return (P)this.deserializer.apply(data);
	}
}
