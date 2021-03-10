/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class FossilFeatureConfig
implements FeatureConfig {
    public static final Codec<FossilFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.listOf().fieldOf("fossil_structures")).forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilStructures), ((MapCodec)Identifier.CODEC.listOf().fieldOf("overlay_structures")).forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayStructures), ((MapCodec)StructureProcessorType.REGISTRY_CODEC.fieldOf("fossil_processors")).forGetter(fossilFeatureConfig -> fossilFeatureConfig.fossilProcessors), ((MapCodec)StructureProcessorType.REGISTRY_CODEC.fieldOf("overlay_processors")).forGetter(fossilFeatureConfig -> fossilFeatureConfig.overlayProcessors), ((MapCodec)Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed")).forGetter(fossilFeatureConfig -> fossilFeatureConfig.maxEmptyCorners)).apply((Applicative<FossilFeatureConfig, ?>)instance, FossilFeatureConfig::new));
    public final List<Identifier> fossilStructures;
    public final List<Identifier> overlayStructures;
    public final Supplier<StructureProcessorList> fossilProcessors;
    public final Supplier<StructureProcessorList> overlayProcessors;
    public final int maxEmptyCorners;

    public FossilFeatureConfig(List<Identifier> fossilStructures, List<Identifier> overlayStructures, Supplier<StructureProcessorList> fossilProcessors, Supplier<StructureProcessorList> overlayProcessors, int maxEmptyCorners) {
        if (fossilStructures.isEmpty()) {
            throw new IllegalArgumentException("Fossil structure lists need at least one entry");
        }
        if (fossilStructures.size() != overlayStructures.size()) {
            throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
        }
        this.fossilStructures = fossilStructures;
        this.overlayStructures = overlayStructures;
        this.fossilProcessors = fossilProcessors;
        this.overlayProcessors = overlayProcessors;
        this.maxEmptyCorners = maxEmptyCorners;
    }

    public FossilFeatureConfig(List<Identifier> fossilStructures, List<Identifier> overlayStructures, StructureProcessorList fossilProcessors, StructureProcessorList overlayProcessors, int maxEmptyCorners) {
        this(fossilStructures, overlayStructures, () -> fossilProcessors, () -> overlayProcessors, maxEmptyCorners);
    }
}

