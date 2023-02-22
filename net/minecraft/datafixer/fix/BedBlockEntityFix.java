/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class BedBlockEntityFix
extends DataFix {
    public BedBlockEntityFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = type.findFieldType("Level");
        Type<?> type3 = type2.findFieldType("TileEntities");
        if (!(type3 instanceof List.ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        }
        List.ListType listType = (List.ListType)type3;
        return this.fix(type2, listType);
    }

    private <TE> TypeRewriteRule fix(Type<?> level, List.ListType<TE> blockEntities) {
        Type type = blockEntities.getElement();
        OpticFinder<?> opticFinder = DSL.fieldFinder("Level", level);
        OpticFinder opticFinder2 = DSL.fieldFinder("TileEntities", blockEntities);
        int i = 416;
        return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY), dynamicOps -> pair -> pair), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(TypeReferences.CHUNK), typed -> {
            Typed<Object> typed2 = typed.getTyped(opticFinder);
            Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
            int i = dynamic.get("xPos").asInt(0);
            int j = dynamic.get("zPos").asInt(0);
            ArrayList list = Lists.newArrayList((Iterable)typed2.getOrCreate(opticFinder2));
            List list2 = dynamic.get("Sections").asList(Function.identity());
            for (int k = 0; k < list2.size(); ++k) {
                Dynamic dynamic2 = (Dynamic)list2.get(k);
                int l2 = dynamic2.get("Y").asInt(0);
                Streams.mapWithIndex(dynamic2.get("Blocks").asIntStream(), (l, m) -> {
                    if (416 == (l & 0xFF) << 4) {
                        int n = (int)m;
                        int o = n & 0xF;
                        int p = n >> 8 & 0xF;
                        int q = n >> 4 & 0xF;
                        HashMap map = Maps.newHashMap();
                        map.put(dynamic2.createString("id"), dynamic2.createString("minecraft:bed"));
                        map.put(dynamic2.createString("x"), dynamic2.createInt(o + (i << 4)));
                        map.put(dynamic2.createString("y"), dynamic2.createInt(p + (l2 << 4)));
                        map.put(dynamic2.createString("z"), dynamic2.createInt(q + (j << 4)));
                        map.put(dynamic2.createString("color"), dynamic2.createShort((short)14));
                        return map;
                    }
                    return null;
                }).forEachOrdered(map -> {
                    if (map != null) {
                        list.add(type.read(dynamic2.createMap((Map<? extends Dynamic<?>, ? extends Dynamic<?>>)map)).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created bed block entity.")).getFirst());
                    }
                });
            }
            if (!list.isEmpty()) {
                return typed.set(opticFinder, typed2.set(opticFinder2, list));
            }
            return typed;
        }));
    }
}

