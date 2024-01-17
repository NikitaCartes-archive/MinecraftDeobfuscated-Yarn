package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Criteria {
	public static final Codec<Criterion<?>> CODEC = Registries.CRITERION.getCodec();
	public static final ImpossibleCriterion IMPOSSIBLE = register("impossible", new ImpossibleCriterion());
	public static final OnKilledCriterion PLAYER_KILLED_ENTITY = register("player_killed_entity", new OnKilledCriterion());
	public static final OnKilledCriterion ENTITY_KILLED_PLAYER = register("entity_killed_player", new OnKilledCriterion());
	public static final EnterBlockCriterion ENTER_BLOCK = register("enter_block", new EnterBlockCriterion());
	public static final InventoryChangedCriterion INVENTORY_CHANGED = register("inventory_changed", new InventoryChangedCriterion());
	public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = register("recipe_unlocked", new RecipeUnlockedCriterion());
	public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = register("player_hurt_entity", new PlayerHurtEntityCriterion());
	public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = register("entity_hurt_player", new EntityHurtPlayerCriterion());
	public static final EnchantedItemCriterion ENCHANTED_ITEM = register("enchanted_item", new EnchantedItemCriterion());
	public static final FilledBucketCriterion FILLED_BUCKET = register("filled_bucket", new FilledBucketCriterion());
	public static final BrewedPotionCriterion BREWED_POTION = register("brewed_potion", new BrewedPotionCriterion());
	public static final ConstructBeaconCriterion CONSTRUCT_BEACON = register("construct_beacon", new ConstructBeaconCriterion());
	public static final UsedEnderEyeCriterion USED_ENDER_EYE = register("used_ender_eye", new UsedEnderEyeCriterion());
	public static final SummonedEntityCriterion SUMMONED_ENTITY = register("summoned_entity", new SummonedEntityCriterion());
	public static final BredAnimalsCriterion BRED_ANIMALS = register("bred_animals", new BredAnimalsCriterion());
	public static final TickCriterion LOCATION = register("location", new TickCriterion());
	public static final TickCriterion SLEPT_IN_BED = register("slept_in_bed", new TickCriterion());
	public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = register("cured_zombie_villager", new CuredZombieVillagerCriterion());
	public static final VillagerTradeCriterion VILLAGER_TRADE = register("villager_trade", new VillagerTradeCriterion());
	public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = register("item_durability_changed", new ItemDurabilityChangedCriterion());
	public static final LevitationCriterion LEVITATION = register("levitation", new LevitationCriterion());
	public static final ChangedDimensionCriterion CHANGED_DIMENSION = register("changed_dimension", new ChangedDimensionCriterion());
	public static final TickCriterion TICK = register("tick", new TickCriterion());
	public static final TameAnimalCriterion TAME_ANIMAL = register("tame_animal", new TameAnimalCriterion());
	public static final ItemCriterion PLACED_BLOCK = register("placed_block", new ItemCriterion());
	public static final ConsumeItemCriterion CONSUME_ITEM = register("consume_item", new ConsumeItemCriterion());
	public static final EffectsChangedCriterion EFFECTS_CHANGED = register("effects_changed", new EffectsChangedCriterion());
	public static final UsedTotemCriterion USED_TOTEM = register("used_totem", new UsedTotemCriterion());
	public static final TravelCriterion NETHER_TRAVEL = register("nether_travel", new TravelCriterion());
	public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = register("fishing_rod_hooked", new FishingRodHookedCriterion());
	public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = register("channeled_lightning", new ChanneledLightningCriterion());
	public static final ShotCrossbowCriterion SHOT_CROSSBOW = register("shot_crossbow", new ShotCrossbowCriterion());
	public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = register("killed_by_crossbow", new KilledByCrossbowCriterion());
	public static final TickCriterion HERO_OF_THE_VILLAGE = register("hero_of_the_village", new TickCriterion());
	public static final TickCriterion VOLUNTARY_EXILE = register("voluntary_exile", new TickCriterion());
	public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK = register("slide_down_block", new SlideDownBlockCriterion());
	public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED = register("bee_nest_destroyed", new BeeNestDestroyedCriterion());
	public static final TargetHitCriterion TARGET_HIT = register("target_hit", new TargetHitCriterion());
	public static final ItemCriterion ITEM_USED_ON_BLOCK = register("item_used_on_block", new ItemCriterion());
	public static final DefaultBlockUseCriterion DEFAULT_BLOCK_USE = register("default_block_use", new DefaultBlockUseCriterion());
	public static final AnyBlockUseCriterion ANY_BLOCK_USE = register("any_block_use", new AnyBlockUseCriterion());
	public static final PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT = register(
		"player_generates_container_loot", new PlayerGeneratesContainerLootCriterion()
	);
	public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY = register(
		"thrown_item_picked_up_by_entity", new ThrownItemPickedUpByEntityCriterion()
	);
	public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_PLAYER = register(
		"thrown_item_picked_up_by_player", new ThrownItemPickedUpByEntityCriterion()
	);
	public static final PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY = register(
		"player_interacted_with_entity", new PlayerInteractedWithEntityCriterion()
	);
	public static final StartedRidingCriterion STARTED_RIDING = register("started_riding", new StartedRidingCriterion());
	public static final LightningStrikeCriterion LIGHTNING_STRIKE = register("lightning_strike", new LightningStrikeCriterion());
	public static final UsingItemCriterion USING_ITEM = register("using_item", new UsingItemCriterion());
	public static final TravelCriterion FALL_FROM_HEIGHT = register("fall_from_height", new TravelCriterion());
	public static final TravelCriterion RIDE_ENTITY_IN_LAVA = register("ride_entity_in_lava", new TravelCriterion());
	public static final OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST = register("kill_mob_near_sculk_catalyst", new OnKilledCriterion());
	public static final ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK = register("allay_drop_item_on_block", new ItemCriterion());
	public static final TickCriterion AVOID_VIBRATION = register("avoid_vibration", new TickCriterion());
	public static final RecipeCraftedCriterion RECIPE_CRAFTED = register("recipe_crafted", new RecipeCraftedCriterion());

	private static <T extends Criterion<?>> T register(String id, T criterion) {
		return Registry.register(Registries.CRITERION, id, criterion);
	}

	public static Criterion<?> getDefault(Registry<Criterion<?>> registry) {
		return IMPOSSIBLE;
	}
}
