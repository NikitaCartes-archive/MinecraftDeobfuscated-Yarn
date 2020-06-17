/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.PlayerGeneratesContainerLootCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.PlayerInteractedWithEntityCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.ThrownItemPickedUpByEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.UsedEnderEyeCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Criteria {
    private static final Map<Identifier, Criterion<?>> VALUES = Maps.newHashMap();
    public static final ImpossibleCriterion IMPOSSIBLE = Criteria.register(new ImpossibleCriterion());
    public static final OnKilledCriterion PLAYER_KILLED_ENTITY = Criteria.register(new OnKilledCriterion(new Identifier("player_killed_entity")));
    public static final OnKilledCriterion ENTITY_KILLED_PLAYER = Criteria.register(new OnKilledCriterion(new Identifier("entity_killed_player")));
    public static final EnterBlockCriterion ENTER_BLOCK = Criteria.register(new EnterBlockCriterion());
    public static final InventoryChangedCriterion INVENTORY_CHANGED = Criteria.register(new InventoryChangedCriterion());
    public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = Criteria.register(new RecipeUnlockedCriterion());
    public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = Criteria.register(new PlayerHurtEntityCriterion());
    public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = Criteria.register(new EntityHurtPlayerCriterion());
    public static final EnchantedItemCriterion ENCHANTED_ITEM = Criteria.register(new EnchantedItemCriterion());
    public static final FilledBucketCriterion FILLED_BUCKET = Criteria.register(new FilledBucketCriterion());
    public static final BrewedPotionCriterion BREWED_POTION = Criteria.register(new BrewedPotionCriterion());
    public static final ConstructBeaconCriterion CONSTRUCT_BEACON = Criteria.register(new ConstructBeaconCriterion());
    public static final UsedEnderEyeCriterion USED_ENDER_EYE = Criteria.register(new UsedEnderEyeCriterion());
    public static final SummonedEntityCriterion SUMMONED_ENTITY = Criteria.register(new SummonedEntityCriterion());
    public static final BredAnimalsCriterion BRED_ANIMALS = Criteria.register(new BredAnimalsCriterion());
    public static final LocationArrivalCriterion LOCATION = Criteria.register(new LocationArrivalCriterion(new Identifier("location")));
    public static final LocationArrivalCriterion SLEPT_IN_BED = Criteria.register(new LocationArrivalCriterion(new Identifier("slept_in_bed")));
    public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = Criteria.register(new CuredZombieVillagerCriterion());
    public static final VillagerTradeCriterion VILLAGER_TRADE = Criteria.register(new VillagerTradeCriterion());
    public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = Criteria.register(new ItemDurabilityChangedCriterion());
    public static final LevitationCriterion LEVITATION = Criteria.register(new LevitationCriterion());
    public static final ChangedDimensionCriterion CHANGED_DIMENSION = Criteria.register(new ChangedDimensionCriterion());
    public static final TickCriterion TICK = Criteria.register(new TickCriterion());
    public static final TameAnimalCriterion TAME_ANIMAL = Criteria.register(new TameAnimalCriterion());
    public static final PlacedBlockCriterion PLACED_BLOCK = Criteria.register(new PlacedBlockCriterion());
    public static final ConsumeItemCriterion CONSUME_ITEM = Criteria.register(new ConsumeItemCriterion());
    public static final EffectsChangedCriterion EFFECTS_CHANGED = Criteria.register(new EffectsChangedCriterion());
    public static final UsedTotemCriterion USED_TOTEM = Criteria.register(new UsedTotemCriterion());
    public static final NetherTravelCriterion NETHER_TRAVEL = Criteria.register(new NetherTravelCriterion());
    public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = Criteria.register(new FishingRodHookedCriterion());
    public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = Criteria.register(new ChanneledLightningCriterion());
    public static final ShotCrossbowCriterion SHOT_CROSSBOW = Criteria.register(new ShotCrossbowCriterion());
    public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = Criteria.register(new KilledByCrossbowCriterion());
    public static final LocationArrivalCriterion HERO_OF_THE_VILLAGE = Criteria.register(new LocationArrivalCriterion(new Identifier("hero_of_the_village")));
    public static final LocationArrivalCriterion VOLUNTARY_EXILE = Criteria.register(new LocationArrivalCriterion(new Identifier("voluntary_exile")));
    public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK = Criteria.register(new SlideDownBlockCriterion());
    public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED = Criteria.register(new BeeNestDestroyedCriterion());
    public static final TargetHitCriterion TARGET_HIT = Criteria.register(new TargetHitCriterion());
    public static final ItemUsedOnBlockCriterion ITEM_USED_ON_BLOCK = Criteria.register(new ItemUsedOnBlockCriterion());
    public static final PlayerGeneratesContainerLootCriterion PLAYER_GENERATES_CONTAINER_LOOT = Criteria.register(new PlayerGeneratesContainerLootCriterion());
    public static final ThrownItemPickedUpByEntityCriterion THROWN_ITEM_PICKED_UP_BY_ENTITY = Criteria.register(new ThrownItemPickedUpByEntityCriterion());
    public static final PlayerInteractedWithEntityCriterion PLAYER_INTERACTED_WITH_ENTITY = Criteria.register(new PlayerInteractedWithEntityCriterion());

    private static <T extends Criterion<?>> T register(T object) {
        if (VALUES.containsKey(object.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + object.getId());
        }
        VALUES.put(object.getId(), object);
        return object;
    }

    @Nullable
    public static <T extends CriterionConditions> Criterion<T> getById(Identifier id) {
        return VALUES.get(id);
    }

    public static Iterable<? extends Criterion<?>> getCriteria() {
        return VALUES.values();
    }
}

