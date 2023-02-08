package net.minecraft.data.server.advancement.vanilla;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureKeys;

public class VanillaStoryTabAdvancementGenerator implements AdvancementTabGenerator {
	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<Advancement> exporter) {
		Advancement advancement = Advancement.Builder.create()
			.display(
				Blocks.GRASS_BLOCK,
				Text.translatable("advancements.story.root.title"),
				Text.translatable("advancements.story.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/stone.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.CRAFTING_TABLE))
			.build(exporter, "story/root");
		Advancement advancement2 = Advancement.Builder.create()
			.parent(advancement)
			.display(
				Items.WOODEN_PICKAXE,
				Text.translatable("advancements.story.mine_stone.title"),
				Text.translatable("advancements.story.mine_stone.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("get_stone", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(ItemTags.STONE_TOOL_MATERIALS).build()))
			.build(exporter, "story/mine_stone");
		Advancement advancement3 = Advancement.Builder.create()
			.parent(advancement2)
			.display(
				Items.STONE_PICKAXE,
				Text.translatable("advancements.story.upgrade_tools.title"),
				Text.translatable("advancements.story.upgrade_tools.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.STONE_PICKAXE))
			.build(exporter, "story/upgrade_tools");
		Advancement advancement4 = Advancement.Builder.create()
			.parent(advancement3)
			.display(
				Items.IRON_INGOT,
				Text.translatable("advancements.story.smelt_iron.title"),
				Text.translatable("advancements.story.smelt_iron.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("iron", InventoryChangedCriterion.Conditions.items(Items.IRON_INGOT))
			.build(exporter, "story/smelt_iron");
		Advancement advancement5 = Advancement.Builder.create()
			.parent(advancement4)
			.display(
				Items.IRON_PICKAXE,
				Text.translatable("advancements.story.iron_tools.title"),
				Text.translatable("advancements.story.iron_tools.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.IRON_PICKAXE))
			.build(exporter, "story/iron_tools");
		Advancement advancement6 = Advancement.Builder.create()
			.parent(advancement5)
			.display(
				Items.DIAMOND,
				Text.translatable("advancements.story.mine_diamond.title"),
				Text.translatable("advancements.story.mine_diamond.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND))
			.build(exporter, "story/mine_diamond");
		Advancement advancement7 = Advancement.Builder.create()
			.parent(advancement4)
			.display(
				Items.LAVA_BUCKET,
				Text.translatable("advancements.story.lava_bucket.title"),
				Text.translatable("advancements.story.lava_bucket.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("lava_bucket", InventoryChangedCriterion.Conditions.items(Items.LAVA_BUCKET))
			.build(exporter, "story/lava_bucket");
		Advancement advancement8 = Advancement.Builder.create()
			.parent(advancement4)
			.display(
				Items.IRON_CHESTPLATE,
				Text.translatable("advancements.story.obtain_armor.title"),
				Text.translatable("advancements.story.obtain_armor.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("iron_helmet", InventoryChangedCriterion.Conditions.items(Items.IRON_HELMET))
			.criterion("iron_chestplate", InventoryChangedCriterion.Conditions.items(Items.IRON_CHESTPLATE))
			.criterion("iron_leggings", InventoryChangedCriterion.Conditions.items(Items.IRON_LEGGINGS))
			.criterion("iron_boots", InventoryChangedCriterion.Conditions.items(Items.IRON_BOOTS))
			.build(exporter, "story/obtain_armor");
		Advancement.Builder.create()
			.parent(advancement6)
			.display(
				Items.ENCHANTED_BOOK,
				Text.translatable("advancements.story.enchant_item.title"),
				Text.translatable("advancements.story.enchant_item.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("enchanted_item", EnchantedItemCriterion.Conditions.any())
			.build(exporter, "story/enchant_item");
		Advancement advancement9 = Advancement.Builder.create()
			.parent(advancement7)
			.display(
				Blocks.OBSIDIAN,
				Text.translatable("advancements.story.form_obsidian.title"),
				Text.translatable("advancements.story.form_obsidian.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("obsidian", InventoryChangedCriterion.Conditions.items(Blocks.OBSIDIAN))
			.build(exporter, "story/form_obsidian");
		Advancement.Builder.create()
			.parent(advancement8)
			.display(
				Items.SHIELD,
				Text.translatable("advancements.story.deflect_arrow.title"),
				Text.translatable("advancements.story.deflect_arrow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"deflected_projectile",
				EntityHurtPlayerCriterion.Conditions.create(
					DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))).blocked(true)
				)
			)
			.build(exporter, "story/deflect_arrow");
		Advancement.Builder.create()
			.parent(advancement6)
			.display(
				Items.DIAMOND_CHESTPLATE,
				Text.translatable("advancements.story.shiny_gear.title"),
				Text.translatable("advancements.story.shiny_gear.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("diamond_helmet", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_HELMET))
			.criterion("diamond_chestplate", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_CHESTPLATE))
			.criterion("diamond_leggings", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_LEGGINGS))
			.criterion("diamond_boots", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_BOOTS))
			.build(exporter, "story/shiny_gear");
		Advancement advancement10 = Advancement.Builder.create()
			.parent(advancement9)
			.display(
				Items.FLINT_AND_STEEL,
				Text.translatable("advancements.story.enter_the_nether.title"),
				Text.translatable("advancements.story.enter_the_nether.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(World.NETHER))
			.build(exporter, "story/enter_the_nether");
		Advancement.Builder.create()
			.parent(advancement10)
			.display(
				Items.GOLDEN_APPLE,
				Text.translatable("advancements.story.cure_zombie_villager.title"),
				Text.translatable("advancements.story.cure_zombie_villager.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("cured_zombie", CuredZombieVillagerCriterion.Conditions.any())
			.build(exporter, "story/cure_zombie_villager");
		Advancement advancement11 = Advancement.Builder.create()
			.parent(advancement10)
			.display(
				Items.ENDER_EYE,
				Text.translatable("advancements.story.follow_ender_eye.title"),
				Text.translatable("advancements.story.follow_ender_eye.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("in_stronghold", TickCriterion.Conditions.createLocation(LocationPredicate.feature(StructureKeys.STRONGHOLD)))
			.build(exporter, "story/follow_ender_eye");
		Advancement.Builder.create()
			.parent(advancement11)
			.display(
				Blocks.END_STONE,
				Text.translatable("advancements.story.enter_the_end.title"),
				Text.translatable("advancements.story.enter_the_end.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(World.END))
			.build(exporter, "story/enter_the_end");
	}
}
