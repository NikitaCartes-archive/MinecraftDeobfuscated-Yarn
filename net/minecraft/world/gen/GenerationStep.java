/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerationStep {

    public static enum Carver {
        AIR("air"),
        LIQUID("liquid");

        private static final Map<String, Carver> BY_NAME;
        private final String name;

        private Carver(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        static {
            BY_NAME = Arrays.stream(Carver.values()).collect(Collectors.toMap(Carver::getName, carver -> carver));
        }
    }

    public static enum Feature {
        RAW_GENERATION("raw_generation"),
        LOCAL_MODIFICATIONS("local_modifications"),
        UNDERGROUND_STRUCTURES("underground_structures"),
        SURFACE_STRUCTURES("surface_structures"),
        UNDERGROUND_ORES("underground_ores"),
        UNDERGROUND_DECORATION("underground_decoration"),
        VEGETAL_DECORATION("vegetal_decoration"),
        TOP_LAYER_MODIFICATION("top_layer_modification");

        private static final Map<String, Feature> BY_NAME;
        private final String name;

        private Feature(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        static {
            BY_NAME = Arrays.stream(Feature.values()).collect(Collectors.toMap(Feature::getName, feature -> feature));
        }
    }
}

