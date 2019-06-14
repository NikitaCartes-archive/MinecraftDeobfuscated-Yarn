package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractSignBlock extends BlockWithEntity implements Waterloggable {
	public static final BooleanProperty field_11491 = Properties.field_12508;
	protected static final VoxelShape field_11492 = Block.method_9541(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

	protected AbstractSignBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if ((Boolean)blockState.method_11654(field_11491)) {
			iWorld.method_8405().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11492;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9589(BlockState blockState) {
		return true;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new SignBlockEntity();
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof SignBlockEntity) {
				SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
				ItemStack itemStack = playerEntity.getStackInHand(hand);
				if (itemStack.getItem() instanceof DyeItem && playerEntity.abilities.allowModifyWorld) {
					boolean bl = signBlockEntity.setTextColor(((DyeItem)itemStack.getItem()).getColor());
					if (bl && !playerEntity.isCreative()) {
						itemStack.decrement(1);
					}
				}

				return signBlockEntity.onActivate(playerEntity);
			} else {
				return false;
			}
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11491) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}
}
