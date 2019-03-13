package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class Criterions {
	private static final Map<Identifier, Criterion<?>> VALUES = Maps.<Identifier, Criterion<?>>newHashMap();
	public static final ImpossibleCriterion IMPOSSIBLE = method_767(new ImpossibleCriterion());
	public static final OnKilledCriterion PLAYER_KILLED_ENTITY = method_767(new OnKilledCriterion(new Identifier("player_killed_entity")));
	public static final OnKilledCriterion ENTITY_KILLED_PLAYER = method_767(new OnKilledCriterion(new Identifier("entity_killed_player")));
	public static final EnterBlockCriterion ENTER_BLOCK = method_767(new EnterBlockCriterion());
	public static final InventoryChangedCriterion INVENTORY_CHANGED = method_767(new InventoryChangedCriterion());
	public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = method_767(new RecipeUnlockedCriterion());
	public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = method_767(new PlayerHurtEntityCriterion());
	public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = method_767(new EntityHurtPlayerCriterion());
	public static final EnchantedItemCriterion ENCHANTED_ITEM = method_767(new EnchantedItemCriterion());
	public static final FilledBucketCriterion FILLED_BUCKET = method_767(new FilledBucketCriterion());
	public static final BrewedPotionCriterion BREWED_POTION = method_767(new BrewedPotionCriterion());
	public static final ConstructBeaconCriterion CONSTRUCT_BEACON = method_767(new ConstructBeaconCriterion());
	public static final UsedEnderEyeCriterion USED_ENDER_EYE = method_767(new UsedEnderEyeCriterion());
	public static final SummonedEntityCriterion SUMMONED_ENTITY = method_767(new SummonedEntityCriterion());
	public static final BredAnimalsCriterion BRED_ANIMALS = method_767(new BredAnimalsCriterion());
	public static final LocationArrivalCriterion LOCATION = method_767(new LocationArrivalCriterion(new Identifier("location")));
	public static final LocationArrivalCriterion SLEPT_IN_BED = method_767(new LocationArrivalCriterion(new Identifier("slept_in_bed")));
	public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = method_767(new CuredZombieVillagerCriterion());
	public static final VillagerTradeCriterion VILLAGER_TRADE = method_767(new VillagerTradeCriterion());
	public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = method_767(new ItemDurabilityChangedCriterion());
	public static final LevitationCriterion LEVITATION = method_767(new LevitationCriterion());
	public static final ChangedDimensionCriterion CHANGED_DIMENSION = method_767(new ChangedDimensionCriterion());
	public static final TickCriterion TICK = method_767(new TickCriterion());
	public static final TameAnimalCriterion TAME_ANIMAL = method_767(new TameAnimalCriterion());
	public static final PlacedBlockCriterion PLACED_BLOCK = method_767(new PlacedBlockCriterion());
	public static final ConsumeItemCriterion CONSUME_ITEM = method_767(new ConsumeItemCriterion());
	public static final EffectsChangedCriterion EFFECTS_CHANGED = method_767(new EffectsChangedCriterion());
	public static final UsedTotemCriterion USED_TOTEM = method_767(new UsedTotemCriterion());
	public static final NetherTravelCriterion NETHER_TRAVEL = method_767(new NetherTravelCriterion());
	public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = method_767(new FishingRodHookedCriterion());
	public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = method_767(new ChanneledLightningCriterion());
	public static final ShotCrossbowCriterion SHOT_CROSSBOW = method_767(new ShotCrossbowCriterion());
	public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = method_767(new KilledByCrossbowCriterion());

	private static <T extends Criterion<?>> T method_767(T criterion) {
		if (VALUES.containsKey(criterion.getId())) {
			throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
		} else {
			VALUES.put(criterion.getId(), criterion);
			return criterion;
		}
	}

	@Nullable
	public static <T extends CriterionConditions> Criterion<T> method_765(Identifier identifier) {
		return (Criterion<T>)VALUES.get(identifier);
	}

	public static Iterable<? extends Criterion<?>> getAllCriterions() {
		return VALUES.values();
	}
}
