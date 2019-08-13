package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class StoryTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	public void method_10347(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.field_10219,
				new TranslatableText("advancements.story.root.title"),
				new TranslatableText("advancements.story.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/stone.png"),
				AdvancementFrame.field_1254,
				false,
				false,
				false
			)
			.criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.field_9980))
			.build(consumer, "story/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8647,
				new TranslatableText("advancements.story.mine_stone.title"),
				new TranslatableText("advancements.story.mine_stone.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("get_stone", InventoryChangedCriterion.Conditions.items(Blocks.field_10445))
			.build(consumer, "story/mine_stone");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8387,
				new TranslatableText("advancements.story.upgrade_tools.title"),
				new TranslatableText("advancements.story.upgrade_tools.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.field_8387))
			.build(consumer, "story/upgrade_tools");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Items.field_8620,
				new TranslatableText("advancements.story.smelt_iron.title"),
				new TranslatableText("advancements.story.smelt_iron.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("iron", InventoryChangedCriterion.Conditions.items(Items.field_8620))
			.build(consumer, "story/smelt_iron");
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8403,
				new TranslatableText("advancements.story.iron_tools.title"),
				new TranslatableText("advancements.story.iron_tools.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.field_8403))
			.build(consumer, "story/iron_tools");
		Advancement advancement6 = Advancement.Task.create()
			.parent(advancement5)
			.display(
				Items.field_8477,
				new TranslatableText("advancements.story.mine_diamond.title"),
				new TranslatableText("advancements.story.mine_diamond.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.field_8477))
			.build(consumer, "story/mine_diamond");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8187,
				new TranslatableText("advancements.story.lava_bucket.title"),
				new TranslatableText("advancements.story.lava_bucket.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("lava_bucket", InventoryChangedCriterion.Conditions.items(Items.field_8187))
			.build(consumer, "story/lava_bucket");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.field_8523,
				new TranslatableText("advancements.story.obtain_armor.title"),
				new TranslatableText("advancements.story.obtain_armor.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("iron_helmet", InventoryChangedCriterion.Conditions.items(Items.field_8743))
			.criterion("iron_chestplate", InventoryChangedCriterion.Conditions.items(Items.field_8523))
			.criterion("iron_leggings", InventoryChangedCriterion.Conditions.items(Items.field_8396))
			.criterion("iron_boots", InventoryChangedCriterion.Conditions.items(Items.field_8660))
			.build(consumer, "story/obtain_armor");
		Advancement advancement9 = Advancement.Task.create()
			.parent(advancement6)
			.display(
				Items.field_8598,
				new TranslatableText("advancements.story.enchant_item.title"),
				new TranslatableText("advancements.story.enchant_item.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("enchanted_item", EnchantedItemCriterion.Conditions.any())
			.build(consumer, "story/enchant_item");
		Advancement advancement10 = Advancement.Task.create()
			.parent(advancement7)
			.display(
				Blocks.field_10540,
				new TranslatableText("advancements.story.form_obsidian.title"),
				new TranslatableText("advancements.story.form_obsidian.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("obsidian", InventoryChangedCriterion.Conditions.items(Blocks.field_10540))
			.build(consumer, "story/form_obsidian");
		Advancement advancement11 = Advancement.Task.create()
			.parent(advancement8)
			.display(
				Items.field_8255,
				new TranslatableText("advancements.story.deflect_arrow.title"),
				new TranslatableText("advancements.story.deflect_arrow.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion(
				"deflected_projectile",
				EntityHurtPlayerCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true)).blocked(true))
			)
			.build(consumer, "story/deflect_arrow");
		Advancement advancement12 = Advancement.Task.create()
			.parent(advancement6)
			.display(
				Items.field_8058,
				new TranslatableText("advancements.story.shiny_gear.title"),
				new TranslatableText("advancements.story.shiny_gear.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criteriaMerger(CriteriaMerger.OR)
			.criterion("diamond_helmet", InventoryChangedCriterion.Conditions.items(Items.field_8805))
			.criterion("diamond_chestplate", InventoryChangedCriterion.Conditions.items(Items.field_8058))
			.criterion("diamond_leggings", InventoryChangedCriterion.Conditions.items(Items.field_8348))
			.criterion("diamond_boots", InventoryChangedCriterion.Conditions.items(Items.field_8285))
			.build(consumer, "story/shiny_gear");
		Advancement advancement13 = Advancement.Task.create()
			.parent(advancement10)
			.display(
				Items.field_8884,
				new TranslatableText("advancements.story.enter_the_nether.title"),
				new TranslatableText("advancements.story.enter_the_nether.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13076))
			.build(consumer, "story/enter_the_nether");
		Advancement advancement14 = Advancement.Task.create()
			.parent(advancement13)
			.display(
				Items.field_8463,
				new TranslatableText("advancements.story.cure_zombie_villager.title"),
				new TranslatableText("advancements.story.cure_zombie_villager.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("cured_zombie", CuredZombieVillagerCriterion.Conditions.any())
			.build(consumer, "story/cure_zombie_villager");
		Advancement advancement15 = Advancement.Task.create()
			.parent(advancement13)
			.display(
				Items.field_8449,
				new TranslatableText("advancements.story.follow_ender_eye.title"),
				new TranslatableText("advancements.story.follow_ender_eye.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("in_stronghold", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.STRONGHOLD)))
			.build(consumer, "story/follow_ender_eye");
		Advancement advancement16 = Advancement.Task.create()
			.parent(advancement15)
			.display(
				Blocks.field_10471,
				new TranslatableText("advancements.story.enter_the_end.title"),
				new TranslatableText("advancements.story.enter_the_end.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13078))
			.build(consumer, "story/enter_the_end");
	}
}
