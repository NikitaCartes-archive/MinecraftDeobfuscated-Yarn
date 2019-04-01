package net.minecraft;

import java.util.function.Consumer;

public class class_2417 implements Consumer<Consumer<class_161>> {
	public void method_10347(Consumer<class_161> consumer) {
		class_161 lv = class_161.class_162.method_707()
			.method_697(
				class_2246.field_10219,
				new class_2588("advancements.story.root.title"),
				new class_2588("advancements.story.root.description"),
				new class_2960("textures/gui/advancements/backgrounds/stone.png"),
				class_189.field_1254,
				false,
				false,
				false
			)
			.method_709("crafting_table", class_2066.class_2068.method_8959(class_2246.field_9980))
			.method_694(consumer, "story/root");
		class_161 lv2 = class_161.class_162.method_707()
			.method_701(lv)
			.method_697(
				class_1802.field_8647,
				new class_2588("advancements.story.mine_stone.title"),
				new class_2588("advancements.story.mine_stone.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("get_stone", class_2066.class_2068.method_8959(class_2246.field_10445))
			.method_694(consumer, "story/mine_stone");
		class_161 lv3 = class_161.class_162.method_707()
			.method_701(lv2)
			.method_697(
				class_1802.field_8387,
				new class_2588("advancements.story.upgrade_tools.title"),
				new class_2588("advancements.story.upgrade_tools.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("stone_pickaxe", class_2066.class_2068.method_8959(class_1802.field_8387))
			.method_694(consumer, "story/upgrade_tools");
		class_161 lv4 = class_161.class_162.method_707()
			.method_701(lv3)
			.method_697(
				class_1802.field_8620,
				new class_2588("advancements.story.smelt_iron.title"),
				new class_2588("advancements.story.smelt_iron.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("iron", class_2066.class_2068.method_8959(class_1802.field_8620))
			.method_694(consumer, "story/smelt_iron");
		class_161 lv5 = class_161.class_162.method_707()
			.method_701(lv4)
			.method_697(
				class_1802.field_8403,
				new class_2588("advancements.story.iron_tools.title"),
				new class_2588("advancements.story.iron_tools.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("iron_pickaxe", class_2066.class_2068.method_8959(class_1802.field_8403))
			.method_694(consumer, "story/iron_tools");
		class_161 lv6 = class_161.class_162.method_707()
			.method_701(lv5)
			.method_697(
				class_1802.field_8477,
				new class_2588("advancements.story.mine_diamond.title"),
				new class_2588("advancements.story.mine_diamond.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("diamond", class_2066.class_2068.method_8959(class_1802.field_8477))
			.method_694(consumer, "story/mine_diamond");
		class_161 lv7 = class_161.class_162.method_707()
			.method_701(lv4)
			.method_697(
				class_1802.field_8187,
				new class_2588("advancements.story.lava_bucket.title"),
				new class_2588("advancements.story.lava_bucket.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("lava_bucket", class_2066.class_2068.method_8959(class_1802.field_8187))
			.method_694(consumer, "story/lava_bucket");
		class_161 lv8 = class_161.class_162.method_707()
			.method_701(lv4)
			.method_697(
				class_1802.field_8523,
				new class_2588("advancements.story.obtain_armor.title"),
				new class_2588("advancements.story.obtain_armor.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_704(class_193.field_1257)
			.method_709("iron_helmet", class_2066.class_2068.method_8959(class_1802.field_8743))
			.method_709("iron_chestplate", class_2066.class_2068.method_8959(class_1802.field_8523))
			.method_709("iron_leggings", class_2066.class_2068.method_8959(class_1802.field_8396))
			.method_709("iron_boots", class_2066.class_2068.method_8959(class_1802.field_8660))
			.method_694(consumer, "story/obtain_armor");
		class_161 lv9 = class_161.class_162.method_707()
			.method_701(lv6)
			.method_697(
				class_1802.field_8598,
				new class_2588("advancements.story.enchant_item.title"),
				new class_2588("advancements.story.enchant_item.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("enchanted_item", class_2030.class_2032.method_8877())
			.method_694(consumer, "story/enchant_item");
		class_161 lv10 = class_161.class_162.method_707()
			.method_701(lv7)
			.method_697(
				class_2246.field_10540,
				new class_2588("advancements.story.form_obsidian.title"),
				new class_2588("advancements.story.form_obsidian.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("obsidian", class_2066.class_2068.method_8959(class_2246.field_10540))
			.method_694(consumer, "story/form_obsidian");
		class_161 lv11 = class_161.class_162.method_707()
			.method_701(lv8)
			.method_697(
				class_1802.field_8255,
				new class_2588("advancements.story.deflect_arrow.title"),
				new class_2588("advancements.story.deflect_arrow.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709(
				"deflected_projectile",
				class_2044.class_2046.method_8908(class_2019.class_2020.method_8844().method_8842(class_2022.class_2023.method_8855().method_8852(true)).method_8841(true))
			)
			.method_694(consumer, "story/deflect_arrow");
		class_161 lv12 = class_161.class_162.method_707()
			.method_701(lv6)
			.method_697(
				class_1802.field_8058,
				new class_2588("advancements.story.shiny_gear.title"),
				new class_2588("advancements.story.shiny_gear.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_704(class_193.field_1257)
			.method_709("diamond_helmet", class_2066.class_2068.method_8959(class_1802.field_8805))
			.method_709("diamond_chestplate", class_2066.class_2068.method_8959(class_1802.field_8058))
			.method_709("diamond_leggings", class_2066.class_2068.method_8959(class_1802.field_8348))
			.method_709("diamond_boots", class_2066.class_2068.method_8959(class_1802.field_8285))
			.method_694(consumer, "story/shiny_gear");
		class_161 lv13 = class_161.class_162.method_707()
			.method_701(lv10)
			.method_697(
				class_1802.field_8884,
				new class_2588("advancements.story.enter_the_nether.title"),
				new class_2588("advancements.story.enter_the_nether.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("entered_nether", class_1999.class_2001.method_8799(class_2874.field_13076))
			.method_694(consumer, "story/enter_the_nether");
		class_161 lv14 = class_161.class_162.method_707()
			.method_701(lv13)
			.method_697(
				class_1802.field_8463,
				new class_2588("advancements.story.cure_zombie_villager.title"),
				new class_2588("advancements.story.cure_zombie_villager.description"),
				null,
				class_189.field_1249,
				true,
				true,
				false
			)
			.method_709("cured_zombie", class_2014.class_2016.method_8836())
			.method_694(consumer, "story/cure_zombie_villager");
		class_161 lv15 = class_161.class_162.method_707()
			.method_701(lv13)
			.method_697(
				class_1802.field_8449,
				new class_2588("advancements.story.follow_ender_eye.title"),
				new class_2588("advancements.story.follow_ender_eye.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("in_stronghold", class_2092.class_2094.method_9034(class_2090.method_9017(class_3031.field_13565)))
			.method_694(consumer, "story/follow_ender_eye");
		class_161 lv16 = class_161.class_162.method_707()
			.method_701(lv15)
			.method_697(
				class_2246.field_10471,
				new class_2588("advancements.story.enter_the_end.title"),
				new class_2588("advancements.story.enter_the_end.description"),
				null,
				class_189.field_1254,
				true,
				true,
				false
			)
			.method_709("entered_end", class_1999.class_2001.method_8799(class_2874.field_13078))
			.method_694(consumer, "story/enter_the_end");
	}
}
