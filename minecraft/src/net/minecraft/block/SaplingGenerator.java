package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;

public final class SaplingGenerator {
	private static final Map<String, SaplingGenerator> GENERATORS = new Object2ObjectArrayMap<>();
	public static final Codec<SaplingGenerator> CODEC = Codec.stringResolver(generator -> generator.id, GENERATORS::get);
	public static final SaplingGenerator OAK = new SaplingGenerator(
		"oak",
		0.1F,
		Optional.empty(),
		Optional.empty(),
		Optional.of(TreeConfiguredFeatures.OAK),
		Optional.of(TreeConfiguredFeatures.FANCY_OAK),
		Optional.of(TreeConfiguredFeatures.OAK_BEES_005),
		Optional.of(TreeConfiguredFeatures.FANCY_OAK_BEES_005)
	);
	public static final SaplingGenerator SPRUCE = new SaplingGenerator(
		"spruce",
		0.5F,
		Optional.of(TreeConfiguredFeatures.MEGA_SPRUCE),
		Optional.of(TreeConfiguredFeatures.MEGA_PINE),
		Optional.of(TreeConfiguredFeatures.SPRUCE),
		Optional.empty(),
		Optional.empty(),
		Optional.empty()
	);
	public static final SaplingGenerator MANGROVE = new SaplingGenerator(
		"mangrove",
		0.85F,
		Optional.empty(),
		Optional.empty(),
		Optional.of(TreeConfiguredFeatures.MANGROVE),
		Optional.of(TreeConfiguredFeatures.TALL_MANGROVE),
		Optional.empty(),
		Optional.empty()
	);
	public static final SaplingGenerator AZALEA = new SaplingGenerator(
		"azalea", Optional.empty(), Optional.of(TreeConfiguredFeatures.AZALEA_TREE), Optional.empty()
	);
	public static final SaplingGenerator BIRCH = new SaplingGenerator(
		"birch", Optional.empty(), Optional.of(TreeConfiguredFeatures.BIRCH), Optional.of(TreeConfiguredFeatures.BIRCH_BEES_005)
	);
	public static final SaplingGenerator JUNGLE = new SaplingGenerator(
		"jungle", Optional.of(TreeConfiguredFeatures.MEGA_JUNGLE_TREE), Optional.of(TreeConfiguredFeatures.JUNGLE_TREE_NO_VINE), Optional.empty()
	);
	public static final SaplingGenerator ACACIA = new SaplingGenerator("acacia", Optional.empty(), Optional.of(TreeConfiguredFeatures.ACACIA), Optional.empty());
	public static final SaplingGenerator CHERRY = new SaplingGenerator(
		"cherry", Optional.empty(), Optional.of(TreeConfiguredFeatures.CHERRY), Optional.of(TreeConfiguredFeatures.CHERRY_BEES_005)
	);
	public static final SaplingGenerator DARK_OAK = new SaplingGenerator(
		"dark_oak", Optional.of(TreeConfiguredFeatures.DARK_OAK), Optional.empty(), Optional.empty()
	);
	private final String id;
	private final float rareChance;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareMegaVariant;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareRegularVariant;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant;
	private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareBeesVariant;

	public SaplingGenerator(
		String id,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant
	) {
		this(id, 0.0F, megaVariant, Optional.empty(), regularVariant, Optional.empty(), beesVariant, Optional.empty());
	}

	public SaplingGenerator(
		String id,
		float rareChance,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> megaVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareMegaVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> regularVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareRegularVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> beesVariant,
		Optional<RegistryKey<ConfiguredFeature<?, ?>>> rareBeesVariant
	) {
		this.id = id;
		this.rareChance = rareChance;
		this.megaVariant = megaVariant;
		this.rareMegaVariant = rareMegaVariant;
		this.regularVariant = regularVariant;
		this.rareRegularVariant = rareRegularVariant;
		this.beesVariant = beesVariant;
		this.rareBeesVariant = rareBeesVariant;
		GENERATORS.put(id, this);
	}

	@Nullable
	private RegistryKey<ConfiguredFeature<?, ?>> getSmallTreeFeature(Random random, boolean flowersNearby) {
		if (random.nextFloat() < this.rareChance) {
			if (flowersNearby && this.rareBeesVariant.isPresent()) {
				return (RegistryKey<ConfiguredFeature<?, ?>>)this.rareBeesVariant.get();
			}

			if (this.rareRegularVariant.isPresent()) {
				return (RegistryKey<ConfiguredFeature<?, ?>>)this.rareRegularVariant.get();
			}
		}

		return flowersNearby && this.beesVariant.isPresent() ? (RegistryKey)this.beesVariant.get() : (RegistryKey)this.regularVariant.orElse(null);
	}

	@Nullable
	private RegistryKey<ConfiguredFeature<?, ?>> getMegaTreeFeature(Random random) {
		return this.rareMegaVariant.isPresent() && random.nextFloat() < this.rareChance
			? (RegistryKey)this.rareMegaVariant.get()
			: (RegistryKey)this.megaVariant.orElse(null);
	}

	public boolean generate(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
		RegistryKey<ConfiguredFeature<?, ?>> registryKey = this.getMegaTreeFeature(random);
		if (registryKey != null) {
			RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = (RegistryEntry<ConfiguredFeature<?, ?>>)world.getRegistryManager()
				.get(RegistryKeys.CONFIGURED_FEATURE)
				.getEntry(registryKey)
				.orElse(null);
			if (registryEntry != null) {
				for (int i = 0; i >= -1; i--) {
					for (int j = 0; j >= -1; j--) {
						if (canGenerateLargeTree(state, world, pos, i, j)) {
							ConfiguredFeature<?, ?> configuredFeature = registryEntry.value();
							BlockState blockState = Blocks.AIR.getDefaultState();
							world.setBlockState(pos.add(i, 0, j), blockState, Block.NO_REDRAW);
							world.setBlockState(pos.add(i + 1, 0, j), blockState, Block.NO_REDRAW);
							world.setBlockState(pos.add(i, 0, j + 1), blockState, Block.NO_REDRAW);
							world.setBlockState(pos.add(i + 1, 0, j + 1), blockState, Block.NO_REDRAW);
							if (configuredFeature.generate(world, chunkGenerator, random, pos.add(i, 0, j))) {
								return true;
							}

							world.setBlockState(pos.add(i, 0, j), state, Block.NO_REDRAW);
							world.setBlockState(pos.add(i + 1, 0, j), state, Block.NO_REDRAW);
							world.setBlockState(pos.add(i, 0, j + 1), state, Block.NO_REDRAW);
							world.setBlockState(pos.add(i + 1, 0, j + 1), state, Block.NO_REDRAW);
							return false;
						}
					}
				}
			}
		}

		RegistryKey<ConfiguredFeature<?, ?>> registryKey2 = this.getSmallTreeFeature(random, this.areFlowersNearby(world, pos));
		if (registryKey2 == null) {
			return false;
		} else {
			RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = (RegistryEntry<ConfiguredFeature<?, ?>>)world.getRegistryManager()
				.get(RegistryKeys.CONFIGURED_FEATURE)
				.getEntry(registryKey2)
				.orElse(null);
			if (registryEntry2 == null) {
				return false;
			} else {
				ConfiguredFeature<?, ?> configuredFeature2 = registryEntry2.value();
				BlockState blockState2 = world.getFluidState(pos).getBlockState();
				world.setBlockState(pos, blockState2, Block.NO_REDRAW);
				if (configuredFeature2.generate(world, chunkGenerator, random, pos)) {
					if (world.getBlockState(pos) == blockState2) {
						world.updateListeners(pos, state, blockState2, Block.NOTIFY_LISTENERS);
					}

					return true;
				} else {
					world.setBlockState(pos, state, Block.NO_REDRAW);
					return false;
				}
			}
		}
	}

	private static boolean canGenerateLargeTree(BlockState state, BlockView world, BlockPos pos, int x, int z) {
		Block block = state.getBlock();
		return world.getBlockState(pos.add(x, 0, z)).isOf(block)
			&& world.getBlockState(pos.add(x + 1, 0, z)).isOf(block)
			&& world.getBlockState(pos.add(x, 0, z + 1)).isOf(block)
			&& world.getBlockState(pos.add(x + 1, 0, z + 1)).isOf(block);
	}

	private boolean areFlowersNearby(WorldAccess world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.Mutable.iterate(pos.down().north(2).west(2), pos.up().south(2).east(2))) {
			if (world.getBlockState(blockPos).isIn(BlockTags.FLOWERS)) {
				return true;
			}
		}

		return false;
	}
}
