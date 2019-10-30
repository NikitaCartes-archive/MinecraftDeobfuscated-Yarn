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
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.util.Int2ObjectBiMap;
import net.minecraft.util.PackedIntegerArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
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
	private static final Map<String, Dynamic<?>> flowerPot = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
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
	private static final Map<String, Dynamic<?>> skull = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		buildSkull(hashMap, 0, "skeleton", "skull");
		buildSkull(hashMap, 1, "wither_skeleton", "skull");
		buildSkull(hashMap, 2, "zombie", "head");
		buildSkull(hashMap, 3, "player", "head");
		buildSkull(hashMap, 4, "creeper", "head");
		buildSkull(hashMap, 5, "dragon", "head");
	});
	private static final Map<String, Dynamic<?>> door = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		buildDoor(hashMap, "oak_door", 1024);
		buildDoor(hashMap, "iron_door", 1136);
		buildDoor(hashMap, "spruce_door", 3088);
		buildDoor(hashMap, "birch_door", 3104);
		buildDoor(hashMap, "jungle_door", 3120);
		buildDoor(hashMap, "acacia_door", 3136);
		buildDoor(hashMap, "dark_oak_door", 3152);
	});
	private static final Map<String, Dynamic<?>> noteblock = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (int i = 0; i < 26; i++) {
			hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> colors = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
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
	private static final Map<String, Dynamic<?>> bed = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (Entry<String> entry : colors.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "red")) {
				buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Map<String, Dynamic<?>> banner = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (Entry<String> entry : colors.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "white")) {
				buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Dynamic<?> air = BlockStateFlattening.lookupState(0);

	public ChunkPalettedStorageFix(Schema outputSchema, boolean changesType) {
		super(outputSchema, changesType);
	}

	private static void buildSkull(Map<String, Dynamic<?>> out, int i, String mob, String block) {
		out.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
		out.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
		out.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
		out.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));

		for (int j = 0; j < 16; j++) {
			out.put(i + "" + j, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + j + "'}}"));
		}
	}

	private static void buildDoor(Map<String, Dynamic<?>> out, String name, int i) {
		out.put(
			"minecraft:" + name + "eastlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(i));
		out.put(
			"minecraft:" + name + "eastlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(i + 4));
		out.put(
			"minecraft:" + name + "eastlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(i + 8));
		out.put("minecraft:" + name + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(i + 10));
		out.put(
			"minecraft:" + name + "eastupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(i + 9));
		out.put("minecraft:" + name + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(i + 11));
		out.put(
			"minecraft:" + name + "eastupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 3));
		out.put(
			"minecraft:" + name + "northlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "northlowerrighttruefalse", BlockStateFlattening.lookupState(i + 7));
		out.put(
			"minecraft:" + name + "northlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "northupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 1));
		out.put(
			"minecraft:" + name + "southlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "southlowerrighttruefalse", BlockStateFlattening.lookupState(i + 5));
		out.put(
			"minecraft:" + name + "southlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "southupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westlowerlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 2));
		out.put(
			"minecraft:" + name + "westlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "westlowerrighttruefalse", BlockStateFlattening.lookupState(i + 6));
		out.put(
			"minecraft:" + name + "westlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperleftfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperleftfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrightfalsefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrighttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "westupperrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
	}

	private static void buildBed(Map<String, Dynamic<?>> out, int i, String string) {
		out.put(
			"southfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}")
		);
		out.put("westfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
		out.put(
			"northfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}")
		);
		out.put("eastfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
		out.put(
			"southfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}")
		);
		out.put("westfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
		out.put(
			"northfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}")
		);
		out.put("eastfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
		out.put("southtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
		out.put("westtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
		out.put("northtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
		out.put("easttruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
	}

	private static void buildBanner(Map<String, Dynamic<?>> out, int i, String string) {
		for (int j = 0; j < 16; j++) {
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
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level").get();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().isPresent()
			? dynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: dynamic;
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
			if (bl2) {
				i |= 2;
			} else if (bl) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (bl4) {
			if (bl) {
				i |= 32;
			} else if (bl2) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (bl2) {
			i |= 4;
		} else if (bl) {
			i |= 64;
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
			return this.method_15641(y << 8 | i << 4 | x) ? this.contents[j] & 15 : this.contents[j] >> 4 & 15;
		}

		private boolean method_15641(int i) {
			return (i & 1) == 0;
		}

		private int method_15642(int i) {
			return i >> 1;
		}
	}

	public static enum Facing {
		DOWN(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		UP(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Y),
		NORTH(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		SOUTH(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.Z),
		WEST(ChunkPalettedStorageFix.Facing.Direction.NEGATIVE, ChunkPalettedStorageFix.Facing.Axis.X),
		EAST(ChunkPalettedStorageFix.Facing.Direction.POSITIVE, ChunkPalettedStorageFix.Facing.Axis.X);

		private final ChunkPalettedStorageFix.Facing.Axis axis;
		private final ChunkPalettedStorageFix.Facing.Direction direction;

		private Facing(ChunkPalettedStorageFix.Facing.Direction direction, ChunkPalettedStorageFix.Facing.Axis axis) {
			this.axis = axis;
			this.direction = direction;
		}

		public ChunkPalettedStorageFix.Facing.Direction getDirection() {
			return this.direction;
		}

		public ChunkPalettedStorageFix.Facing.Axis getAxis() {
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

	static final class Level {
		private int sides;
		private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
		private final Dynamic<?> level;
		private final int xPos;
		private final int yPos;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

		public Level(Dynamic<?> dynamic) {
			this.level = dynamic;
			this.xPos = dynamic.get("xPos").asInt(0) << 4;
			this.yPos = dynamic.get("zPos").asInt(0) << 4;
			dynamic.get("TileEntities").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
					int ix = dynamicx.get("x").asInt(0) - this.xPos & 15;
					int jx = dynamicx.get("y").asInt(0);
					int k = dynamicx.get("z").asInt(0) - this.yPos & 15;
					int l = jx << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, dynamicx) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.xPos, this.yPos, ix, jx, k);
					}
				}));
			boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
			dynamic.get("Sections").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
					ChunkPalettedStorageFix.Section sectionx = new ChunkPalettedStorageFix.Section(dynamicx);
					this.sides = sectionx.visit(this.sides);
					this.sections[sectionx.y] = sectionx;
				}));

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					for (java.util.Map.Entry<Integer, IntList> entry : section.seenIds.entrySet()) {
						int i = section.y << 12;
						switch (entry.getKey()) {
							case 2:
								for (int j : (IntList)entry.getValue()) {
									j |= i;
									Dynamic<?> dynamic2 = this.getBlock(j);
									if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.snowyGrass);
										}
									}
								}
								break;
							case 3:
								for (int jxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxxxx);
									if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxxxx, ChunkPalettedStorageFix.podzol);
										}
									}
								}
								break;
							case 25:
								for (int jxxxxx : (IntList)entry.getValue()) {
									jxxxxx |= i;
									Dynamic<?> dynamic2 = this.removeBlockEntity(jxxxxx);
									if (dynamic2 != null) {
										String string = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
										this.setBlock(jxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.noteblock.getOrDefault(string, ChunkPalettedStorageFix.noteblock.get("false0")));
									}
								}
								break;
							case 26:
								for (int jxxxx : (IntList)entry.getValue()) {
									jxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlockEntity(jxxxx);
									Dynamic<?> dynamic3 = this.getBlock(jxxxx);
									if (dynamic2 != null) {
										int k = dynamic2.get("color").asInt(0);
										if (k != 14 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing")
												+ ChunkPalettedStorageFix.getProperty(dynamic3, "occupied")
												+ ChunkPalettedStorageFix.getProperty(dynamic3, "part")
												+ k;
											if (ChunkPalettedStorageFix.bed.containsKey(string2)) {
												this.setBlock(jxxxx, (Dynamic<?>)ChunkPalettedStorageFix.bed.get(string2));
											}
										}
									}
								}
								break;
							case 64:
							case 71:
							case 193:
							case 194:
							case 195:
							case 196:
							case 197:
								for (int jxxx : (IntList)entry.getValue()) {
									jxxx |= i;
									Dynamic<?> dynamic2 = this.getBlock(jxxx);
									if (ChunkPalettedStorageFix.getName(dynamic2).endsWith("_door")) {
										Dynamic<?> dynamic3 = this.getBlock(jxxx);
										if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic3, "half"))) {
											int k = adjacentTo(jxxx, ChunkPalettedStorageFix.Facing.UP);
											Dynamic<?> dynamic4 = this.getBlock(k);
											String string4 = ChunkPalettedStorageFix.getName(dynamic3);
											if (string4.equals(ChunkPalettedStorageFix.getName(dynamic4))) {
												String string5 = ChunkPalettedStorageFix.getProperty(dynamic3, "facing");
												String string6 = ChunkPalettedStorageFix.getProperty(dynamic3, "open");
												String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic4, "hinge");
												String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic4, "powered");
												this.setBlock(jxxx, (Dynamic<?>)ChunkPalettedStorageFix.door.get(string4 + string5 + "lower" + string7 + string6 + string8));
												this.setBlock(k, (Dynamic<?>)ChunkPalettedStorageFix.door.get(string4 + string5 + "upper" + string7 + string6 + string8));
											}
										}
									}
								}
								break;
							case 86:
								for (int jxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxxx);
									if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxx, ChunkPalettedStorageFix.Facing.DOWN)));
										if ("minecraft:grass_block".equals(string) || "minecraft:dirt".equals(string)) {
											this.setBlock(jxxxxxxxx, ChunkPalettedStorageFix.pumpkin);
										}
									}
								}
								break;
							case 110:
								for (int jxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxx);
									if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic2))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxx, ChunkPalettedStorageFix.snowyMycelium);
										}
									}
								}
								break;
							case 140:
								for (int jxx : (IntList)entry.getValue()) {
									jxx |= i;
									Dynamic<?> dynamic2 = this.removeBlockEntity(jxx);
									if (dynamic2 != null) {
										String string = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
										this.setBlock(jxx, (Dynamic<?>)ChunkPalettedStorageFix.flowerPot.getOrDefault(string, ChunkPalettedStorageFix.flowerPot.get("minecraft:air0")));
									}
								}
								break;
							case 144:
								for (int jxxxxxx : (IntList)entry.getValue()) {
									jxxxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlockEntity(jxxxxxx);
									if (dynamic2 != null) {
										String string = String.valueOf(dynamic2.get("SkullType").asInt(0));
										String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(jxxxxxx), "facing");
										String string2;
										if (!"up".equals(string3) && !"down".equals(string3)) {
											string2 = string + string3;
										} else {
											string2 = string + String.valueOf(dynamic2.get("Rot").asInt(0));
										}

										dynamic2.remove("SkullType");
										dynamic2.remove("facing");
										dynamic2.remove("Rot");
										this.setBlock(jxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.skull.getOrDefault(string2, ChunkPalettedStorageFix.skull.get("0north")));
									}
								}
								break;
							case 175:
								for (int jx : (IntList)entry.getValue()) {
									jx |= i;
									Dynamic<?> dynamic2 = this.getBlock(jx);
									if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
										Dynamic<?> dynamic3 = this.getBlock(adjacentTo(jx, ChunkPalettedStorageFix.Facing.DOWN));
										String string3 = ChunkPalettedStorageFix.getName(dynamic3);
										if ("minecraft:sunflower".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.sunflowerUpper);
										} else if ("minecraft:lilac".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.lilacUpper);
										} else if ("minecraft:tall_grass".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.grassUpper);
										} else if ("minecraft:large_fern".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.fernUpper);
										} else if ("minecraft:rose_bush".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.roseUpper);
										} else if ("minecraft:peony".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.peonyUpper);
										}
									}
								}
								break;
							case 176:
							case 177:
								for (int jxxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.getBlockEntity(jxxxxxxxxxx);
									Dynamic<?> dynamic3 = this.getBlock(jxxxxxxxxxx);
									if (dynamic2 != null) {
										int k = dynamic2.get("Base").asInt(0);
										if (k != 15 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic3, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
											if (ChunkPalettedStorageFix.banner.containsKey(string2)) {
												this.setBlock(jxxxxxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.banner.get(string2));
											}
										}
									}
								}
						}
					}
				}
			}
		}

		@Nullable
		private Dynamic<?> getBlockEntity(int i) {
			return this.blockEntities.get(i);
		}

		@Nullable
		private Dynamic<?> removeBlockEntity(int i) {
			return this.blockEntities.remove(i);
		}

		public static int adjacentTo(int i, ChunkPalettedStorageFix.Facing direction) {
			switch (direction.getAxis()) {
				case X:
					int j = (i & 15) + direction.getDirection().getOffset();
					return j >= 0 && j <= 15 ? i & -16 | j : -1;
				case Y:
					int k = (i >> 8) + direction.getDirection().getOffset();
					return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
				case Z:
					int l = (i >> 4 & 15) + direction.getDirection().getOffset();
					return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
				default:
					return -1;
			}
		}

		private void setBlock(int i, Dynamic<?> dynamic) {
			if (i >= 0 && i <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(i);
				if (section != null) {
					section.setBlock(i & 4095, dynamic);
				}
			}
		}

		@Nullable
		private ChunkPalettedStorageFix.Section getSection(int i) {
			int j = i >> 12;
			return j < this.sections.length ? this.sections[j] : null;
		}

		public Dynamic<?> getBlock(int i) {
			if (i >= 0 && i <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(i);
				return section == null ? ChunkPalettedStorageFix.air : section.getBlock(i & 4095);
			} else {
				return ChunkPalettedStorageFix.air;
			}
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.level;
			if (this.blockEntities.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			Dynamic<?> dynamic3 = dynamic.emptyList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					dynamic3 = dynamic3.merge(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic4 = dynamic.emptyMap();
			dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.sides));
			dynamic4 = dynamic4.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic4).set("Sections", dynamic3);
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap<>(32);
		private Dynamic<?> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		private final Int2ObjectMap<IntList> seenIds = new Int2ObjectLinkedOpenHashMap<>();
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
			if (i >= 0 && i <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[i]);
				return dynamic == null ? ChunkPalettedStorageFix.air : dynamic;
			} else {
				return ChunkPalettedStorageFix.air;
			}
		}

		public void setBlock(int pos, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData = this.paletteData.merge("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.air : dynamic);
			}

			this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int i) {
			if (!this.hasBlocks) {
				return i;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.air);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.air);
				this.paletteData = this.paletteData.merge(ChunkPalettedStorageFix.air);

				for (int j = 0; j < 4096; j++) {
					int k = j & 15;
					int l = j >> 8 & 15;
					int m = j >> 4 & 15;
					int n = chunkNibbleArray2.get(k, l, m) << 12 | (byteBuffer.get(j) & 255) << 4 | chunkNibbleArray.get(k, l, m);
					if (ChunkPalettedStorageFix.field_15854.get(n >> 4)) {
						this.addBlockAt(n >> 4, j);
					}

					if (ChunkPalettedStorageFix.field_15842.get(n >> 4)) {
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
		}

		private void addBlockAt(int i, int j) {
			IntList intList = this.seenIds.get(i);
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
			} else {
				dynamic = dynamic.set("Palette", this.paletteData);
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				PackedIntegerArray packedIntegerArray = new PackedIntegerArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					packedIntegerArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(packedIntegerArray.getStorage())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}
}
