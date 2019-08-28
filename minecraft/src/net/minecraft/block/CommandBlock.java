package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4538;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameRules;
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
		commandBlockBlockEntity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
		return commandBlockBlockEntity;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl2 = world.isReceivingRedstonePower(blockPos);
				boolean bl3 = commandBlockBlockEntity.isPowered();
				commandBlockBlockEntity.setPowered(bl2);
				if (!bl3 && !commandBlockBlockEntity.isAuto() && commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.SEQUENCE) {
					if (bl2) {
						commandBlockBlockEntity.updateConditionMet();
						world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					}
				}
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			boolean bl = !ChatUtil.isEmpty(commandBlockExecutor.getCommand());
			CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getType();
			boolean bl2 = commandBlockBlockEntity.isConditionMet();
			if (type == CommandBlockBlockEntity.Type.AUTO) {
				commandBlockBlockEntity.updateConditionMet();
				if (bl2) {
					this.execute(blockState, serverWorld, blockPos, commandBlockExecutor, bl);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}

				if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
					serverWorld.method_14196().schedule(blockPos, this, this.getTickRate(serverWorld));
				}
			} else if (type == CommandBlockBlockEntity.Type.REDSTONE) {
				if (bl2) {
					this.execute(blockState, serverWorld, blockPos, commandBlockExecutor, bl);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			serverWorld.updateHorizontalAdjacent(blockPos, this);
		}
	}

	private void execute(BlockState blockState, World world, BlockPos blockPos, CommandBlockExecutor commandBlockExecutor, boolean bl) {
		if (bl) {
			commandBlockExecutor.execute(world);
		} else {
			commandBlockExecutor.setSuccessCount(0);
		}

		executeCommandChain(world, blockPos, blockState.get(FACING));
	}

	@Override
	public int getTickRate(class_4538 arg) {
		return 1;
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity && playerEntity.isCreativeLevelTwoOp()) {
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
			if (itemStack.hasCustomName()) {
				commandBlockExecutor.setCustomName(itemStack.getName());
			}

			if (!world.isClient) {
				if (itemStack.getSubTag("BlockEntityTag") == null) {
					commandBlockExecutor.shouldTrackOutput(world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
					commandBlockBlockEntity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
				}

				if (commandBlockBlockEntity.getType() == CommandBlockBlockEntity.Type.SEQUENCE) {
					boolean bl = world.isReceivingRedstonePower(blockPos);
					commandBlockBlockEntity.setPowered(bl);
				}
			}
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, CONDITIONAL);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerLookDirection().getOpposite());
	}

	private static void executeCommandChain(World world, BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		GameRules gameRules = world.getGameRules();
		int i = gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);

		while (i-- > 0) {
			mutable.setOffset(direction);
			BlockState blockState = world.getBlockState(mutable);
			Block block = blockState.getBlock();
			if (block != Blocks.CHAIN_COMMAND_BLOCK) {
				break;
			}

			BlockEntity blockEntity = world.getBlockEntity(mutable);
			if (!(blockEntity instanceof CommandBlockBlockEntity)) {
				break;
			}

			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			if (commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.SEQUENCE) {
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
			int j = Math.max(gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
			LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
