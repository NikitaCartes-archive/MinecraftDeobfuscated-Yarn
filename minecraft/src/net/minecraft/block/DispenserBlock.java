package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class DispenserBlock extends BlockWithEntity {
	public static final DirectionProperty field_10918 = FacingBlock.field_10927;
	public static final BooleanProperty field_10920 = Properties.field_12522;
	private static final Map<Item, DispenserBehavior> BEHAVIORS = SystemUtil.consume(
		new Object2ObjectOpenHashMap<>(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue(new ItemDispenserBehavior())
	);

	public static void registerBehavior(ItemConvertible itemConvertible, DispenserBehavior dispenserBehavior) {
		BEHAVIORS.put(itemConvertible.asItem(), dispenserBehavior);
	}

	protected DispenserBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10918, Direction.field_11043).method_11657(field_10920, Boolean.valueOf(false)));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 4;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (world.isClient) {
			return true;
		} else {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				playerEntity.openContainer((DispenserBlockEntity)blockEntity);
				if (blockEntity instanceof DropperBlockEntity) {
					playerEntity.incrementStat(Stats.field_15367);
				} else {
					playerEntity.incrementStat(Stats.field_15371);
				}
			}

			return true;
		}
	}

	protected void dispense(World world, BlockPos blockPos) {
		BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, blockPos);
		DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();
		int i = dispenserBlockEntity.chooseNonEmptySlot();
		if (i < 0) {
			world.playLevelEvent(1001, blockPos, 0);
		} else {
			ItemStack itemStack = dispenserBlockEntity.getInvStack(i);
			DispenserBehavior dispenserBehavior = this.getBehaviorForItem(itemStack);
			if (dispenserBehavior != DispenserBehavior.NOOP) {
				dispenserBlockEntity.setInvStack(i, dispenserBehavior.dispense(blockPointerImpl, itemStack));
			}
		}
	}

	protected DispenserBehavior getBehaviorForItem(ItemStack itemStack) {
		return (DispenserBehavior)BEHAVIORS.get(itemStack.getItem());
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		boolean bl2 = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
		boolean bl3 = (Boolean)blockState.method_11654(field_10920);
		if (bl2 && !bl3) {
			world.method_8397().schedule(blockPos, this, this.getTickRate(world));
			world.method_8652(blockPos, blockState.method_11657(field_10920, Boolean.valueOf(true)), 4);
		} else if (!bl2 && bl3) {
			world.method_8652(blockPos, blockState.method_11657(field_10920, Boolean.valueOf(false)), 4);
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			this.dispense(world, blockPos);
		}
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		return new DispenserBlockEntity();
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10918, itemPlacementContext.getPlayerLookDirection().getOpposite());
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.hasCustomName()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				((DispenserBlockEntity)blockEntity).setCustomName(itemStack.getName());
			}
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof DispenserBlockEntity) {
				ItemScatterer.method_5451(world, blockPos, (DispenserBlockEntity)blockEntity);
				world.method_8455(blockPos, this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	public static Position getOutputLocation(BlockPointer blockPointer) {
		Direction direction = blockPointer.getBlockState().method_11654(field_10918);
		double d = blockPointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = blockPointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = blockPointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return Container.method_7608(world.method_8321(blockPos));
	}

	@Override
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10918, blockRotation.rotate(blockState.method_11654(field_10918)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_10918)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10918, field_10920);
	}
}
