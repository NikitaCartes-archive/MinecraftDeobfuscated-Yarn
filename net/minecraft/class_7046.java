/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class class_7046
extends DataFix {
    private static final Map<String, class_7047> field_37050 = ImmutableMap.builder().put("mineshaft", class_7047.method_41029(Map.of(List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands"), "minecraft:mineshaft_mesa"), "minecraft:mineshaft")).put("shipwreck", class_7047.method_41029(Map.of(List.of("minecraft:beach", "minecraft:snowy_beach"), "minecraft:shipwreck_beached"), "minecraft:shipwreck")).put("ocean_ruin", class_7047.method_41029(Map.of(List.of("minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"), "minecraft:ocean_ruin_warm"), "minecraft:ocean_ruin_cold")).put("village", class_7047.method_41029(Map.of(List.of("minecraft:desert"), "minecraft:village_desert", List.of("minecraft:savanna"), "minecraft:village_savanna", List.of("minecraft:snowy_plains"), "minecraft:village_snowy", List.of("minecraft:taiga"), "minecraft:village_taiga"), "minecraft:village_plains")).put("ruined_portal", class_7047.method_41029(Map.of(List.of("minecraft:desert"), "minecraft:ruined_portal_desert", List.of("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands", "minecraft:windswept_hills", "minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:stony_shore", "minecraft:meadow", "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes"), "minecraft:ruined_portal_mountain", List.of("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle"), "minecraft:ruined_portal_jungle", List.of("minecraft:deep_frozen_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:frozen_ocean", "minecraft:ocean", "minecraft:cold_ocean", "minecraft:lukewarm_ocean", "minecraft:warm_ocean"), "minecraft:ruined_portal_ocean"), "minecraft:ruined_portal_standard")).put("pillager_outpost", class_7047.method_41027("minecraft:pillager_outpost")).put("mansion", class_7047.method_41027("minecraft:mansion")).put("jungle_pyramid", class_7047.method_41027("minecraft:jungle_pyramid")).put("desert_pyramid", class_7047.method_41027("minecraft:desert_pyramid")).put("igloo", class_7047.method_41027("minecraft:igloo")).put("swamp_hut", class_7047.method_41027("minecraft:swamp_hut")).put("stronghold", class_7047.method_41027("minecraft:stronghold")).put("monument", class_7047.method_41027("minecraft:monument")).put("fortress", class_7047.method_41027("minecraft:fortress")).put("endcity", class_7047.method_41027("minecraft:end_city")).put("buried_treasure", class_7047.method_41027("minecraft:buried_treasure")).put("nether_fossil", class_7047.method_41027("minecraft:nether_fossil")).put("bastion_remnant", class_7047.method_41027("minecraft:bastion_remnant")).build();

    public class_7046(Schema schema) {
        super(schema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = this.getInputSchema().getType(TypeReferences.CHUNK);
        return this.writeFixAndRead("StucturesToConfiguredStructures", type, type2, this::method_41012);
    }

    private Dynamic<?> method_41012(Dynamic<?> dynamic) {
        return dynamic.update("structures", dynamic22 -> dynamic22.update("starts", dynamic2 -> this.method_41015((Dynamic<?>)dynamic2, dynamic)).update("References", dynamic2 -> this.method_41020((Dynamic<?>)dynamic2, dynamic)));
    }

    private Dynamic<?> method_41015(Dynamic<?> dynamic3, Dynamic<?> dynamic22) {
        Map<Dynamic<?>, Dynamic<?>> map = dynamic3.getMapValues().result().get();
        ArrayList list = new ArrayList();
        map.forEach((dynamic, dynamic2) -> {
            if (dynamic2.get("id").asString("INVALID").equals("INVALID")) {
                list.add(dynamic);
            }
        });
        for (Dynamic dynamic32 : list) {
            dynamic3 = dynamic3.remove(dynamic32.asString(""));
        }
        return dynamic3.updateMapValues(pair -> this.method_41010((Pair<Dynamic<?>, Dynamic<?>>)pair, dynamic22));
    }

    private Pair<Dynamic<?>, Dynamic<?>> method_41010(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        Dynamic<?> dynamic2 = this.method_41022(pair, dynamic);
        return new Pair(dynamic2, pair.getSecond().set("id", dynamic2));
    }

    private Dynamic<?> method_41020(Dynamic<?> dynamic3, Dynamic<?> dynamic22) {
        Map<Dynamic<?>, Dynamic<?>> map = dynamic3.getMapValues().result().get();
        ArrayList list = new ArrayList();
        map.forEach((dynamic, dynamic2) -> {
            if (dynamic2.asLongStream().count() == 0L) {
                list.add(dynamic);
            }
        });
        for (Dynamic dynamic32 : list) {
            dynamic3 = dynamic3.remove(dynamic32.asString(""));
        }
        return dynamic3.updateMapValues(pair -> this.method_41018((Pair<Dynamic<?>, Dynamic<?>>)pair, dynamic22));
    }

    private Pair<Dynamic<?>, Dynamic<?>> method_41018(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        return pair.mapFirst(dynamic2 -> this.method_41022(pair, dynamic));
    }

    private Dynamic<?> method_41022(Pair<Dynamic<?>, Dynamic<?>> pair, Dynamic<?> dynamic) {
        Optional<String> optional;
        String string = pair.getFirst().asString("UNKNOWN").toLowerCase(Locale.ROOT);
        class_7047 lv = field_37050.get(string);
        if (lv == null) {
            throw new IllegalStateException("Found unknown structure: " + string);
        }
        Dynamic<?> dynamic2 = pair.getSecond();
        String string2 = lv.fallback;
        if (!lv.biomeMapping().isEmpty() && (optional = this.method_41013(dynamic, lv)).isPresent()) {
            string2 = optional.get();
        }
        Dynamic dynamic3 = dynamic2.createString(string2);
        return dynamic3;
    }

    private Optional<String> method_41013(Dynamic<?> dynamic, class_7047 arg) {
        Object2IntArrayMap object2IntArrayMap = new Object2IntArrayMap();
        dynamic.get("sections").asList(Function.identity()).forEach(dynamic2 -> dynamic2.get("biomes").get("palette").asList(Function.identity()).forEach(dynamic -> {
            String string = arg.biomeMapping().get(dynamic.asString(""));
            if (string != null) {
                object2IntArrayMap.mergeInt(string, 1, Integer::sum);
            }
        }));
        return object2IntArrayMap.object2IntEntrySet().stream().max(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).map(Map.Entry::getKey);
    }

    record class_7047(Map<String, String> biomeMapping, String fallback) {
        public static class_7047 method_41027(String string) {
            return new class_7047(Map.of(), string);
        }

        public static class_7047 method_41029(Map<List<String>, String> map, String string) {
            return new class_7047(class_7047.method_41028(map), string);
        }

        private static Map<String, String> method_41028(Map<List<String>, String> map) {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (Map.Entry<List<String>, String> entry : map.entrySet()) {
                entry.getKey().forEach(string -> builder.put(string, (String)entry.getValue()));
            }
            return builder.build();
        }
    }
}

