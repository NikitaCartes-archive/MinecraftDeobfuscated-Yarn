/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class StructureSeparationDataFix
extends DataFix {
    private static final ImmutableMap<String, Information> STRUCTURE_SPACING = ImmutableMap.builder().put("minecraft:village", new Information(32, 8, 10387312)).put("minecraft:desert_pyramid", new Information(32, 8, 14357617)).put("minecraft:igloo", new Information(32, 8, 14357618)).put("minecraft:jungle_pyramid", new Information(32, 8, 14357619)).put("minecraft:swamp_hut", new Information(32, 8, 14357620)).put("minecraft:pillager_outpost", new Information(32, 8, 165745296)).put("minecraft:monument", new Information(32, 5, 10387313)).put("minecraft:endcity", new Information(20, 11, 10387313)).put("minecraft:mansion", new Information(80, 20, 10387319)).build();

    public StructureSeparationDataFix(Schema schema) {
        super(schema, true);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(TypeReferences.CHUNK_GENERATOR_SETTINGS), typed -> typed.update(DSL.remainderFinder(), StructureSeparationDataFix::method_28271));
    }

    private static <T> Dynamic<T> method_28268(long l, DynamicLike<T> dynamicLike, Dynamic<T> dynamic, Dynamic<T> dynamic2) {
        return dynamicLike.createMap(ImmutableMap.of(dynamicLike.createString("type"), dynamicLike.createString("minecraft:noise"), dynamicLike.createString("biome_source"), dynamic2, dynamicLike.createString("seed"), dynamicLike.createLong(l), dynamicLike.createString("settings"), dynamic));
    }

    private static <T> Dynamic<T> method_28272(Dynamic<T> dynamic, long l, boolean bl, boolean bl2) {
        ImmutableMap.Builder builder = ImmutableMap.builder().put(dynamic.createString("type"), dynamic.createString("minecraft:vanilla_layered")).put(dynamic.createString("seed"), dynamic.createLong(l)).put(dynamic.createString("large_biomes"), dynamic.createBoolean(bl2));
        if (bl) {
            builder.put(dynamic.createString("legacy_biome_init_layer"), dynamic.createBoolean(bl));
        }
        return dynamic.createMap(builder.build());
    }

    private static <T> Dynamic<T> method_28271(Dynamic<T> dynamic2) {
        Dynamic<T> dynamic22;
        DynamicOps dynamicOps = dynamic2.getOps();
        long l = dynamic2.get("RandomSeed").asLong(0L);
        Optional<String> optional = dynamic2.get("generatorName").asString().map(string -> string.toLowerCase(Locale.ROOT)).result();
        Optional optional2 = dynamic2.get("legacy_custom_options").asString().result().map(Optional::of).orElseGet(() -> {
            if (optional.equals(Optional.of("customized"))) {
                return dynamic2.get("generatorOptions").asString().result();
            }
            return Optional.empty();
        });
        boolean bl = false;
        if (optional.equals(Optional.of("customized"))) {
            dynamic22 = StructureSeparationDataFix.method_29916(dynamic2, l);
        } else if (!optional.isPresent()) {
            dynamic22 = StructureSeparationDataFix.method_29916(dynamic2, l);
        } else {
            OptionalDynamic<T> optionalDynamic = dynamic2.get("generatorOptions");
            switch (optional.get()) {
                case "flat": {
                    Map map = StructureSeparationDataFix.method_28275(dynamicOps, optionalDynamic);
                    dynamic22 = dynamic2.createMap(ImmutableMap.of(dynamic2.createString("type"), dynamic2.createString("minecraft:flat"), dynamic2.createString("settings"), dynamic2.createMap(ImmutableMap.of(dynamic2.createString("structures"), dynamic2.createMap(map), dynamic2.createString("layers"), optionalDynamic.get("layers").orElseEmptyList(), dynamic2.createString("biome"), dynamic2.createString(optionalDynamic.get("biome").asString("plains"))))));
                    break;
                }
                case "debug_all_block_states": {
                    dynamic22 = dynamic2.createMap(ImmutableMap.of(dynamic2.createString("type"), dynamic2.createString("minecraft:debug")));
                    break;
                }
                case "buffet": {
                    Dynamic dynamic5;
                    Dynamic dynamic3;
                    OptionalDynamic<T> optionalDynamic2 = optionalDynamic.get("chunk_generator");
                    Optional<String> optional3 = optionalDynamic2.get("type").asString().result();
                    if (Objects.equals(optional3, Optional.of("minecraft:caves"))) {
                        dynamic3 = dynamic2.createString("minecraft:caves");
                        bl = true;
                    } else {
                        dynamic3 = Objects.equals(optional3, Optional.of("minecraft:floating_islands")) ? dynamic2.createString("minecraft:floating_islands") : dynamic2.createString("minecraft:overworld");
                    }
                    Dynamic dynamic4 = optionalDynamic.get("biome_source").result().orElseGet(() -> dynamic2.createMap(ImmutableMap.of(dynamic2.createString("type"), dynamic2.createString("minecraft:fixed"))));
                    if (dynamic4.get("type").asString().result().equals(Optional.of("minecraft:fixed"))) {
                        String string2 = dynamic4.get("options").get("biomes").asStream().findFirst().flatMap(dynamic -> dynamic.asString().result()).orElse("minecraft:ocean");
                        dynamic5 = dynamic4.remove("options").set("biome", dynamic2.createString(string2));
                    } else {
                        dynamic5 = dynamic4;
                    }
                    dynamic22 = StructureSeparationDataFix.method_28268(l, dynamic2, dynamic3, dynamic5);
                    break;
                }
                default: {
                    boolean bl2 = optional.get().equals("default");
                    boolean bl3 = optional.get().equals("default_1_1") || bl2 && dynamic2.get("generatorVersion").asInt(0) == 0;
                    boolean bl4 = optional.get().equals("amplified");
                    boolean bl5 = optional.get().equals("largebiomes");
                    dynamic22 = StructureSeparationDataFix.method_28268(l, dynamic2, dynamic2.createString(bl4 ? "minecraft:amplified" : "minecraft:overworld"), StructureSeparationDataFix.method_28272(dynamic2, l, bl3, bl5));
                }
            }
        }
        boolean bl6 = dynamic2.get("MapFeatures").asBoolean(true);
        boolean bl7 = dynamic2.get("BonusChest").asBoolean(false);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(dynamicOps.createString("seed"), dynamicOps.createLong(l));
        builder.put(dynamicOps.createString("generate_features"), dynamicOps.createBoolean(bl6));
        builder.put(dynamicOps.createString("bonus_chest"), dynamicOps.createBoolean(bl7));
        builder.put(dynamicOps.createString("dimensions"), StructureSeparationDataFix.method_29917(dynamic2, l, dynamic22, bl));
        optional2.ifPresent(string -> builder.put(dynamicOps.createString("legacy_custom_options"), dynamicOps.createString((String)string)));
        return new Dynamic(dynamicOps, dynamicOps.createMap(builder.build()));
    }

    protected static <T> Dynamic<T> method_29916(Dynamic<T> dynamic, long l) {
        return StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:overworld"), StructureSeparationDataFix.method_28272(dynamic, l, false, false));
    }

    protected static <T> T method_29917(Dynamic<T> dynamic, long l, Dynamic<T> dynamic2, boolean bl) {
        DynamicOps dynamicOps = dynamic.getOps();
        return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("minecraft:overworld"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:overworld" + (bl ? "_caves" : "")), dynamicOps.createString("generator"), dynamic2.getValue())), dynamicOps.createString("minecraft:the_nether"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:the_nether"), dynamicOps.createString("generator"), StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:nether"), dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:multi_noise"), dynamic.createString("seed"), dynamic.createLong(l), dynamic.createString("preset"), dynamic.createString("minecraft:nether")))).getValue())), dynamicOps.createString("minecraft:the_end"), dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("type"), dynamicOps.createString("minecraft:the_end"), dynamicOps.createString("generator"), StructureSeparationDataFix.method_28268(l, dynamic, dynamic.createString("minecraft:end"), dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:the_end"), dynamic.createString("seed"), dynamic.createLong(l)))).getValue()))));
    }

    private static <T> Map<Dynamic<T>, Dynamic<T>> method_28275(DynamicOps<T> dynamicOps, OptionalDynamic<T> optionalDynamic) {
        MutableInt mutableInt = new MutableInt(32);
        MutableInt mutableInt2 = new MutableInt(3);
        MutableInt mutableInt3 = new MutableInt(128);
        MutableBoolean mutableBoolean = new MutableBoolean(false);
        HashMap map = Maps.newHashMap();
        optionalDynamic.get("structures").flatMap(Dynamic::getMapValues).result().ifPresent(map2 -> map2.forEach((dynamic, dynamic2) -> dynamic2.getMapValues().result().ifPresent(map2 -> map2.forEach((dynamic2, dynamic3) -> {
            String string = dynamic.asString("");
            String string2 = dynamic2.asString("");
            String string3 = dynamic3.asString("");
            if ("stronghold".equals(string)) {
                mutableBoolean.setTrue();
                switch (string2) {
                    case "distance": {
                        mutableInt.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt.getValue(), 1));
                        return;
                    }
                    case "spread": {
                        mutableInt2.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt2.getValue(), 1));
                        return;
                    }
                    case "count": {
                        mutableInt3.setValue(StructureSeparationDataFix.method_28280(string3, mutableInt3.getValue(), 1));
                        return;
                    }
                }
                return;
            }
            switch (string2) {
                case "distance": {
                    switch (string) {
                        case "village": {
                            StructureSeparationDataFix.method_28281(map, "minecraft:village", string3, 9);
                            return;
                        }
                        case "biome_1": {
                            StructureSeparationDataFix.method_28281(map, "minecraft:desert_pyramid", string3, 9);
                            StructureSeparationDataFix.method_28281(map, "minecraft:igloo", string3, 9);
                            StructureSeparationDataFix.method_28281(map, "minecraft:jungle_pyramid", string3, 9);
                            StructureSeparationDataFix.method_28281(map, "minecraft:swamp_hut", string3, 9);
                            StructureSeparationDataFix.method_28281(map, "minecraft:pillager_outpost", string3, 9);
                            return;
                        }
                        case "endcity": {
                            StructureSeparationDataFix.method_28281(map, "minecraft:endcity", string3, 1);
                            return;
                        }
                        case "mansion": {
                            StructureSeparationDataFix.method_28281(map, "minecraft:mansion", string3, 1);
                            return;
                        }
                    }
                    return;
                }
                case "separation": {
                    if ("oceanmonument".equals(string)) {
                        Information information = map.getOrDefault("minecraft:monument", STRUCTURE_SPACING.get("minecraft:monument"));
                        int i = StructureSeparationDataFix.method_28280(string3, information.separation, 1);
                        map.put("minecraft:monument", new Information(i, information.separation, information.salt));
                    }
                    return;
                }
                case "spacing": {
                    if ("oceanmonument".equals(string)) {
                        StructureSeparationDataFix.method_28281(map, "minecraft:monument", string3, 1);
                    }
                    return;
                }
            }
        }))));
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put(optionalDynamic.createString("structures"), optionalDynamic.createMap(map.entrySet().stream().collect(Collectors.toMap(entry -> optionalDynamic.createString((String)entry.getKey()), entry -> ((Information)entry.getValue()).method_28288(dynamicOps)))));
        if (mutableBoolean.isTrue()) {
            builder.put(optionalDynamic.createString("stronghold"), optionalDynamic.createMap(ImmutableMap.of(optionalDynamic.createString("distance"), optionalDynamic.createInt(mutableInt.getValue()), optionalDynamic.createString("spread"), optionalDynamic.createInt(mutableInt2.getValue()), optionalDynamic.createString("count"), optionalDynamic.createInt(mutableInt3.getValue()))));
        }
        return builder.build();
    }

    private static int method_28279(String string, int i) {
        return NumberUtils.toInt(string, i);
    }

    private static int method_28280(String string, int i, int j) {
        return Math.max(j, StructureSeparationDataFix.method_28279(string, i));
    }

    private static void method_28281(Map<String, Information> map, String string, String string2, int i) {
        Information information = map.getOrDefault(string, STRUCTURE_SPACING.get(string));
        int j = StructureSeparationDataFix.method_28280(string2, information.spacing, i);
        map.put(string, new Information(j, information.separation, information.salt));
    }

    static final class Information {
        public static final Codec<Information> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("spacing")).forGetter(information -> information.spacing), ((MapCodec)Codec.INT.fieldOf("separation")).forGetter(information -> information.separation), ((MapCodec)Codec.INT.fieldOf("salt")).forGetter(information -> information.salt)).apply((Applicative<Information, ?>)instance, Information::new));
        private final int spacing;
        private final int separation;
        private final int salt;

        public Information(int spacing, int separation, int salt) {
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }

        public <T> Dynamic<T> method_28288(DynamicOps<T> dynamicOps) {
            return new Dynamic<T>(dynamicOps, CODEC.encodeStart(dynamicOps, this).result().orElse(dynamicOps.emptyMap()));
        }
    }
}

