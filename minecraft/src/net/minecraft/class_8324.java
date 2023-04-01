package net.minecraft;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.StringIdentifiable;

public enum class_8324 implements StringIdentifiable {
	ANY("any", null, "rule.food_restriction.edible"),
	APPLE("apple", Items.APPLE, "rule.food_restriction.edible"),
	BAKED_POTATO("baked_potato", Items.BAKED_POTATO, "rule.food_restriction.edible"),
	BEEF("beef", Items.BEEF, "rule.food_restriction.edible"),
	BEETROOT("beetroot", Items.BEETROOT, "rule.food_restriction.edible"),
	BEETROOT_SOUP("beetroot_soup", Items.BEETROOT_SOUP, "rule.food_restriction.edible"),
	BREAD("bread", Items.BREAD, "rule.food_restriction.edible"),
	CAKE("cake", Items.CAKE, "rule.food_restriction.edible"),
	CARROT("carrot", Items.CARROT, "rule.food_restriction.edible"),
	CHICKEN("chicken", Items.CHICKEN, "rule.food_restriction.edible"),
	CHORUS_FRUIT("chorus_fruit", Items.CHORUS_FRUIT, "rule.food_restriction.maybe_edible"),
	COD("cod", Items.COD, "rule.food_restriction.edible"),
	COOKED_BEEF("cooked_beef", Items.COOKED_BEEF, "rule.food_restriction.edible"),
	COOKED_CHICKEN("cooked_chicken", Items.COOKED_CHICKEN, "rule.food_restriction.edible"),
	COOKED_COD("cooked_cod", Items.COOKED_COD, "rule.food_restriction.edible"),
	COOKED_MUTTON("cooked_mutton", Items.COOKED_MUTTON, "rule.food_restriction.edible"),
	COOKED_PORKCHOP("cooked_porkchop", Items.COOKED_PORKCHOP, "rule.food_restriction.edible"),
	COOKED_RABBIT("cooked_rabbit", Items.COOKED_RABBIT, "rule.food_restriction.edible"),
	COOKED_SALMON("cooked_salmon", Items.COOKED_SALMON, "rule.food_restriction.edible"),
	COOKIE("cookie", Items.COOKIE, "rule.food_restriction.edible"),
	DRIED_KELP("dried_kelp", Items.DRIED_KELP, "rule.food_restriction.edible"),
	ENCHANTED_GOLDEN_APPLE("enchanted_golden_apple", Items.ENCHANTED_GOLDEN_APPLE, "rule.food_restriction.edible"),
	GLOW_BERRIES("glow_berries", Items.GLOW_BERRIES, "rule.food_restriction.edible"),
	GOLDEN_APPLE("golden_apple", Items.GOLDEN_APPLE, "rule.food_restriction.edible"),
	GOLDEN_CARROT("golden_carrot", Items.GOLDEN_CARROT, "rule.food_restriction.edible"),
	HONEY_BOTTLE("honey_bottle", Items.HONEY_BOTTLE, "rule.food_restriction.edible"),
	MELON_SLICE("melon_slice", Items.MELON_SLICE, "rule.food_restriction.edible"),
	MUSHROOM_STEW("mushroom_stew", Items.MUSHROOM_STEW, "rule.food_restriction.edible"),
	MUTTON("mutton", Items.MUTTON, "rule.food_restriction.edible"),
	POISONOUS_POTATO("poisonous_potato", Items.POISONOUS_POTATO, "rule.food_restriction.maybe_edible"),
	PORKCHOP("porkchop", Items.PORKCHOP, "rule.food_restriction.edible"),
	POTATO("potato", Items.POTATO, "rule.food_restriction.edible"),
	PUFFERFISH("pufferfish", Items.PUFFERFISH, "rule.food_restriction.maybe_edible"),
	PUMPKIN_PIE("pumpkin_pie", Items.PUMPKIN_PIE, "rule.food_restriction.edible"),
	RABBIT("rabbit", Items.RABBIT, "rule.food_restriction.edible"),
	RABBIT_STEW("rabbit_stew", Items.RABBIT_STEW, "rule.food_restriction.edible"),
	ROTTEN_FLESH("rotten_flesh", Items.ROTTEN_FLESH, "rule.food_restriction.maybe_edible"),
	SALMON("salmon", Items.SALMON, "rule.food_restriction.edible"),
	SPIDER_EYE("spider_eye", Items.SPIDER_EYE, "rule.food_restriction.maybe_edible"),
	SUSPICIOUS_STEW("suspicious_stew", Items.SUSPICIOUS_STEW, "rule.food_restriction.maybe_edible"),
	SWEET_BERRIES("sweet_berries", Items.SWEET_BERRIES, "rule.food_restriction.edible"),
	TROPICAL_FISH("tropical_fish", Items.TROPICAL_FISH, "rule.food_restriction.edible"),
	AIR("air_block", Items.AIR_BLOCK, "rule.food_restriction.maybe_edible");

	private static final String field_43837 = "rule.food_restriction.edible";
	private static final String field_43838 = "rule.food_restriction.maybe_edible";
	private final String field_43839;
	@Nullable
	private final Item field_43840;
	private final String field_43841;
	public static final com.mojang.serialization.Codec<class_8324> field_43835 = StringIdentifiable.createCodec(class_8324::values);
	public static final Set<Item> field_43836 = (Set<Item>)Arrays.stream(values())
		.filter(arg -> arg.method_50352() != null)
		.map(class_8324::method_50352)
		.collect(Collectors.toSet());

	private class_8324(String string2, @Nullable Item item, String string3) {
		this.field_43839 = string2;
		this.field_43840 = item;
		this.field_43841 = string3;
	}

	@Override
	public String asString() {
		return this.field_43839;
	}

	@Nullable
	public Item method_50352() {
		return this.field_43840;
	}

	public String method_50354() {
		return this.field_43841;
	}
}
