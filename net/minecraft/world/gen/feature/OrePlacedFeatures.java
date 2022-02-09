/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

public class OrePlacedFeatures {
    public static final RegistryEntry<PlacedFeature> ORE_MAGMA = PlacedFeatures.register("ore_magma", OreConfiguredFeatures.ORE_MAGMA, OrePlacedFeatures.modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.fixed(27), YOffset.fixed(36))));
    public static final RegistryEntry<PlacedFeature> ORE_SOUL_SAND = PlacedFeatures.register("ore_soul_sand", OreConfiguredFeatures.ORE_SOUL_SAND, OrePlacedFeatures.modifiersWithCount(12, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(31))));
    public static final RegistryEntry<PlacedFeature> ORE_GOLD_DELTAS = PlacedFeatures.register("ore_gold_deltas", OreConfiguredFeatures.ORE_NETHER_GOLD, OrePlacedFeatures.modifiersWithCount(20, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
    public static final RegistryEntry<PlacedFeature> ORE_QUARTZ_DELTAS = PlacedFeatures.register("ore_quartz_deltas", OreConfiguredFeatures.ORE_QUARTZ, OrePlacedFeatures.modifiersWithCount(32, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
    public static final RegistryEntry<PlacedFeature> ORE_GOLD_NETHER = PlacedFeatures.register("ore_gold_nether", OreConfiguredFeatures.ORE_NETHER_GOLD, OrePlacedFeatures.modifiersWithCount(10, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
    public static final RegistryEntry<PlacedFeature> ORE_QUARTZ_NETHER = PlacedFeatures.register("ore_quartz_nether", OreConfiguredFeatures.ORE_QUARTZ, OrePlacedFeatures.modifiersWithCount(16, PlacedFeatures.TEN_ABOVE_AND_BELOW_RANGE));
    public static final RegistryEntry<PlacedFeature> ORE_GRAVEL_NETHER = PlacedFeatures.register("ore_gravel_nether", OreConfiguredFeatures.ORE_GRAVEL_NETHER, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(5), YOffset.fixed(41))));
    public static final RegistryEntry<PlacedFeature> ORE_BLACKSTONE = PlacedFeatures.register("ore_blackstone", OreConfiguredFeatures.ORE_BLACKSTONE, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(5), YOffset.fixed(31))));
    public static final RegistryEntry<PlacedFeature> ORE_DIRT = PlacedFeatures.register("ore_dirt", OreConfiguredFeatures.ORE_DIRT, OrePlacedFeatures.modifiersWithCount(7, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(160))));
    public static final RegistryEntry<PlacedFeature> ORE_GRAVEL = PlacedFeatures.register("ore_gravel", OreConfiguredFeatures.ORE_GRAVEL, OrePlacedFeatures.modifiersWithCount(14, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop())));
    public static final RegistryEntry<PlacedFeature> ORE_GRANITE_UPPER = PlacedFeatures.register("ore_granite_upper", OreConfiguredFeatures.ORE_GRANITE, OrePlacedFeatures.modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128))));
    public static final RegistryEntry<PlacedFeature> ORE_GRANITE_LOWER = PlacedFeatures.register("ore_granite_lower", OreConfiguredFeatures.ORE_GRANITE, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));
    public static final RegistryEntry<PlacedFeature> ORE_DIORITE_UPPER = PlacedFeatures.register("ore_diorite_upper", OreConfiguredFeatures.ORE_DIORITE, OrePlacedFeatures.modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128))));
    public static final RegistryEntry<PlacedFeature> ORE_DIORITE_LOWER = PlacedFeatures.register("ore_diorite_lower", OreConfiguredFeatures.ORE_DIORITE, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));
    public static final RegistryEntry<PlacedFeature> ORE_ANDESITE_UPPER = PlacedFeatures.register("ore_andesite_upper", OreConfiguredFeatures.ORE_ANDESITE, OrePlacedFeatures.modifiersWithRarity(6, HeightRangePlacementModifier.uniform(YOffset.fixed(64), YOffset.fixed(128))));
    public static final RegistryEntry<PlacedFeature> ORE_ANDESITE_LOWER = PlacedFeatures.register("ore_andesite_lower", OreConfiguredFeatures.ORE_ANDESITE, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(60))));
    public static final RegistryEntry<PlacedFeature> ORE_TUFF = PlacedFeatures.register("ore_tuff", OreConfiguredFeatures.ORE_TUFF, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(0))));
    public static final RegistryEntry<PlacedFeature> ORE_COAL_UPPER = PlacedFeatures.register("ore_coal_upper", OreConfiguredFeatures.ORE_COAL, OrePlacedFeatures.modifiersWithCount(30, HeightRangePlacementModifier.uniform(YOffset.fixed(136), YOffset.getTop())));
    public static final RegistryEntry<PlacedFeature> ORE_COAL_LOWER = PlacedFeatures.register("ore_coal_lower", OreConfiguredFeatures.ORE_COAL_BURIED, OrePlacedFeatures.modifiersWithCount(20, HeightRangePlacementModifier.trapezoid(YOffset.fixed(0), YOffset.fixed(192))));
    public static final RegistryEntry<PlacedFeature> ORE_IRON_UPPER = PlacedFeatures.register("ore_iron_upper", OreConfiguredFeatures.ORE_IRON, OrePlacedFeatures.modifiersWithCount(90, HeightRangePlacementModifier.trapezoid(YOffset.fixed(80), YOffset.fixed(384))));
    public static final RegistryEntry<PlacedFeature> ORE_IRON_MIDDLE = PlacedFeatures.register("ore_iron_middle", OreConfiguredFeatures.ORE_IRON, OrePlacedFeatures.modifiersWithCount(10, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-24), YOffset.fixed(56))));
    public static final RegistryEntry<PlacedFeature> ORE_IRON_SMALL = PlacedFeatures.register("ore_iron_small", OreConfiguredFeatures.ORE_IRON_SMALL, OrePlacedFeatures.modifiersWithCount(10, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(72))));
    public static final RegistryEntry<PlacedFeature> ORE_GOLD_EXTRA = PlacedFeatures.register("ore_gold_extra", OreConfiguredFeatures.ORE_GOLD, OrePlacedFeatures.modifiersWithCount(50, HeightRangePlacementModifier.uniform(YOffset.fixed(32), YOffset.fixed(256))));
    public static final RegistryEntry<PlacedFeature> ORE_GOLD = PlacedFeatures.register("ore_gold", OreConfiguredFeatures.ORE_GOLD_BURIED, OrePlacedFeatures.modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-64), YOffset.fixed(32))));
    public static final RegistryEntry<PlacedFeature> ORE_GOLD_LOWER = PlacedFeatures.register("ore_gold_lower", OreConfiguredFeatures.ORE_GOLD_BURIED, OrePlacedFeatures.modifiers(CountPlacementModifier.of(UniformIntProvider.create(0, 1)), HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48))));
    public static final RegistryEntry<PlacedFeature> ORE_REDSTONE = PlacedFeatures.register("ore_redstone", OreConfiguredFeatures.ORE_REDSTONE, OrePlacedFeatures.modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(15))));
    public static final RegistryEntry<PlacedFeature> ORE_REDSTONE_LOWER = PlacedFeatures.register("ore_redstone_lower", OreConfiguredFeatures.ORE_REDSTONE, OrePlacedFeatures.modifiersWithCount(8, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-32), YOffset.aboveBottom(32))));
    public static final RegistryEntry<PlacedFeature> ORE_DIAMOND = PlacedFeatures.register("ore_diamond", OreConfiguredFeatures.ORE_DIAMOND_SMALL, OrePlacedFeatures.modifiersWithCount(7, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
    public static final RegistryEntry<PlacedFeature> ORE_DIAMOND_LARGE = PlacedFeatures.register("ore_diamond_large", OreConfiguredFeatures.ORE_DIAMOND_LARGE, OrePlacedFeatures.modifiersWithRarity(9, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
    public static final RegistryEntry<PlacedFeature> ORE_DIAMOND_BURIED = PlacedFeatures.register("ore_diamond_buried", OreConfiguredFeatures.ORE_DIAMOND_BURIED, OrePlacedFeatures.modifiersWithCount(4, HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
    public static final RegistryEntry<PlacedFeature> ORE_LAPIS = PlacedFeatures.register("ore_lapis", OreConfiguredFeatures.ORE_LAPIS, OrePlacedFeatures.modifiersWithCount(2, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-32), YOffset.fixed(32))));
    public static final RegistryEntry<PlacedFeature> ORE_LAPIS_BURIED = PlacedFeatures.register("ore_lapis_buried", OreConfiguredFeatures.ORE_LAPIS_BURIED, OrePlacedFeatures.modifiersWithCount(4, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))));
    public static final RegistryEntry<PlacedFeature> ORE_INFESTED = PlacedFeatures.register("ore_infested", OreConfiguredFeatures.ORE_INFESTED, OrePlacedFeatures.modifiersWithCount(14, HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(63))));
    public static final RegistryEntry<PlacedFeature> ORE_EMERALD = PlacedFeatures.register("ore_emerald", OreConfiguredFeatures.ORE_EMERALD, OrePlacedFeatures.modifiersWithCount(100, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(480))));
    public static final RegistryEntry<PlacedFeature> ORE_ANCIENT_DEBRIS_LARGE = PlacedFeatures.register("ore_ancient_debris_large", OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_LARGE, SquarePlacementModifier.of(), HeightRangePlacementModifier.trapezoid(YOffset.fixed(8), YOffset.fixed(24)), BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_DEBRIS_SMALL = PlacedFeatures.register("ore_debris_small", OreConfiguredFeatures.ORE_ANCIENT_DEBRIS_SMALL, SquarePlacementModifier.of(), PlacedFeatures.EIGHT_ABOVE_AND_BELOW_RANGE, BiomePlacementModifier.of());
    public static final RegistryEntry<PlacedFeature> ORE_COPPER = PlacedFeatures.register("ore_copper", OreConfiguredFeatures.ORE_COPPER_SMALL, OrePlacedFeatures.modifiersWithCount(16, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(112))));
    public static final RegistryEntry<PlacedFeature> ORE_COPPER_LARGE = PlacedFeatures.register("ore_copper_large", OreConfiguredFeatures.ORE_COPPER_LARGE, OrePlacedFeatures.modifiersWithCount(16, HeightRangePlacementModifier.trapezoid(YOffset.fixed(-16), YOffset.fixed(112))));
    public static final RegistryEntry<PlacedFeature> ORE_CLAY = PlacedFeatures.register("ore_clay", OreConfiguredFeatures.ORE_CLAY, OrePlacedFeatures.modifiersWithCount(46, PlacedFeatures.BOTTOM_TO_120_RANGE));

    private static List<PlacementModifier> modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
    }

    private static List<PlacementModifier> modifiersWithCount(int count, PlacementModifier heightModifier) {
        return OrePlacedFeatures.modifiers(CountPlacementModifier.of(count), heightModifier);
    }

    private static List<PlacementModifier> modifiersWithRarity(int chance, PlacementModifier heightModifier) {
        return OrePlacedFeatures.modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
    }
}

