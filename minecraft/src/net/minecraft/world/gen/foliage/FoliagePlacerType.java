package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_5205;
import net.minecraft.class_5206;
import net.minecraft.class_5207;
import net.minecraft.class_5209;
import net.minecraft.class_5210;
import net.minecraft.util.registry.Registry;

public class FoliagePlacerType<P extends FoliagePlacer> {
	public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer::new);
	public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer::new);
	public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer::new);
	public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer::new);
	public static final FoliagePlacerType<class_5205> BUSH_FOLIAGE_PLACER = register("bush_foliage_placer", class_5205::new);
	public static final FoliagePlacerType<class_5207> FANCY_FOLIAGE_PLACER = register("fancy_foliage_placer", class_5207::new);
	public static final FoliagePlacerType<class_5209> JUNGLE_FOLIAGE_PLACER = register("jungle_foliage_placer", class_5209::new);
	public static final FoliagePlacerType<class_5210> MEGA_PINE_FOLIAGE_PLACER = register("mega_pine_foliage_placer", class_5210::new);
	public static final FoliagePlacerType<class_5206> DARK_OAK_FOLIAGE_PLACER = register("dark_oak_foliage_placer", class_5206::new);
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
