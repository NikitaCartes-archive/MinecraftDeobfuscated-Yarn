/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.dynamic.NumberCodecs;

public class StructureConfig {
    public static final Codec<StructureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)NumberCodecs.rangedInt(0, 4096).fieldOf("spacing")).forGetter(structureConfig -> structureConfig.spacing), ((MapCodec)NumberCodecs.rangedInt(0, 4096).fieldOf("separation")).forGetter(structureConfig -> structureConfig.separation), ((MapCodec)NumberCodecs.rangedInt(0, Integer.MAX_VALUE).fieldOf("salt")).forGetter(structureConfig -> structureConfig.salt)).apply((Applicative<StructureConfig, ?>)instance, StructureConfig::new)).comapFlatMap(structureConfig -> {
        if (structureConfig.spacing <= structureConfig.separation) {
            return DataResult.error("Spacing has to be smaller than separation");
        }
        return DataResult.success(structureConfig);
    }, Function.identity());
    private final int spacing;
    private final int separation;
    private final int salt;

    public StructureConfig(int spacing, int separation, int salt) {
        this.spacing = spacing;
        this.separation = separation;
        this.salt = salt;
    }

    public int getSpacing() {
        return this.spacing;
    }

    public int getSeparation() {
        return this.separation;
    }

    public int getSalt() {
        return this.salt;
    }
}

