/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.StringUtils;

public class GeneratorOptions {
    public static final MapCodec<GeneratorOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)Codec.LONG.fieldOf("seed")).stable().forGetter(GeneratorOptions::getSeed), ((MapCodec)Codec.BOOL.fieldOf("generate_features")).orElse(true).stable().forGetter(GeneratorOptions::shouldGenerateStructures), ((MapCodec)Codec.BOOL.fieldOf("bonus_chest")).orElse(false).stable().forGetter(GeneratorOptions::hasBonusChest), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter(generatorOptions -> generatorOptions.legacyCustomOptions)).apply((Applicative<GeneratorOptions, ?>)instance, instance.stable(GeneratorOptions::new)));
    public static final GeneratorOptions DEMO_OPTIONS = new GeneratorOptions("North Carolina".hashCode(), true, true);
    private final long seed;
    private final boolean generateStructures;
    private final boolean bonusChest;
    private final Optional<String> legacyCustomOptions;

    public GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest) {
        this(seed, generateStructures, bonusChest, Optional.empty());
    }

    public static GeneratorOptions createRandom() {
        return new GeneratorOptions(GeneratorOptions.getRandomSeed(), true, false);
    }

    private GeneratorOptions(long seed, boolean generateStructures, boolean bonusChest, Optional<String> legacyCustomOptions) {
        this.seed = seed;
        this.generateStructures = generateStructures;
        this.bonusChest = bonusChest;
        this.legacyCustomOptions = legacyCustomOptions;
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateStructures() {
        return this.generateStructures;
    }

    public boolean hasBonusChest() {
        return this.bonusChest;
    }

    public boolean isLegacyCustomizedType() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorOptions withBonusChest(boolean bonusChest) {
        return new GeneratorOptions(this.seed, this.generateStructures, bonusChest, this.legacyCustomOptions);
    }

    public GeneratorOptions withStructures(boolean structures) {
        return new GeneratorOptions(this.seed, structures, this.bonusChest, this.legacyCustomOptions);
    }

    public GeneratorOptions withSeed(long seed) {
        return new GeneratorOptions(seed, this.generateStructures, this.bonusChest, this.legacyCustomOptions);
    }

    public static long parseSeed(String seed) {
        if (StringUtils.isEmpty(seed = seed.trim())) {
            return GeneratorOptions.getRandomSeed();
        }
        try {
            return Long.parseLong(seed);
        } catch (NumberFormatException numberFormatException) {
            return seed.hashCode();
        }
    }

    public static long getRandomSeed() {
        return Random.create().nextLong();
    }
}

