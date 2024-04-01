package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class AttachedStemBlock extends PlantBlock {
	public static final MapCodec<AttachedStemBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("fruit").forGetter(block -> block.gourdBlock),
					RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("stem").forGetter(block -> block.stemBlock),
					RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("seed").forGetter(block -> block.pickBlockItem),
					createSettingsCodec()
				)
				.apply(instance, AttachedStemBlock::new)
	);
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	protected static final float field_30995 = 2.0F;
	private static final Map<Direction, VoxelShape> FACING_TO_SHAPE = Maps.newEnumMap(
		ImmutableMap.of(
			Direction.SOUTH,
			Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
			Direction.WEST,
			Block.createCuboidShape(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
			Direction.NORTH,
			Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
			Direction.EAST,
			Block.createCuboidShape(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
		)
	);
	private final RegistryKey<Block> gourdBlock;
	private final RegistryKey<Block> stemBlock;
	private final RegistryKey<Item> pickBlockItem;

	@Override
	public MapCodec<AttachedStemBlock> getCodec() {
		return CODEC;
	}

	protected AttachedStemBlock(RegistryKey<Block> stemBlock, RegistryKey<Block> gourdBlock, RegistryKey<Item> pickBlockItem, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
		this.stemBlock = stemBlock;
		this.gourdBlock = gourdBlock;
		this.pickBlockItem = pickBlockItem;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)FACING_TO_SHAPE.get(state.get(FACING));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (!neighborState.matchesKey(this.gourdBlock) && direction == state.get(FACING)) {
			Optional<Block> optional = world.getRegistryManager().get(RegistryKeys.BLOCK).getOrEmpty(this.stemBlock);
			if (optional.isPresent()) {
				return ((Block)optional.get()).getDefaultState().withIfExists(StemBlock.AGE, Integer.valueOf(7));
			}
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isOf(Blocks.FARMLAND) || floor.isOf(Blocks.POISON_FARMLAND);
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return new ItemStack(DataFixUtils.orElse(world.getRegistryManager().get(RegistryKeys.ITEM).getOrEmpty(this.pickBlockItem), this));
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
