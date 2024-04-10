package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;
import org.slf4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
	private static final int field_29871 = 128;
	private static final int field_29872 = 64;
	private static final int field_29873 = 32;
	private static final int field_29874 = 16;
	private static final int field_29875 = 8;
	private static final int field_29876 = 4;
	private static final int field_29877 = 2;
	private static final int field_29878 = 1;
	static final Logger LOGGER = LogUtils.getLogger();
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
	static final Map<String, Dynamic<?>> FLOWER_POT = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		map.put("minecraft:air0", BlockStateFlattening.parseState("{Name:'minecraft:flower_pot'}"));
		map.put("minecraft:red_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_poppy'}"));
		map.put("minecraft:red_flower1", BlockStateFlattening.parseState("{Name:'minecraft:potted_blue_orchid'}"));
		map.put("minecraft:red_flower2", BlockStateFlattening.parseState("{Name:'minecraft:potted_allium'}"));
		map.put("minecraft:red_flower3", BlockStateFlattening.parseState("{Name:'minecraft:potted_azure_bluet'}"));
		map.put("minecraft:red_flower4", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_tulip'}"));
		map.put("minecraft:red_flower5", BlockStateFlattening.parseState("{Name:'minecraft:potted_orange_tulip'}"));
		map.put("minecraft:red_flower6", BlockStateFlattening.parseState("{Name:'minecraft:potted_white_tulip'}"));
		map.put("minecraft:red_flower7", BlockStateFlattening.parseState("{Name:'minecraft:potted_pink_tulip'}"));
		map.put("minecraft:red_flower8", BlockStateFlattening.parseState("{Name:'minecraft:potted_oxeye_daisy'}"));
		map.put("minecraft:yellow_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dandelion'}"));
		map.put("minecraft:sapling0", BlockStateFlattening.parseState("{Name:'minecraft:potted_oak_sapling'}"));
		map.put("minecraft:sapling1", BlockStateFlattening.parseState("{Name:'minecraft:potted_spruce_sapling'}"));
		map.put("minecraft:sapling2", BlockStateFlattening.parseState("{Name:'minecraft:potted_birch_sapling'}"));
		map.put("minecraft:sapling3", BlockStateFlattening.parseState("{Name:'minecraft:potted_jungle_sapling'}"));
		map.put("minecraft:sapling4", BlockStateFlattening.parseState("{Name:'minecraft:potted_acacia_sapling'}"));
		map.put("minecraft:sapling5", BlockStateFlattening.parseState("{Name:'minecraft:potted_dark_oak_sapling'}"));
		map.put("minecraft:red_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_mushroom'}"));
		map.put("minecraft:brown_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_brown_mushroom'}"));
		map.put("minecraft:deadbush0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dead_bush'}"));
		map.put("minecraft:tallgrass2", BlockStateFlattening.parseState("{Name:'minecraft:potted_fern'}"));
		map.put("minecraft:cactus0", BlockStateFlattening.lookupState(2240));
	});
	static final Map<String, Dynamic<?>> SKULL = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		buildSkull(map, 0, "skeleton", "skull");
		buildSkull(map, 1, "wither_skeleton", "skull");
		buildSkull(map, 2, "zombie", "head");
		buildSkull(map, 3, "player", "head");
		buildSkull(map, 4, "creeper", "head");
		buildSkull(map, 5, "dragon", "head");
	});
	static final Map<String, Dynamic<?>> DOOR = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		buildDoor(map, "oak_door", 1024);
		buildDoor(map, "iron_door", 1136);
		buildDoor(map, "spruce_door", 3088);
		buildDoor(map, "birch_door", 3104);
		buildDoor(map, "jungle_door", 3120);
		buildDoor(map, "acacia_door", 3136);
		buildDoor(map, "dark_oak_door", 3152);
	});
	static final Map<String, Dynamic<?>> NOTE_BLOCK = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		for (int i = 0; i < 26; i++) {
			map.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			map.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> COLORS = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), map -> {
		map.put(0, "white");
		map.put(1, "orange");
		map.put(2, "magenta");
		map.put(3, "light_blue");
		map.put(4, "yellow");
		map.put(5, "lime");
		map.put(6, "pink");
		map.put(7, "gray");
		map.put(8, "light_gray");
		map.put(9, "cyan");
		map.put(10, "purple");
		map.put(11, "blue");
		map.put(12, "brown");
		map.put(13, "green");
		map.put(14, "red");
		map.put(15, "black");
	});
	static final Map<String, Dynamic<?>> BED = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		for (Entry<String> entry : COLORS.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "red")) {
				buildBed(map, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	static final Map<String, Dynamic<?>> BANNER = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), map -> {
		for (Entry<String> entry : COLORS.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "white")) {
				buildBanner(map, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	static final Dynamic<?> AIR = BlockStateFlattening.lookupState(0);
	private static final int field_29870 = 4096;

	public ChunkPalettedStorageFix(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static void buildSkull(Map<String, Dynamic<?>> out, int variant, String mob, String block) {
		out.put(variant + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'north'}}"));
		out.put(variant + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'east'}}"));
		out.put(variant + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'south'}}"));
		out.put(variant + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_wall_" + block + "',Properties:{facing:'west'}}"));

		for (int i = 0; i < 16; i++) {
			out.put("" + variant + i, BlockStateFlattening.parseState("{Name:'minecraft:" + mob + "_" + block + "',Properties:{rotation:'" + i + "'}}"));
		}
	}

	private static void buildDoor(Map<String, Dynamic<?>> out, String name, int firstStateId) {
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
		out.put("minecraft:" + name + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(firstStateId));
		out.put(
			"minecraft:" + name + "eastlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(firstStateId + 4));
		out.put(
			"minecraft:" + name + "eastlowerrighttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(firstStateId + 8));
		out.put("minecraft:" + name + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(firstStateId + 10));
		out.put(
			"minecraft:" + name + "eastupperlefttruefalse",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		out.put(
			"minecraft:" + name + "eastupperlefttruetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		out.put("minecraft:" + name + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(firstStateId + 9));
		out.put("minecraft:" + name + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(firstStateId + 11));
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
		out.put("minecraft:" + name + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(firstStateId + 3));
		out.put(
			"minecraft:" + name + "northlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "northlowerrighttruefalse", BlockStateFlattening.lookupState(firstStateId + 7));
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
		out.put("minecraft:" + name + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(firstStateId + 1));
		out.put(
			"minecraft:" + name + "southlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "southlowerrighttruefalse", BlockStateFlattening.lookupState(firstStateId + 5));
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
		out.put("minecraft:" + name + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(firstStateId + 2));
		out.put(
			"minecraft:" + name + "westlowerrightfalsetrue",
			BlockStateFlattening.parseState("{Name:'minecraft:" + name + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		out.put("minecraft:" + name + "westlowerrighttruefalse", BlockStateFlattening.lookupState(firstStateId + 6));
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

	private static void buildBed(Map<String, Dynamic<?>> out, int colorId, String color) {
		out.put(
			"southfalsefoot" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}")
		);
		out.put(
			"westfalsefoot" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}")
		);
		out.put(
			"northfalsefoot" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}")
		);
		out.put(
			"eastfalsefoot" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}")
		);
		out.put(
			"southfalsehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}")
		);
		out.put(
			"westfalsehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}")
		);
		out.put(
			"northfalsehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}")
		);
		out.put(
			"eastfalsehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}")
		);
		out.put(
			"southtruehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}")
		);
		out.put(
			"westtruehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}")
		);
		out.put(
			"northtruehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}")
		);
		out.put(
			"easttruehead" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}")
		);
	}

	private static void buildBanner(Map<String, Dynamic<?>> out, int colorId, String color) {
		for (int i = 0; i < 16; i++) {
			out.put(i + "_" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_banner',Properties:{rotation:'" + i + "'}}"));
		}

		out.put("north_" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_wall_banner',Properties:{facing:'north'}}"));
		out.put("south_" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_wall_banner',Properties:{facing:'south'}}"));
		out.put("west_" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_wall_banner',Properties:{facing:'west'}}"));
		out.put("east_" + colorId, BlockStateFlattening.parseState("{Name:'minecraft:" + color + "_wall_banner',Properties:{facing:'east'}}"));
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
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level").result();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()
			? dynamic.set("Level", new ChunkPalettedStorageFix.Level((Dynamic<?>)optional.get()).transform())
			: dynamic;
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
			if (east) {
				i |= 2;
			} else if (west) {
				i |= 128;
			} else {
				i |= 1;
			}
		} else if (south) {
			if (west) {
				i |= 32;
			} else if (east) {
				i |= 8;
			} else {
				i |= 16;
			}
		} else if (east) {
			i |= 4;
		} else if (west) {
			i |= 64;
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
	}

	static class ChunkNibbleArray {
		private static final int CONTENTS_LENGTH = 2048;
		private static final int field_29880 = 4;
		private final byte[] contents;

		public ChunkNibbleArray() {
			this.contents = new byte[2048];
		}

		public ChunkNibbleArray(byte[] contents) {
			this.contents = contents;
			if (contents.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + contents.length);
			}
		}

		public int get(int x, int y, int z) {
			int i = this.getRawIndex(y << 8 | z << 4 | x);
			return this.usesLowNibble(y << 8 | z << 4 | x) ? this.contents[i] & 15 : this.contents[i] >> 4 & 15;
		}

		private boolean usesLowNibble(int index) {
			return (index & 1) == 0;
		}

		private int getRawIndex(int index) {
			return index >> 1;
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

		private Facing(final ChunkPalettedStorageFix.Facing.Direction direction, final ChunkPalettedStorageFix.Facing.Axis axis) {
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

			private Direction(final int offset) {
				this.offset = offset;
			}

			public int getOffset() {
				return this.offset;
			}
		}
	}

	static final class Level {
		private int sidesToUpgrade;
		private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
		private final Dynamic<?> level;
		private final int x;
		private final int z;
		private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap<>(16);

		public Level(Dynamic<?> chunkTag) {
			this.level = chunkTag;
			this.x = chunkTag.get("xPos").asInt(0) << 4;
			this.z = chunkTag.get("zPos").asInt(0) << 4;
			chunkTag.get("TileEntities").asStreamOpt().ifSuccess(stream -> stream.forEach(blockEntityTag -> {
					int ix = blockEntityTag.get("x").asInt(0) - this.x & 15;
					int jx = blockEntityTag.get("y").asInt(0);
					int k = blockEntityTag.get("z").asInt(0) - this.z & 15;
					int l = jx << 8 | k << 4 | ix;
					if (this.blockEntities.put(l, blockEntityTag) != null) {
						ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.x, this.z, ix, jx, k);
					}
				}));
			boolean bl = chunkTag.get("convertedFromAlphaFormat").asBoolean(false);
			chunkTag.get("Sections").asStreamOpt().ifSuccess(stream -> stream.forEach(sectionTag -> {
					ChunkPalettedStorageFix.Section sectionx = new ChunkPalettedStorageFix.Section(sectionTag);
					this.sidesToUpgrade = sectionx.visit(this.sidesToUpgrade);
					this.sections[sectionx.y] = sectionx;
				}));

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					for (java.util.Map.Entry<Integer, IntList> entry : section.inPlaceUpdates.entrySet()) {
						int i = section.y << 12;
						switch (entry.getKey()) {
							case 2:
								for (int j : (IntList)entry.getValue()) {
									j |= i;
									Dynamic<?> dynamic = this.getBlock(j);
									if ("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(j, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(j, ChunkPalettedStorageFix.SNOWY_GRASS);
										}
									}
								}
								break;
							case 3:
								for (int jxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxxx);
									if ("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxxxx, ChunkPalettedStorageFix.PODZOL);
										}
									}
								}
								break;
							case 25:
								for (int jxxxxx : (IntList)entry.getValue()) {
									jxxxxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxxxxx);
									if (dynamic != null) {
										String string = Boolean.toString(dynamic.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic.get("note").asInt(0), 0), 24);
										this.setBlock(jxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.NOTE_BLOCK.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.NOTE_BLOCK.get("false0")));
									}
								}
								break;
							case 26:
								for (int jxxxx : (IntList)entry.getValue()) {
									jxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxx);
									if (dynamic != null) {
										int k = dynamic.get("color").asInt(0);
										if (k != 14 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "occupied")
												+ ChunkPalettedStorageFix.getProperty(dynamic2, "part")
												+ k;
											if (ChunkPalettedStorageFix.BED.containsKey(string2)) {
												this.setBlock(jxxxx, (Dynamic<?>)ChunkPalettedStorageFix.BED.get(string2));
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
									Dynamic<?> dynamic = this.getBlock(jxxx);
									if (ChunkPalettedStorageFix.getName(dynamic).endsWith("_door")) {
										Dynamic<?> dynamic2 = this.getBlock(jxxx);
										if ("lower".equals(ChunkPalettedStorageFix.getProperty(dynamic2, "half"))) {
											int k = adjacentTo(jxxx, ChunkPalettedStorageFix.Facing.UP);
											Dynamic<?> dynamic3 = this.getBlock(k);
											String string4 = ChunkPalettedStorageFix.getName(dynamic2);
											if (string4.equals(ChunkPalettedStorageFix.getName(dynamic3))) {
												String string5 = ChunkPalettedStorageFix.getProperty(dynamic2, "facing");
												String string6 = ChunkPalettedStorageFix.getProperty(dynamic2, "open");
												String string7 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic3, "hinge");
												String string8 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic3, "powered");
												this.setBlock(jxxx, (Dynamic<?>)ChunkPalettedStorageFix.DOOR.get(string4 + string5 + "lower" + string7 + string6 + string8));
												this.setBlock(k, (Dynamic<?>)ChunkPalettedStorageFix.DOOR.get(string4 + string5 + "upper" + string7 + string6 + string8));
											}
										}
									}
								}
								break;
							case 86:
								for (int jxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxxx);
									if ("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxxx, ChunkPalettedStorageFix.Facing.DOWN)));
										if ("minecraft:grass_block".equals(string) || "minecraft:dirt".equals(string)) {
											this.setBlock(jxxxxxxxx, ChunkPalettedStorageFix.PUMPKIN);
										}
									}
								}
								break;
							case 110:
								for (int jxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlock(jxxxxxxx);
									if ("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic))) {
										String string = ChunkPalettedStorageFix.getName(this.getBlock(adjacentTo(jxxxxxxx, ChunkPalettedStorageFix.Facing.UP)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.setBlock(jxxxxxxx, ChunkPalettedStorageFix.SNOWY_MYCELIUM);
										}
									}
								}
								break;
							case 140:
								for (int jxx : (IntList)entry.getValue()) {
									jxx |= i;
									Dynamic<?> dynamic = this.removeBlockEntity(jxx);
									if (dynamic != null) {
										String string = dynamic.get("Item").asString("") + dynamic.get("Data").asInt(0);
										this.setBlock(
											jxx, (Dynamic<?>)ChunkPalettedStorageFix.FLOWER_POT.getOrDefault(string, (Dynamic)ChunkPalettedStorageFix.FLOWER_POT.get("minecraft:air0"))
										);
									}
								}
								break;
							case 144:
								for (int jxxxxxx : (IntList)entry.getValue()) {
									jxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxx);
									if (dynamic != null) {
										String string = String.valueOf(dynamic.get("SkullType").asInt(0));
										String string3 = ChunkPalettedStorageFix.getProperty(this.getBlock(jxxxxxx), "facing");
										String string2;
										if (!"up".equals(string3) && !"down".equals(string3)) {
											string2 = string + string3;
										} else {
											string2 = string + dynamic.get("Rot").asInt(0);
										}

										dynamic.remove("SkullType");
										dynamic.remove("facing");
										dynamic.remove("Rot");
										this.setBlock(jxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.SKULL.getOrDefault(string2, (Dynamic)ChunkPalettedStorageFix.SKULL.get("0north")));
									}
								}
								break;
							case 175:
								for (int jx : (IntList)entry.getValue()) {
									jx |= i;
									Dynamic<?> dynamic = this.getBlock(jx);
									if ("upper".equals(ChunkPalettedStorageFix.getProperty(dynamic, "half"))) {
										Dynamic<?> dynamic2 = this.getBlock(adjacentTo(jx, ChunkPalettedStorageFix.Facing.DOWN));
										String string3 = ChunkPalettedStorageFix.getName(dynamic2);
										if ("minecraft:sunflower".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.SUNFLOWER_UPPER);
										} else if ("minecraft:lilac".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.LILAC_UPPER);
										} else if ("minecraft:tall_grass".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.GRASS_UPPER);
										} else if ("minecraft:large_fern".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.FERN_UPPER);
										} else if ("minecraft:rose_bush".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.ROSE_UPPER);
										} else if ("minecraft:peony".equals(string3)) {
											this.setBlock(jx, ChunkPalettedStorageFix.PEONY_UPPER);
										}
									}
								}
								break;
							case 176:
							case 177:
								for (int jxxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxxx |= i;
									Dynamic<?> dynamic = this.getBlockEntity(jxxxxxxxxxx);
									Dynamic<?> dynamic2 = this.getBlock(jxxxxxxxxxx);
									if (dynamic != null) {
										int k = dynamic.get("Base").asInt(0);
										if (k != 15 && k >= 0 && k < 16) {
											String string2 = ChunkPalettedStorageFix.getProperty(dynamic2, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
											if (ChunkPalettedStorageFix.BANNER.containsKey(string2)) {
												this.setBlock(jxxxxxxxxxx, (Dynamic<?>)ChunkPalettedStorageFix.BANNER.get(string2));
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
		private Dynamic<?> getBlockEntity(int packedLocalPos) {
			return this.blockEntities.get(packedLocalPos);
		}

		@Nullable
		private Dynamic<?> removeBlockEntity(int packedLocalPos) {
			return this.blockEntities.remove(packedLocalPos);
		}

		public static int adjacentTo(int packedLocalPos, ChunkPalettedStorageFix.Facing direction) {
			switch (direction.getAxis()) {
				case X:
					int i = (packedLocalPos & 15) + direction.getDirection().getOffset();
					return i >= 0 && i <= 15 ? packedLocalPos & -16 | i : -1;
				case Y:
					int j = (packedLocalPos >> 8) + direction.getDirection().getOffset();
					return j >= 0 && j <= 255 ? packedLocalPos & 0xFF | j << 8 : -1;
				case Z:
					int k = (packedLocalPos >> 4 & 15) + direction.getDirection().getOffset();
					return k >= 0 && k <= 15 ? packedLocalPos & -241 | k << 4 : -1;
				default:
					return -1;
			}
		}

		private void setBlock(int packedLocalPos, Dynamic<?> dynamic) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				if (section != null) {
					section.setBlock(packedLocalPos & 4095, dynamic);
				}
			}
		}

		@Nullable
		private ChunkPalettedStorageFix.Section getSection(int packedLocalPos) {
			int i = packedLocalPos >> 12;
			return i < this.sections.length ? this.sections[i] : null;
		}

		public Dynamic<?> getBlock(int packedLocalPos) {
			if (packedLocalPos >= 0 && packedLocalPos <= 65535) {
				ChunkPalettedStorageFix.Section section = this.getSection(packedLocalPos);
				return section == null ? ChunkPalettedStorageFix.AIR : section.getBlock(packedLocalPos & 4095);
			} else {
				return ChunkPalettedStorageFix.AIR;
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
			List<Dynamic<?>> list = Lists.<Dynamic<?>>newArrayList();

			for (ChunkPalettedStorageFix.Section section : this.sections) {
				if (section != null) {
					list.add(section.transform());
					dynamic2 = dynamic2.set(String.valueOf(section.y), dynamic2.createIntList(Arrays.stream(section.innerPositions.toIntArray())));
				}
			}

			Dynamic<?> dynamic3 = dynamic.emptyMap();
			dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
			dynamic3 = dynamic3.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
		}
	}

	static class Section {
		private final Int2ObjectBiMap<Dynamic<?>> paletteMap = Int2ObjectBiMap.create(32);
		private final List<Dynamic<?>> paletteData;
		private final Dynamic<?> section;
		private final boolean hasBlocks;
		final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap<>();
		final IntList innerPositions = new IntArrayList();
		public final int y;
		private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
		private final int[] states = new int[4096];

		public Section(Dynamic<?> section) {
			this.paletteData = Lists.<Dynamic<?>>newArrayList();
			this.section = section;
			this.y = section.get("Y").asInt(0);
			this.hasBlocks = section.get("Blocks").result().isPresent();
		}

		public Dynamic<?> getBlock(int index) {
			if (index >= 0 && index <= 4095) {
				Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
				return dynamic == null ? ChunkPalettedStorageFix.AIR : dynamic;
			} else {
				return ChunkPalettedStorageFix.AIR;
			}
		}

		public void setBlock(int pos, Dynamic<?> dynamic) {
			if (this.seenStates.add(dynamic)) {
				this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.AIR : dynamic);
			}

			this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
		}

		public int visit(int sidesToUpgrade) {
			if (!this.hasBlocks) {
				return sidesToUpgrade;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = (ChunkPalettedStorageFix.ChunkNibbleArray)this.section
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray(byteBufferx)))
					.result()
					.orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
				this.seenStates.add(ChunkPalettedStorageFix.AIR);
				ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.AIR);
				this.paletteData.add(ChunkPalettedStorageFix.AIR);

				for (int i = 0; i < 4096; i++) {
					int j = i & 15;
					int k = i >> 8 & 15;
					int l = i >> 4 & 15;
					int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer.get(i) & 255) << 4 | chunkNibbleArray.get(j, k, l);
					if (ChunkPalettedStorageFix.BLOCKS_NEEDING_IN_PLACE_UPDATE.get(m >> 4)) {
						this.addInPlaceUpdate(m >> 4, i);
					}

					if (ChunkPalettedStorageFix.BLOCKS_NEEDING_SIDE_UPDATE.get(m >> 4)) {
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
		}

		private void addInPlaceUpdate(int section, int index) {
			IntList intList = this.inPlaceUpdates.get(section);
			if (intList == null) {
				intList = new IntArrayList();
				this.inPlaceUpdates.put(section, intList);
			}

			intList.add(index);
		}

		public Dynamic<?> transform() {
			Dynamic<?> dynamic = this.section;
			if (!this.hasBlocks) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
				int i = Math.max(4, DataFixUtils.ceillog2(this.seenStates.size()));
				WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);

				for (int j = 0; j < this.states.length; j++) {
					wordPackedArray.set(j, this.states[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}
}
