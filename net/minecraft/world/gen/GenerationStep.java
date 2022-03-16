/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public class GenerationStep {

    public static enum Carver implements StringIdentifiable
    {
        AIR("air"),
        LIQUID("liquid");

        public static final Codec<Carver> CODEC;
        private static final Map<String, Carver> BY_NAME;
        private final String name;

        private Carver(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Carver byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Carver::values, Carver::byName);
            BY_NAME = Arrays.stream(Carver.values()).collect(Collectors.toMap(Carver::getName, carver -> carver));
        }
    }

    public static enum Feature implements StringIdentifiable
    {
        RAW_GENERATION("raw_generation"),
        LAKES("lakes"),
        LOCAL_MODIFICATIONS("local_modifications"),
        UNDERGROUND_STRUCTURES("underground_structures"),
        SURFACE_STRUCTURES("surface_structures"),
        STRONGHOLDS("strongholds"),
        UNDERGROUND_ORES("underground_ores"),
        UNDERGROUND_DECORATION("underground_decoration"),
        FLUID_SPRINGS("fluid_springs"),
        VEGETAL_DECORATION("vegetal_decoration"),
        TOP_LAYER_MODIFICATION("top_layer_modification");

        public static final Codec<Feature> CODEC;
        private static final Map<String, Feature> VALUES;
        private final String name;

        private Feature(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Feature get(String name) {
            return VALUES.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Feature::values, Feature::get);
            VALUES = Arrays.stream(Feature.values()).collect(Collectors.toMap(Feature::getName, feature -> feature));
        }
    }
}

