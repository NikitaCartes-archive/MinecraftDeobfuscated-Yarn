package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BigBrainBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.XpComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BigBrainBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE = VoxelShapes.union(
		Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 5.0, 10.0), Block.createCuboidShape(1.0, 5.0, 1.0, 15.0, 15.0, 15.0)
	);
	public static final MapCodec<BigBrainBlock> CODEC = createCodec(BigBrainBlock::new);
	public static final int XP_PACKET_SIZE = 1000;

	public BigBrainBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
	}

	@Override
	public MapCodec<BigBrainBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BigBrainBlockEntity(pos, state);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient && entity instanceof ExperienceOrbEntity experienceOrbEntity) {
			world.getBlockEntity(pos, BlockEntityType.BIG_BRAIN).ifPresent(blockEntity -> {
				int i = blockEntity.getAmount() + experienceOrbEntity.method_58858();
				experienceOrbEntity.discard();

				while (i >= 1000) {
					ItemStack itemStack = new ItemStack(Items.POTATO_OF_KNOWLEDGE);
					itemStack.set(DataComponentTypes.XP, new XpComponent(1000));
					dropStack(world, pos, itemStack);
					i -= 1000;
				}

				blockEntity.setAmount(i);
				blockEntity.markDirty();
			});
		}
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return validateTicker(type, BlockEntityType.BIG_BRAIN, BigBrainBlockEntity::tick);
	}
}
