package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.pool.alias.StructurePoolAliasBinding;
import net.minecraft.structure.pool.alias.StructurePoolAliasBindings;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.collection.DataPool;

public class TrialChamberData {
	public static final RegistryKey<StructurePool> CHAMBER_END_POOL_KEY = StructurePools.ofVanilla("trial_chambers/chamber/end");
	public static final RegistryKey<StructurePool> HALLWAY_FALLBACK_POOL_KEY = StructurePools.ofVanilla("trial_chambers/hallway/fallback");
	public static final RegistryKey<StructurePool> CHAMBER_ENTRANCE_CAP_POOL_KEY = StructurePools.ofVanilla("trial_chambers/chamber/entrance_cap");
	public static final List<StructurePoolAliasBinding> ALIAS_BINDINGS = ImmutableList.<StructurePoolAliasBinding>builder()
		.add(
			StructurePoolAliasBinding.randomGroup(
				DataPool.<List<StructurePoolAliasBinding>>builder()
					.add(
						List.of(
							StructurePoolAliasBinding.direct(spawner("contents/ranged"), spawner("ranged/skeleton")),
							StructurePoolAliasBinding.direct(spawner("contents/slow_ranged"), spawner("slow_ranged/skeleton"))
						)
					)
					.add(
						List.of(
							StructurePoolAliasBinding.direct(spawner("contents/ranged"), spawner("ranged/stray")),
							StructurePoolAliasBinding.direct(spawner("contents/slow_ranged"), spawner("slow_ranged/stray"))
						)
					)
					.add(
						List.of(
							StructurePoolAliasBinding.direct(spawner("contents/ranged"), spawner("ranged/poison_skeleton")),
							StructurePoolAliasBinding.direct(spawner("contents/slow_ranged"), spawner("slow_ranged/poison_skeleton"))
						)
					)
					.build()
			)
		)
		.add(
			StructurePoolAliasBinding.random(
				spawner("contents/melee"), DataPool.<String>builder().add(spawner("melee/zombie")).add(spawner("melee/husk")).add(spawner("melee/spider")).build()
			)
		)
		.add(
			StructurePoolAliasBinding.random(
				spawner("contents/small_melee"),
				DataPool.<String>builder()
					.add(spawner("small_melee/slime"))
					.add(spawner("small_melee/cave_spider"))
					.add(spawner("small_melee/silverfish"))
					.add(spawner("small_melee/baby_zombie"))
					.build()
			)
		)
		.build();

	public static String spawner(String path) {
		return "trial_chambers/spawner/" + path;
	}

	public static void bootstrap(Registerable<StructurePool> poolRegisterable) {
		RegistryEntryLookup<StructurePool> registryEntryLookup = poolRegisterable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		RegistryEntry<StructurePool> registryEntry = registryEntryLookup.getOrThrow(StructurePools.EMPTY);
		RegistryEntry<StructurePool> registryEntry2 = registryEntryLookup.getOrThrow(HALLWAY_FALLBACK_POOL_KEY);
		RegistryEntry<StructurePool> registryEntry3 = registryEntryLookup.getOrThrow(CHAMBER_ENTRANCE_CAP_POOL_KEY);
		RegistryEntryLookup<StructureProcessorList> registryEntryLookup2 = poolRegisterable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);
		RegistryEntry<StructureProcessorList> registryEntry4 = registryEntryLookup2.getOrThrow(StructureProcessorLists.TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION);
		poolRegisterable.register(
			CHAMBER_END_POOL_KEY,
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/end_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/end_2", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/entrance_cap",
			new StructurePool(
				registryEntry3,
				List.of(Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/entrance_cap", registryEntry4), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chambers/end",
			new StructurePool(
				registryEntry2,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/chamber_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/assembly", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/eruption", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/slanted", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/corridor",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/second_plate"), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/intersection/intersection_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/intersection/intersection_2", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/intersection/intersection_3", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/first_plate"), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/atrium_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/entrance_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/entrance_2", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/entrance_3", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/addon",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/full_stacked_walkway"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/full_stacked_walkway_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/full_corner_column"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/grate_bridge"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/hanging_platform"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/short_grate_platform"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/short_platform"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/lower_staircase_down"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/walkway_with_bridge_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/addon/c1_breeze"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/assembly",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/full_column"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_1"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_2"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_3"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_4"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_5"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_6"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/cover_7"), 5),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/platform_1"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/spawner_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/hanging_1"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/hanging_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/hanging_3"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/hanging_4"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/hanging_5"), 4),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/left_staircase_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/left_staircase_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/left_staircase_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/right_staircase_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/right_staircase_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/assembly/right_staircase_3"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/eruption",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/center_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/breeze_slice_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/slice_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/slice_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/slice_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/quadrant_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/quadrant_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/quadrant_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/quadrant_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/eruption/quadrant_5"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/slanted",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/center"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/hallway_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/hallway_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/hallway_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/hallway_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/hallway_5"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/ramp_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/ramp_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/ramp_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/ramp_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/ominous_upper_arm_1"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chamber/pedestal",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/center_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/slice_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/slice_2"), 3),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/slice_3"), 3),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/slice_4"), 3),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/slice_5"), 3),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/ominous_slice_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/quadrant_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/quadrant_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/pedestal/quadrant_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/chamber/slanted/quadrant_4"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/corridor/slices",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_2", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_3", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_4", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_5", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_6", registryEntry4), 2),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_7", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/corridor/straight_8", registryEntry4), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		poolRegisterable.register(
			HALLWAY_FALLBACK_POOL_KEY,
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/rubble"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/rubble_chamber"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/rubble_thin"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/rubble_chamber_thin"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/hallway",
			new StructurePool(
				registryEntry2,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/corridor_connector_1"), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/upper_hallway_connector", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/lower_hallway_connector", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/hallway/rubble"), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/chamber_1", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/chamber_2", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/chamber_4", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/chamber_8", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/assembly", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/eruption", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/slanted", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/chamber/pedestal", registryEntry4), 150),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/rubble_chamber", registryEntry4), 10),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/rubble_chamber_thin", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/cache_1", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/left_corner", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/right_corner", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/corner_staircase", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/corner_staircase_down", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/long_straight_staircase", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/long_straight_staircase_down", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/straight", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/straight_staircase", registryEntry4), 1),
					Pair.of(StructurePoolElement.ofProcessedSingle("trial_chambers/hallway/straight_staircase_down", registryEntry4), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/corridors/addon/lower",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofEmpty(), 8),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/staircase"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/wall"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/ladder_to_middle"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/arrow_dispenser"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/bridge_lower"), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/corridors/addon/middle",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofEmpty(), 8),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/open_walkway"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/walled_walkway"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/corridors/addon/middle_upper",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofEmpty(), 6),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/open_walkway_upper"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/chandelier_upper"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/decoration_upper"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/head_upper"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/reward_upper"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/atrium",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/bogged_relief"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/breeze_relief"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/spiral_relief"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/spider_relief"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/grand_staircase_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/grand_staircase_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/atrium/grand_staircase_3"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/decor",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofEmpty(), 22),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/empty_pot"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/dead_bush_pot"), 2),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/undecorated_pot"), 10),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/flow_pot"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/guster_pot"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/scrape_pot"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/candle_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/candle_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/candle_3"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/candle_4"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/barrel"), 2)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/entrance",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/display_1"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/display_2"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/corridor/addon/display_3"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/decor/chamber",
			new StructurePool(
				registryEntry,
				List.of(Pair.of(StructurePoolElement.ofEmpty(), 4), Pair.of(StructurePoolElement.ofSingle("trial_chambers/decor/undecorated_pot"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/reward/all",
			new StructurePool(registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/reward/vault"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/reward/ominous_vault",
			new StructurePool(registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/reward/ominous_vault"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/reward/contents/default",
			new StructurePool(registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/reward/vault"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chests/supply",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/chests/connectors/supply"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/chests/contents/supply",
			new StructurePool(registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/chests/supply"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/ranged",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/ranged"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/slow_ranged",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/slow_ranged"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/melee",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/melee"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/small_melee",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/small_melee"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/breeze",
			new StructurePool(
				registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/breeze"), 1)), StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/all",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/ranged"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/melee"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/connectors/small_melee"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/spawner/contents/breeze",
			new StructurePool(registryEntry, List.of(Pair.of(StructurePoolElement.ofSingle("trial_chambers/spawner/breeze/breeze"), 1)), StructurePool.Projection.RIGID)
		);
		StructurePools.register(
			poolRegisterable,
			"trial_chambers/dispensers/chamber",
			new StructurePool(
				registryEntry,
				List.of(
					Pair.of(StructurePoolElement.ofEmpty(), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/dispensers/chamber"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/dispensers/wall_dispenser"), 1),
					Pair.of(StructurePoolElement.ofSingle("trial_chambers/dispensers/floor_dispenser"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePoolAliasBindings.registerPools(poolRegisterable, registryEntry, ALIAS_BINDINGS);
	}
}
