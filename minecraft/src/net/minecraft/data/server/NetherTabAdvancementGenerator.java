package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class NetherTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	public void method_10346(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.field_9986,
				new TranslatableText("advancements.nether.root.title"),
				new TranslatableText("advancements.nether.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/nether.png"),
				AdvancementFrame.field_1254,
				false,
				false,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13076))
			.build(consumer, "nether/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8814,
				new TranslatableText("advancements.nether.return_to_sender.title"),
				new TranslatableText("advancements.nether.return_to_sender.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.field_6107),
					DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.field_6066))
				)
			)
			.build(consumer, "nether/return_to_sender");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Blocks.field_10266,
				new TranslatableText("advancements.nether.find_fortress.title"),
				new TranslatableText("advancements.nether.find_fortress.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("fortress", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.NETHER_BRIDGE)))
			.build(consumer, "nether/find_fortress");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.field_8895,
				new TranslatableText("advancements.nether.fast_travel.title"),
				new TranslatableText("advancements.nether.fast_travel.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("travelled", NetherTravelCriterion.Conditions.distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(7000.0F))))
			.build(consumer, "nether/fast_travel");
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.field_8070,
				new TranslatableText("advancements.nether.uneasy_alliance.title"),
				new TranslatableText("advancements.nether.uneasy_alliance.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.field_6107).location(LocationPredicate.dimension(DimensionType.field_13072))
				)
			)
			.build(consumer, "nether/uneasy_alliance");
		Advancement advancement6 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Blocks.field_10177,
				new TranslatableText("advancements.nether.get_wither_skull.title"),
				new TranslatableText("advancements.nether.get_wither_skull.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.field_10177))
			.build(consumer, "nether/get_wither_skull");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement6)
			.display(
				Items.field_8137,
				new TranslatableText("advancements.nether.summon_wither.title"),
				new TranslatableText("advancements.nether.summon_wither.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.field_6119)))
			.build(consumer, "nether/summon_wither");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Items.field_8894,
				new TranslatableText("advancements.nether.obtain_blaze_rod.title"),
				new TranslatableText("advancements.nether.obtain_blaze_rod.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.field_8894))
			.build(consumer, "nether/obtain_blaze_rod");
		Advancement advancement9 = Advancement.Task.create()
			.parent(advancement7)
			.display(
				Blocks.field_10327,
				new TranslatableText("advancements.nether.create_beacon.title"),
				new TranslatableText("advancements.nether.create_beacon.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1)))
			.build(consumer, "nether/create_beacon");
		Advancement advancement10 = Advancement.Task.create()
			.parent(advancement9)
			.display(
				Blocks.field_10327,
				new TranslatableText("advancements.nether.create_full_beacon.title"),
				new TranslatableText("advancements.nether.create_full_beacon.description"),
				null,
				AdvancementFrame.field_1249,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.exactly(4)))
			.build(consumer, "nether/create_full_beacon");
		Advancement advancement11 = Advancement.Task.create()
			.parent(advancement8)
			.display(
				Items.field_8574,
				new TranslatableText("advancements.nether.brew_potion.title"),
				new TranslatableText("advancements.nether.brew_potion.description"),
				null,
				AdvancementFrame.field_1254,
				true,
				true,
				false
			)
			.criterion("potion", BrewedPotionCriterion.Conditions.any())
			.build(consumer, "nether/brew_potion");
		Advancement advancement12 = Advancement.Task.create()
			.parent(advancement11)
			.display(
				Items.field_8103,
				new TranslatableText("advancements.nether.all_potions.title"),
				new TranslatableText("advancements.nether.all_potions.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.create()
						.withEffect(StatusEffects.field_5904)
						.withEffect(StatusEffects.field_5909)
						.withEffect(StatusEffects.field_5910)
						.withEffect(StatusEffects.field_5913)
						.withEffect(StatusEffects.field_5924)
						.withEffect(StatusEffects.field_5918)
						.withEffect(StatusEffects.field_5923)
						.withEffect(StatusEffects.field_5905)
						.withEffect(StatusEffects.field_5925)
						.withEffect(StatusEffects.field_5911)
						.withEffect(StatusEffects.field_5899)
						.withEffect(StatusEffects.field_5906)
						.withEffect(StatusEffects.field_5907)
				)
			)
			.build(consumer, "nether/all_potions");
		Advancement advancement13 = Advancement.Task.create()
			.parent(advancement12)
			.display(
				Items.field_8550,
				new TranslatableText("advancements.nether.all_effects.title"),
				new TranslatableText("advancements.nether.all_effects.description"),
				null,
				AdvancementFrame.field_1250,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.create()
						.withEffect(StatusEffects.field_5904)
						.withEffect(StatusEffects.field_5909)
						.withEffect(StatusEffects.field_5910)
						.withEffect(StatusEffects.field_5913)
						.withEffect(StatusEffects.field_5924)
						.withEffect(StatusEffects.field_5918)
						.withEffect(StatusEffects.field_5923)
						.withEffect(StatusEffects.field_5905)
						.withEffect(StatusEffects.field_5925)
						.withEffect(StatusEffects.field_5911)
						.withEffect(StatusEffects.field_5899)
						.withEffect(StatusEffects.field_5920)
						.withEffect(StatusEffects.field_5917)
						.withEffect(StatusEffects.field_5901)
						.withEffect(StatusEffects.field_5902)
						.withEffect(StatusEffects.field_5912)
						.withEffect(StatusEffects.field_5898)
						.withEffect(StatusEffects.field_5903)
						.withEffect(StatusEffects.field_5916)
						.withEffect(StatusEffects.field_5907)
						.withEffect(StatusEffects.field_5906)
						.withEffect(StatusEffects.field_5927)
						.withEffect(StatusEffects.field_5900)
						.withEffect(StatusEffects.field_5919)
						.withEffect(StatusEffects.field_16595)
						.withEffect(StatusEffects.field_18980)
				)
			)
			.build(consumer, "nether/all_effects");
	}
}
