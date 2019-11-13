/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BlockUsedCriterion;
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
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LevitationCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.advancement.criterion.UsedEnderEyeCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Criterions {
    private static final Map<Identifier, Criterion<?>> VALUES = Maps.newHashMap();
    public static final ImpossibleCriterion IMPOSSIBLE = Criterions.register(new ImpossibleCriterion());
    public static final OnKilledCriterion PLAYER_KILLED_ENTITY = Criterions.register(new OnKilledCriterion(new Identifier("player_killed_entity")));
    public static final OnKilledCriterion ENTITY_KILLED_PLAYER = Criterions.register(new OnKilledCriterion(new Identifier("entity_killed_player")));
    public static final EnterBlockCriterion ENTER_BLOCK = Criterions.register(new EnterBlockCriterion());
    public static final InventoryChangedCriterion INVENTORY_CHANGED = Criterions.register(new InventoryChangedCriterion());
    public static final RecipeUnlockedCriterion RECIPE_UNLOCKED = Criterions.register(new RecipeUnlockedCriterion());
    public static final PlayerHurtEntityCriterion PLAYER_HURT_ENTITY = Criterions.register(new PlayerHurtEntityCriterion());
    public static final EntityHurtPlayerCriterion ENTITY_HURT_PLAYER = Criterions.register(new EntityHurtPlayerCriterion());
    public static final EnchantedItemCriterion ENCHANTED_ITEM = Criterions.register(new EnchantedItemCriterion());
    public static final FilledBucketCriterion FILLED_BUCKET = Criterions.register(new FilledBucketCriterion());
    public static final BrewedPotionCriterion BREWED_POTION = Criterions.register(new BrewedPotionCriterion());
    public static final ConstructBeaconCriterion CONSTRUCT_BEACON = Criterions.register(new ConstructBeaconCriterion());
    public static final UsedEnderEyeCriterion USED_ENDER_EYE = Criterions.register(new UsedEnderEyeCriterion());
    public static final SummonedEntityCriterion SUMMONED_ENTITY = Criterions.register(new SummonedEntityCriterion());
    public static final BredAnimalsCriterion BRED_ANIMALS = Criterions.register(new BredAnimalsCriterion());
    public static final LocationArrivalCriterion LOCATION = Criterions.register(new LocationArrivalCriterion(new Identifier("location")));
    public static final LocationArrivalCriterion SLEPT_IN_BED = Criterions.register(new LocationArrivalCriterion(new Identifier("slept_in_bed")));
    public static final CuredZombieVillagerCriterion CURED_ZOMBIE_VILLAGER = Criterions.register(new CuredZombieVillagerCriterion());
    public static final VillagerTradeCriterion VILLAGER_TRADE = Criterions.register(new VillagerTradeCriterion());
    public static final ItemDurabilityChangedCriterion ITEM_DURABILITY_CHANGED = Criterions.register(new ItemDurabilityChangedCriterion());
    public static final LevitationCriterion LEVITATION = Criterions.register(new LevitationCriterion());
    public static final ChangedDimensionCriterion CHANGED_DIMENSION = Criterions.register(new ChangedDimensionCriterion());
    public static final TickCriterion TICK = Criterions.register(new TickCriterion());
    public static final TameAnimalCriterion TAME_ANIMAL = Criterions.register(new TameAnimalCriterion());
    public static final PlacedBlockCriterion PLACED_BLOCK = Criterions.register(new PlacedBlockCriterion());
    public static final ConsumeItemCriterion CONSUME_ITEM = Criterions.register(new ConsumeItemCriterion());
    public static final EffectsChangedCriterion EFFECTS_CHANGED = Criterions.register(new EffectsChangedCriterion());
    public static final UsedTotemCriterion USED_TOTEM = Criterions.register(new UsedTotemCriterion());
    public static final NetherTravelCriterion NETHER_TRAVEL = Criterions.register(new NetherTravelCriterion());
    public static final FishingRodHookedCriterion FISHING_ROD_HOOKED = Criterions.register(new FishingRodHookedCriterion());
    public static final ChanneledLightningCriterion CHANNELED_LIGHTNING = Criterions.register(new ChanneledLightningCriterion());
    public static final ShotCrossbowCriterion SHOT_CROSSBOW = Criterions.register(new ShotCrossbowCriterion());
    public static final KilledByCrossbowCriterion KILLED_BY_CROSSBOW = Criterions.register(new KilledByCrossbowCriterion());
    public static final LocationArrivalCriterion HERO_OF_THE_VILLAGE = Criterions.register(new LocationArrivalCriterion(new Identifier("hero_of_the_village")));
    public static final LocationArrivalCriterion VOLUNTARY_EXILE = Criterions.register(new LocationArrivalCriterion(new Identifier("voluntary_exile")));
    public static final BlockUsedCriterion SAFELY_HARVEST_HONEY = Criterions.register(new BlockUsedCriterion(new Identifier("safely_harvest_honey")));
    public static final SlideDownBlockCriterion SLIDE_DOWN_BLOCK = Criterions.register(new SlideDownBlockCriterion());
    public static final BeeNestDestroyedCriterion BEE_NEST_DESTROYED = Criterions.register(new BeeNestDestroyedCriterion());

    private static <T extends Criterion<?>> T register(T criterion) {
        if (VALUES.containsKey(criterion.getId())) {
            throw new IllegalArgumentException("Duplicate criterion id " + criterion.getId());
        }
        VALUES.put(criterion.getId(), criterion);
        return criterion;
    }

    @Nullable
    public static <T extends CriterionConditions> Criterion<T> getById(Identifier identifier) {
        return VALUES.get(identifier);
    }

    public static Iterable<? extends Criterion<?>> getAllCriterions() {
        return VALUES.values();
    }
}

