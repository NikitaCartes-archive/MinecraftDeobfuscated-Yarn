/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockStateFlattening;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PackedIntegerArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkPalettedStorageFix
extends DataFix {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BitSet field_15842 = new BitSet(256);
    private static final BitSet field_15854 = new BitSet(256);
    private static final Dynamic<?> pumpkin = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
    private static final Dynamic<?> podzol = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    private static final Dynamic<?> snowyGrass = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    private static final Dynamic<?> snowyMycelium = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    private static final Dynamic<?> sunflowerUpper = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    private static final Dynamic<?> lilacUpper = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    private static final Dynamic<?> grassUpper = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    private static final Dynamic<?> fernUpper = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    private static final Dynamic<?> roseUpper = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    private static final Dynamic<?> peonyUpper = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    private static final Map<String, Dynamic<?>> flowerPot = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:air0", BlockStateFlattening.parseState("{Name:'minecraft:flower_pot'}"));
        hashMap.put("minecraft:red_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_poppy'}"));
        hashMap.put("minecraft:red_flower1", BlockStateFlattening.parseState("{Name:'minecraft:potted_blue_orchid'}"));
        hashMap.put("minecraft:red_flower2", BlockStateFlattening.parseState("{Name:'minecraft:potted_allium'}"));
        hashMap.put("minecraft:red_flower3", BlockStateFlattening.parseState("{Name:'minecraft:potted_azure_bluet'}"));
        hashMap.put("minecraft:red_flower4", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_tulip'}"));
        hashMap.put("minecraft:red_flower5", BlockStateFlattening.parseState("{Name:'minecraft:potted_orange_tulip'}"));
        hashMap.put("minecraft:red_flower6", BlockStateFlattening.parseState("{Name:'minecraft:potted_white_tulip'}"));
        hashMap.put("minecraft:red_flower7", BlockStateFlattening.parseState("{Name:'minecraft:potted_pink_tulip'}"));
        hashMap.put("minecraft:red_flower8", BlockStateFlattening.parseState("{Name:'minecraft:potted_oxeye_daisy'}"));
        hashMap.put("minecraft:yellow_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dandelion'}"));
        hashMap.put("minecraft:sapling0", BlockStateFlattening.parseState("{Name:'minecraft:potted_oak_sapling'}"));
        hashMap.put("minecraft:sapling1", BlockStateFlattening.parseState("{Name:'minecraft:potted_spruce_sapling'}"));
        hashMap.put("minecraft:sapling2", BlockStateFlattening.parseState("{Name:'minecraft:potted_birch_sapling'}"));
        hashMap.put("minecraft:sapling3", BlockStateFlattening.parseState("{Name:'minecraft:potted_jungle_sapling'}"));
        hashMap.put("minecraft:sapling4", BlockStateFlattening.parseState("{Name:'minecraft:potted_acacia_sapling'}"));
        hashMap.put("minecraft:sapling5", BlockStateFlattening.parseState("{Name:'minecraft:potted_dark_oak_sapling'}"));
        hashMap.put("minecraft:red_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_mushroom'}"));
        hashMap.put("minecraft:brown_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_brown_mushroom'}"));
        hashMap.put("minecraft:deadbush0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dead_bush'}"));
        hashMap.put("minecraft:tallgrass2", BlockStateFlattening.parseState("{Name:'minecraft:potted_fern'}"));
        hashMap.put("minecraft:cactus0", BlockStateFlattening.lookupState(2240));
    });
    private static final Map<String, Dynamic<?>> skull = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildSkull(hashMap, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 2, "zombie", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 3, "player", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 4, "creeper", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 5, "dragon", "head");
    });
    private static final Map<String, Dynamic<?>> door = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildDoor(hashMap, "oak_door", 1024);
        ChunkPalettedStorageFix.buildDoor(hashMap, "iron_door", 1136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "spruce_door", 3088);
        ChunkPalettedStorageFix.buildDoor(hashMap, "birch_door", 3104);
        ChunkPalettedStorageFix.buildDoor(hashMap, "jungle_door", 3120);
        ChunkPalettedStorageFix.buildDoor(hashMap, "acacia_door", 3136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "dark_oak_door", 3152);
    });
    private static final Map<String, Dynamic<?>> noteblock = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (int i = 0; i < 26; ++i) {
            hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
            hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
        }
    });
    private static final Int2ObjectMap<String> colors = DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(0, "white");
        int2ObjectOpenHashMap.put(1, "orange");
        int2ObjectOpenHashMap.put(2, "magenta");
        int2ObjectOpenHashMap.put(3, "light_blue");
        int2ObjectOpenHashMap.put(4, "yellow");
        int2ObjectOpenHashMap.put(5, "lime");
        int2ObjectOpenHashMap.put(6, "pink");
        int2ObjectOpenHashMap.put(7, "gray");
        int2ObjectOpenHashMap.put(8, "light_gray");
        int2ObjectOpenHashMap.put(9, "cyan");
        int2ObjectOpenHashMap.put(10, "purple");
        int2ObjectOpenHashMap.put(11, "blue");
        int2ObjectOpenHashMap.put(12, "brown");
        int2ObjectOpenHashMap.put(13, "green");
        int2ObjectOpenHashMap.put(14, "red");
        int2ObjectOpenHashMap.put(15, "black");
    });
    private static final Map<String, Dynamic<?>> bed = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : colors.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "red")) continue;
            ChunkPalettedStorageFix.buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Map<String, Dynamic<?>> banner = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : colors.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "white")) continue;
            ChunkPalettedStorageFix.buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Dynamic<?> air;

    public ChunkPalettedStorageFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private static void buildSkull(Map<String, Dynamic<?>> out, int i, String mob, String block) {
        out.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
        out.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
        out.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
        out.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));
        for (int j = 0; j < 16; ++j) {
            out.put(i + "" + j, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + j + "'}}"));
        }
    }

    private static void buildDoor(Map<String, Dynamic<?>> out, String name, int i) {
        out.put("minecraft:" + name + "eastlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "eastlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(i));
        out.put("minecraft:" + name + "eastlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(i + 4));
        out.put("minecraft:" + name + "eastlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(i + 8));
        out.put("minecraft:" + name + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(i + 10));
        out.put("minecraft:" + name + "eastupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(i + 9));
        out.put("minecraft:" + name + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(i + 11));
        out.put("minecraft:" + name + "eastupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "eastupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 3));
        out.put("minecraft:" + name + "northlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northlowerrighttruefalse", BlockStateFlattening.lookupState(i + 7));
        out.put("minecraft:" + name + "northlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "northupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "northupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 1));
        out.put("minecraft:" + name + "southlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southlowerrighttruefalse", BlockStateFlattening.lookupState(i + 5));
        out.put("minecraft:" + name + "southlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "southupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "southupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 2));
        out.put("minecraft:" + name + "westlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westlowerrighttruefalse", BlockStateFlattening.lookupState(i + 6));
        out.put("minecraft:" + name + "westlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        out.put("minecraft:" + name + "westupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        out.put("minecraft:" + name + "westupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }

    private static void buildBed(Map<String, Dynamic<?>> out, int i, String string) {
        out.put("southfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        out.put("westfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        out.put("northfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        out.put("eastfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        out.put("southfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        out.put("westfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        out.put("northfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        out.put("eastfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        out.put("southtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        out.put("westtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        out.put("northtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        out.put("easttruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }

    private static void buildBanner(Map<String, Dynamic<?>> out, int i, String string) {
        for (int j = 0; j < 16; ++j) {
            out.put("" + j + "_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
        }
        out.put("north_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
        out.put("south_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
        out.put("west_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
        out.put("east_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
    }

    public static String getName(Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }

    public static String getProperty(Dynamic<?> dynamic, String string) {
        return dynamic.get("Properties").get(string).asString("");
    }

    public static int addTo(Int2ObjectBiMap<Dynamic<?>> int2ObjectBiMap, Dynamic<?> dynamic) {
        int i = int2ObjectBiMap.getId(dynamic);
        if (i == -1) {
            i = int2ObjectBiMap.add(dynamic);
        }
        return i;
    }

    private Dynamic<?> fixChunk(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = dynamic.get("Level").get();
        if (optional.isPresent() && optional.get().get("Sections").asStreamOpt().isPresent()) {
            return dynamic.set("Level", new Level(optional.get()).transform());
        }
        return dynamic;
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
    }

    public static int method_15615(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            i = bl2 ? (i |= 2) : (bl ? (i |= 0x80) : (i |= 1));
        } else if (bl4) {
            i = bl ? (i |= 0x20) : (bl2 ? (i |= 8) : (i |= 0x10));
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 0x40;
        }
        return i;
    }

    static {
        field_15854.set(2);
        field_15854.set(3);
        field_15854.set(110);
        field_15854.set(140);
        field_15854.set(144);
        field_15854.set(25);
        field_15854.set(86);
        field_15854.set(26);
        field_15854.set(176);
        field_15854.set(177);
        field_15854.set(175);
        field_15854.set(64);
        field_15854.set(71);
        field_15854.set(193);
        field_15854.set(194);
        field_15854.set(195);
        field_15854.set(196);
        field_15854.set(197);
        field_15842.set(54);
        field_15842.set(146);
        field_15842.set(25);
        field_15842.set(26);
        field_15842.set(51);
        field_15842.set(53);
        field_15842.set(67);
        field_15842.set(108);
        field_15842.set(109);
        field_15842.set(114);
        field_15842.set(128);
        field_15842.set(134);
        field_15842.set(135);
        field_15842.set(136);
        field_15842.set(156);
        field_15842.set(163);
        field_15842.set(164);
        field_15842.set(180);
        field_15842.set(203);
        field_15842.set(55);
        field_15842.set(85);
        field_15842.set(113);
        field_15842.set(188);
        field_15842.set(189);
        field_15842.set(190);
        field_15842.set(191);
        field_15842.set(192);
        field_15842.set(93);
        field_15842.set(94);
        field_15842.set(101);
        field_15842.set(102);
        field_15842.set(160);
        field_15842.set(106);
        field_15842.set(107);
        field_15842.set(183);
        field_15842.set(184);
        field_15842.set(185);
        field_15842.set(186);
        field_15842.set(187);
        field_15842.set(132);
        field_15842.set(139);
        field_15842.set(199);
        air = BlockStateFlattening.lookupState(0);
    }

    public static enum Facing {
        DOWN(Direction.NEGATIVE, Axis.Y),
        UP(Direction.POSITIVE, Axis.Y),
        NORTH(Direction.NEGATIVE, Axis.Z),
        SOUTH(Direction.POSITIVE, Axis.Z),
        WEST(Direction.NEGATIVE, Axis.X),
        EAST(Direction.POSITIVE, Axis.X);

        private final Axis axis;
        private final Direction direction;

        private Facing(Direction direction, Axis axis) {
            this.axis = axis;
            this.direction = direction;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public Axis getAxis() {
            return this.axis;
        }

        public static enum Direction {
            POSITIVE(1),
            NEGATIVE(-1);

            private final int offset;

            private Direction(int j) {
                this.offset = j;
            }

            public int getOffset() {
                return this.offset;
            }
        }

        public static enum Axis {
            X,
            Y,
            Z;

        }
    }

    static class ChunkNibbleArray {
        private final byte[] contents;

        public ChunkNibbleArray() {
            this.contents = new byte[2048];
        }

        public ChunkNibbleArray(byte[] bs) {
            this.contents = bs;
            if (bs.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
            }
        }

        public int get(int x, int y, int i) {
            int j = this.method_15642(y << 8 | i << 4 | x);
            if (this.method_15641(y << 8 | i << 4 | x)) {
                return this.contents[j] & 0xF;
            }
            return this.contents[j] >> 4 & 0xF;
        }

        private boolean method_15641(int i) {
            return (i & 1) == 0;
        }

        private int method_15642(int i) {
            return i >> 1;
        }
    }

    static final class Level {
        private int sides;
        private final Section[] sections = new Section[16];
        private final Dynamic<?> level;
        private final int xPos;
        private final int yPos;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public Level(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.xPos = dynamic.get("xPos").asInt(0) << 4;
            this.yPos = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().ifPresent(stream -> stream.forEach(dynamic -> {
                int k;
                int i = dynamic.get("x").asInt(0) - this.xPos & 0xF;
                int j = dynamic.get("y").asInt(0);
                int l = j << 8 | (k = dynamic.get("z").asInt(0) - this.yPos & 0xF) << 4 | i;
                if (this.blockEntities.put(l, (Dynamic<?>)dynamic) != null) {
                    LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", (Object)this.xPos, (Object)this.yPos, (Object)i, (Object)j, (Object)k);
                }
            }));
            boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
            dynamic.get("Sections").asStreamOpt().ifPresent(stream -> stream.forEach(dynamic -> {
                Section section = new Section((Dynamic<?>)dynamic);
                this.sides = section.visit(this.sides);
                this.sections[section.y] = section;
            }));
            for (Section section : this.sections) {
                if (section == null) continue;
                block14: for (Map.Entry entry : section.seenIds.entrySet()) {
                    int i = section.y << 12;
                    switch ((Integer)entry.getKey()) {
                        case 2: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, snowyGrass);
                            }
                            continue block14;
                        }
                        case 3: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, podzol);
                            }
                            continue block14;
                        }
                        case 110: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, snowyMycelium);
                            }
                            continue block14;
                        }
                        case 25: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
                                this.setBlock(j, (Dynamic)noteblock.getOrDefault(string, noteblock.get("false0")));
                            }
                            continue block14;
                        }
                        case 26: {
                            String string2;
                            Dynamic<?> dynamic3;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int k;
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(j |= i);
                                dynamic3 = this.getBlock(j);
                                if (dynamic2 == null || (k = dynamic2.get("color").asInt(0)) == 14 || k < 0 || k >= 16) continue;
                                string2 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing") + ChunkPalettedStorageFix.getProperty(dynamic3, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic3, "part") + k;
                                if (!bed.containsKey(string2)) continue;
                                this.setBlock(j, (Dynamic)bed.get(string2));
                            }
                            continue block14;
                        }
                        case 176: 
                        case 177: {
                            String string2;
                            Dynamic<?> dynamic3;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int k;
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(j |= i);
                                dynamic3 = this.getBlock(j);
                                if (dynamic2 == null || (k = dynamic2.get("Base").asInt(0)) == 15 || k < 0 || k >= 16) continue;
                                string2 = ChunkPalettedStorageFix.getProperty(dynamic3, (Integer)entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
                                if (!banner.containsKey(string2)) continue;
                                this.setBlock(j, (Dynamic)banner.get(string2));
                            }
                            continue block14;
                        }
                        case 86: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:grass_block".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.DOWN)))) && !"minecraft:dirt".equals(string)) continue;
                                this.setBlock(j, pumpkin);
                            }
                            continue block14;
                        }
                        case 140: {
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
                                this.setBlock(j, (Dynamic)flowerPot.getOrDefault(string, flowerPot.get("minecraft:air0")));
                            }
                            continue block14;
                        }
                        case 144: {
                            String string2;
                            String string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = String.valueOf(dynamic2.get("SkullType").asInt(0));
                                String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(j), "facing");
                                string2 = "up".equals(string3) || "down".equals(string3) ? string + String.valueOf(dynamic2.get("Rot").asInt(0)) : string + string3;
                                dynamic2.remove("SkullType");
                                dynamic2.remove("facing");
                                dynamic2.remove("Rot");
                                this.setBlock(j, (Dynamic)skull.getOrDefault(string2, skull.get("0north")));
                            }
                            continue block14;
                        }
                        case 64: 
                        case 71: 
                        case 193: 
                        case 194: 
                        case 195: 
                        case 196: 
                        case 197: {
                            Dynamic<?> dynamic3;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!ChunkPalettedStorageFix.getName(dynamic2).endsWith("_door") || !"lower".equals(ChunkPalettedStorageFix.getProperty(dynamic3 = this.getBlock(j), "half"))) continue;
                                int k = Level.adjacentTo(j, Facing.UP);
                                Dynamic<?> dynamic4 = this.getBlock(k);
                                String string4 = ChunkPalettedStorageFix.getName(dynamic3);
                                if (!string4.equals(ChunkPalettedStorageFix.getName(dynamic4))) continue;
                                String string5 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing");
                                String string6 = ChunkPalettedStorageFix.getProperty(dynamic3, "open");
                                String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic4, "hinge");
                                String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic4, "powered");
                                this.setBlock(j, (Dynamic)door.get(string4 + string5 + "lower" + string7 + string6 + string8));
                                this.setBlock(k, (Dynamic)door.get(string4 + string5 + "upper" + string7 + string6 + string8));
                            }
                            continue block14;
                        }
                        case 175: {
                            Dynamic<?> dynamic3;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"upper".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) continue;
                                dynamic3 = this.getBlock(Level.adjacentTo(j, Facing.DOWN));
                                String string3 = ChunkPalettedStorageFix.getName(dynamic3);
                                if ("minecraft:sunflower".equals(string3)) {
                                    this.setBlock(j, sunflowerUpper);
                                    continue;
                                }
                                if ("minecraft:lilac".equals(string3)) {
                                    this.setBlock(j, lilacUpper);
                                    continue;
                                }
                                if ("minecraft:tall_grass".equals(string3)) {
                                    this.setBlock(j, grassUpper);
                                    continue;
                                }
                                if ("minecraft:large_fern".equals(string3)) {
                                    this.setBlock(j, fernUpper);
                                    continue;
                                }
                                if ("minecraft:rose_bush".equals(string3)) {
                                    this.setBlock(j, roseUpper);
                                    continue;
                                }
                                if (!"minecraft:peony".equals(string3)) continue;
                                this.setBlock(j, peonyUpper);
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Nullable
        private Dynamic<?> getBlockEntity(int i) {
            return (Dynamic)this.blockEntities.get(i);
        }

        @Nullable
        private Dynamic<?> removeBlockEntity(int i) {
            return (Dynamic)this.blockEntities.remove(i);
        }

        public static int adjacentTo(int i, Facing direction) {
            switch (direction.getAxis()) {
                case X: {
                    int j = (i & 0xF) + direction.getDirection().getOffset();
                    return j < 0 || j > 15 ? -1 : i & 0xFFFFFFF0 | j;
                }
                case Y: {
                    int k = (i >> 8) + direction.getDirection().getOffset();
                    return k < 0 || k > 255 ? -1 : i & 0xFF | k << 8;
                }
                case Z: {
                    int l = (i >> 4 & 0xF) + direction.getDirection().getOffset();
                    return l < 0 || l > 15 ? -1 : i & 0xFFFFFF0F | l << 4;
                }
            }
            return -1;
        }

        private void setBlock(int i, Dynamic<?> dynamic) {
            if (i < 0 || i > 65535) {
                return;
            }
            Section section = this.getSection(i);
            if (section == null) {
                return;
            }
            section.setBlock(i & 0xFFF, dynamic);
        }

        @Nullable
        private Section getSection(int i) {
            int j = i >> 12;
            return j < this.sections.length ? this.sections[j] : null;
        }

        public Dynamic<?> getBlock(int i) {
            if (i < 0 || i > 65535) {
                return air;
            }
            Section section = this.getSection(i);
            if (section == null) {
                return air;
            }
            return section.getBlock(i & 0xFFF);
        }

        public Dynamic<?> transform() {
            Dynamic<Object> dynamic = this.level;
            dynamic = this.blockEntities.isEmpty() ? dynamic.remove("TileEntities") : dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            Dynamic dynamic2 = dynamic.emptyMap();
            Dynamic dynamic3 = dynamic.emptyList();
            for (Section section : this.sections) {
                if (section == null) continue;
                dynamic3 = dynamic3.merge(section.transform());
                dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
            }
            Dynamic dynamic4 = dynamic.emptyMap();
            dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.sides));
            dynamic4 = dynamic4.set("Indices", dynamic2);
            return dynamic.set("UpgradeData", dynamic4).set("Sections", dynamic3);
        }
    }

    static class Section {
        private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap(32);
        private Dynamic<?> paletteData;
        private final Dynamic<?> section;
        private final boolean hasBlocks;
        private final Int2ObjectMap<IntList> seenIds = new Int2ObjectLinkedOpenHashMap<IntList>();
        private final IntList innerPositions = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
        private final int[] states = new int[4096];

        public Section(Dynamic<?> dynamic) {
            this.paletteData = dynamic.emptyList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasBlocks = dynamic.get("Blocks").get().isPresent();
        }

        public Dynamic<?> getBlock(int i) {
            if (i < 0 || i > 4095) {
                return air;
            }
            Dynamic<?> dynamic = this.paletteMap.get(this.states[i]);
            return dynamic == null ? air : dynamic;
        }

        public void setBlock(int pos, Dynamic<?> dynamic) {
            if (this.seenStates.add(dynamic)) {
                this.paletteData = this.paletteData.merge("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? air : dynamic);
            }
            this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
        }

        public int visit(int i) {
            if (!this.hasBlocks) {
                return i;
            }
            ByteBuffer byteBuffer2 = this.section.get("Blocks").asByteBufferOpt().get();
            ChunkNibbleArray chunkNibbleArray = this.section.get("Data").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).orElseGet(ChunkNibbleArray::new);
            ChunkNibbleArray chunkNibbleArray2 = this.section.get("Add").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).orElseGet(ChunkNibbleArray::new);
            this.seenStates.add(air);
            ChunkPalettedStorageFix.addTo(this.paletteMap, air);
            this.paletteData = this.paletteData.merge(air);
            for (int j = 0; j < 4096; ++j) {
                int k = j & 0xF;
                int l = j >> 8 & 0xF;
                int m = j >> 4 & 0xF;
                int n = chunkNibbleArray2.get(k, l, m) << 12 | (byteBuffer2.get(j) & 0xFF) << 4 | chunkNibbleArray.get(k, l, m);
                if (field_15854.get(n >> 4)) {
                    this.addBlockAt(n >> 4, j);
                }
                if (field_15842.get(n >> 4)) {
                    int o = ChunkPalettedStorageFix.method_15615(k == 0, k == 15, m == 0, m == 15);
                    if (o == 0) {
                        this.innerPositions.add(j);
                    } else {
                        i |= o;
                    }
                }
                this.setBlock(j, BlockStateFlattening.lookupState(n));
            }
            return i;
        }

        private void addBlockAt(int i, int j) {
            IntList intList = (IntList)this.seenIds.get(i);
            if (intList == null) {
                intList = new IntArrayList();
                this.seenIds.put(i, intList);
            }
            intList.add(j);
        }

        public Dynamic<?> transform() {
            Dynamic<?> dynamic = this.section;
            if (!this.hasBlocks) {
                return dynamic;
            }
            dynamic = dynamic.set("Palette", this.paletteData);
            int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
            PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096);
            for (int j = 0; j < this.states.length; ++j) {
                packedIntegerArray.set(j, this.states[j]);
            }
            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedIntegerArray.getStorage())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
        }
    }
}

