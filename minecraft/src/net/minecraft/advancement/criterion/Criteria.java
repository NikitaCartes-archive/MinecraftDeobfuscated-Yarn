package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class Criteria {
	private static final Map<Identifier, Criterion<?>> VALUES = Maps.<Identifier, Criterion<?>>newHashMap();
	public static final ImpossibleCriterion IMPOSSIBLE = register(new ImpossibleCriterion());
	public static final OnKilledCriterion PLAYER_KILLED_ENTITY = register(new OnKilledCriterion(new Identifier("player_killed_entity")));
	public static final OnKilledCriterion ENTITY_KILLED_PLAYER = register(new OnKilledCriterion(new Identifier("entity_killed_player")));
	public static final EnterBlockCriterion ENTER_BLOCK = register(new EnterBlockCriterion());
	public static final InventoryChangedCriterion INVENTORY_CHANGED = register(new InventoryChangedCriterion());
	public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = register(new RecipeUnlockedCriterion());
	public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = register(new PlayerHurtEntityCriterion());
	public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = register(new EntityHurtPlayerCriterion());
	public static final EnchantedItemCriterion ENCHANTED_ITEM = register(new EnchantedItemCriterion());
	public static final FilledBucketCriterion FILLED_BUCKET = register(new FilledBucketCriterion());
	public static final BrewedPotionCriterion BREWED_POTION = register(new BrewedPotionCriterion());
	public static final ConstructBeaconCriterion CONSTRUCT_BEACON = register(new ConstructBeaconCriterion());
	public static final UsedEnderEyeCriterion USED_ENDER_EYE = register(new UsedEnderEyeCriterion());
	public static final SummonedEntityCriterion SUMMONED_ENTITY = register(new SummonedEntityCriterion());
	public static final BredAnimalsCriterion BRED_ANIMALS = register(new BredAnimalsCriterion());
	public static final TickCriterion LOCATION = register(new TickCriterion(new Identifier("location")));
	public static final TickCriterion SLEPT_IN_BED = register(new TickCriterion(new Identifier("slept_in_bed")));
	public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = register(new CuredZombieVillagerCriterion());
	public static final VillagerTradeCriterion VILLAGER_TRADE = register(new VillagerTradeCriterion());
	public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = register(new ItemDurabilityChangedCriterion());
	public static final LevitationCriterion LEVITATION = register(new LevitationCriterion());
	public static final ChangedDimensionCriterion CHANGED_DIMENSION = register(new ChangedDimensionCriterion());
	public static final TickCriterion TICK = register(new TickCriterion(new Identifier("tick")));
	public static final TameAnimalCriterion TAME_ANIMAL = register(new TameAnimalCriterion());
	public static final PlacedBlockCriterion PLACED_BLOCK = register(new PlacedBlockCriterion());
	public static final ConsumeItemCriterion CONSUME_ITEM = register(new ConsumeItemCriterion());
	public static final EffectsChangedCriterion EFFECTS_CHANGED = register(new EffectsChangedCriterion());
	public static final UsedTotemCriterion USED_TOTEM = register(new UsedTotemCriterion());
	public static final TravelCriterion NETHER_TRAVEL = register(new TravelCriterion(new Identifier("nether_travel")));
	public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = register(new FishingRodHookedCriterion());
	public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = register(new ChanneledLightningCriterion());
	public static final ShotCrossbowCriterion SHOT_CROSSBOW = register(new ShotCrossbowCriterion());
	public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = register(new KilledByCrossbowCriterion());
	public static final TickCriterion HERO_OF_THE_VILLAGE = register(new TickCriterion(new Identifier("hero_of_the_village")));
	public static final TickCriterion VOLUNTARY_EXILE = register(new TickCriterion(new Identifier("voluntary_exile")));
	public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK = register(new SlideDownBlockCriterion());
	public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED = register(new BeeNestDestroyedCriterion());
	public static final TargetHitCriterion TARGET_HIT = register(new TargetHitCriterion());
	public static final ItemCriterion ITEM_USED_ON_BLOCK = register(new ItemCriterion(new Identifier("item_used_on_block")));
	public static final PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT = register(new PlayerGeneratesContainerLootCriterion());
	public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY = register(
		new ThrownItemPickedUpByEntityCriterion(new Identifier("thrown_item_picked_up_by_entity"))
	);
	public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_PLAYER = register(
		new ThrownItemPickedUpByEntityCriterion(new Identifier("thrown_item_picked_up_by_player"))
	);
	public static final PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY = register(new PlayerInteractedWithEntityCriterion());
	public static final StartedRidingCriterion STARTED_RIDING = register(new StartedRidingCriterion());
	public static final LightningStrikeCriterion LIGHTNING_STRIKE = register(new LightningStrikeCriterion());
	public static final UsingItemCriterion USING_ITEM = register(new UsingItemCriterion());
	public static final TravelCriterion FALL_FROM_HEIGHT = register(new TravelCriterion(new Identifier("fall_from_height")));
	public static final TravelCriterion RIDE_ENTITY_IN_LAVA = register(new TravelCriterion(new Identifier("ride_entity_in_lava")));
	public static final OnKilledCriterion KILL_MOB_NEAR_SCULK_CATALYST = register(new OnKilledCriterion(new Identifier("kill_mob_near_sculk_catalyst")));
	public static final ItemCriterion ALLAY_DROP_ITEM_ON_BLOCK = register(new ItemCriterion(new Identifier("allay_drop_item_on_block")));
	public static final TickCriterion AVOID_VIBRATION = register(new TickCriterion(new Identifier("avoid_vibration")));
	public static final TickCriterion VOTED = register(new TickCriterion(new Identifier("voted")));

	private static <T extends Criterion<?>> T register(T object) {
		if (VALUES.containsKey(object.getId())) {
			throw new IllegalArgumentException("Duplicate criterion id " + object.getId());
		} else {
			VALUES.put(object.getId(), object);
			return object;
		}
	}

	@Nullable
	public static <T extends CriterionConditions> Criterion<T> getById(Identifier id) {
		return (Criterion<T>)VALUES.get(id);
	}

	public static Iterable<? extends Criterion<?>> getCriteria() {
		return VALUES.values();
	}
}
