/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ChunkPalettedStorageFix
extends DataFix {
    private static final int field_29871 = 128;
    private static final int field_29872 = 64;
    private static final int field_29873 = 32;
    private static final int field_29874 = 16;
    private static final int field_29875 = 8;
    private static final int field_29876 = 4;
    private static final int field_29877 = 2;
    private static final int field_29878 = 1;
    static final Logger LOGGER = LogManager.getLogger();
    static final BitSet BLOCKS_NEEDING_SIDE_UPDATE = new BitSet(256);
    static final BitSet BLOCKS_NEEDING_IN_PLACE_UPDATE = new BitSet(256);
    static final Dynamic<?> PUMPKIN = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
    static final Dynamic<?> PODZOL = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_GRASS = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    static final Dynamic<?> SNOWY_MYCELIUM = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    static final Dynamic<?> SUNFLOWER_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    static final Dynamic<?> LILAC_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    static final Dynamic<?> GRASS_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    static final Dynamic<?> FERN_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    static final Dynamic<?> ROSE_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    static final Dynamic<?> PEONY_UPPER = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    static final Map<String, Dynamic<?>> FLOWER_POT = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
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
    static final Map<String, Dynamic<?>> SKULL = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildSkull(hashMap, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 2, "zombie", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 3, "player", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 4, "creeper", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 5, "dragon", "head");
    });
    static final Map<String, Dynamic<?>> DOOR = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildDoor(hashMap, "oak_door", 1024);
        ChunkPalettedStorageFix.buildDoor(hashMap, "iron_door", 1136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "spruce_door", 3088);
        ChunkPalettedStorageFix.buildDoor(hashMap, "birch_door", 3104);
        ChunkPalettedStorageFix.buildDoor(hashMap, "jungle_door", 3120);
        ChunkPalettedStorageFix.buildDoor(hashMap, "acacia_door", 3136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "dark_oak_door", 3152);
    });
    static final Map<String, Dynamic<?>> NOTE_BLOCK = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (int i = 0; i < 26; ++i) {
            hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
            hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
        }
    });
    private static final Int2ObjectMap<String> COLORS = DataFixUtils.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
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
    static final Map<String, Dynamic<?>> BED = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : COLORS.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "red")) continue;
            ChunkPalettedStorageFix.buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
        }
    });
    static final Map<String, Dynamic<?>> BANNER = DataFixUtils.make(Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : COLORS.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "white")) continue;
            ChunkPalettedStorageFix.buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
        }
    });
    static final Dynamic<?> AIR;
    private static final int field_29870 = 4096;

    public ChunkPalettedStorageFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    private static void buildSkull(Map<String, Dynamic<?>> out, int i, String mob, String block) {
        out.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
        out.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
        out.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
        out.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));
        for (int j = 0; j < 16; ++j) {
            out.put("" + i + j, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + j + "'}}"));
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
            out.put(j + "_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
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
        int i = int2ObjectBiMap.getRawId(dynamic);
        if (i == -1) {
            i = int2ObjectBiMap.add(dynamic);
        }
        return i;
    }

    private Dynamic<?> fixChunk(Dynamic<?> dynamic) {
        Optional<Dynamic<?>> optional = dynamic.get("Level").result();
        if (optional.isPresent() && optional.get().get("Sections").asStreamOpt().result().isPresent()) {
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

    public static int getSideToUpgradeFlag(boolean west, boolean east, boolean north, boolean south) {
        int i = 0;
        if (north) {
            i = east ? (i |= 2) : (west ? (i |= 0x80) : (i |= 1));
        } else if (south) {
            i = west ? (i |= 0x20) : (east ? (i |= 8) : (i |= 0x10));
        } else if (east) {
            i |= 4;
        } else if (west) {
            i |= 0x40;
        }
        return i;
    }

    static {
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(2);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(3);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(110);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(140);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(144);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(25);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(86);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(26);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(176);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(177);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(175);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(64);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(71);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(193);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(194);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(195);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(196);
        BLOCKS_NEEDING_IN_PLACE_UPDATE.set(197);
        BLOCKS_NEEDING_SIDE_UPDATE.set(54);
        BLOCKS_NEEDING_SIDE_UPDATE.set(146);
        BLOCKS_NEEDING_SIDE_UPDATE.set(25);
        BLOCKS_NEEDING_SIDE_UPDATE.set(26);
        BLOCKS_NEEDING_SIDE_UPDATE.set(51);
        BLOCKS_NEEDING_SIDE_UPDATE.set(53);
        BLOCKS_NEEDING_SIDE_UPDATE.set(67);
        BLOCKS_NEEDING_SIDE_UPDATE.set(108);
        BLOCKS_NEEDING_SIDE_UPDATE.set(109);
        BLOCKS_NEEDING_SIDE_UPDATE.set(114);
        BLOCKS_NEEDING_SIDE_UPDATE.set(128);
        BLOCKS_NEEDING_SIDE_UPDATE.set(134);
        BLOCKS_NEEDING_SIDE_UPDATE.set(135);
        BLOCKS_NEEDING_SIDE_UPDATE.set(136);
        BLOCKS_NEEDING_SIDE_UPDATE.set(156);
        BLOCKS_NEEDING_SIDE_UPDATE.set(163);
        BLOCKS_NEEDING_SIDE_UPDATE.set(164);
        BLOCKS_NEEDING_SIDE_UPDATE.set(180);
        BLOCKS_NEEDING_SIDE_UPDATE.set(203);
        BLOCKS_NEEDING_SIDE_UPDATE.set(55);
        BLOCKS_NEEDING_SIDE_UPDATE.set(85);
        BLOCKS_NEEDING_SIDE_UPDATE.set(113);
        BLOCKS_NEEDING_SIDE_UPDATE.set(188);
        BLOCKS_NEEDING_SIDE_UPDATE.set(189);
        BLOCKS_NEEDING_SIDE_UPDATE.set(190);
        BLOCKS_NEEDING_SIDE_UPDATE.set(191);
        BLOCKS_NEEDING_SIDE_UPDATE.set(192);
        BLOCKS_NEEDING_SIDE_UPDATE.set(93);
        BLOCKS_NEEDING_SIDE_UPDATE.set(94);
        BLOCKS_NEEDING_SIDE_UPDATE.set(101);
        BLOCKS_NEEDING_SIDE_UPDATE.set(102);
        BLOCKS_NEEDING_SIDE_UPDATE.set(160);
        BLOCKS_NEEDING_SIDE_UPDATE.set(106);
        BLOCKS_NEEDING_SIDE_UPDATE.set(107);
        BLOCKS_NEEDING_SIDE_UPDATE.set(183);
        BLOCKS_NEEDING_SIDE_UPDATE.set(184);
        BLOCKS_NEEDING_SIDE_UPDATE.set(185);
        BLOCKS_NEEDING_SIDE_UPDATE.set(186);
        BLOCKS_NEEDING_SIDE_UPDATE.set(187);
        BLOCKS_NEEDING_SIDE_UPDATE.set(132);
        BLOCKS_NEEDING_SIDE_UPDATE.set(139);
        BLOCKS_NEEDING_SIDE_UPDATE.set(199);
        AIR = BlockStateFlattening.lookupState(0);
    }

    static final class Level {
        private int sidesToUpgrade;
        private final Section[] sections = new Section[16];
        private final Dynamic<?> level;
        private final int x;
        private final int z;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public Level(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.x = dynamic.get("xPos").asInt(0) << 4;
            this.z = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                int k;
                int i = dynamic.get("x").asInt(0) - this.x & 0xF;
                int j = dynamic.get("y").asInt(0);
                int l = j << 8 | (k = dynamic.get("z").asInt(0) - this.z & 0xF) << 4 | i;
                if (this.blockEntities.put(l, (Dynamic<?>)dynamic) != null) {
                    LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", (Object)this.x, (Object)this.z, (Object)i, (Object)j, (Object)k);
                }
            }));
            boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
            dynamic.get("Sections").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                Section section = new Section((Dynamic<?>)dynamic);
                this.sidesToUpgrade = section.visit(this.sidesToUpgrade);
                this.sections[section.y] = section;
            }));
            for (Section section : this.sections) {
                if (section == null) continue;
                block14: for (Map.Entry entry : section.inPlaceUpdates.entrySet()) {
                    int i = section.y << 12;
                    switch ((Integer)entry.getKey()) {
                        case 2: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, SNOWY_GRASS);
                            }
                            continue block14;
                        }
                        case 3: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, PODZOL);
                            }
                            continue block14;
                        }
                        case 110: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, SNOWY_MYCELIUM);
                            }
                            continue block14;
                        }
                        case 25: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
                                this.setBlock(j, NOTE_BLOCK.getOrDefault(string, NOTE_BLOCK.get("false0")));
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
                                if (dynamic2 == null || (k = dynamic2.get("color").asInt(0)) == 14 || k < 0 || k >= 16 || !BED.containsKey(string2 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing") + ChunkPalettedStorageFix.getProperty(dynamic3, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic3, "part") + k)) continue;
                                this.setBlock(j, BED.get(string2));
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
                                if (dynamic2 == null || (k = dynamic2.get("Base").asInt(0)) == 15 || k < 0 || k >= 16 || !BANNER.containsKey(string2 = ChunkPalettedStorageFix.getProperty(dynamic3, (Integer)entry.getKey() == 176 ? "rotation" : "facing") + "_" + k)) continue;
                                this.setBlock(j, BANNER.get(string2));
                            }
                            continue block14;
                        }
                        case 86: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:grass_block".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.DOWN)))) && !"minecraft:dirt".equals(string)) continue;
                                this.setBlock(j, PUMPKIN);
                            }
                            continue block14;
                        }
                        case 140: {
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.removeBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
                                this.setBlock(j, FLOWER_POT.getOrDefault(string, FLOWER_POT.get("minecraft:air0")));
                            }
                            continue block14;
                        }
                        case 144: {
                            String string2;
                            Object string;
                            Dynamic<?> dynamic2;
                            int j;
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                j = (Integer)intListIterator.next();
                                dynamic2 = this.getBlockEntity(j |= i);
                                if (dynamic2 == null) continue;
                                string = String.valueOf(dynamic2.get("SkullType").asInt(0));
                                String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(j), "facing");
                                string2 = "up".equals(string3) || "down".equals(string3) ? (String)string + String.valueOf(dynamic2.get("Rot").asInt(0)) : (String)string + string3;
                                dynamic2.remove("SkullType");
                                dynamic2.remove("facing");
                                dynamic2.remove("Rot");
                                this.setBlock(j, SKULL.getOrDefault(string2, SKULL.get("0north")));
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
                                this.setBlock(j, DOOR.get(string4 + string5 + "lower" + string7 + string6 + string8));
                                this.setBlock(k, DOOR.get(string4 + string5 + "upper" + string7 + string6 + string8));
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
                                    this.setBlock(j, SUNFLOWER_UPPER);
                                    continue;
                                }
                                if ("minecraft:lilac".equals(string3)) {
                                    this.setBlock(j, LILAC_UPPER);
                                    continue;
                                }
                                if ("minecraft:tall_grass".equals(string3)) {
                                    this.setBlock(j, GRASS_UPPER);
                                    continue;
                                }
                                if ("minecraft:large_fern".equals(string3)) {
                                    this.setBlock(j, FERN_UPPER);
                                    continue;
                                }
                                if ("minecraft:rose_bush".equals(string3)) {
                                    this.setBlock(j, ROSE_UPPER);
                                    continue;
                                }
                                if (!"minecraft:peony".equals(string3)) continue;
                                this.setBlock(j, PEONY_UPPER);
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
                return AIR;
            }
            Section section = this.getSection(i);
            if (section == null) {
                return AIR;
            }
            return section.getBlock(i & 0xFFF);
        }

        public Dynamic<?> transform() {
            Dynamic<Object> dynamic = this.level;
            dynamic = this.blockEntities.isEmpty() ? dynamic.remove("TileEntities") : dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            Dynamic dynamic2 = dynamic.emptyMap();
            ArrayList<Dynamic<?>> list = Lists.newArrayList();
            for (Section section : this.sections) {
                if (section == null) continue;
                list.add(section.transform());
                dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
            }
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
            dynamic3 = dynamic3.set("Indices", dynamic2);
            return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
        }
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

        public static enum Axis {
            X,
            Y,
            Z;

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
    }

    static class ChunkNibbleArray {
        private static final int field_29879 = 2048;
        private static final int field_29880 = 4;
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
            int j = this.getRawIndex(y << 8 | i << 4 | x);
            if (this.usesLowNibble(y << 8 | i << 4 | x)) {
                return this.contents[j] & 0xF;
            }
            return this.contents[j] >> 4 & 0xF;
        }

        private boolean usesLowNibble(int index) {
            return (index & 1) == 0;
        }

        private int getRawIndex(int index) {
            return index >> 1;
        }
    }

    static class Section {
        private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap(32);
        private final List<Dynamic<?>> paletteData;
        private final Dynamic<?> section;
        private final boolean hasBlocks;
        final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap<IntList>();
        final IntList innerPositions = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
        private final int[] states = new int[4096];

        public Section(Dynamic<?> dynamic) {
            this.paletteData = Lists.newArrayList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasBlocks = dynamic.get("Blocks").result().isPresent();
        }

        public Dynamic<?> getBlock(int index) {
            if (index < 0 || index > 4095) {
                return AIR;
            }
            Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
            return dynamic == null ? AIR : dynamic;
        }

        public void setBlock(int pos, Dynamic<?> dynamic) {
            if (this.seenStates.add(dynamic)) {
                this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? AIR : dynamic);
            }
            this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
        }

        public int visit(int sidesToUpgrade) {
            if (!this.hasBlocks) {
                return sidesToUpgrade;
            }
            ByteBuffer byteBuffer2 = this.section.get("Blocks").asByteBufferOpt().result().get();
            ChunkNibbleArray chunkNibbleArray = this.section.get("Data").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).result().orElseGet(ChunkNibbleArray::new);
            ChunkNibbleArray chunkNibbleArray2 = this.section.get("Add").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray(byteBuffer))).result().orElseGet(ChunkNibbleArray::new);
            this.seenStates.add(AIR);
            ChunkPalettedStorageFix.addTo(this.paletteMap, AIR);
            this.paletteData.add(AIR);
            for (int i = 0; i < 4096; ++i) {
                int j = i & 0xF;
                int k = i >> 8 & 0xF;
                int l = i >> 4 & 0xF;
                int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer2.get(i) & 0xFF) << 4 | chunkNibbleArray.get(j, k, l);
                if (BLOCKS_NEEDING_IN_PLACE_UPDATE.get(m >> 4)) {
                    this.addInPlaceUpdate(m >> 4, i);
                }
                if (BLOCKS_NEEDING_SIDE_UPDATE.get(m >> 4)) {
                    int n = ChunkPalettedStorageFix.getSideToUpgradeFlag(j == 0, j == 15, l == 0, l == 15);
                    if (n == 0) {
                        this.innerPositions.add(i);
                    } else {
                        sidesToUpgrade |= n;
                    }
                }
                this.setBlock(i, BlockStateFlattening.lookupState(m));
            }
            return sidesToUpgrade;
        }

        private void addInPlaceUpdate(int section, int index) {
            IntList intList = (IntList)this.inPlaceUpdates.get(section);
            if (intList == null) {
                intList = new IntArrayList();
                this.inPlaceUpdates.put(section, intList);
            }
            intList.add(index);
        }

        public Dynamic<?> transform() {
            Dynamic<Object> dynamic = this.section;
            if (!this.hasBlocks) {
                return dynamic;
            }
            dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
            int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
            WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);
            for (int j = 0; j < this.states.length; ++j) {
                wordPackedArray.set(j, this.states[j]);
            }
            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
        }
    }
}

