/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class LevelDataGeneratorOptionsFix
extends DataFix {
    static final Map<String, String> NUMERICAL_IDS_TO_BIOME_IDS = Util.make(Maps.newHashMap(), map -> {
        map.put("0", "minecraft:ocean");
        map.put("1", "minecraft:plains");
        map.put("2", "minecraft:desert");
        map.put("3", "minecraft:mountains");
        map.put("4", "minecraft:forest");
        map.put("5", "minecraft:taiga");
        map.put("6", "minecraft:swamp");
        map.put("7", "minecraft:river");
        map.put("8", "minecraft:nether");
        map.put("9", "minecraft:the_end");
        map.put("10", "minecraft:frozen_ocean");
        map.put("11", "minecraft:frozen_river");
        map.put("12", "minecraft:snowy_tundra");
        map.put("13", "minecraft:snowy_mountains");
        map.put("14", "minecraft:mushroom_fields");
        map.put("15", "minecraft:mushroom_field_shore");
        map.put("16", "minecraft:beach");
        map.put("17", "minecraft:desert_hills");
        map.put("18", "minecraft:wooded_hills");
        map.put("19", "minecraft:taiga_hills");
        map.put("20", "minecraft:mountain_edge");
        map.put("21", "minecraft:jungle");
        map.put("22", "minecraft:jungle_hills");
        map.put("23", "minecraft:jungle_edge");
        map.put("24", "minecraft:deep_ocean");
        map.put("25", "minecraft:stone_shore");
        map.put("26", "minecraft:snowy_beach");
        map.put("27", "minecraft:birch_forest");
        map.put("28", "minecraft:birch_forest_hills");
        map.put("29", "minecraft:dark_forest");
        map.put("30", "minecraft:snowy_taiga");
        map.put("31", "minecraft:snowy_taiga_hills");
        map.put("32", "minecraft:giant_tree_taiga");
        map.put("33", "minecraft:giant_tree_taiga_hills");
        map.put("34", "minecraft:wooded_mountains");
        map.put("35", "minecraft:savanna");
        map.put("36", "minecraft:savanna_plateau");
        map.put("37", "minecraft:badlands");
        map.put("38", "minecraft:wooded_badlands_plateau");
        map.put("39", "minecraft:badlands_plateau");
        map.put("40", "minecraft:small_end_islands");
        map.put("41", "minecraft:end_midlands");
        map.put("42", "minecraft:end_highlands");
        map.put("43", "minecraft:end_barrens");
        map.put("44", "minecraft:warm_ocean");
        map.put("45", "minecraft:lukewarm_ocean");
        map.put("46", "minecraft:cold_ocean");
        map.put("47", "minecraft:deep_warm_ocean");
        map.put("48", "minecraft:deep_lukewarm_ocean");
        map.put("49", "minecraft:deep_cold_ocean");
        map.put("50", "minecraft:deep_frozen_ocean");
        map.put("127", "minecraft:the_void");
        map.put("129", "minecraft:sunflower_plains");
        map.put("130", "minecraft:desert_lakes");
        map.put("131", "minecraft:gravelly_mountains");
        map.put("132", "minecraft:flower_forest");
        map.put("133", "minecraft:taiga_mountains");
        map.put("134", "minecraft:swamp_hills");
        map.put("140", "minecraft:ice_spikes");
        map.put("149", "minecraft:modified_jungle");
        map.put("151", "minecraft:modified_jungle_edge");
        map.put("155", "minecraft:tall_birch_forest");
        map.put("156", "minecraft:tall_birch_hills");
        map.put("157", "minecraft:dark_forest_hills");
        map.put("158", "minecraft:snowy_taiga_mountains");
        map.put("160", "minecraft:giant_spruce_taiga");
        map.put("161", "minecraft:giant_spruce_taiga_hills");
        map.put("162", "minecraft:modified_gravelly_mountains");
        map.put("163", "minecraft:shattered_savanna");
        map.put("164", "minecraft:shattered_savanna_plateau");
        map.put("165", "minecraft:eroded_badlands");
        map.put("166", "minecraft:modified_wooded_badlands_plateau");
        map.put("167", "minecraft:modified_badlands_plateau");
    });
    public static final String GENERATOR_OPTIONS_KEY = "generatorOptions";

    public LevelDataGeneratorOptionsFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(TypeReferences.LEVEL);
        return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(TypeReferences.LEVEL), type, (Typed<?> typed) -> typed.write().flatMap(dynamic -> {
            Dynamic dynamic2;
            Optional<String> optional = dynamic.get(GENERATOR_OPTIONS_KEY).asString().result();
            if ("flat".equalsIgnoreCase(dynamic.get("generatorName").asString(""))) {
                String string = optional.orElse("");
                dynamic2 = dynamic.set(GENERATOR_OPTIONS_KEY, LevelDataGeneratorOptionsFix.fixGeneratorOptions(string, dynamic.getOps()));
            } else if ("buffet".equalsIgnoreCase(dynamic.get("generatorName").asString("")) && optional.isPresent()) {
                Dynamic<JsonObject> dynamic3 = new Dynamic<JsonObject>(JsonOps.INSTANCE, JsonHelper.deserialize(optional.get(), true));
                dynamic2 = dynamic.set(GENERATOR_OPTIONS_KEY, dynamic3.convert(dynamic.getOps()));
            } else {
                dynamic2 = dynamic;
            }
            return type.readTyped(dynamic2);
        }).map(Pair::getFirst).result().orElseThrow(() -> new IllegalStateException("Could not read new level type.")));
    }

    private static <T> Dynamic<T> fixGeneratorOptions(String generatorOptions, DynamicOps<T> dynamicOps) {
        List<Object> list;
        Iterator<String> iterator = Splitter.on(';').split(generatorOptions).iterator();
        String string = "minecraft:plains";
        HashMap map = Maps.newHashMap();
        if (!generatorOptions.isEmpty() && iterator.hasNext()) {
            list = LevelDataGeneratorOptionsFix.parseFlatLayers(iterator.next());
            if (!list.isEmpty()) {
                if (iterator.hasNext()) {
                    string = NUMERICAL_IDS_TO_BIOME_IDS.getOrDefault(iterator.next(), "minecraft:plains");
                }
                if (iterator.hasNext()) {
                    String[] strings;
                    for (String string2 : strings = iterator.next().toLowerCase(Locale.ROOT).split(",")) {
                        String[] strings3;
                        String[] strings2 = string2.split("\\(", 2);
                        if (strings2[0].isEmpty()) continue;
                        map.put(strings2[0], Maps.newHashMap());
                        if (strings2.length <= 1 || !strings2[1].endsWith(")") || strings2[1].length() <= 1) continue;
                        for (String string3 : strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ")) {
                            String[] strings4 = string3.split("=", 2);
                            if (strings4.length != 2) continue;
                            ((Map)map.get(strings2[0])).put(strings4[0], strings4[1]);
                        }
                    }
                } else {
                    map.put("village", Maps.newHashMap());
                }
            }
        } else {
            list = Lists.newArrayList();
            list.add(Pair.of(1, "minecraft:bedrock"));
            list.add(Pair.of(2, "minecraft:dirt"));
            list.add(Pair.of(1, "minecraft:grass_block"));
            map.put("village", Maps.newHashMap());
        }
        Object object = dynamicOps.createList(list.stream().map(pair -> dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt((Integer)pair.getFirst()), dynamicOps.createString("block"), dynamicOps.createString((String)pair.getSecond())))));
        Object object2 = dynamicOps.createMap(map.entrySet().stream().map(entry2 -> Pair.of(dynamicOps.createString(((String)entry2.getKey()).toLowerCase(Locale.ROOT)), dynamicOps.createMap(((Map)entry2.getValue()).entrySet().stream().map(entry -> Pair.of(dynamicOps.createString((String)entry.getKey()), dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("layers"), object, dynamicOps.createString("biome"), dynamicOps.createString(string), dynamicOps.createString("structures"), object2)));
    }

    @Nullable
    private static Pair<Integer, String> parseFlatLayer(String layer) {
        int i;
        String[] strings = layer.split("\\*", 2);
        if (strings.length == 2) {
            try {
                i = Integer.parseInt(strings[0]);
            } catch (NumberFormatException numberFormatException) {
                return null;
            }
        } else {
            i = 1;
        }
        String string = strings[strings.length - 1];
        return Pair.of(i, string);
    }

    private static List<Pair<Integer, String>> parseFlatLayers(String layers) {
        String[] strings;
        ArrayList<Pair<Integer, String>> list = Lists.newArrayList();
        for (String string : strings = layers.split(",")) {
            Pair<Integer, String> pair = LevelDataGeneratorOptionsFix.parseFlatLayer(string);
            if (pair == null) {
                return Collections.emptyList();
            }
            list.add(pair);
        }
        return list;
    }
}

