package net.minecraft;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3582 extends DataFix {
	private static final Logger field_15844 = LogManager.getLogger();
	private static final BitSet field_15842 = new BitSet(256);
	private static final BitSet field_15854 = new BitSet(256);
	private static final Dynamic<?> field_15840 = class_3580.method_15598("{Name:'minecraft:pumpkin'}");
	private static final Dynamic<?> field_15852 = class_3580.method_15598("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_15838 = class_3580.method_15598("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_15850 = class_3580.method_15598("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
	private static final Dynamic<?> field_15839 = class_3580.method_15598("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_15848 = class_3580.method_15598("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_15834 = class_3580.method_15598("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_15845 = class_3580.method_15598("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_15835 = class_3580.method_15598("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
	private static final Dynamic<?> field_15847 = class_3580.method_15598("{Name:'minecraft:peony',Properties:{half:'upper'}}");
	private static final Map<String, Dynamic<?>> field_15841 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		hashMap.put("minecraft:air0", class_3580.method_15598("{Name:'minecraft:flower_pot'}"));
		hashMap.put("minecraft:red_flower0", class_3580.method_15598("{Name:'minecraft:potted_poppy'}"));
		hashMap.put("minecraft:red_flower1", class_3580.method_15598("{Name:'minecraft:potted_blue_orchid'}"));
		hashMap.put("minecraft:red_flower2", class_3580.method_15598("{Name:'minecraft:potted_allium'}"));
		hashMap.put("minecraft:red_flower3", class_3580.method_15598("{Name:'minecraft:potted_azure_bluet'}"));
		hashMap.put("minecraft:red_flower4", class_3580.method_15598("{Name:'minecraft:potted_red_tulip'}"));
		hashMap.put("minecraft:red_flower5", class_3580.method_15598("{Name:'minecraft:potted_orange_tulip'}"));
		hashMap.put("minecraft:red_flower6", class_3580.method_15598("{Name:'minecraft:potted_white_tulip'}"));
		hashMap.put("minecraft:red_flower7", class_3580.method_15598("{Name:'minecraft:potted_pink_tulip'}"));
		hashMap.put("minecraft:red_flower8", class_3580.method_15598("{Name:'minecraft:potted_oxeye_daisy'}"));
		hashMap.put("minecraft:yellow_flower0", class_3580.method_15598("{Name:'minecraft:potted_dandelion'}"));
		hashMap.put("minecraft:sapling0", class_3580.method_15598("{Name:'minecraft:potted_oak_sapling'}"));
		hashMap.put("minecraft:sapling1", class_3580.method_15598("{Name:'minecraft:potted_spruce_sapling'}"));
		hashMap.put("minecraft:sapling2", class_3580.method_15598("{Name:'minecraft:potted_birch_sapling'}"));
		hashMap.put("minecraft:sapling3", class_3580.method_15598("{Name:'minecraft:potted_jungle_sapling'}"));
		hashMap.put("minecraft:sapling4", class_3580.method_15598("{Name:'minecraft:potted_acacia_sapling'}"));
		hashMap.put("minecraft:sapling5", class_3580.method_15598("{Name:'minecraft:potted_dark_oak_sapling'}"));
		hashMap.put("minecraft:red_mushroom0", class_3580.method_15598("{Name:'minecraft:potted_red_mushroom'}"));
		hashMap.put("minecraft:brown_mushroom0", class_3580.method_15598("{Name:'minecraft:potted_brown_mushroom'}"));
		hashMap.put("minecraft:deadbush0", class_3580.method_15598("{Name:'minecraft:potted_dead_bush'}"));
		hashMap.put("minecraft:tallgrass2", class_3580.method_15598("{Name:'minecraft:potted_fern'}"));
		hashMap.put("minecraft:cactus0", class_3580.method_15594(2240));
	});
	private static final Map<String, Dynamic<?>> field_15853 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		method_15610(hashMap, 0, "skeleton", "skull");
		method_15610(hashMap, 1, "wither_skeleton", "skull");
		method_15610(hashMap, 2, "zombie", "head");
		method_15610(hashMap, 3, "player", "head");
		method_15610(hashMap, 4, "creeper", "head");
		method_15610(hashMap, 5, "dragon", "head");
	});
	private static final Map<String, Dynamic<?>> field_15836 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		method_15611(hashMap, "oak_door", 1024);
		method_15611(hashMap, "iron_door", 1136);
		method_15611(hashMap, "spruce_door", 3088);
		method_15611(hashMap, "birch_door", 3104);
		method_15611(hashMap, "jungle_door", 3120);
		method_15611(hashMap, "acacia_door", 3136);
		method_15611(hashMap, "dark_oak_door", 3152);
	});
	private static final Map<String, Dynamic<?>> field_15846 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (int i = 0; i < 26; i++) {
			hashMap.put("true" + i, class_3580.method_15598("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
			hashMap.put("false" + i, class_3580.method_15598("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
		}
	});
	private static final Int2ObjectMap<String> field_15837 = DataFixUtils.make(new Int2ObjectOpenHashMap<>(), int2ObjectOpenHashMap -> {
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
	private static final Map<String, Dynamic<?>> field_15849 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (Entry<String> entry : field_15837.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "red")) {
				method_15636(hashMap, entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Map<String, Dynamic<?>> field_15851 = DataFixUtils.make(Maps.<String, Dynamic<?>>newHashMap(), hashMap -> {
		for (Entry<String> entry : field_15837.int2ObjectEntrySet()) {
			if (!Objects.equals(entry.getValue(), "white")) {
				method_15605(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
			}
		}
	});
	private static final Dynamic<?> field_15843 = class_3580.method_15594(0);

	public class_3582(Schema schema, boolean bl) {
		super(schema, bl);
	}

	private static void method_15610(Map<String, Dynamic<?>> map, int i, String string, String string2) {
		map.put(i + "north", class_3580.method_15598("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'north'}}"));
		map.put(i + "east", class_3580.method_15598("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'east'}}"));
		map.put(i + "south", class_3580.method_15598("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'south'}}"));
		map.put(i + "west", class_3580.method_15598("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'west'}}"));

		for (int j = 0; j < 16; j++) {
			map.put(i + "" + j, class_3580.method_15598("{Name:'minecraft:" + string + "_" + string2 + "',Properties:{rotation:'" + j + "'}}"));
		}
	}

	private static void method_15611(Map<String, Dynamic<?>> map, String string, int i) {
		map.put(
			"minecraft:" + string + "eastlowerleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastlowerlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrightfalsefalse", class_3580.method_15594(i));
		map.put(
			"minecraft:" + string + "eastlowerrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastlowerrighttruefalse", class_3580.method_15594(i + 4));
		map.put(
			"minecraft:" + string + "eastlowerrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperleftfalsefalse", class_3580.method_15594(i + 8));
		map.put("minecraft:" + string + "eastupperleftfalsetrue", class_3580.method_15594(i + 10));
		map.put(
			"minecraft:" + string + "eastupperlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "eastupperrightfalsefalse", class_3580.method_15594(i + 9));
		map.put("minecraft:" + string + "eastupperrightfalsetrue", class_3580.method_15594(i + 11));
		map.put(
			"minecraft:" + string + "eastupperrighttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "eastupperrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northlowerlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrightfalsefalse", class_3580.method_15594(i + 3));
		map.put(
			"minecraft:" + string + "northlowerrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "northlowerrighttruefalse", class_3580.method_15594(i + 7));
		map.put(
			"minecraft:" + string + "northlowerrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "northupperrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southlowerlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrightfalsefalse", class_3580.method_15594(i + 1));
		map.put(
			"minecraft:" + string + "southlowerrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "southlowerrighttruefalse", class_3580.method_15594(i + 5));
		map.put(
			"minecraft:" + string + "southlowerrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "southupperrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westlowerlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrightfalsefalse", class_3580.method_15594(i + 2));
		map.put(
			"minecraft:" + string + "westlowerrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put("minecraft:" + string + "westlowerrighttruefalse", class_3580.method_15594(i + 6));
		map.put(
			"minecraft:" + string + "westlowerrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperleftfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperlefttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrightfalsetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruefalse",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}")
		);
		map.put(
			"minecraft:" + string + "westupperrighttruetrue",
			class_3580.method_15598("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}")
		);
	}

	private static void method_15636(Map<String, Dynamic<?>> map, int i, String string) {
		map.put("southfalsefoot" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
		map.put("westfalsefoot" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
		map.put("northfalsefoot" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
		map.put("eastfalsefoot" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
		map.put("southfalsehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
		map.put("westfalsehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
		map.put("northfalsehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
		map.put("eastfalsehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
		map.put("southtruehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
		map.put("westtruehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
		map.put("northtruehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
		map.put("easttruehead" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
	}

	private static void method_15605(Map<String, Dynamic<?>> map, int i, String string) {
		for (int j = 0; j < 16; j++) {
			map.put("" + j + "_" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
		}

		map.put("north_" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
		map.put("south_" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
		map.put("west_" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
		map.put("east_" + i, class_3580.method_15598("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
	}

	public static String method_15637(Dynamic<?> dynamic) {
		return dynamic.get("Name").asString("");
	}

	public static String method_15638(Dynamic<?> dynamic, String string) {
		return dynamic.get("Properties").get(string).asString("");
	}

	public static int method_15616(class_3513<Dynamic<?>> arg, Dynamic<?> dynamic) {
		int i = arg.method_15231(dynamic);
		if (i == -1) {
			i = arg.method_15225(dynamic);
		}

		return i;
	}

	private Dynamic<?> method_15633(Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = dynamic.get("Level").get();
		return optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().isPresent()
			? dynamic.set("Level", new class_3582.class_3588((Dynamic<?>)optional.get()).method_15664())
			: dynamic;
	}

	@Override
	public TypeRewriteRule makeRule() {
		Type<?> type = this.getInputSchema().getType(class_1208.field_5726);
		Type<?> type2 = this.getOutputSchema().getType(class_1208.field_5726);
		return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::method_15633);
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

	static class class_3583 {
		private final byte[] field_15855;

		public class_3583() {
			this.field_15855 = new byte[2048];
		}

		public class_3583(byte[] bs) {
			this.field_15855 = bs;
			if (bs.length != 2048) {
				throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
			}
		}

		public int method_15640(int i, int j, int k) {
			int l = this.method_15642(j << 8 | k << 4 | i);
			return this.method_15641(j << 8 | k << 4 | i) ? this.field_15855[l] & 15 : this.field_15855[l] >> 4 & 15;
		}

		private boolean method_15641(int i) {
			return (i & 1) == 0;
		}

		private int method_15642(int i) {
			return i >> 1;
		}
	}

	public static enum class_3584 {
		field_15858(class_3582.class_3584.class_3586.field_15870, class_3582.class_3584.class_3585.field_15866),
		field_15863(class_3582.class_3584.class_3586.field_15873, class_3582.class_3584.class_3585.field_15866),
		field_15859(class_3582.class_3584.class_3586.field_15870, class_3582.class_3584.class_3585.field_15867),
		field_15862(class_3582.class_3584.class_3586.field_15873, class_3582.class_3584.class_3585.field_15867),
		field_15857(class_3582.class_3584.class_3586.field_15870, class_3582.class_3584.class_3585.field_15869),
		field_15860(class_3582.class_3584.class_3586.field_15873, class_3582.class_3584.class_3585.field_15869);

		private final class_3582.class_3584.class_3585 field_15864;
		private final class_3582.class_3584.class_3586 field_15861;

		private class_3584(class_3582.class_3584.class_3586 arg, class_3582.class_3584.class_3585 arg2) {
			this.field_15864 = arg2;
			this.field_15861 = arg;
		}

		public class_3582.class_3584.class_3586 method_15643() {
			return this.field_15861;
		}

		public class_3582.class_3584.class_3585 method_15644() {
			return this.field_15864;
		}

		public static enum class_3585 {
			field_15869,
			field_15866,
			field_15867;
		}

		public static enum class_3586 {
			field_15873(1),
			field_15870(-1);

			private final int field_15872;

			private class_3586(int j) {
				this.field_15872 = j;
			}

			public int method_15645() {
				return this.field_15872;
			}
		}
	}

	static class class_3587 {
		private final class_3513<Dynamic<?>> field_15882 = new class_3513<>(32);
		private Dynamic<?> field_15880;
		private final Dynamic<?> field_15878;
		private final boolean field_15876;
		private final Int2ObjectMap<IntList> field_15881 = new Int2ObjectLinkedOpenHashMap<>();
		private final IntList field_15874 = new IntArrayList();
		public final int field_15879;
		private final Set<Dynamic<?>> field_15875 = Sets.newIdentityHashSet();
		private final int[] field_15877 = new int[4096];

		public class_3587(Dynamic<?> dynamic) {
			this.field_15880 = dynamic.emptyList();
			this.field_15878 = dynamic;
			this.field_15879 = dynamic.get("Y").asInt(0);
			this.field_15876 = dynamic.get("Blocks").get().isPresent();
		}

		public Dynamic<?> method_15649(int i) {
			if (i >= 0 && i <= 4095) {
				Dynamic<?> dynamic = this.field_15882.method_10200(this.field_15877[i]);
				return dynamic == null ? class_3582.field_15843 : dynamic;
			} else {
				return class_3582.field_15843;
			}
		}

		public void method_15647(int i, Dynamic<?> dynamic) {
			if (this.field_15875.add(dynamic)) {
				this.field_15880 = this.field_15880.merge("%%FILTER_ME%%".equals(class_3582.method_15637(dynamic)) ? class_3582.field_15843 : dynamic);
			}

			this.field_15877[i] = class_3582.method_15616(this.field_15882, dynamic);
		}

		public int method_15652(int i) {
			if (!this.field_15876) {
				return i;
			} else {
				ByteBuffer byteBuffer = (ByteBuffer)this.field_15878.get("Blocks").asByteBufferOpt().get();
				class_3582.class_3583 lv = (class_3582.class_3583)this.field_15878
					.get("Data")
					.asByteBufferOpt()
					.map(byteBufferx -> new class_3582.class_3583(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(class_3582.class_3583::new);
				class_3582.class_3583 lv2 = (class_3582.class_3583)this.field_15878
					.get("Add")
					.asByteBufferOpt()
					.map(byteBufferx -> new class_3582.class_3583(DataFixUtils.toArray(byteBufferx)))
					.orElseGet(class_3582.class_3583::new);
				this.field_15875.add(class_3582.field_15843);
				class_3582.method_15616(this.field_15882, class_3582.field_15843);
				this.field_15880 = this.field_15880.merge(class_3582.field_15843);

				for (int j = 0; j < 4096; j++) {
					int k = j & 15;
					int l = j >> 8 & 15;
					int m = j >> 4 & 15;
					int n = lv2.method_15640(k, l, m) << 12 | (byteBuffer.get(j) & 255) << 4 | lv.method_15640(k, l, m);
					if (class_3582.field_15854.get(n >> 4)) {
						this.method_15650(n >> 4, j);
					}

					if (class_3582.field_15842.get(n >> 4)) {
						int o = class_3582.method_15615(k == 0, k == 15, m == 0, m == 15);
						if (o == 0) {
							this.field_15874.add(j);
						} else {
							i |= o;
						}
					}

					this.method_15647(j, class_3580.method_15594(n));
				}

				return i;
			}
		}

		private void method_15650(int i, int j) {
			IntList intList = this.field_15881.get(i);
			if (intList == null) {
				intList = new IntArrayList();
				this.field_15881.put(i, intList);
			}

			intList.add(j);
		}

		public Dynamic<?> method_15651() {
			Dynamic<?> dynamic = this.field_15878;
			if (!this.field_15876) {
				return dynamic;
			} else {
				dynamic = dynamic.set("Palette", this.field_15880);
				int i = Math.max(4, DataFixUtils.ceillog2(this.field_15875.size()));
				class_3508 lv = new class_3508(i, 4096);

				for (int j = 0; j < this.field_15877.length; j++) {
					lv.method_15210(j, this.field_15877[j]);
				}

				dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(lv.method_15212())));
				dynamic = dynamic.remove("Blocks");
				dynamic = dynamic.remove("Data");
				return dynamic.remove("Add");
			}
		}
	}

	static final class class_3588 {
		private int field_15885;
		private final class_3582.class_3587[] field_15888 = new class_3582.class_3587[16];
		private final Dynamic<?> field_15886;
		private final int field_15884;
		private final int field_15883;
		private final Int2ObjectMap<Dynamic<?>> field_15887 = new Int2ObjectLinkedOpenHashMap<>(16);

		public class_3588(Dynamic<?> dynamic) {
			this.field_15886 = dynamic;
			this.field_15884 = dynamic.get("xPos").asInt(0) << 4;
			this.field_15883 = dynamic.get("zPos").asInt(0) << 4;
			dynamic.get("TileEntities").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
					int ix = dynamicx.get("x").asInt(0) - this.field_15884 & 15;
					int jx = dynamicx.get("y").asInt(0);
					int k = dynamicx.get("z").asInt(0) - this.field_15883 & 15;
					int l = jx << 8 | k << 4 | ix;
					if (this.field_15887.put(l, dynamicx) != null) {
						class_3582.field_15844.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", this.field_15884, this.field_15883, ix, jx, k);
					}
				}));
			boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
			dynamic.get("Sections").asStreamOpt().ifPresent(stream -> stream.forEach(dynamicx -> {
					class_3582.class_3587 lvx = new class_3582.class_3587(dynamicx);
					this.field_15885 = lvx.method_15652(this.field_15885);
					this.field_15888[lvx.field_15879] = lvx;
				}));

			for (class_3582.class_3587 lv : this.field_15888) {
				if (lv != null) {
					for (java.util.Map.Entry<Integer, IntList> entry : lv.field_15881.entrySet()) {
						int i = lv.field_15879 << 12;
						switch (entry.getKey()) {
							case 2:
								for (int j : (IntList)entry.getValue()) {
									j |= i;
									Dynamic<?> dynamic2 = this.method_15662(j);
									if ("minecraft:grass_block".equals(class_3582.method_15637(dynamic2))) {
										String string = class_3582.method_15637(this.method_15662(method_15663(j, class_3582.class_3584.field_15863)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.method_15657(j, class_3582.field_15838);
										}
									}
								}
								break;
							case 3:
								for (int jxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15662(jxxxxxxxxx);
									if ("minecraft:podzol".equals(class_3582.method_15637(dynamic2))) {
										String string = class_3582.method_15637(this.method_15662(method_15663(jxxxxxxxxx, class_3582.class_3584.field_15863)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.method_15657(jxxxxxxxxx, class_3582.field_15852);
										}
									}
								}
								break;
							case 25:
								for (int jxxxxx : (IntList)entry.getValue()) {
									jxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15660(jxxxxx);
									if (dynamic2 != null) {
										String string = Boolean.toString(dynamic2.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic2.get("note").asInt(0), 0), 24);
										this.method_15657(jxxxxx, (Dynamic<?>)class_3582.field_15846.getOrDefault(string, class_3582.field_15846.get("false0")));
									}
								}
								break;
							case 26:
								for (int jxxxx : (IntList)entry.getValue()) {
									jxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15655(jxxxx);
									Dynamic<?> dynamic3 = this.method_15662(jxxxx);
									if (dynamic2 != null) {
										int k = dynamic2.get("color").asInt(0);
										if (k != 14 && k >= 0 && k < 16) {
											String string2 = class_3582.method_15638(dynamic3, "facing")
												+ class_3582.method_15638(dynamic3, "occupied")
												+ class_3582.method_15638(dynamic3, "part")
												+ k;
											if (class_3582.field_15849.containsKey(string2)) {
												this.method_15657(jxxxx, (Dynamic<?>)class_3582.field_15849.get(string2));
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
									Dynamic<?> dynamic2 = this.method_15662(jxxx);
									if (class_3582.method_15637(dynamic2).endsWith("_door")) {
										Dynamic<?> dynamic3 = this.method_15662(jxxx);
										if ("lower".equals(class_3582.method_15638(dynamic3, "half"))) {
											int k = method_15663(jxxx, class_3582.class_3584.field_15863);
											Dynamic<?> dynamic4 = this.method_15662(k);
											String string4 = class_3582.method_15637(dynamic3);
											if (string4.equals(class_3582.method_15637(dynamic4))) {
												String string5 = class_3582.method_15638(dynamic3, "facing");
												String string6 = class_3582.method_15638(dynamic3, "open");
												String string7 = bl ? "left" : class_3582.method_15638(dynamic4, "hinge");
												String string8 = bl ? "false" : class_3582.method_15638(dynamic4, "powered");
												this.method_15657(jxxx, (Dynamic<?>)class_3582.field_15836.get(string4 + string5 + "lower" + string7 + string6 + string8));
												this.method_15657(k, (Dynamic<?>)class_3582.field_15836.get(string4 + string5 + "upper" + string7 + string6 + string8));
											}
										}
									}
								}
								break;
							case 86:
								for (int jxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15662(jxxxxxxxx);
									if ("minecraft:carved_pumpkin".equals(class_3582.method_15637(dynamic2))) {
										String string = class_3582.method_15637(this.method_15662(method_15663(jxxxxxxxx, class_3582.class_3584.field_15858)));
										if ("minecraft:grass_block".equals(string) || "minecraft:dirt".equals(string)) {
											this.method_15657(jxxxxxxxx, class_3582.field_15840);
										}
									}
								}
								break;
							case 110:
								for (int jxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15662(jxxxxxxx);
									if ("minecraft:mycelium".equals(class_3582.method_15637(dynamic2))) {
										String string = class_3582.method_15637(this.method_15662(method_15663(jxxxxxxx, class_3582.class_3584.field_15863)));
										if ("minecraft:snow".equals(string) || "minecraft:snow_layer".equals(string)) {
											this.method_15657(jxxxxxxx, class_3582.field_15850);
										}
									}
								}
								break;
							case 140:
								for (int jxx : (IntList)entry.getValue()) {
									jxx |= i;
									Dynamic<?> dynamic2 = this.method_15660(jxx);
									if (dynamic2 != null) {
										String string = dynamic2.get("Item").asString("") + dynamic2.get("Data").asInt(0);
										this.method_15657(jxx, (Dynamic<?>)class_3582.field_15841.getOrDefault(string, class_3582.field_15841.get("minecraft:air0")));
									}
								}
								break;
							case 144:
								for (int jxxxxxx : (IntList)entry.getValue()) {
									jxxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15655(jxxxxxx);
									if (dynamic2 != null) {
										String string = String.valueOf(dynamic2.get("SkullType").asInt(0));
										String string3 = class_3582.method_15638(this.method_15662(jxxxxxx), "facing");
										String string2;
										if (!"up".equals(string3) && !"down".equals(string3)) {
											string2 = string + string3;
										} else {
											string2 = string + String.valueOf(dynamic2.get("Rot").asInt(0));
										}

										dynamic2.remove("SkullType");
										dynamic2.remove("facing");
										dynamic2.remove("Rot");
										this.method_15657(jxxxxxx, (Dynamic<?>)class_3582.field_15853.getOrDefault(string2, class_3582.field_15853.get("0north")));
									}
								}
								break;
							case 175:
								for (int jx : (IntList)entry.getValue()) {
									jx |= i;
									Dynamic<?> dynamic2 = this.method_15662(jx);
									if ("upper".equals(class_3582.method_15638(dynamic2, "half"))) {
										Dynamic<?> dynamic3 = this.method_15662(method_15663(jx, class_3582.class_3584.field_15858));
										String string3 = class_3582.method_15637(dynamic3);
										if ("minecraft:sunflower".equals(string3)) {
											this.method_15657(jx, class_3582.field_15839);
										} else if ("minecraft:lilac".equals(string3)) {
											this.method_15657(jx, class_3582.field_15848);
										} else if ("minecraft:tall_grass".equals(string3)) {
											this.method_15657(jx, class_3582.field_15834);
										} else if ("minecraft:large_fern".equals(string3)) {
											this.method_15657(jx, class_3582.field_15845);
										} else if ("minecraft:rose_bush".equals(string3)) {
											this.method_15657(jx, class_3582.field_15835);
										} else if ("minecraft:peony".equals(string3)) {
											this.method_15657(jx, class_3582.field_15847);
										}
									}
								}
								break;
							case 176:
							case 177:
								for (int jxxxxxxxxxx : (IntList)entry.getValue()) {
									jxxxxxxxxxx |= i;
									Dynamic<?> dynamic2 = this.method_15655(jxxxxxxxxxx);
									Dynamic<?> dynamic3 = this.method_15662(jxxxxxxxxxx);
									if (dynamic2 != null) {
										int k = dynamic2.get("Base").asInt(0);
										if (k != 15 && k >= 0 && k < 16) {
											String string2 = class_3582.method_15638(dynamic3, entry.getKey() == 176 ? "rotation" : "facing") + "_" + k;
											if (class_3582.field_15851.containsKey(string2)) {
												this.method_15657(jxxxxxxxxxx, (Dynamic<?>)class_3582.field_15851.get(string2));
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
		private Dynamic<?> method_15655(int i) {
			return this.field_15887.get(i);
		}

		@Nullable
		private Dynamic<?> method_15660(int i) {
			return this.field_15887.remove(i);
		}

		public static int method_15663(int i, class_3582.class_3584 arg) {
			switch (arg.method_15644()) {
				case field_15869:
					int j = (i & 15) + arg.method_15643().method_15645();
					return j >= 0 && j <= 15 ? i & -16 | j : -1;
				case field_15866:
					int k = (i >> 8) + arg.method_15643().method_15645();
					return k >= 0 && k <= 255 ? i & 0xFF | k << 8 : -1;
				case field_15867:
					int l = (i >> 4 & 15) + arg.method_15643().method_15645();
					return l >= 0 && l <= 15 ? i & -241 | l << 4 : -1;
				default:
					return -1;
			}
		}

		private void method_15657(int i, Dynamic<?> dynamic) {
			if (i >= 0 && i <= 65535) {
				class_3582.class_3587 lv = this.method_15658(i);
				if (lv != null) {
					lv.method_15647(i & 4095, dynamic);
				}
			}
		}

		@Nullable
		private class_3582.class_3587 method_15658(int i) {
			int j = i >> 12;
			return j < this.field_15888.length ? this.field_15888[j] : null;
		}

		public Dynamic<?> method_15662(int i) {
			if (i >= 0 && i <= 65535) {
				class_3582.class_3587 lv = this.method_15658(i);
				return lv == null ? class_3582.field_15843 : lv.method_15649(i & 4095);
			} else {
				return class_3582.field_15843;
			}
		}

		public Dynamic<?> method_15664() {
			Dynamic<?> dynamic = this.field_15886;
			if (this.field_15887.isEmpty()) {
				dynamic = dynamic.remove("TileEntities");
			} else {
				dynamic = dynamic.set("TileEntities", dynamic.createList(this.field_15887.values().stream()));
			}

			Dynamic<?> dynamic2 = dynamic.emptyMap();
			Dynamic<?> dynamic3 = dynamic.emptyList();

			for (class_3582.class_3587 lv : this.field_15888) {
				if (lv != null) {
					dynamic3 = dynamic3.merge(lv.method_15651());
					dynamic2 = dynamic2.set(String.valueOf(lv.field_15879), dynamic2.createIntList(Arrays.stream(lv.field_15874.toIntArray())));
				}
			}

			Dynamic<?> dynamic4 = dynamic.emptyMap();
			dynamic4 = dynamic4.set("Sides", dynamic4.createByte((byte)this.field_15885));
			dynamic4 = dynamic4.set("Indices", dynamic2);
			return dynamic.set("UpgradeData", dynamic4).set("Sections", dynamic3);
		}
	}
}
