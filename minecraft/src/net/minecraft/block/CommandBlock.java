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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BlockWithEntity {
	private static final Logger field_10792 = LogManager.getLogger();
	public static final DirectionProperty field_10791 = FacingBlock.field_10927;
	public static final BooleanProperty field_10793 = Properties.CONDITIONAL;

	public CommandBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_10791, Direction.NORTH).with(field_10793, Boolean.valueOf(false)));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity();
		commandBlockBlockEntity.setAuto(this == Blocks.field_10395);
		return commandBlockBlockEntity;
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2) {
		if (!world.isRemote) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl = world.isReceivingRedstonePower(blockPos);
				boolean bl2 = commandBlockBlockEntity.isPowered();
				commandBlockBlockEntity.setPowered(bl);
				if (!bl2 && !commandBlockBlockEntity.isAuto() && commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.CHAIN) {
					if (bl) {
						commandBlockBlockEntity.method_11045();
						world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					}
				}
			}
		}
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isRemote) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				boolean bl = !ChatUtil.isEmpty(commandBlockExecutor.getCommand());
				CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getType();
				boolean bl2 = commandBlockBlockEntity.isConditionMet();
				if (type == CommandBlockBlockEntity.Type.REPEATING) {
					commandBlockBlockEntity.method_11045();
					if (bl2) {
						this.method_9780(blockState, world, blockPos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
						commandBlockExecutor.setSuccessCount(0);
					}

					if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
						world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
					}
				} else if (type == CommandBlockBlockEntity.Type.NORMAL) {
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

		method_9779(world, blockPos, blockState.get(field_10791));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 1;
	}

	@Override
	public boolean method_9534(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity && playerEntity.method_7338()) {
			playerEntity.openCommandBlock((CommandBlockBlockEntity)blockEntity);
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

			if (!world.isRemote) {
				if (itemStack.getSubCompoundTag("BlockEntityTag") == null) {
					commandBlockExecutor.shouldTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
					commandBlockBlockEntity.setAuto(this == Blocks.field_10395);
				}

				if (commandBlockBlockEntity.getType() == CommandBlockBlockEntity.Type.CHAIN) {
					boolean bl = world.isReceivingRedstonePower(blockPos);
					commandBlockBlockEntity.setPowered(bl);
				}
			}
		}
	}

	@Override
	public RenderTypeBlock getRenderType(BlockState blockState) {
		return RenderTypeBlock.MODEL;
	}

	@Override
	public BlockState applyRotation(BlockState blockState, Rotation rotation) {
		return blockState.with(field_10791, rotation.method_10503(blockState.get(field_10791)));
	}

	@Override
	public BlockState applyMirror(BlockState blockState, Mirror mirror) {
		return blockState.applyRotation(mirror.method_10345(blockState.get(field_10791)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_10791, field_10793);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return this.getDefaultState().with(field_10791, itemPlacementContext.method_7715().getOpposite());
	}

	private static void method_9779(World world, BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		GameRules gameRules = world.getGameRules();
		int i = gameRules.getInteger("maxCommandChainLength");

		while (i-- > 0) {
			mutable.method_10098(direction);
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
			if (commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.CHAIN) {
				break;
			}

			if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				if (commandBlockBlockEntity.method_11045()) {
					if (!commandBlockExecutor.execute(world)) {
						break;
					}

					world.updateHorizontalAdjacent(mutable, block);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			direction = blockState.get(field_10791);
		}

		if (i <= 0) {
			int j = Math.max(gameRules.getInteger("maxCommandChainLength"), 0);
			field_10792.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
