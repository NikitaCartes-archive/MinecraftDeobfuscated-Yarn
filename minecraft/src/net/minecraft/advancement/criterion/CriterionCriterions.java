package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class CriterionCriterions {
	private static final Map<Identifier, Criterion<?>> TRIGGERS = Maps.<Identifier, Criterion<?>>newHashMap();
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
	public static final LocationArrivalCriterion LOCATION = register(new LocationArrivalCriterion(new Identifier("location")));
	public static final LocationArrivalCriterion SLEPT_IN_BED = register(new LocationArrivalCriterion(new Identifier("slept_in_bed")));
	public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = register(new CuredZombieVillagerCriterion());
	public static final VillagerTradeCriterion VILLAGER_TRADE = register(new VillagerTradeCriterion());
	public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = register(new ItemDurabilityChangedCriterion());
	public static final LevitationCriterion LEVITATION = register(new LevitationCriterion());
	public static final ChangedDimensionCriterion CHANGED_DIMENSION = register(new ChangedDimensionCriterion());
	public static final TickCriterion TICK = register(new TickCriterion());
	public static final TameAnimalCriterion TAME_ANIMAL = register(new TameAnimalCriterion());
	public static final PlacedBlockCriterion PLACED_BLOCK = register(new PlacedBlockCriterion());
	public static final ConsumeItemCriterion CONSUME_ITEM = register(new ConsumeItemCriterion());
	public static final EffectsChangedCriterion EFFECTS_CHANGED = register(new EffectsChangedCriterion());
	public static final UsedTotemCriterion USED_TOTEM = register(new UsedTotemCriterion());
	public static final NetherTravelCriterion NETHER_TRAVEL = register(new NetherTravelCriterion());
	public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = register(new FishingRodHookedCriterion());
	public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = register(new ChanneledLightningCriterion());
	public static final ShotCrossbowCriterion SHOT_CROSSBOW = register(new ShotCrossbowCriterion());
	public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = register(new KilledByCrossbowCriterion());

	private static <T extends Criterion<?>> T register(T criterion) {
		if (TRIGGERS.containsKey(criterion.getId())) {
			throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
		} else {
			TRIGGERS.put(criterion.getId(), criterion);
			return criterion;
		}
	}

	@Nullable
	public static <T extends CriterionConditions> Criterion<T> getById(Identifier identifier) {
		return (Criterion<T>)TRIGGERS.get(identifier);
	}

	public static Iterable<? extends Criterion<?>> getAllCriterions() {
		return TRIGGERS.values();
	}
}
