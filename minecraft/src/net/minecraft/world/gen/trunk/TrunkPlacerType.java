package net.minecraft.world.gen.trunk;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_5211;
import net.minecraft.class_5212;
import net.minecraft.class_5214;
import net.minecraft.class_5215;
import net.minecraft.util.registry.Registry;

public class TrunkPlacerType<P extends TrunkPlacer> {
	public static final TrunkPlacerType<StraightTrunkPlacer> STRAIGHT_TRUNK_PLACER = register("straight_trunk_placer", StraightTrunkPlacer::new);
	public static final TrunkPlacerType<ForkingTrunkPlacer> FORKING_TRUNK_PLACER = register("forking_trunk_placer", ForkingTrunkPlacer::new);
	public static final TrunkPlacerType<class_5214> GIANT_TRUNK_PLACER = register("giant_trunk_placer", class_5214::new);
	public static final TrunkPlacerType<class_5215> MEGA_JUNGLE_TRUNK_PLACER = register("mega_jungle_trunk_placer", class_5215::new);
	public static final TrunkPlacerType<class_5211> DARK_OAK_TRUNK_PLACER = register("dark_oak_trunk_placer", class_5211::new);
	public static final TrunkPlacerType<class_5212> FANCY_TRUNK_PLACER = register("fancy_trunk_placer", class_5212::new);
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
