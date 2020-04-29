package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.JungleFoliagePlacer;
import net.minecraft.world.gen.feature.MegaPineFoliagePlacer;

public class FoliagePlacerType<P extends FoliagePlacer> {
	public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer::new);
	public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer::new);
	public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer::new);
	public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer::new);
	public static final FoliagePlacerType<BushFoliagePlacer> BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", BushFoliagePlacer::new);
	public static final FoliagePlacerType<LargeOakFoliagePlacer> FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", LargeOakFoliagePlacer::new);
	public static final FoliagePlacerType<JungleFoliagePlacer> JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", JungleFoliagePlacer::new);
	public static final FoliagePlacerType<MegaPineFoliagePlacer> MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", MegaPineFoliagePlacer::new);
	public static final FoliagePlacerType<DarkOakFoliagePlacer> DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", DarkOakFoliagePlacer::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
		return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id, new FoliagePlacerType<>(deserializer));
	}

	private FoliagePlacerType(Function<Dynamic<?>, P> deserializer) {
		this.deserializer = deserializer;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}
}
