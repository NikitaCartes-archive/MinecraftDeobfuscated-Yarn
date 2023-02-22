/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.datafixer.TypeReferences;

public class ChunkToProtoChunkFix
extends DataFix {
    private static final int field_29881 = 16;

    public ChunkToProtoChunkFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("ChunkToProtoChunkFix", this.getInputSchema().getType(TypeReferences.CHUNK), this.getOutputSchema().getType(TypeReferences.CHUNK), dynamic -> dynamic.update("Level", ChunkToProtoChunkFix::fixLevel));
    }

    private static <T> Dynamic<T> fixLevel(Dynamic<T> dynamic) {
        boolean bl2;
        boolean bl = dynamic.get("TerrainPopulated").asBoolean(false);
        boolean bl3 = bl2 = dynamic.get("LightPopulated").asNumber().result().isEmpty() || dynamic.get("LightPopulated").asBoolean(false);
        String string = bl ? (bl2 ? "mobs_spawned" : "decorated") : "carved";
        return ChunkToProtoChunkFix.fixTileTicks(ChunkToProtoChunkFix.fixBiomes(dynamic)).set("Status", dynamic.createString(string)).set("hasLegacyStructureData", dynamic.createBoolean(true));
    }

    private static <T> Dynamic<T> fixBiomes(Dynamic<T> dynamic) {
        return dynamic.update("Biomes", dynamic2 -> DataFixUtils.orElse(dynamic2.asByteBufferOpt().result().map(byteBuffer -> {
            int[] is = new int[256];
            for (int i = 0; i < is.length; ++i) {
                if (i >= byteBuffer.capacity()) continue;
                is[i] = byteBuffer.get(i) & 0xFF;
            }
            return dynamic.createIntList(Arrays.stream(is));
        }), dynamic2));
    }

    private static <T> Dynamic<T> fixTileTicks(Dynamic<T> dynamic) {
        return DataFixUtils.orElse(dynamic.get("TileTicks").asStreamOpt().result().map(stream -> {
            List list = IntStream.range(0, 16).mapToObj(i -> new ShortArrayList()).collect(Collectors.toList());
            stream.forEach(dynamic -> {
                int i = dynamic.get("x").asInt(0);
                int j = dynamic.get("y").asInt(0);
                int k = dynamic.get("z").asInt(0);
                short s = ChunkToProtoChunkFix.packChunkSectionPos(i, j, k);
                ((ShortList)list.get(j >> 4)).add(s);
            });
            return dynamic.remove("TileTicks").set("ToBeTicked", dynamic.createList(list.stream().map(shortList -> dynamic.createList(shortList.intStream().mapToObj(i -> dynamic.createShort((short)i))))));
        }), dynamic);
    }

    private static short packChunkSectionPos(int x, int y, int z) {
        return (short)(x & 0xF | (y & 0xF) << 4 | (z & 0xF) << 8);
    }
}

