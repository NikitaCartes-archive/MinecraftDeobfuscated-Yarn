package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.SimpleAdvancement;
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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class StoryTabAdvancementGenerator implements Consumer<Consumer<SimpleAdvancement>> {
	public void method_10347(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.Task.create()
			.display(
				Blocks.field_10219,
				new TranslatableTextComponent("advancements.story.root.title"),
				new TranslatableTextComponent("advancements.story.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/stone.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.field_9980))
			.build(consumer, "story/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8647,
				new TranslatableTextComponent("advancements.story.mine_stone.title"),
				new TranslatableTextComponent("advancements.story.mine_stone.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("get_stone", InventoryChangedCriterion.Conditions.items(Blocks.field_10445))
			.build(consumer, "story/mine_stone");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8387,
				new TranslatableTextComponent("advancements.story.upgrade_tools.title"),
				new TranslatableTextComponent("advancements.story.upgrade_tools.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.field_8387))
			.build(consumer, "story/upgrade_tools");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement3)
			.display(
				Items.field_8620,
				new TranslatableTextComponent("advancements.story.smelt_iron.title"),
				new TranslatableTextComponent("advancements.story.smelt_iron.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("iron", InventoryChangedCriterion.Conditions.items(Items.field_8620))
			.build(consumer, "story/smelt_iron");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement4)
			.display(
				Items.field_8403,
				new TranslatableTextComponent("advancements.story.iron_tools.title"),
				new TranslatableTextComponent("advancements.story.iron_tools.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.field_8403))
			.build(consumer, "story/iron_tools");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8477,
				new TranslatableTextComponent("advancements.story.mine_diamond.title"),
				new TranslatableTextComponent("advancements.story.mine_diamond.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.field_8477))
			.build(consumer, "story/mine_diamond");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement4)
			.display(
				Items.field_8187,
				new TranslatableTextComponent("advancements.story.lava_bucket.title"),
				new TranslatableTextComponent("advancements.story.lava_bucket.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("lava_bucket", InventoryChangedCriterion.Conditions.items(Items.field_8187))
			.build(consumer, "story/lava_bucket");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement4)
			.display(
				Items.field_8523,
				new TranslatableTextComponent("advancements.story.obtain_armor.title"),
				new TranslatableTextComponent("advancements.story.obtain_armor.description"),
				null,
				AdvancementFrame.TASK,
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
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement6)
			.display(
				Items.field_8598,
				new TranslatableTextComponent("advancements.story.enchant_item.title"),
				new TranslatableTextComponent("advancements.story.enchant_item.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("enchanted_item", EnchantedItemCriterion.Conditions.any())
			.build(consumer, "story/enchant_item");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement7)
			.display(
				Blocks.field_10540,
				new TranslatableTextComponent("advancements.story.form_obsidian.title"),
				new TranslatableTextComponent("advancements.story.form_obsidian.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("obsidian", InventoryChangedCriterion.Conditions.items(Blocks.field_10540))
			.build(consumer, "story/form_obsidian");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement8)
			.display(
				Items.field_8255,
				new TranslatableTextComponent("advancements.story.deflect_arrow.title"),
				new TranslatableTextComponent("advancements.story.deflect_arrow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"deflected_projectile",
				EntityHurtPlayerCriterion.Conditions.method_8908(
					DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true)).blocked(true)
				)
			)
			.build(consumer, "story/deflect_arrow");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement6)
			.display(
				Items.field_8058,
				new TranslatableTextComponent("advancements.story.shiny_gear.title"),
				new TranslatableTextComponent("advancements.story.shiny_gear.description"),
				null,
				AdvancementFrame.TASK,
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
		SimpleAdvancement simpleAdvancement13 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement10)
			.display(
				Items.field_8884,
				new TranslatableTextComponent("advancements.story.enter_the_nether.title"),
				new TranslatableTextComponent("advancements.story.enter_the_nether.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13076))
			.build(consumer, "story/enter_the_nether");
		SimpleAdvancement simpleAdvancement14 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement13)
			.display(
				Items.field_8463,
				new TranslatableTextComponent("advancements.story.cure_zombie_villager.title"),
				new TranslatableTextComponent("advancements.story.cure_zombie_villager.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("cured_zombie", CuredZombieVillagerCriterion.Conditions.any())
			.build(consumer, "story/cure_zombie_villager");
		SimpleAdvancement simpleAdvancement15 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement13)
			.display(
				Items.field_8449,
				new TranslatableTextComponent("advancements.story.follow_ender_eye.title"),
				new TranslatableTextComponent("advancements.story.follow_ender_eye.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("in_stronghold", LocationArrivalCriterion.Conditions.method_9034(LocationPredicate.feature(Feature.STRONGHOLD)))
			.build(consumer, "story/follow_ender_eye");
		SimpleAdvancement simpleAdvancement16 = SimpleAdvancement.Task.create()
			.parent(simpleAdvancement15)
			.display(
				Blocks.field_10471,
				new TranslatableTextComponent("advancements.story.enter_the_end.title"),
				new TranslatableTextComponent("advancements.story.enter_the_end.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13078))
			.build(consumer, "story/enter_the_end");
	}
}
