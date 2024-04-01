package net.minecraft.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class StemBlock extends PlantBlock implements Fertilizable {
	public static final MapCodec<StemBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("fruit").forGetter(block -> block.gourdBlock),
					RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("attached_stem").forGetter(block -> block.attachedStemBlock),
					RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("seed").forGetter(block -> block.pickBlockItem),
					createSettingsCodec()
				)
				.apply(instance, StemBlock::new)
	);
	public static final int MAX_AGE = 7;
	public static final IntProperty AGE = Properties.AGE_7;
	protected static final float field_31256 = 1.0F;
	protected static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 4.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 6.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 12.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 14.0, 9.0),
		Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
	};
	private final RegistryKey<Block> gourdBlock;
	private final RegistryKey<Block> attachedStemBlock;
	private final RegistryKey<Item> pickBlockItem;

	@Override
	public MapCodec<StemBlock> getCodec() {
		return CODEC;
	}

	protected StemBlock(RegistryKey<Block> gourdBlock, RegistryKey<Block> attachedStemBlock, RegistryKey<Item> pickBlockItem, AbstractBlock.Settings settings) {
		super(settings);
		this.gourdBlock = gourdBlock;
		this.attachedStemBlock = attachedStemBlock;
		this.pickBlockItem = pickBlockItem;
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return AGE_TO_SHAPE[state.get(AGE)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND) || floor.isOf(Blocks.POISON_FARMLAND);
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBaseLightLevel(pos, 0) >= 9) {
			float f = CropBlock.getAvailableMoisture(this, world, pos);
			if (random.nextInt((int)(25.0F / f) + 1) == 0) {
				int i = (Integer)state.get(AGE);
				if (i < 7) {
					state = state.with(AGE, Integer.valueOf(i + 1));
					world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
				} else {
					Direction direction = Direction.Type.HORIZONTAL.random(random);
					BlockPos blockPos = pos.offset(direction);
					BlockState blockState = world.getBlockState(blockPos.down());
					if (world.getBlockState(blockPos).isAir()
						&& (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT) || blockState.isOf(Blocks.POISON_FARMLAND))) {
						Registry<Block> registry = world.getRegistryManager().get(RegistryKeys.BLOCK);
						Optional<Block> optional = registry.getOrEmpty(this.gourdBlock);
						Optional<Block> optional2 = registry.getOrEmpty(this.attachedStemBlock);
						if (optional.isPresent() && optional2.isPresent()) {
							world.setBlockState(blockPos, ((Block)optional.get()).getDefaultState());
							world.setBlockState(pos, ((Block)optional2.get()).getDefaultState().with(HorizontalFacingBlock.FACING, direction));
						}
					}
				}
			}
		}
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return new ItemStack(DataFixUtils.orElse(world.getRegistryManager().get(RegistryKeys.ITEM).getOrEmpty(this.pickBlockItem), this));
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return (Integer)state.get(AGE) != 7;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		int i = Math.min(7, (Integer)state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
		BlockState blockState = state.with(AGE, Integer.valueOf(i));
		world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
		if (i == 7) {
			blockState.randomTick(world, pos, world.random);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
