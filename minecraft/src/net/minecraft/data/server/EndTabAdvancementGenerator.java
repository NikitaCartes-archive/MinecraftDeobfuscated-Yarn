package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class EndTabAdvancementGenerator implements Consumer<Consumer<SimpleAdvancement>> {
	public void method_10348(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.Builder.create()
			.display(
				Blocks.field_10471,
				new TranslatableTextComponent("advancements.end.root.title"),
				new TranslatableTextComponent("advancements.end.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/end.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13078))
			.build(consumer, "end/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Blocks.field_10337,
				new TranslatableTextComponent("advancements.end.kill_dragon.title"),
				new TranslatableTextComponent("advancements.end.kill_dragon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("killed_dragon", OnKilledCriterion.Conditions.createKill(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON)))
			.build(consumer, "end/kill_dragon");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8634,
				new TranslatableTextComponent("advancements.end.enter_end_gateway.title"),
				new TranslatableTextComponent("advancements.end.enter_end_gateway.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_end_gateway", EnterBlockCriterion.Conditions.block(Blocks.field_10613))
			.build(consumer, "end/enter_end_gateway");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8301,
				new TranslatableTextComponent("advancements.end.respawn_dragon.title"),
				new TranslatableTextComponent("advancements.end.respawn_dragon.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("summoned_dragon", SummonedEntityCriterion.Conditions.method_9129(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON)))
			.build(consumer, "end/respawn_dragon");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement3)
			.display(
				Blocks.field_10286,
				new TranslatableTextComponent("advancements.end.find_end_city.title"),
				new TranslatableTextComponent("advancements.end.find_end_city.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("in_city", LocationArrivalCriterion.Conditions.method_9034(LocationPredicate.method_9017(Feature.END_CITY)))
			.build(consumer, "end/find_end_city");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8613,
				new TranslatableTextComponent("advancements.end.dragon_breath.title"),
				new TranslatableTextComponent("advancements.end.dragon_breath.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("dragon_breath", InventoryChangedCriterion.Conditions.items(Items.field_8613))
			.build(consumer, "end/dragon_breath");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8815,
				new TranslatableTextComponent("advancements.end.levitate.title"),
				new TranslatableTextComponent("advancements.end.levitate.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion("levitated", LevitationCriterion.Conditions.method_9013(DistancePredicate.method_8856(NumberRange.Float.atLeast(50.0F))))
			.build(consumer, "end/levitate");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement5)
			.display(
				Items.field_8833,
				new TranslatableTextComponent("advancements.end.elytra.title"),
				new TranslatableTextComponent("advancements.end.elytra.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("elytra", InventoryChangedCriterion.Conditions.items(Items.field_8833))
			.build(consumer, "end/elytra");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Blocks.field_10081,
				new TranslatableTextComponent("advancements.end.dragon_egg.title"),
				new TranslatableTextComponent("advancements.end.dragon_egg.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("dragon_egg", InventoryChangedCriterion.Conditions.items(Blocks.field_10081))
			.build(consumer, "end/dragon_egg");
	}
}
