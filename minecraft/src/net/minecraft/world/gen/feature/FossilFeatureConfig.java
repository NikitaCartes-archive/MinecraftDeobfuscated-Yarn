package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

public class FossilFeatureConfig implements FeatureConfig {
	public static final Codec<FossilFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.listOf().fieldOf("fossil_structures").forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilStructures),
					Identifier.CODEC.listOf().fieldOf("overlay_structures").forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayStructures),
					StructureProcessorType.REGISTRY_CODEC.fieldOf("fossil_processors").forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilProcessors),
					StructureProcessorType.REGISTRY_CODEC.fieldOf("overlay_processors").forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayProcessors),
					Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed").forGetter(fossilFeatureConfig -> fossilFeatureConfig.maxEmptyCorners)
				)
				.apply(instance, FossilFeatureConfig::new)
	);
	public final List<Identifier> fossilStructures;
	public final List<Identifier> overlayStructures;
	public final Supplier<StructureProcessorList> fossilProcessors;
	public final Supplier<StructureProcessorList> overlayProcessors;
	public final int maxEmptyCorners;

	public FossilFeatureConfig(
		List<Identifier> fossilStructures,
		List<Identifier> overlayStructures,
		Supplier<StructureProcessorList> fossilProcessors,
		Supplier<StructureProcessorList> overlayProcessors,
		int maxEmptyCorners
	) {
		if (fossilStructures.isEmpty()) {
			throw new IllegalArgumentException("Fossil structure lists need at least one entry");
		} else if (fossilStructures.size() != overlayStructures.size()) {
			throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
		} else {
			this.fossilStructures = fossilStructures;
			this.overlayStructures = overlayStructures;
			this.fossilProcessors = fossilProcessors;
			this.overlayProcessors = overlayProcessors;
			this.maxEmptyCorners = maxEmptyCorners;
		}
	}

	public FossilFeatureConfig(
		List<Identifier> fossilStructures,
		List<Identifier> overlayStructures,
		StructureProcessorList fossilProcessors,
		StructureProcessorList overlayProcessors,
		int maxEmptyCorners
	) {
		this(fossilStructures, overlayStructures, () -> fossilProcessors, () -> overlayProcessors, maxEmptyCorners);
	}
}
