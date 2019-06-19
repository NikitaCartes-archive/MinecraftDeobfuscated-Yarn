package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class EndTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	public void method_10348(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.field_10471,
				new TranslatableText("advancements.end.root.title"),
				new TranslatableText("advancements.end.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/end.png"),
				AdvancementFrame.field_1254,
				false,
				false,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13078))
			.build(consumer, "end/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Blocks.field_10337,
				new TranslatableText("advancements.end.kill_dragon.title"),
				new TranslatableText("advancements.end.kill_dragon.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("killed_dragon", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.field_6116)))
			.build(consumer, "end/kill_dragon");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8634,
				new TranslatableText("advancements.end.enter_end_gateway.title"),
				new TranslatableText("advancements.end.enter_end_gateway.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("entered_end_gateway", EnterBlockCriterion.Conditions.block(Blocks.field_10613))
			.build(consumer, "end/enter_end_gateway");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8301,
				new TranslatableText("advancements.end.respawn_dragon.title"),
				new TranslatableText("advancements.end.respawn_dragon.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("summoned_dragon", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.field_6116)))
			.build(consumer, "end/respawn_dragon");
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Blocks.field_10286,
				new TranslatableText("advancements.end.find_end_city.title"),
				new TranslatableText("advancements.end.find_end_city.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("in_city", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.END_CITY)))
			.build(consumer, "end/find_end_city");
		Advancement advancement6 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8613,
				new TranslatableText("advancements.end.dragon_breath.title"),
				new TranslatableText("advancements.end.dragon_breath.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("dragon_breath", InventoryChangedCriterion.Conditions.items(Items.field_8613))
			.build(consumer, "end/dragon_breath");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement5)
			.display(
				Items.field_8815,
				new TranslatableText("advancements.end.levitate.title"),
				new TranslatableText("advancements.end.levitate.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion("levitated", LevitationCriterion.Conditions.create(DistancePredicate.y(NumberRange.FloatRange.atLeast(50.0F))))
			.build(consumer, "end/levitate");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement5)
			.display(
				Items.field_8833,
				new TranslatableText("advancements.end.elytra.title"),
				new TranslatableText("advancements.end.elytra.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("elytra", InventoryChangedCriterion.Conditions.items(Items.field_8833))
			.build(consumer, "end/elytra");
		Advancement advancement9 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Blocks.field_10081,
				new TranslatableText("advancements.end.dragon_egg.title"),
				new TranslatableText("advancements.end.dragon_egg.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("dragon_egg", InventoryChangedCriterion.Conditions.items(Blocks.field_10081))
			.build(consumer, "end/dragon_egg");
	}
}
