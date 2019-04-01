package net.minecraft;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

public abstract class class_3031<FC extends class_3037> {
	public static final class_3195<class_3772> field_16655 = method_13150("pillager_outpost", new class_3770(class_3772::method_16589));
	public static final class_3195<class_3101> field_13547 = method_13150("mineshaft", new class_3098(class_3101::method_13536));
	public static final class_3195<class_3111> field_13528 = method_13150("woodland_mansion", new class_3223(class_3111::method_13565));
	public static final class_3195<class_3111> field_13586 = method_13150("jungle_temple", new class_3076(class_3111::method_13565));
	public static final class_3195<class_3111> field_13515 = method_13150("desert_pyramid", new class_3006(class_3111::method_13565));
	public static final class_3195<class_3111> field_13527 = method_13150("igloo", new class_3071(class_3111::method_13565));
	public static final class_3195<class_3172> field_13589 = method_13150("shipwreck", new class_3170(class_3172::method_13928));
	public static final class_3197 field_13520 = method_13150("swamp_hut", new class_3197(class_3111::method_13565));
	public static final class_3195<class_3111> field_13565 = method_13150("stronghold", new class_3188(class_3111::method_13565));
	public static final class_3195<class_3111> field_13588 = method_13150("ocean_monument", new class_3116(class_3111::method_13565));
	public static final class_3195<class_3114> field_13536 = method_13150("ocean_ruin", new class_3411(class_3114::method_13573));
	public static final class_3195<class_3111> field_13569 = method_13150("nether_bridge", new class_3108(class_3111::method_13565));
	public static final class_3195<class_3111> field_13553 = method_13150("end_city", new class_3021(class_3111::method_13565));
	public static final class_3195<class_2959> field_13538 = method_13150("buried_treasure", new class_2956(class_2959::method_12828));
	public static final class_3195<class_3812> field_13587 = method_13150("village", new class_3211(class_3812::method_16752));
	public static final class_3031<class_3111> field_13529 = method_13150("fancy_tree", new class_2948(class_3111::method_13565, false));
	public static final class_3031<class_3111> field_13566 = method_13150("birch_tree", new class_2947(class_3111::method_13565, false, false));
	public static final class_3031<class_3111> field_13578 = method_13150("super_birch_tree", new class_2947(class_3111::method_13565, false, true));
	public static final class_3031<class_3111> field_13537 = method_13150(
		"jungle_ground_bush", new class_3056(class_3111::method_13565, class_2246.field_10306.method_9564(), class_2246.field_10503.method_9564())
	);
	public static final class_3031<class_3111> field_13508 = method_13150(
		"jungle_tree", new class_3084(class_3111::method_13565, false, 4, class_2246.field_10306.method_9564(), class_2246.field_10335.method_9564(), true)
	);
	public static final class_3031<class_3111> field_13581 = method_13150("pine_tree", new class_3129(class_3111::method_13565));
	public static final class_3031<class_3111> field_13532 = method_13150("dark_oak_tree", new class_3159(class_3111::method_13565, false));
	public static final class_3031<class_3111> field_13545 = method_13150("savanna_tree", new class_3157(class_3111::method_13565, false));
	public static final class_3031<class_3111> field_13577 = method_13150("spruce_tree", new class_3190(class_3111::method_13565, false));
	public static final class_3031<class_3111> field_13530 = method_13150("swamp_tree", new class_3200(class_3111::method_13565));
	public static final class_3031<class_3111> field_13510 = method_13150("normal_tree", new class_3207(class_3111::method_13565, false));
	public static final class_3031<class_3111> field_13558 = method_13150(
		"mega_jungle_tree", new class_3092(class_3111::method_13565, false, 10, 20, class_2246.field_10306.method_9564(), class_2246.field_10335.method_9564())
	);
	public static final class_3031<class_3111> field_13543 = method_13150("mega_pine_tree", new class_3090(class_3111::method_13565, false, false));
	public static final class_3031<class_3111> field_13580 = method_13150("mega_spruce_tree", new class_3090(class_3111::method_13565, false, true));
	public static final class_3038 field_13541 = method_13150("default_flower", new class_3001(class_3111::method_13565));
	public static final class_3038 field_13570 = method_13150("forest_flower", new class_3046(class_3111::method_13565));
	public static final class_3038 field_13549 = method_13150("plain_flower", new class_3126(class_3111::method_13565));
	public static final class_3038 field_13533 = method_13150("swamp_flower", new class_3192(class_3111::method_13565));
	public static final class_3038 field_13582 = method_13150("general_forest_flower", new class_3049(class_3111::method_13565));
	public static final class_3031<class_3111> field_13590 = method_13150("jungle_grass", new class_3080(class_3111::method_13565));
	public static final class_3031<class_3111> field_13521 = method_13150("taiga_grass", new class_3206(class_3111::method_13565));
	public static final class_3031<class_3203> field_13511 = method_13150("grass", new class_3209(class_3203::method_14034));
	public static final class_3031<class_3111> field_13591 = method_13150("void_start_platform", new class_3217(class_3111::method_13565));
	public static final class_3031<class_3111> field_13554 = method_13150("cactus", new class_2971(class_3111::method_13565));
	public static final class_3031<class_3111> field_13548 = method_13150("dead_bush", new class_2982(class_3111::method_13565));
	public static final class_3031<class_3111> field_13592 = method_13150("desert_well", new class_3005(class_3111::method_13565));
	public static final class_3031<class_3111> field_13516 = method_13150("fossil", new class_3044(class_3111::method_13565));
	public static final class_3031<class_3111> field_13523 = method_13150("hell_fire", new class_3053(class_3111::method_13565));
	public static final class_3031<class_3111> field_13571 = method_13150("huge_red_mushroom", new class_3066(class_3111::method_13565));
	public static final class_3031<class_3111> field_13531 = method_13150("huge_brown_mushroom", new class_3059(class_3111::method_13565));
	public static final class_3031<class_3111> field_13562 = method_13150("ice_spike", new class_3070(class_3111::method_13565));
	public static final class_3031<class_3111> field_13568 = method_13150("glowstone_blob", new class_3047(class_3111::method_13565));
	public static final class_3031<class_3111> field_13534 = method_13150("melon", new class_3094(class_3111::method_13565));
	public static final class_3031<class_3111> field_13524 = method_13150(
		"pumpkin", new class_3130(class_3111::method_13565, class_2246.field_10261.method_9564())
	);
	public static final class_3031<class_3111> field_13583 = method_13150("reed", new class_3148(class_3111::method_13565));
	public static final class_3031<class_3111> field_13539 = method_13150("freeze_top_layer", new class_3183(class_3111::method_13565));
	public static final class_3031<class_3111> field_13559 = method_13150("vines", new class_3219(class_3111::method_13565));
	public static final class_3031<class_3111> field_13542 = method_13150("waterlily", new class_3220(class_3111::method_13565));
	public static final class_3031<class_3111> field_13579 = method_13150("monster_room", new class_3103(class_3111::method_13565));
	public static final class_3031<class_3111> field_13560 = method_13150("blue_ice", new class_2954(class_3111::method_13565));
	public static final class_3031<class_3067> field_13544 = method_13150("iceberg", new class_3074(class_3067::method_13399));
	public static final class_3031<class_2951> field_13584 = method_13150("forest_rock", new class_2950(class_2951::method_12814));
	public static final class_3031<class_3111> field_16797 = method_13150("hay_pile", new class_3831(class_3111::method_13565));
	public static final class_3031<class_3111> field_17005 = method_13150("snow_pile", new class_3835(class_3111::method_13565));
	public static final class_3031<class_3111> field_17006 = method_13150("ice_pile", new class_3832(class_3111::method_13565));
	public static final class_3031<class_3111> field_17007 = method_13150("melon_pile", new class_3833(class_3111::method_13565));
	public static final class_3031<class_3111> field_17106 = method_13150("pumpkin_pile", new class_3869(class_3111::method_13565));
	public static final class_3031<class_2963> field_13519 = method_13150("bush", new class_2962(class_2963::method_12842));
	public static final class_3031<class_3013> field_13509 = method_13150("disk", new class_3011(class_3013::method_13012));
	public static final class_3031<class_3017> field_13576 = method_13150("double_plant", new class_3015(class_3017::method_13025));
	public static final class_3031<class_3061> field_13563 = method_13150("nether_spring", new class_3105(class_3061::method_13379));
	public static final class_3031<class_3034> field_13551 = method_13150("ice_patch", new class_3063(class_3034::method_13164));
	public static final class_3031<class_3087> field_13573 = method_13150("lake", new class_3085(class_3087::method_13477));
	public static final class_3031<class_3124> field_13517 = method_13150("ore", new class_3122(class_3124::method_13634));
	public static final class_3031<class_3147> field_13512 = method_13150("random_random_selector", new class_3139(class_3147::method_13780));
	public static final class_3031<class_3141> field_13593 = method_13150("random_selector", new class_3150(class_3141::method_13709));
	public static final class_3031<class_3179> field_13555 = method_13150("simple_random_selector", new class_3177(class_3179::method_13957));
	public static final class_3031<class_3137> field_13550 = method_13150("random_boolean_selector", new class_3135(class_3137::method_13687));
	public static final class_3031<class_3154> field_13594 = method_13150("emerald_ore", new class_3152(class_3154::method_13822));
	public static final class_3031<class_3187> field_13513 = method_13150("spring_feature", new class_3185(class_3187::method_13984));
	public static final class_3031<class_3666> field_13522 = method_13150("end_spike", new class_3310(class_3666::method_15881));
	public static final class_3031<class_3111> field_13574 = method_13150("end_island", new class_3026(class_3111::method_13565));
	public static final class_3031<class_3111> field_13552 = method_13150("chorus_plant", new class_2964(class_3111::method_13565));
	public static final class_3031<class_3018> field_13564 = method_13150("end_gateway", new class_3029(class_3018::method_13027));
	public static final class_3031<class_3163> field_13567 = method_13150("seagrass", new class_3168(class_3163::method_13884));
	public static final class_3031<class_3111> field_13535 = method_13150("kelp", new class_3081(class_3111::method_13565));
	public static final class_3031<class_3111> field_13525 = method_13150("coral_tree", new class_2979(class_3111::method_13565));
	public static final class_3031<class_3111> field_13585 = method_13150("coral_mushroom", new class_2977(class_3111::method_13565));
	public static final class_3031<class_3111> field_13546 = method_13150("coral_claw", new class_2972(class_3111::method_13565));
	public static final class_3031<class_2984> field_13575 = method_13150("sea_pickle", new class_3160(class_2984::method_12871));
	public static final class_3031<class_3175> field_13518 = method_13150("simple_block", new class_3173(class_3175::method_13939));
	public static final class_3031<class_3133> field_13540 = method_13150("bamboo", new class_2942(class_3133::method_13674));
	public static final class_3031<class_2986> field_13572 = method_13150("decorated", new class_2988(class_2986::method_12891));
	public static final class_3031<class_2986> field_13561 = method_13150("decorated_flower", new class_2993(class_2986::method_12891));
	public static final class_3031<class_3111> field_17004 = method_13150(
		"sweet_berry_bush", new class_3130(class_3111::method_13565, class_2246.field_16999.method_9564().method_11657(class_3830.field_17000, Integer.valueOf(3)))
	);
	public static final class_2953 field_13526 = method_13150("bonus_chest", new class_2953(class_3111::method_13565));
	public static final class_3031<class_3111> field_19178 = method_13150("barrel", new class_4276(class_3111::method_13565));
	public static final BiMap<String, class_3195<?>> field_13557 = class_156.method_654(HashBiMap.create(), hashBiMap -> {
		hashBiMap.put("Pillager_Outpost".toLowerCase(Locale.ROOT), field_16655);
		hashBiMap.put("Mineshaft".toLowerCase(Locale.ROOT), field_13547);
		hashBiMap.put("Mansion".toLowerCase(Locale.ROOT), field_13528);
		hashBiMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), field_13586);
		hashBiMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), field_13515);
		hashBiMap.put("Igloo".toLowerCase(Locale.ROOT), field_13527);
		hashBiMap.put("Shipwreck".toLowerCase(Locale.ROOT), field_13589);
		hashBiMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), field_13520);
		hashBiMap.put("Stronghold".toLowerCase(Locale.ROOT), field_13565);
		hashBiMap.put("Monument".toLowerCase(Locale.ROOT), field_13588);
		hashBiMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), field_13536);
		hashBiMap.put("Fortress".toLowerCase(Locale.ROOT), field_13569);
		hashBiMap.put("EndCity".toLowerCase(Locale.ROOT), field_13553);
		hashBiMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), field_13538);
		hashBiMap.put("Village".toLowerCase(Locale.ROOT), field_13587);
	});
	public static final List<class_3195<?>> field_16654 = ImmutableList.of(field_16655, field_13587);
	private final Function<Dynamic<?>, ? extends FC> field_13514;
	protected final boolean field_13556;

	private static <C extends class_3037, F extends class_3031<C>> F method_13150(String string, F arg) {
		return class_2378.method_10226(class_2378.field_11138, string, arg);
	}

	public class_3031(Function<Dynamic<?>, ? extends FC> function) {
		this.field_13514 = function;
		this.field_13556 = false;
	}

	public class_3031(Function<Dynamic<?>, ? extends FC> function, boolean bl) {
		this.field_13514 = function;
		this.field_13556 = bl;
	}

	public FC method_13148(Dynamic<?> dynamic) {
		return (FC)this.field_13514.apply(dynamic);
	}

	protected void method_13153(class_1945 arg, class_2338 arg2, class_2680 arg3) {
		if (this.field_13556) {
			arg.method_8652(arg2, arg3, 3);
		} else {
			arg.method_8652(arg2, arg3, 2);
		}
	}

	public abstract boolean method_13151(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, FC arg4);

	public List<class_1959.class_1964> method_13149() {
		return Collections.emptyList();
	}

	public List<class_1959.class_1964> method_16140() {
		return Collections.emptyList();
	}
}
