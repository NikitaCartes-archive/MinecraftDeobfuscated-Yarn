package net.minecraft.world.gen.foliage;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.JungleFoliagePlacer;
import net.minecraft.world.gen.feature.MegaPineFoliagePlacer;

public class FoliagePlacerType<P extends FoliagePlacer> {
	public static final FoliagePlacerType<BlobFoliagePlacer> field_21299 = register("blob_foliage_placer", BlobFoliagePlacer.CODEC);
	public static final FoliagePlacerType<SpruceFoliagePlacer> field_21300 = register("spruce_foliage_placer", SpruceFoliagePlacer.field_24936);
	public static final FoliagePlacerType<PineFoliagePlacer> field_21301 = register("pine_foliage_placer", PineFoliagePlacer.CODEC);
	public static final FoliagePlacerType<AcaciaFoliagePlacer> field_21302 = register("acacia_foliage_placer", AcaciaFoliagePlacer.CODEC);
	public static final FoliagePlacerType<BushFoliagePlacer> field_24161 = register("bush_foliage_placer", BushFoliagePlacer.CODEC);
	public static final FoliagePlacerType<LargeOakFoliagePlacer> field_24162 = register("fancy_foliage_placer", LargeOakFoliagePlacer.CODEC);
	public static final FoliagePlacerType<JungleFoliagePlacer> field_24163 = register("jungle_foliage_placer", JungleFoliagePlacer.CODEC);
	public static final FoliagePlacerType<MegaPineFoliagePlacer> field_24164 = register("mega_pine_foliage_placer", MegaPineFoliagePlacer.CODEC);
	public static final FoliagePlacerType<DarkOakFoliagePlacer> field_24165 = register("dark_oak_foliage_placer", DarkOakFoliagePlacer.CODEC);
	private final Codec<P> codec;

	private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.FOLIAGE_PLACER_TYPE, id, new FoliagePlacerType<>(codec));
	}

	private FoliagePlacerType(Codec<P> codec) {
		this.codec = codec;
	}

	public Codec<P> getCodec() {
		return this.codec;
	}
}
