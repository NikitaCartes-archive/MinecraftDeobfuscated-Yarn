/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.world.gen.feature.BasicTempleStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class DesertPyramidFeature
extends BasicTempleStructureFeature {
    public static final Codec<DesertPyramidFeature> CODEC = DesertPyramidFeature.createCodec(DesertPyramidFeature::new);

    public DesertPyramidFeature(StructureFeature.Config config) {
        super(DesertTempleGenerator::new, 21, 21, config);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.DESERT_PYRAMID;
    }
}

