/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;
import org.jetbrains.annotations.Nullable;

public record class_7061(class_7062 boundingBox, Pool<SpawnSettings.SpawnEntry> spawns) {
    public static final Codec<class_7061> field_37198 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)class_7062.field_37202.fieldOf("bounding_box")).forGetter(class_7061::boundingBox), ((MapCodec)Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).fieldOf("spawns")).forGetter(class_7061::spawns)).apply((Applicative<class_7061, ?>)instance, class_7061::new));

    public static enum class_7062 implements StringIdentifiable
    {
        PIECE("piece"),
        STRUCTURE("full");

        public static final class_7062[] field_37201;
        public static final Codec<class_7062> field_37202;
        private final String field_37203;

        private class_7062(String string2) {
            this.field_37203 = string2;
        }

        @Override
        public String asString() {
            return this.field_37203;
        }

        @Nullable
        public static class_7062 method_41151(@Nullable String string) {
            if (string == null) {
                return null;
            }
            for (class_7062 lv : field_37201) {
                if (!lv.field_37203.equals(string)) continue;
                return lv;
            }
            return null;
        }

        static {
            field_37201 = class_7062.values();
            field_37202 = StringIdentifiable.createCodec(() -> field_37201, class_7062::method_41151);
        }
    }
}

