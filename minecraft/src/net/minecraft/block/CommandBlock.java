package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BlockWithEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty CONDITIONAL = Properties.CONDITIONAL;

	public CommandBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(FACING, Direction.NORTH).with(CONDITIONAL, Boolean.valueOf(false)));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity();
		commandBlockBlockEntity.setAuto(this == Blocks.field_10395);
		return commandBlockBlockEntity;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(blockPos);
				boolean bl2 = commandBlockBlockEntity.isPowered();
				commandBlockBlockEntity.setPowered(bl);
				if (!bl2 && !commandBlockBlockEntity.isAuto() && commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.field_11922) {
					if (bl) {
						commandBlockBlockEntity.updateConditionMet();
						world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					}
				}
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				boolean bl = !ChatUtil.isEmpty(commandBlockExecutor.getCommand());
				CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getType();
				boolean bl2 = commandBlockBlockEntity.isConditionMet();
				if (type == CommandBlockBlockEntity.Type.field_11923) {
					commandBlockBlockEntity.updateConditionMet();
					if (bl2) {
						this.method_9780(blockState, world, blockPos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
						commandBlockExecutor.setSuccessCount(0);
					}

					if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
						world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					}
				} else if (type == CommandBlockBlockEntity.Type.field_11924) {
					if (bl2) {
						this.method_9780(blockState, world, blockPos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
						commandBlockExecutor.setSuccessCount(0);
					}
				}

				world.updateHorizontalAdjacent(blockPos, this);
			}
		}
	}

	private void method_9780(BlockState blockState, World world, BlockPos blockPos, CommandBlockExecutor commandBlockExecutor, boolean bl) {
		if (bl) {
			commandBlockExecutor.execute(world);
		} else {
			commandBlockExecutor.setSuccessCount(0);
		}

		method_9779(world, blockPos, blockState.get(FACING));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 1;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity && playerEntity.method_7338()) {
			playerEntity.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			if (itemStack.hasDisplayName()) {
				commandBlockExecutor.setCustomName(itemStack.getDisplayName());
			}

			if (!world.isClient) {
				if (itemStack.getSubCompoundTag("BlockEntityTag") == null) {
					commandBlockExecutor.shouldTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
					commandBlockBlockEntity.setAuto(this == Blocks.field_10395);
				}

				if (commandBlockBlockEntity.getType() == CommandBlockBlockEntity.Type.field_11922) {
					boolean bl = world.isReceivingRedstonePower(blockPos);
					commandBlockBlockEntity.setPowered(bl);
				}
			}
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return blockState.with(FACING, rotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(FACING, CONDITIONAL);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
	}

	private static void method_9779(World world, BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		GameRules gameRules = world.getGameRules();
		int i = gameRules.getInteger("maxCommandChainLength");

		while (i-- > 0) {
			mutable.setOffset(direction);
			BlockState blockState = world.getBlockState(mutable);
			Block block = blockState.getBlock();
			if (block != Blocks.field_10395) {
				break;
			}

			BlockEntity blockEntity = world.getBlockEntity(mutable);
			if (!(blockEntity instanceof CommandBlockBlockEntity)) {
				break;
			}

			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			if (commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.field_11922) {
				break;
			}

			if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				if (commandBlockBlockEntity.updateConditionMet()) {
					if (!commandBlockExecutor.execute(world)) {
						break;
					}

					world.updateHorizontalAdjacent(mutable, block);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			direction = blockState.get(FACING);
		}

		if (i <= 0) {
			int j = Math.max(gameRules.getInteger("maxCommandChainLength"), 0);
			LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
