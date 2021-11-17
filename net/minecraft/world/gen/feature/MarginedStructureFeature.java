/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class MarginedStructureFeature<C extends FeatureConfig>
extends StructureFeature<C> {
    public MarginedStructureFeature(Codec<C> codec, class_6834<C> arg) {
        super(codec, arg);
    }

    public MarginedStructureFeature(Codec<C> codec, class_6834<C> arg, PostPlacementProcessor postPlacementProcessor) {
        super(codec, arg, postPlacementProcessor);
    }

    @Override
    public BlockBox calculateBoundingBox(BlockBox box) {
        return super.calculateBoundingBox(box).expand(12);
    }
}

