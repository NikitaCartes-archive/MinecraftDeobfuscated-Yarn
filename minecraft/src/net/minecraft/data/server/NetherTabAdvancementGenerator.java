package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.feature.StructureFeature;

public class NetherTabAdvancementGenerator implements Consumer<Consumer<Advancement>> {
	private static final List<RegistryKey<Biome>> NETHER_BIOMES = ImmutableList.of(
		BiomeKeys.NETHER_WASTES, BiomeKeys.SOUL_SAND_VALLEY, BiomeKeys.WARPED_FOREST, BiomeKeys.CRIMSON_FOREST, BiomeKeys.BASALT_DELTAS
	);
	private static final EntityPredicate.Extended field_25712 = EntityPredicate.Extended.create(
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().head(ItemPredicate.Builder.create().item(Items.GOLDEN_HELMET).build()).build())
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().chest(ItemPredicate.Builder.create().item(Items.GOLDEN_CHESTPLATE).build()).build())
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().legs(ItemPredicate.Builder.create().item(Items.GOLDEN_LEGGINGS).build()).build())
			)
			.invert()
			.build(),
		EntityPropertiesLootCondition.builder(
				LootContext.EntityTarget.THIS,
				EntityPredicate.Builder.create()
					.equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().item(Items.GOLDEN_BOOTS).build()).build())
			)
			.invert()
			.build()
	);

	public void accept(Consumer<Advancement> consumer) {
		Advancement advancement = Advancement.Task.create()
			.display(
				Blocks.RED_NETHER_BRICKS,
				new TranslatableText("advancements.nether.root.title"),
				new TranslatableText("advancements.nether.root.description"),
				new Identifier("textures/gui/advancements/backgrounds/nether.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(World.NETHER))
			.build(consumer, "nether/root");
		Advancement advancement2 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.FIRE_CHARGE,
				new TranslatableText("advancements.nether.return_to_sender.title"),
				new TranslatableText("advancements.nether.return_to_sender.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.GHAST),
					DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.FIREBALL))
				)
			)
			.build(consumer, "nether/return_to_sender");
		Advancement advancement3 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Blocks.NETHER_BRICKS,
				new TranslatableText("advancements.nether.find_fortress.title"),
				new TranslatableText("advancements.nether.find_fortress.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("fortress", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(StructureFeature.FORTRESS)))
			.build(consumer, "nether/find_fortress");
		Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.MAP,
				new TranslatableText("advancements.nether.fast_travel.title"),
				new TranslatableText("advancements.nether.fast_travel.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("travelled", NetherTravelCriterion.Conditions.distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(7000.0F))))
			.build(consumer, "nether/fast_travel");
		Advancement.Task.create()
			.parent(advancement2)
			.display(
				Items.GHAST_TEAR,
				new TranslatableText("advancements.nether.uneasy_alliance.title"),
				new TranslatableText("advancements.nether.uneasy_alliance.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"killed_ghast",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.dimension(World.OVERWORLD))
				)
			)
			.build(consumer, "nether/uneasy_alliance");
		Advancement advancement4 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Blocks.WITHER_SKELETON_SKULL,
				new TranslatableText("advancements.nether.get_wither_skull.title"),
				new TranslatableText("advancements.nether.get_wither_skull.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.WITHER_SKELETON_SKULL))
			.build(consumer, "nether/get_wither_skull");
		Advancement advancement5 = Advancement.Task.create()
			.parent(advancement4)
			.display(
				Items.NETHER_STAR,
				new TranslatableText("advancements.nether.summon_wither.title"),
				new TranslatableText("advancements.nether.summon_wither.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.WITHER)))
			.build(consumer, "nether/summon_wither");
		Advancement advancement6 = Advancement.Task.create()
			.parent(advancement3)
			.display(
				Items.BLAZE_ROD,
				new TranslatableText("advancements.nether.obtain_blaze_rod.title"),
				new TranslatableText("advancements.nether.obtain_blaze_rod.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.BLAZE_ROD))
			.build(consumer, "nether/obtain_blaze_rod");
		Advancement advancement7 = Advancement.Task.create()
			.parent(advancement5)
			.display(
				Blocks.BEACON,
				new TranslatableText("advancements.nether.create_beacon.title"),
				new TranslatableText("advancements.nether.create_beacon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1)))
			.build(consumer, "nether/create_beacon");
		Advancement.Task.create()
			.parent(advancement7)
			.display(
				Blocks.BEACON,
				new TranslatableText("advancements.nether.create_full_beacon.title"),
				new TranslatableText("advancements.nether.create_full_beacon.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.exactly(4)))
			.build(consumer, "nether/create_full_beacon");
		Advancement advancement8 = Advancement.Task.create()
			.parent(advancement6)
			.display(
				Items.POTION,
				new TranslatableText("advancements.nether.brew_potion.title"),
				new TranslatableText("advancements.nether.brew_potion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("potion", BrewedPotionCriterion.Conditions.any())
			.build(consumer, "nether/brew_potion");
		Advancement advancement9 = Advancement.Task.create()
			.parent(advancement8)
			.display(
				Items.MILK_BUCKET,
				new TranslatableText("advancements.nether.all_potions.title"),
				new TranslatableText("advancements.nether.all_potions.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.create()
						.withEffect(StatusEffects.SPEED)
						.withEffect(StatusEffects.SLOWNESS)
						.withEffect(StatusEffects.STRENGTH)
						.withEffect(StatusEffects.JUMP_BOOST)
						.withEffect(StatusEffects.REGENERATION)
						.withEffect(StatusEffects.FIRE_RESISTANCE)
						.withEffect(StatusEffects.WATER_BREATHING)
						.withEffect(StatusEffects.INVISIBILITY)
						.withEffect(StatusEffects.NIGHT_VISION)
						.withEffect(StatusEffects.WEAKNESS)
						.withEffect(StatusEffects.POISON)
						.withEffect(StatusEffects.SLOW_FALLING)
						.withEffect(StatusEffects.RESISTANCE)
				)
			)
			.build(consumer, "nether/all_potions");
		Advancement.Task.create()
			.parent(advancement9)
			.display(
				Items.BUCKET,
				new TranslatableText("advancements.nether.all_effects.title"),
				new TranslatableText("advancements.nether.all_effects.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(1000))
			.criterion(
				"all_effects",
				EffectsChangedCriterion.Conditions.create(
					EntityEffectPredicate.create()
						.withEffect(StatusEffects.SPEED)
						.withEffect(StatusEffects.SLOWNESS)
						.withEffect(StatusEffects.STRENGTH)
						.withEffect(StatusEffects.JUMP_BOOST)
						.withEffect(StatusEffects.REGENERATION)
						.withEffect(StatusEffects.FIRE_RESISTANCE)
						.withEffect(StatusEffects.WATER_BREATHING)
						.withEffect(StatusEffects.INVISIBILITY)
						.withEffect(StatusEffects.NIGHT_VISION)
						.withEffect(StatusEffects.WEAKNESS)
						.withEffect(StatusEffects.POISON)
						.withEffect(StatusEffects.WITHER)
						.withEffect(StatusEffects.HASTE)
						.withEffect(StatusEffects.MINING_FATIGUE)
						.withEffect(StatusEffects.LEVITATION)
						.withEffect(StatusEffects.GLOWING)
						.withEffect(StatusEffects.ABSORPTION)
						.withEffect(StatusEffects.HUNGER)
						.withEffect(StatusEffects.NAUSEA)
						.withEffect(StatusEffects.RESISTANCE)
						.withEffect(StatusEffects.SLOW_FALLING)
						.withEffect(StatusEffects.CONDUIT_POWER)
						.withEffect(StatusEffects.DOLPHINS_GRACE)
						.withEffect(StatusEffects.BLINDNESS)
						.withEffect(StatusEffects.BAD_OMEN)
						.withEffect(StatusEffects.HERO_OF_THE_VILLAGE)
				)
			)
			.build(consumer, "nether/all_effects");
		Advancement advancement10 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.ANCIENT_DEBRIS,
				new TranslatableText("advancements.nether.obtain_ancient_debris.title"),
				new TranslatableText("advancements.nether.obtain_ancient_debris.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("ancient_debris", InventoryChangedCriterion.Conditions.items(Items.ANCIENT_DEBRIS))
			.build(consumer, "nether/obtain_ancient_debris");
		Advancement.Task.create()
			.parent(advancement10)
			.display(
				Items.NETHERITE_CHESTPLATE,
				new TranslatableText("advancements.nether.netherite_armor.title"),
				new TranslatableText("advancements.nether.netherite_armor.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion(
				"netherite_armor",
				InventoryChangedCriterion.Conditions.items(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)
			)
			.build(consumer, "nether/netherite_armor");
		Advancement.Task.create()
			.parent(advancement10)
			.display(
				Items.LODESTONE,
				new TranslatableText("advancements.nether.use_lodestone.title"),
				new TranslatableText("advancements.nether.use_lodestone.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"use_lodestone",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().block(Blocks.LODESTONE).build()),
					ItemPredicate.Builder.create().item(Items.COMPASS)
				)
			)
			.build(consumer, "nether/use_lodestone");
		Advancement advancement11 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.CRYING_OBSIDIAN,
				new TranslatableText("advancements.nether.obtain_crying_obsidian.title"),
				new TranslatableText("advancements.nether.obtain_crying_obsidian.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("crying_obsidian", InventoryChangedCriterion.Conditions.items(Items.CRYING_OBSIDIAN))
			.build(consumer, "nether/obtain_crying_obsidian");
		Advancement.Task.create()
			.parent(advancement11)
			.display(
				Items.RESPAWN_ANCHOR,
				new TranslatableText("advancements.nether.charge_respawn_anchor.title"),
				new TranslatableText("advancements.nether.charge_respawn_anchor.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"charge_respawn_anchor",
				ItemUsedOnBlockCriterion.Conditions.create(
					LocationPredicate.Builder.create()
						.block(
							BlockPredicate.Builder.create()
								.block(Blocks.RESPAWN_ANCHOR)
								.state(StatePredicate.Builder.create().exactMatch(RespawnAnchorBlock.CHARGES, 4).build())
								.build()
						),
					ItemPredicate.Builder.create().item(Blocks.GLOWSTONE)
				)
			)
			.build(consumer, "nether/charge_respawn_anchor");
		Advancement advancement12 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.WARPED_FUNGUS_ON_A_STICK,
				new TranslatableText("advancements.nether.ride_strider.title"),
				new TranslatableText("advancements.nether.ride_strider.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"used_warped_fungus_on_a_stick",
				ItemDurabilityChangedCriterion.Conditions.create(
					EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().vehicle(EntityPredicate.Builder.create().type(EntityType.STRIDER).build()).build()),
					ItemPredicate.Builder.create().item(Items.WARPED_FUNGUS_ON_A_STICK).build(),
					NumberRange.IntRange.ANY
				)
			)
			.build(consumer, "nether/ride_strider");
		AdventureTabAdvancementGenerator.requireListedBiomesVisited(Advancement.Task.create(), NETHER_BIOMES)
			.parent(advancement12)
			.display(
				Items.NETHERITE_BOOTS,
				new TranslatableText("advancements.nether.explore_nether.title"),
				new TranslatableText("advancements.nether.explore_nether.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(500))
			.build(consumer, "nether/explore_nether");
		Advancement advancement13 = Advancement.Task.create()
			.parent(advancement)
			.display(
				Items.POLISHED_BLACKSTONE_BRICKS,
				new TranslatableText("advancements.nether.find_bastion.title"),
				new TranslatableText("advancements.nether.find_bastion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("bastion", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(StructureFeature.BASTION_REMNANT)))
			.build(consumer, "nether/find_bastion");
		Advancement.Task.create()
			.parent(advancement13)
			.display(
				Blocks.CHEST,
				new TranslatableText("advancements.nether.loot_bastion.title"),
				new TranslatableText("advancements.nether.loot_bastion.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(CriterionMerger.OR)
			.criterion("loot_bastion_other", PlayerGeneratesContainerLootCriterion.Conditions.create(new Identifier("minecraft:chests/bastion_other")))
			.criterion("loot_bastion_treasure", PlayerGeneratesContainerLootCriterion.Conditions.create(new Identifier("minecraft:chests/bastion_treasure")))
			.criterion("loot_bastion_hoglin_stable", PlayerGeneratesContainerLootCriterion.Conditions.create(new Identifier("minecraft:chests/bastion_hoglin_stable")))
			.criterion("loot_bastion_bridge", PlayerGeneratesContainerLootCriterion.Conditions.create(new Identifier("minecraft:chests/bastion_bridge")))
			.build(consumer, "nether/loot_bastion");
		Advancement.Task.create()
			.parent(advancement)
			.criteriaMerger(CriterionMerger.OR)
			.display(
				Items.GOLD_INGOT,
				new TranslatableText("advancements.nether.distract_piglin.title"),
				new TranslatableText("advancements.nether.distract_piglin.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"distract_piglin",
				ThrownItemPickedUpByEntityCriterion.Conditions.create(
					field_25712,
					ItemPredicate.Builder.create().tag(ItemTags.PIGLIN_LOVED),
					EntityPredicate.Extended.ofLegacy(
						EntityPredicate.Builder.create().type(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.create().method_29935(false).build()).build()
					)
				)
			)
			.criterion(
				"distract_piglin_directly",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					field_25712,
					ItemPredicate.Builder.create().item(PiglinBrain.BARTERING_ITEM),
					EntityPredicate.Extended.ofLegacy(
						EntityPredicate.Builder.create().type(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.create().method_29935(false).build()).build()
					)
				)
			)
			.build(consumer, "nether/distract_piglin");
	}
}
