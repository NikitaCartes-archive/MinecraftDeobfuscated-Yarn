package net.minecraft.world.gen.root;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MangroveRootPlacer extends RootPlacer {
	public static final int field_38769 = 8;
	public static final int field_38770 = 15;
	public static final Codec<MangroveRootPlacer> CODEC = RecordCodecBuilder.create(
		instance -> method_43182(instance)
				.<RegistryEntryList<Block>, RegistryEntryList<Block>, BlockStateProvider, int, int, IntProvider, float>and(
					instance.group(
						RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("can_grow_through").forGetter(rootPlacer -> rootPlacer.canGrowThrough),
						RegistryCodecs.entryList(Registry.BLOCK_KEY).fieldOf("muddy_roots_in").forGetter(rootPlacer -> rootPlacer.muddyRootsIn),
						BlockStateProvider.TYPE_CODEC.fieldOf("muddy_roots_provider").forGetter(rootPlacer -> rootPlacer.muddyRootsProvider),
						Codec.intRange(1, 12).fieldOf("max_root_width").forGetter(rootPlacer -> rootPlacer.maxRootWidth),
						Codec.intRange(1, 64).fieldOf("max_root_length").forGetter(rootPlacer -> rootPlacer.maxRootLength),
						IntProvider.VALUE_CODEC.fieldOf("y_offset").forGetter(rootPlacer -> rootPlacer.yOffset),
						Codec.floatRange(0.0F, 1.0F).fieldOf("random_skew_chance").forGetter(rootPlacer -> rootPlacer.randomSkewChance)
					)
				)
				.apply(instance, MangroveRootPlacer::new)
	);
	private final RegistryEntryList<Block> canGrowThrough;
	private final RegistryEntryList<Block> muddyRootsIn;
	private final BlockStateProvider muddyRootsProvider;
	private final int maxRootWidth;
	private final int maxRootLength;
	private final IntProvider yOffset;
	private final float randomSkewChance;

	public MangroveRootPlacer(
		BlockStateProvider rootProvider,
		RegistryEntryList<Block> canGrowThrough,
		RegistryEntryList<Block> muddyRootsIn,
		BlockStateProvider muddyRootsProvider,
		int maxRootWidth,
		int maxRootLength,
		IntProvider yOffset,
		float randomSkewChance
	) {
		super(rootProvider);
		this.canGrowThrough = canGrowThrough;
		this.muddyRootsIn = muddyRootsIn;
		this.muddyRootsProvider = muddyRootsProvider;
		this.maxRootWidth = maxRootWidth;
		this.maxRootLength = maxRootLength;
		this.yOffset = yOffset;
		this.randomSkewChance = randomSkewChance;
	}

	@Override
	public Optional<BlockPos> generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos, TreeFeatureConfig config
	) {
		BlockPos blockPos = pos.add(0, this.yOffset.get(random), 0);
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		if (!this.canGrowThrough(world, blockPos)) {
			return Optional.empty();
		} else {
			list.add(blockPos.down());

			for (Direction direction : Direction.Type.HORIZONTAL) {
				BlockPos blockPos2 = blockPos.offset(direction);
				List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
				if (!this.canGrow(world, random, blockPos2, direction, blockPos, list2, 0)) {
					return Optional.empty();
				}

				list.addAll(list2);
				list.add(blockPos.offset(direction));
			}

			for (BlockPos blockPos3 : list) {
				this.placeRoots(world, replacer, random, blockPos3, config);
			}

			return Optional.of(blockPos);
		}
	}

	private boolean canGrow(
		TestableWorld world, AbstractRandom random, BlockPos pos, Direction direction, BlockPos origin, List<BlockPos> offshootPositions, int rootLength
	) {
		if (rootLength != this.maxRootLength && offshootPositions.size() <= this.maxRootLength) {
			for (BlockPos blockPos : this.getOffshootPositions(pos, direction, random, origin)) {
				if (this.canGrowThrough(world, blockPos)) {
					offshootPositions.add(blockPos);
					if (!this.canGrow(world, random, blockPos, direction, origin, offshootPositions, rootLength + 1)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected List<BlockPos> getOffshootPositions(BlockPos pos, Direction direction, AbstractRandom random, BlockPos origin) {
		BlockPos blockPos = pos.down();
		BlockPos blockPos2 = pos.offset(direction);
		int i = pos.getManhattanDistance(origin);
		if (i > this.maxRootWidth - 3 && i <= this.maxRootWidth) {
			return random.nextFloat() < this.randomSkewChance ? List.of(blockPos, blockPos2.down()) : List.of(blockPos);
		} else if (i > this.maxRootWidth) {
			return List.of(blockPos);
		} else if (random.nextFloat() < this.randomSkewChance) {
			return List.of(blockPos);
		} else {
			return random.nextBoolean() ? List.of(blockPos2) : List.of(blockPos);
		}
	}

	protected boolean canGrowThrough(TestableWorld world, BlockPos pos) {
		return TreeFeature.canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(this.canGrowThrough));
	}

	@Override
	protected void placeRoots(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, AbstractRandom random, BlockPos pos, TreeFeatureConfig config) {
		if (world.testBlockState(pos, state -> state.isIn(this.muddyRootsIn))) {
			BlockState blockState = this.muddyRootsProvider.getBlockState(random, pos);
			replacer.accept(pos, this.applyWaterlogging(world, pos, blockState));
		} else {
			super.placeRoots(world, replacer, random, pos, config);
		}
	}

	@Override
	protected RootPlacerType<?> getType() {
		return RootPlacerType.MANGROVE_ROOT_PLACER;
	}
}
