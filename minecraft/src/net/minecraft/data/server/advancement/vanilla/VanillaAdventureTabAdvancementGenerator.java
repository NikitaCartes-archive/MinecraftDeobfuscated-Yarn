package net.minecraft.data.server.advancement.vanilla;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.FallAfterExplosionCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LightningStrikeCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.RecipeCraftedCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BulbBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.data.server.advancement.AdvancementTabGenerator;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.AllOfLootCondition;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.PlayerPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.predicate.item.JukeboxPlayablePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.gen.structure.StructureKeys;

public class VanillaAdventureTabAdvancementGenerator implements AdvancementTabGenerator {
	private static final int OVERWORLD_HEIGHT = 384;
	private static final int OVERWORLD_MAX_Y = 320;
	private static final int OVERWORLD_MIN_Y = -64;
	private static final int OVERWORLD_BEDROCK_LAYER_HEIGHT = 5;
	protected static final List<EntityType<?>> MONSTERS = Arrays.asList(
		EntityType.BLAZE,
		EntityType.BOGGED,
		EntityType.BREEZE,
		EntityType.CAVE_SPIDER,
		EntityType.CREEPER,
		EntityType.DROWNED,
		EntityType.ELDER_GUARDIAN,
		EntityType.ENDER_DRAGON,
		EntityType.ENDERMAN,
		EntityType.ENDERMITE,
		EntityType.EVOKER,
		EntityType.GHAST,
		EntityType.GUARDIAN,
		EntityType.HOGLIN,
		EntityType.HUSK,
		EntityType.MAGMA_CUBE,
		EntityType.PHANTOM,
		EntityType.PIGLIN,
		EntityType.PIGLIN_BRUTE,
		EntityType.PILLAGER,
		EntityType.RAVAGER,
		EntityType.SHULKER,
		EntityType.SILVERFISH,
		EntityType.SKELETON,
		EntityType.SLIME,
		EntityType.SPIDER,
		EntityType.STRAY,
		EntityType.VEX,
		EntityType.VINDICATOR,
		EntityType.WITCH,
		EntityType.WITHER_SKELETON,
		EntityType.WITHER,
		EntityType.ZOGLIN,
		EntityType.ZOMBIE_VILLAGER,
		EntityType.ZOMBIE,
		EntityType.ZOMBIFIED_PIGLIN
	);

	private static AdvancementCriterion<LightningStrikeCriterion.Conditions> createLightningStrike(NumberRange.IntRange range, Optional<EntityPredicate> entity) {
		return LightningStrikeCriterion.Conditions.create(
			Optional.of(
				EntityPredicate.Builder.create()
					.distance(DistancePredicate.absolute(NumberRange.DoubleRange.atMost(30.0)))
					.typeSpecific(LightningBoltPredicate.of(range))
					.build()
			),
			entity
		);
	}

	private static AdvancementCriterion<UsingItemCriterion.Conditions> createLookingAtEntityUsing(EntityType<?> entity, Item item) {
		return UsingItemCriterion.Conditions.create(
			EntityPredicate.Builder.create().typeSpecific(PlayerPredicate.Builder.create().lookingAt(EntityPredicate.Builder.create().type(entity)).build()),
			ItemPredicate.Builder.create().items(item)
		);
	}

	@Override
	public void accept(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
		AdvancementEntry advancementEntry = Advancement.Builder.create()
			.display(
				Items.MAP,
				Text.translatable("advancements.adventure.root.title"),
				Text.translatable("advancements.adventure.root.description"),
				Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
				AdvancementFrame.TASK,
				false,
				false,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity())
			.criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer())
			.build(exporter, "adventure/root");
		AdvancementEntry advancementEntry2 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.RED_BED,
				Text.translatable("advancements.adventure.sleep_in_bed.title"),
				Text.translatable("advancements.adventure.sleep_in_bed.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("slept_in_bed", TickCriterion.Conditions.createSleptInBed())
			.build(exporter, "adventure/sleep_in_bed");
		buildAdventuringTime(lookup, exporter, advancementEntry2, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
		AdvancementEntry advancementEntry3 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.EMERALD,
				Text.translatable("advancements.adventure.trade.title"),
				Text.translatable("advancements.adventure.trade.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("traded", VillagerTradeCriterion.Conditions.any())
			.build(exporter, "adventure/trade");
		Advancement.Builder.create()
			.parent(advancementEntry3)
			.display(
				Items.EMERALD,
				Text.translatable("advancements.adventure.trade_at_world_height.title"),
				Text.translatable("advancements.adventure.trade_at_world_height.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"trade_at_world_height",
				VillagerTradeCriterion.Conditions.create(
					EntityPredicate.Builder.create().location(LocationPredicate.Builder.createY(NumberRange.DoubleRange.atLeast(319.0)))
				)
			)
			.build(exporter, "adventure/trade_at_world_height");
		AdvancementEntry advancementEntry4 = createKillMobAdvancements(advancementEntry, exporter, MONSTERS);
		AdvancementEntry advancementEntry5 = Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.BOW,
				Text.translatable("advancements.adventure.shoot_arrow.title"),
				Text.translatable("advancements.adventure.shoot_arrow.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"shot_arrow",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.type(
							DamageSourcePredicate.Builder.create()
								.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
								.directEntity(EntityPredicate.Builder.create().type(EntityTypeTags.ARROWS))
						)
				)
			)
			.build(exporter, "adventure/shoot_arrow");
		AdvancementEntry advancementEntry6 = Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.TRIDENT,
				Text.translatable("advancements.adventure.throw_trident.title"),
				Text.translatable("advancements.adventure.throw_trident.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"shot_trident",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.type(
							DamageSourcePredicate.Builder.create()
								.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
								.directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT))
						)
				)
			)
			.build(exporter, "adventure/throw_trident");
		Advancement.Builder.create()
			.parent(advancementEntry6)
			.display(
				Items.TRIDENT,
				Text.translatable("advancements.adventure.very_very_frightening.title"),
				Text.translatable("advancements.adventure.very_very_frightening.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER)))
			.build(exporter, "adventure/very_very_frightening");
		Advancement.Builder.create()
			.parent(advancementEntry3)
			.display(
				Blocks.CARVED_PUMPKIN,
				Text.translatable("advancements.adventure.summon_iron_golem.title"),
				Text.translatable("advancements.adventure.summon_iron_golem.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM)))
			.build(exporter, "adventure/summon_iron_golem");
		Advancement.Builder.create()
			.parent(advancementEntry5)
			.display(
				Items.ARROW,
				Text.translatable("advancements.adventure.sniper_duel.title"),
				Text.translatable("advancements.adventure.sniper_duel.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"killed_skeleton",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.horizontal(NumberRange.DoubleRange.atLeast(50.0))),
					DamageSourcePredicate.Builder.create().tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
				)
			)
			.build(exporter, "adventure/sniper_duel");
		Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Items.TOTEM_OF_UNDYING,
				Text.translatable("advancements.adventure.totem_of_undying.title"),
				Text.translatable("advancements.adventure.totem_of_undying.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.TOTEM_OF_UNDYING))
			.build(exporter, "adventure/totem_of_undying");
		AdvancementEntry advancementEntry7 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.CROSSBOW,
				Text.translatable("advancements.adventure.ol_betsy.title"),
				Text.translatable("advancements.adventure.ol_betsy.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.CROSSBOW))
			.build(exporter, "adventure/ol_betsy");
		Advancement.Builder.create()
			.parent(advancementEntry7)
			.display(
				Items.CROSSBOW,
				Text.translatable("advancements.adventure.whos_the_pillager_now.title"),
				Text.translatable("advancements.adventure.whos_the_pillager_now.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PILLAGER)))
			.build(exporter, "adventure/whos_the_pillager_now");
		Advancement.Builder.create()
			.parent(advancementEntry7)
			.display(
				Items.CROSSBOW,
				Text.translatable("advancements.adventure.two_birds_one_arrow.title"),
				Text.translatable("advancements.adventure.two_birds_one_arrow.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(65))
			.criterion(
				"two_birds",
				KilledByCrossbowCriterion.Conditions.create(
					EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM)
				)
			)
			.build(exporter, "adventure/two_birds_one_arrow");
		Advancement.Builder.create()
			.parent(advancementEntry7)
			.display(
				Items.CROSSBOW,
				Text.translatable("advancements.adventure.arbalistic.title"),
				Text.translatable("advancements.adventure.arbalistic.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(85))
			.criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5)))
			.build(exporter, "adventure/arbalistic");
		RegistryWrapper.Impl<BannerPattern> impl = lookup.getWrapperOrThrow(RegistryKeys.BANNER_PATTERN);
		AdvancementEntry advancementEntry8 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Raid.getOminousBanner(impl),
				Text.translatable("advancements.adventure.voluntary_exile.title"),
				Text.translatable("advancements.adventure.voluntary_exile.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				true
			)
			.criterion(
				"voluntary_exile",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.ominousBannerOnHead(impl))
				)
			)
			.build(exporter, "adventure/voluntary_exile");
		Advancement.Builder.create()
			.parent(advancementEntry8)
			.display(
				Raid.getOminousBanner(impl),
				Text.translatable("advancements.adventure.hero_of_the_village.title"),
				Text.translatable("advancements.adventure.hero_of_the_village.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				true
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.criterion("hero_of_the_village", TickCriterion.Conditions.createHeroOfTheVillage())
			.build(exporter, "adventure/hero_of_the_village");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.HONEY_BLOCK.asItem(),
				Text.translatable("advancements.adventure.honey_block_slide.title"),
				Text.translatable("advancements.adventure.honey_block_slide.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("honey_block_slide", SlideDownBlockCriterion.Conditions.create(Blocks.HONEY_BLOCK))
			.build(exporter, "adventure/honey_block_slide");
		Advancement.Builder.create()
			.parent(advancementEntry5)
			.display(
				Blocks.TARGET.asItem(),
				Text.translatable("advancements.adventure.bullseye.title"),
				Text.translatable("advancements.adventure.bullseye.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"bullseye",
				TargetHitCriterion.Conditions.create(
					NumberRange.IntRange.exactly(15),
					Optional.of(
						EntityPredicate.contextPredicateFromEntityPredicate(
							EntityPredicate.Builder.create().distance(DistancePredicate.horizontal(NumberRange.DoubleRange.atLeast(30.0)))
						)
					)
				)
			)
			.build(exporter, "adventure/bullseye");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.LEATHER_BOOTS,
				Text.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"),
				Text.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("walk_on_powder_snow_with_leather_boots", TickCriterion.Conditions.createLocation(Blocks.POWDER_SNOW, Items.LEATHER_BOOTS))
			.build(exporter, "adventure/walk_on_powder_snow_with_leather_boots");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.LIGHTNING_ROD,
				Text.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.title"),
				Text.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"lightning_rod_with_villager_no_fire",
				createLightningStrike(NumberRange.IntRange.exactly(0), Optional.of(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build()))
			)
			.build(exporter, "adventure/lightning_rod_with_villager_no_fire");
		AdvancementEntry advancementEntry9 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.SPYGLASS,
				Text.translatable("advancements.adventure.spyglass_at_parrot.title"),
				Text.translatable("advancements.adventure.spyglass_at_parrot.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("spyglass_at_parrot", createLookingAtEntityUsing(EntityType.PARROT, Items.SPYGLASS))
			.build(exporter, "adventure/spyglass_at_parrot");
		AdvancementEntry advancementEntry10 = Advancement.Builder.create()
			.parent(advancementEntry9)
			.display(
				Items.SPYGLASS,
				Text.translatable("advancements.adventure.spyglass_at_ghast.title"),
				Text.translatable("advancements.adventure.spyglass_at_ghast.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("spyglass_at_ghast", createLookingAtEntityUsing(EntityType.GHAST, Items.SPYGLASS))
			.build(exporter, "adventure/spyglass_at_ghast");
		Advancement.Builder.create()
			.parent(advancementEntry2)
			.display(
				Items.JUKEBOX,
				Text.translatable("advancements.adventure.play_jukebox_in_meadows.title"),
				Text.translatable("advancements.adventure.play_jukebox_in_meadows.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"play_jukebox_in_meadows",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.biome(RegistryEntryList.of(lookup.getWrapperOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.MEADOW)))
						.block(BlockPredicate.Builder.create().blocks(Blocks.JUKEBOX)),
					ItemPredicate.Builder.create().subPredicate(ItemSubPredicateTypes.JUKEBOX_PLAYABLE, JukeboxPlayablePredicate.empty())
				)
			)
			.build(exporter, "adventure/play_jukebox_in_meadows");
		Advancement.Builder.create()
			.parent(advancementEntry10)
			.display(
				Items.SPYGLASS,
				Text.translatable("advancements.adventure.spyglass_at_dragon.title"),
				Text.translatable("advancements.adventure.spyglass_at_dragon.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("spyglass_at_dragon", createLookingAtEntityUsing(EntityType.ENDER_DRAGON, Items.SPYGLASS))
			.build(exporter, "adventure/spyglass_at_dragon");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.WATER_BUCKET,
				Text.translatable("advancements.adventure.fall_from_world_height.title"),
				Text.translatable("advancements.adventure.fall_from_world_height.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"fall_from_world_height",
				TravelCriterion.Conditions.fallFromHeight(
					EntityPredicate.Builder.create().location(LocationPredicate.Builder.createY(NumberRange.DoubleRange.atMost(-59.0))),
					DistancePredicate.y(NumberRange.DoubleRange.atLeast(379.0)),
					LocationPredicate.Builder.createY(NumberRange.DoubleRange.atLeast(319.0))
				)
			)
			.build(exporter, "adventure/fall_from_world_height");
		Advancement.Builder.create()
			.parent(advancementEntry4)
			.display(
				Blocks.SCULK_CATALYST,
				Text.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.title"),
				Text.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.criterion("kill_mob_near_sculk_catalyst", OnKilledCriterion.Conditions.createKillMobNearSculkCatalyst())
			.build(exporter, "adventure/kill_mob_near_sculk_catalyst");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.SCULK_SENSOR,
				Text.translatable("advancements.adventure.avoid_vibration.title"),
				Text.translatable("advancements.adventure.avoid_vibration.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("avoid_vibration", TickCriterion.Conditions.createAvoidVibration())
			.build(exporter, "adventure/avoid_vibration");
		AdvancementEntry advancementEntry11 = requireSalvagedSherd(Advancement.Builder.create())
			.parent(advancementEntry)
			.display(
				Items.BRUSH,
				Text.translatable("advancements.adventure.salvage_sherd.title"),
				Text.translatable("advancements.adventure.salvage_sherd.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "adventure/salvage_sherd");
		Advancement.Builder.create()
			.parent(advancementEntry11)
			.display(
				DecoratedPotBlockEntity.getStackWith(
					new Sherds(Optional.empty(), Optional.of(Items.HEART_POTTERY_SHERD), Optional.empty(), Optional.of(Items.EXPLORER_POTTERY_SHERD))
				),
				Text.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.title"),
				Text.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"pot_crafted_using_only_sherds",
				RecipeCraftedCriterion.Conditions.create(
					Identifier.ofVanilla("decorated_pot"),
					List.of(
						ItemPredicate.Builder.create().tag(ItemTags.DECORATED_POT_SHERDS),
						ItemPredicate.Builder.create().tag(ItemTags.DECORATED_POT_SHERDS),
						ItemPredicate.Builder.create().tag(ItemTags.DECORATED_POT_SHERDS),
						ItemPredicate.Builder.create().tag(ItemTags.DECORATED_POT_SHERDS)
					)
				)
			)
			.build(exporter, "adventure/craft_decorated_pot_using_only_sherds");
		AdvancementEntry advancementEntry12 = requireTrimmedArmor(Advancement.Builder.create())
			.parent(advancementEntry)
			.display(
				new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
				Text.translatable("advancements.adventure.trim_with_any_armor_pattern.title"),
				Text.translatable("advancements.adventure.trim_with_any_armor_pattern.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.build(exporter, "adventure/trim_with_any_armor_pattern");
		requireAllExclusiveTrimmedArmor(Advancement.Builder.create())
			.parent(advancementEntry12)
			.display(
				new ItemStack(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE),
				Text.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.title"),
				Text.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(150))
			.build(exporter, "adventure/trim_with_all_exclusive_armor_patterns");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.CHISELED_BOOKSHELF,
				Text.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.title"),
				Text.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.criterion("chiseled_bookshelf", requirePlacedBlockReadByComparator(Blocks.CHISELED_BOOKSHELF))
			.criterion("comparator", requirePlacedComparatorReadingBlock(Blocks.CHISELED_BOOKSHELF))
			.build(exporter, "adventure/read_power_of_chiseled_bookshelf");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.ARMADILLO_SCUTE,
				Text.translatable("advancements.adventure.brush_armadillo.title"),
				Text.translatable("advancements.adventure.brush_armadillo.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"brush_armadillo",
				PlayerInteractedWithEntityCriterion.Conditions.create(
					ItemPredicate.Builder.create().items(Items.BRUSH),
					Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().type(EntityType.ARMADILLO)))
				)
			)
			.build(exporter, "adventure/brush_armadillo");
		AdvancementEntry advancementEntry13 = Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Blocks.CHISELED_TUFF,
				Text.translatable("advancements.adventure.minecraft_trials_edition.title"),
				Text.translatable("advancements.adventure.minecraft_trials_edition.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"minecraft_trials_edition",
				TickCriterion.Conditions.createLocation(
					LocationPredicate.Builder.createStructure(lookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(StructureKeys.TRIAL_CHAMBERS))
				)
			)
			.build(exporter, "adventure/minecraft_trials_edition");
		Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.COPPER_BULB,
				Text.translatable("advancements.adventure.lighten_up.title"),
				Text.translatable("advancements.adventure.lighten_up.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"lighten_up",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(
							BlockPredicate.Builder.create()
								.blocks(
									Blocks.OXIDIZED_COPPER_BULB,
									Blocks.WEATHERED_COPPER_BULB,
									Blocks.EXPOSED_COPPER_BULB,
									Blocks.WAXED_OXIDIZED_COPPER_BULB,
									Blocks.WAXED_WEATHERED_COPPER_BULB,
									Blocks.WAXED_EXPOSED_COPPER_BULB
								)
								.state(StatePredicate.Builder.create().exactMatch(BulbBlock.LIT, true))
						),
					ItemPredicate.Builder.create().items(VanillaHusbandryTabAdvancementGenerator.AXE_ITEMS)
				)
			)
			.build(exporter, "adventure/lighten_up");
		AdvancementEntry advancementEntry14 = Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.TRIAL_KEY,
				Text.translatable("advancements.adventure.under_lock_and_key.title"),
				Text.translatable("advancements.adventure.under_lock_and_key.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"under_lock_and_key",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(BlockPredicate.Builder.create().blocks(Blocks.VAULT).state(StatePredicate.Builder.create().exactMatch(VaultBlock.OMINOUS, false))),
					ItemPredicate.Builder.create().items(Items.TRIAL_KEY)
				)
			)
			.build(exporter, "adventure/under_lock_and_key");
		Advancement.Builder.create()
			.parent(advancementEntry14)
			.display(
				Items.OMINOUS_TRIAL_KEY,
				Text.translatable("advancements.adventure.revaulting.title"),
				Text.translatable("advancements.adventure.revaulting.description"),
				null,
				AdvancementFrame.GOAL,
				true,
				true,
				false
			)
			.criterion(
				"revaulting",
				ItemCriterion.Conditions.createItemUsedOnBlock(
					LocationPredicate.Builder.create()
						.block(BlockPredicate.Builder.create().blocks(Blocks.VAULT).state(StatePredicate.Builder.create().exactMatch(VaultBlock.OMINOUS, true))),
					ItemPredicate.Builder.create().items(Items.OMINOUS_TRIAL_KEY)
				)
			)
			.build(exporter, "adventure/revaulting");
		Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.WIND_CHARGE,
				Text.translatable("advancements.adventure.blowback.title"),
				Text.translatable("advancements.adventure.blowback.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(40))
			.criterion(
				"blowback",
				OnKilledCriterion.Conditions.createPlayerKilledEntity(
					EntityPredicate.Builder.create().type(EntityType.BREEZE),
					DamageSourcePredicate.Builder.create()
						.tag(TagPredicate.expected(DamageTypeTags.IS_PROJECTILE))
						.directEntity(EntityPredicate.Builder.create().type(EntityType.BREEZE_WIND_CHARGE))
				)
			)
			.build(exporter, "adventure/blowback");
		Advancement.Builder.create()
			.parent(advancementEntry)
			.display(
				Items.CRAFTER,
				Text.translatable("advancements.adventure.crafters_crafting_crafters.title"),
				Text.translatable("advancements.adventure.crafters_crafting_crafters.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion("crafter_crafted_crafter", RecipeCraftedCriterion.Conditions.createCrafterRecipeCrafted(Identifier.ofVanilla("crafter")))
			.build(exporter, "adventure/crafters_crafting_crafters");
		Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.WIND_CHARGE,
				Text.translatable("advancements.adventure.who_needs_rockets.title"),
				Text.translatable("advancements.adventure.who_needs_rockets.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criterion(
				"who_needs_rockets",
				FallAfterExplosionCriterion.Conditions.create(
					DistancePredicate.y(NumberRange.DoubleRange.atLeast(7.0)), EntityPredicate.Builder.create().type(EntityType.WIND_CHARGE)
				)
			)
			.build(exporter, "adventure/who_needs_rockets");
		Advancement.Builder.create()
			.parent(advancementEntry13)
			.display(
				Items.MACE,
				Text.translatable("advancements.adventure.overoverkill.title"),
				Text.translatable("advancements.adventure.overoverkill.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(50))
			.criterion(
				"overoverkill",
				PlayerHurtEntityCriterion.Conditions.create(
					DamagePredicate.Builder.create()
						.dealt(NumberRange.DoubleRange.atLeast(100.0))
						.type(
							DamageSourcePredicate.Builder.create()
								.tag(TagPredicate.expected(DamageTypeTags.IS_PLAYER_ATTACK))
								.directEntity(
									EntityPredicate.Builder.create()
										.type(EntityType.PLAYER)
										.equipment(EntityEquipmentPredicate.Builder.create().mainhand(ItemPredicate.Builder.create().items(Items.MACE)))
								)
						)
				)
			)
			.build(exporter, "adventure/overoverkill");
	}

	public static AdvancementEntry createKillMobAdvancements(AdvancementEntry parent, Consumer<AdvancementEntry> exporter, List<EntityType<?>> monsters) {
		AdvancementEntry advancementEntry = requireListedMobsKilled(Advancement.Builder.create(), monsters)
			.parent(parent)
			.display(
				Items.IRON_SWORD,
				Text.translatable("advancements.adventure.kill_a_mob.title"),
				Text.translatable("advancements.adventure.kill_a_mob.description"),
				null,
				AdvancementFrame.TASK,
				true,
				true,
				false
			)
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.build(exporter, "adventure/kill_a_mob");
		requireListedMobsKilled(Advancement.Builder.create(), monsters)
			.parent(advancementEntry)
			.display(
				Items.DIAMOND_SWORD,
				Text.translatable("advancements.adventure.kill_all_mobs.title"),
				Text.translatable("advancements.adventure.kill_all_mobs.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(100))
			.build(exporter, "adventure/kill_all_mobs");
		return advancementEntry;
	}

	private static AdvancementCriterion<ItemCriterion.Conditions> requirePlacedBlockReadByComparator(Block block) {
		LootCondition.Builder[] builders = (LootCondition.Builder[])ComparatorBlock.FACING.getValues().stream().map(facing -> {
			StatePredicate.Builder builder = StatePredicate.Builder.create().exactMatch(ComparatorBlock.FACING, facing);
			BlockPredicate.Builder builder2 = BlockPredicate.Builder.create().blocks(Blocks.COMPARATOR).state(builder);
			return LocationCheckLootCondition.builder(LocationPredicate.Builder.create().block(builder2), new BlockPos(facing.getOpposite().getVector()));
		}).toArray(LootCondition.Builder[]::new);
		return ItemCriterion.Conditions.createPlacedBlock(BlockStatePropertyLootCondition.builder(block), AnyOfLootCondition.builder(builders));
	}

	private static AdvancementCriterion<ItemCriterion.Conditions> requirePlacedComparatorReadingBlock(Block block) {
		LootCondition.Builder[] builders = (LootCondition.Builder[])ComparatorBlock.FACING
			.getValues()
			.stream()
			.map(
				facing -> {
					StatePredicate.Builder builder = StatePredicate.Builder.create().exactMatch(ComparatorBlock.FACING, facing);
					BlockStatePropertyLootCondition.Builder builder2 = new BlockStatePropertyLootCondition.Builder(Blocks.COMPARATOR).properties(builder);
					LootCondition.Builder builder3 = LocationCheckLootCondition.builder(
						LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(block)), new BlockPos(facing.getVector())
					);
					return AllOfLootCondition.builder(builder2, builder3);
				}
			)
			.toArray(LootCondition.Builder[]::new);
		return ItemCriterion.Conditions.createPlacedBlock(AnyOfLootCondition.builder(builders));
	}

	private static Advancement.Builder requireAllExclusiveTrimmedArmor(Advancement.Builder builder) {
		builder.criteriaMerger(AdvancementRequirements.CriterionMerger.AND);
		Set<Item> set = Set.of(
			Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
			Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE
		);
		VanillaRecipeProvider.streamSmithingTemplates()
			.filter(template -> set.contains(template.template()))
			.forEach(templatex -> builder.criterion("armor_trimmed_" + templatex.id(), RecipeCraftedCriterion.Conditions.create(templatex.id())));
		return builder;
	}

	private static Advancement.Builder requireTrimmedArmor(Advancement.Builder builder) {
		builder.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		VanillaRecipeProvider.streamSmithingTemplates()
			.map(VanillaRecipeProvider.SmithingTemplate::id)
			.forEach(template -> builder.criterion("armor_trimmed_" + template, RecipeCraftedCriterion.Conditions.create(template)));
		return builder;
	}

	private static Advancement.Builder requireSalvagedSherd(Advancement.Builder builder) {
		List<Pair<String, AdvancementCriterion<PlayerGeneratesContainerLootCriterion.Conditions>>> list = List.of(
			Pair.of("desert_pyramid", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.DESERT_PYRAMID_ARCHAEOLOGY)),
			Pair.of("desert_well", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.DESERT_WELL_ARCHAEOLOGY)),
			Pair.of("ocean_ruin_cold", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)),
			Pair.of("ocean_ruin_warm", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY)),
			Pair.of("trail_ruins_rare", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.TRAIL_RUINS_RARE_ARCHAEOLOGY)),
			Pair.of("trail_ruins_common", PlayerGeneratesContainerLootCriterion.Conditions.create(LootTables.TRAIL_RUINS_COMMON_ARCHAEOLOGY))
		);
		list.forEach(pair -> builder.criterion((String)pair.getFirst(), (AdvancementCriterion<?>)pair.getSecond()));
		String string = "has_sherd";
		builder.criterion("has_sherd", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(ItemTags.DECORATED_POT_SHERDS)));
		builder.requirements(new AdvancementRequirements(List.of(list.stream().map(Pair::getFirst).toList(), List.of("has_sherd"))));
		return builder;
	}

	protected static void buildAdventuringTime(
		RegistryWrapper.WrapperLookup registryLookup,
		Consumer<AdvancementEntry> exporter,
		AdvancementEntry parent,
		MultiNoiseBiomeSourceParameterList.Preset biomeSourceListPreset
	) {
		requireListedBiomesVisited(Advancement.Builder.create(), registryLookup, biomeSourceListPreset.biomeStream().toList())
			.parent(parent)
			.display(
				Items.DIAMOND_BOOTS,
				Text.translatable("advancements.adventure.adventuring_time.title"),
				Text.translatable("advancements.adventure.adventuring_time.description"),
				null,
				AdvancementFrame.CHALLENGE,
				true,
				true,
				false
			)
			.rewards(AdvancementRewards.Builder.experience(500))
			.build(exporter, "adventure/adventuring_time");
	}

	private static Advancement.Builder requireListedMobsKilled(Advancement.Builder builder, List<EntityType<?>> entityTypes) {
		entityTypes.forEach(
			type -> builder.criterion(
					Registries.ENTITY_TYPE.getId(type).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(type))
				)
		);
		return builder;
	}

	protected static Advancement.Builder requireListedBiomesVisited(
		Advancement.Builder builder, RegistryWrapper.WrapperLookup registryLookup, List<RegistryKey<Biome>> biomes
	) {
		RegistryEntryLookup<Biome> registryEntryLookup = registryLookup.getWrapperOrThrow(RegistryKeys.BIOME);

		for (RegistryKey<Biome> registryKey : biomes) {
			builder.criterion(
				registryKey.getValue().toString(),
				TickCriterion.Conditions.createLocation(LocationPredicate.Builder.createBiome(registryEntryLookup.getOrThrow(registryKey)))
			);
		}

		return builder;
	}
}
