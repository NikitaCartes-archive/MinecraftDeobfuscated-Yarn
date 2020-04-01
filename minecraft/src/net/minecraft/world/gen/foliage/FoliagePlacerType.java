package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class FoliagePlacerType<P extends FoliagePlacer> {
	public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = register(
		"blob_foliage_placer", BlobFoliagePlacer::new, BlobFoliagePlacer::method_26653
	);
	public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = register(
		"spruce_foliage_placer", SpruceFoliagePlacer::new, SpruceFoliagePlacer::method_26656
	);
	public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = register(
		"pine_foliage_placer", PineFoliagePlacer::new, PineFoliagePlacer::method_26655
	);
	public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = register(
		"acacia_foliage_placer", AcaciaFoliagePlacer::new, AcaciaFoliagePlacer::method_26652
	);
	private final Function<Dynamic<?>, P> deserializer;
	private final Function<Random, P> field_23591;

	private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer, Function<Random, P> function) {
		return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id, new FoliagePlacerType<>(deserializer, function));
	}

	private FoliagePlacerType(Function<Dynamic<?>, P> deserializer, Function<Random, P> function) {
		this.deserializer = deserializer;
		this.field_23591 = function;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}

	public P method_26654(Random random) {
		return (P)this.field_23591.apply(random);
	}
}
