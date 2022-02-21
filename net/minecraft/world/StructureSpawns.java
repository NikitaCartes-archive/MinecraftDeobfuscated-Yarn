/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;
import org.jetbrains.annotations.Nullable;

public record StructureSpawns(BoundingBox boundingBox, Pool<SpawnSettings.SpawnEntry> spawns) {
    public static final Codec<StructureSpawns> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BoundingBox.CODEC.fieldOf("bounding_box")).forGetter(StructureSpawns::boundingBox), ((MapCodec)Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).fieldOf("spawns")).forGetter(StructureSpawns::spawns)).apply((Applicative<StructureSpawns, ?>)instance, StructureSpawns::new));

    public static enum BoundingBox implements StringIdentifiable
    {
        PIECE("piece"),
        STRUCTURE("full");

        public static final BoundingBox[] VALUES;
        public static final Codec<BoundingBox> CODEC;
        private final String name;

        private BoundingBox(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        @Nullable
        public static BoundingBox byName(@Nullable String name) {
            if (name == null) {
                return null;
            }
            for (BoundingBox boundingBox : VALUES) {
                if (!boundingBox.name.equals(name)) continue;
                return boundingBox;
            }
            return null;
        }

        static {
            VALUES = BoundingBox.values();
            CODEC = StringIdentifiable.createCodec(() -> VALUES, BoundingBox::byName);
        }
    }
}

