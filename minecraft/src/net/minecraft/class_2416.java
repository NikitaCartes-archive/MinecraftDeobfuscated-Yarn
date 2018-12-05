package net.minecraft;

import java.util.function.Consumer;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.SimpleAdvancement;
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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class class_2416 implements Consumer<Consumer<SimpleAdvancement>> {
	public void method_10346(Consumer<SimpleAdvancement> consumer) {
		SimpleAdvancement simpleAdvancement = SimpleAdvancement.Builder.create()
			.display(
				Blocks.field_9986,
				new TranslatableTextComponent("advancements.nether.root.title"),
				new TranslatableTextComponent("advancements.nether.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/nether.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.field_13076))
			.method_694(consumer, "nether/root");
		SimpleAdvancement simpleAdvancement2 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8814,
				new TranslatableTextComponent("advancements.nether.return_to_sender.title"),
				new TranslatableTextComponent("advancements.nether.return_to_sender.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.method_703(AdvancementRewards.Builder.method_750(50))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.class_2083.method_9001(
					EntityPredicate.Builder.create().type(EntityType.GHAST),
					DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.FIREBALL))
				)
			)
			.method_694(consumer, "nether/return_to_sender");
		SimpleAdvancement simpleAdvancement3 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Blocks.field_10266,
				new TranslatableTextComponent("advancements.nether.find_fortress.title"),
				new TranslatableTextComponent("advancements.nether.find_fortress.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("fortress", LocationArrivalCriterion.Conditions.method_9034(LocationPredicate.method_9017(Feature.NETHER_BRIDGE)))
			.method_694(consumer, "nether/find_fortress");
		SimpleAdvancement simpleAdvancement4 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement)
			.display(
				Items.field_8895,
				new TranslatableTextComponent("advancements.nether.fast_travel.title"),
				new TranslatableTextComponent("advancements.nether.fast_travel.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.method_703(AdvancementRewards.Builder.method_750(100))
			.criterion("travelled", NetherTravelCriterion.Conditions.distance(DistancePredicate.method_8860(NumberRange.Float.atLeast(7000.0F))))
			.method_694(consumer, "nether/fast_travel");
		SimpleAdvancement simpleAdvancement5 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement2)
			.display(
				Items.field_8070,
				new TranslatableTextComponent("advancements.nether.uneasy_alliance.title"),
				new TranslatableTextComponent("advancements.nether.uneasy_alliance.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.method_703(AdvancementRewards.Builder.method_750(100))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.class_2083.method_8997(
					EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.method_9016(DimensionType.field_13072))
				)
			)
			.method_694(consumer, "nether/uneasy_alliance");
		SimpleAdvancement simpleAdvancement6 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement3)
			.display(
				Blocks.field_10177,
				new TranslatableTextComponent("advancements.nether.get_wither_skull.title"),
				new TranslatableTextComponent("advancements.nether.get_wither_skull.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.field_10177))
			.method_694(consumer, "nether/get_wither_skull");
		SimpleAdvancement simpleAdvancement7 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement6)
			.display(
				Items.field_8137,
				new TranslatableTextComponent("advancements.nether.summon_wither.title"),
				new TranslatableTextComponent("advancements.nether.summon_wither.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("summoned", SummonedEntityCriterion.Conditions.method_9129(EntityPredicate.Builder.create().type(EntityType.WITHER)))
			.method_694(consumer, "nether/summon_wither");
		SimpleAdvancement simpleAdvancement8 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement3)
			.display(
				Items.field_8894,
				new TranslatableTextComponent("advancements.nether.obtain_blaze_rod.title"),
				new TranslatableTextComponent("advancements.nether.obtain_blaze_rod.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.field_8894))
			.method_694(consumer, "nether/obtain_blaze_rod");
		SimpleAdvancement simpleAdvancement9 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement7)
			.display(
				Blocks.field_10327,
				new TranslatableTextComponent("advancements.nether.create_beacon.title"),
				new TranslatableTextComponent("advancements.nether.create_beacon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.Integer.atLeast(1)))
			.method_694(consumer, "nether/create_beacon");
		SimpleAdvancement simpleAdvancement10 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement9)
			.display(
				Blocks.field_10327,
				new TranslatableTextComponent("advancements.nether.create_full_beacon.title"),
				new TranslatableTextComponent("advancements.nether.create_full_beacon.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.Integer.exactly(4)))
			.method_694(consumer, "nether/create_full_beacon");
		SimpleAdvancement simpleAdvancement11 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement8)
			.display(
				Items.field_8574,
				new TranslatableTextComponent("advancements.nether.brew_potion.title"),
				new TranslatableTextComponent("advancements.nether.brew_potion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("potion", BrewedPotionCriterion.Conditions.any())
			.method_694(consumer, "nether/brew_potion");
		SimpleAdvancement simpleAdvancement12 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement11)
			.display(
				Items.field_8103,
				new TranslatableTextComponent("advancements.nether.all_potions.title"),
				new TranslatableTextComponent("advancements.nether.all_potions.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.method_703(AdvancementRewards.Builder.method_750(100))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.method_8869(
					EntityEffectPredicate.create()
						.method_9065(StatusEffects.field_5904)
						.method_9065(StatusEffects.field_5909)
						.method_9065(StatusEffects.field_5910)
						.method_9065(StatusEffects.field_5913)
						.method_9065(StatusEffects.field_5924)
						.method_9065(StatusEffects.field_5918)
						.method_9065(StatusEffects.field_5923)
						.method_9065(StatusEffects.field_5905)
						.method_9065(StatusEffects.field_5925)
						.method_9065(StatusEffects.field_5911)
						.method_9065(StatusEffects.field_5899)
						.method_9065(StatusEffects.field_5906)
						.method_9065(StatusEffects.field_5907)
				)
			)
			.method_694(consumer, "nether/all_potions");
		SimpleAdvancement simpleAdvancement13 = SimpleAdvancement.Builder.create()
			.parent(simpleAdvancement12)
			.display(
				Items.field_8550,
				new TranslatableTextComponent("advancements.nether.all_effects.title"),
				new TranslatableTextComponent("advancements.nether.all_effects.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.method_703(AdvancementRewards.Builder.method_750(1000))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.method_8869(
					EntityEffectPredicate.create()
						.method_9065(StatusEffects.field_5904)
						.method_9065(StatusEffects.field_5909)
						.method_9065(StatusEffects.field_5910)
						.method_9065(StatusEffects.field_5913)
						.method_9065(StatusEffects.field_5924)
						.method_9065(StatusEffects.field_5918)
						.method_9065(StatusEffects.field_5923)
						.method_9065(StatusEffects.field_5905)
						.method_9065(StatusEffects.field_5925)
						.method_9065(StatusEffects.field_5911)
						.method_9065(StatusEffects.field_5899)
						.method_9065(StatusEffects.field_5920)
						.method_9065(StatusEffects.field_5917)
						.method_9065(StatusEffects.field_5901)
						.method_9065(StatusEffects.field_5902)
						.method_9065(StatusEffects.field_5912)
						.method_9065(StatusEffects.field_5898)
						.method_9065(StatusEffects.field_5903)
						.method_9065(StatusEffects.field_5916)
						.method_9065(StatusEffects.field_5907)
						.method_9065(StatusEffects.field_5906)
						.method_9065(StatusEffects.field_5927)
						.method_9065(StatusEffects.field_5900)
						.method_9065(StatusEffects.field_5922)
						.method_9065(StatusEffects.field_5919)
				)
			)
			.method_694(consumer, "nether/all_effects");
	}
}
