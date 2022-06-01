/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.world.gen.structure.BasicTempleStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class DesertPyramidStructure
extends BasicTempleStructure {
    public static final Codec<DesertPyramidStructure> CODEC = DesertPyramidStructure.createCodec(DesertPyramidStructure::new);

    public DesertPyramidStructure(Structure.Config config) {
        super(DesertTempleGenerator::new, 21, 21, config);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.DESERT_PYRAMID;
    }
}

