/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureType;
import net.minecraft.world.gen.feature.BasicTempleStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

public class JungleTempleFeature
extends BasicTempleStructureFeature {
    public static final Codec<JungleTempleFeature> CODEC = JungleTempleFeature.createCodec(JungleTempleFeature::new);

    public JungleTempleFeature(StructureFeature.Config config) {
        super(JungleTempleGenerator::new, 12, 15, config);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.JUNGLE_TEMPLE;
    }
}

