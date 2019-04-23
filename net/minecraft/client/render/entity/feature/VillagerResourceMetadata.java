/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadataReader;

@Environment(value=EnvType.CLIENT)
public class VillagerResourceMetadata {
    public static final VillagerResourceMetadataReader READER = new VillagerResourceMetadataReader();
    private final HatType hatType;

    public VillagerResourceMetadata(HatType hatType) {
        this.hatType = hatType;
    }

    public HatType getHatType() {
        return this.hatType;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum HatType {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        private static final Map<String, HatType> byName;
        private final String name;

        private HatType(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static HatType from(String string) {
            return byName.getOrDefault(string, NONE);
        }

        static {
            byName = Arrays.stream(HatType.values()).collect(Collectors.toMap(HatType::getName, hatType -> hatType));
        }
    }
}

