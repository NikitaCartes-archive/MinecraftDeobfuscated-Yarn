package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty field_12196 = PistonHeadBlock.field_10927;
	public static final EnumProperty<PistonType> field_12197 = PistonHeadBlock.field_12224;

	public PistonExtensionBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_12196, Direction.field_11043).method_11657(field_12197, PistonType.field_12637));
	}

	@Nullable
	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return null;
	}

	public static BlockEntity method_11489(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		return new PistonBlockEntity(blockState, direction, bl, bl2);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}
		}
	}

	@Override
	public void method_9585(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.offset(((Direction)blockState.method_11654(field_12196)).getOpposite());
		BlockState blockState2 = iWorld.method_8320(blockPos2);
		if (blockState2.getBlock() instanceof PistonBlock && (Boolean)blockState2.method_11654(PistonBlock.field_12191)) {
			iWorld.clearBlockState(blockPos2, false);
		}
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_9521(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_16362(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient && world.method_8321(blockPos) == null) {
			world.clearBlockState(blockPos, false);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		PistonBlockEntity pistonBlockEntity = this.method_11488(builder.getWorld(), builder.method_308(LootContextParameters.field_1232));
		return pistonBlockEntity == null ? Collections.emptyList() : pistonBlockEntity.method_11495().method_11612(builder);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.method_1073();
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		PistonBlockEntity pistonBlockEntity = this.method_11488(blockView, blockPos);
		return pistonBlockEntity != null ? pistonBlockEntity.method_11512(blockView, blockPos) : VoxelShapes.method_1073();
	}

	@Nullable
	private PistonBlockEntity method_11488(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.method_8321(blockPos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_12196, blockRotation.rotate(blockState.method_11654(field_12196)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_12196)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_12196, field_12197);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
