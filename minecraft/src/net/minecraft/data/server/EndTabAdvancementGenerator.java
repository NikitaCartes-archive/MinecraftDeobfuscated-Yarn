package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureKeys;

public class EndTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	public void accept(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Builder.create()
			.display(
				Blocks.END_STONE,
				Text.translatable("advancements.end.root.title"),
				Text.translatable("advancements.end.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/end.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("entered_end", ChangedDimensionCriterion.Conditions.to(World.END))
			.build(consumer, "end/root");
		Advancement advancement2 = Advancement.Builder.create()
			.parent(advancement)
			.display(
				Blocks.DRAGON_HEAD,
				Text.translatable("advancements.end.kill_dragon.title"),
				Text.translatable("advancements.end.kill_dragon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("killed_dragon", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON)))
			.build(consumer, "end/kill_dragon");
		Advancement advancement3 = Advancement.Builder.create()
			.parent(advancement2)
			.display(
				Items.ENDER_PEARL,
				Text.translatable("advancements.end.enter_end_gateway.title"),
				Text.translatable("advancements.end.enter_end_gateway.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("entered_end_gateway", EnterBlockCriterion.Conditions.block(Blocks.END_GATEWAY))
			.build(consumer, "end/enter_end_gateway");
		Advancement.Builder.create()
			.parent(advancement2)
			.display(
				Items.END_CRYSTAL,
				Text.translatable("advancements.end.respawn_dragon.title"),
				Text.translatable("advancements.end.respawn_dragon.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("summoned_dragon", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON)))
			.build(consumer, "end/respawn_dragon");
		Advancement advancement4 = Advancement.Builder.create()
			.parent(advancement3)
			.display(
				Blocks.PURPUR_BLOCK,
				Text.translatable("advancements.end.find_end_city.title"),
				Text.translatable("advancements.end.find_end_city.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("in_city", TickCriterion.Conditions.createLocation(LocationPredicate.feature(StructureKeys.END_CITY)))
			.build(consumer, "end/find_end_city");
		Advancement.Builder.create()
			.parent(advancement2)
			.display(
				Items.DRAGON_BREATH,
				Text.translatable("advancements.end.dragon_breath.title"),
				Text.translatable("advancements.end.dragon_breath.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("dragon_breath", InventoryChangedCriterion.Conditions.items(Items.DRAGON_BREATH))
			.build(consumer, "end/dragon_breath");
		Advancement.Builder.create()
			.parent(advancement4)
			.display(
				Items.SHULKER_SHELL,
				Text.translatable("advancements.end.levitate.title"),
				Text.translatable("advancements.end.levitate.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion("levitated", LevitationCriterion.Conditions.create(DistancePredicate.y(NumberRange.FloatRange.atLeast(50.0))))
			.build(consumer, "end/levitate");
		Advancement.Builder.create()
			.parent(advancement4)
			.display(
				Items.ELYTRA,
				Text.translatable("advancements.end.elytra.title"),
				Text.translatable("advancements.end.elytra.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("elytra", InventoryChangedCriterion.Conditions.items(Items.ELYTRA))
			.build(consumer, "end/elytra");
		Advancement.Builder.create()
			.parent(advancement2)
			.display(
				Blocks.DRAGON_EGG,
				Text.translatable("advancements.end.dragon_egg.title"),
				Text.translatable("advancements.end.dragon_egg.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("dragon_egg", InventoryChangedCriterion.Conditions.items(Blocks.DRAGON_EGG))
			.build(consumer, "end/dragon_egg");
	}
}
