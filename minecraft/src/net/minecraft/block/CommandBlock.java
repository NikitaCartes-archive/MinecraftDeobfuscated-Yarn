package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BlockWithEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final DirectionProperty field_10791 = FacingBlock.field_10927;
	public static final BooleanProperty field_10793 = Properties.field_12486;

	public CommandBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10791, Direction.field_11043).method_11657(field_10793, Boolean.valueOf(false)));
	}

	@Override
	public BlockEntity method_10123(BlockView blockView) {
		CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity();
		commandBlockBlockEntity.setAuto(this == Blocks.field_10395);
		return commandBlockBlockEntity;
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.method_8321(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				boolean bl2 = world.isReceivingRedstonePower(blockPos);
				boolean bl3 = commandBlockBlockEntity.isPowered();
				commandBlockBlockEntity.setPowered(bl2);
				if (!bl3 && !commandBlockBlockEntity.isAuto() && commandBlockBlockEntity.getType() != CommandBlockBlockEntity.Type.field_11922) {
					if (bl2) {
						commandBlockBlockEntity.updateConditionMet();
						world.method_8397().schedule(blockPos, this, this.getTickRate(world));
					}
				}
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			BlockEntity blockEntity = world.method_8321(blockPos);
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
						world.method_8397().schedule(blockPos, this, this.getTickRate(world));
					}
				} else if (type == CommandBlockBlockEntity.Type.field_11924) {
					if (bl2) {
						this.method_9780(blockState, world, blockPos, commandBlockExecutor, bl);
					} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
						commandBlockExecutor.setSuccessCount(0);
					}
				}

				world.method_8455(blockPos, this);
			}
		}
	}

	private void method_9780(BlockState blockState, World world, BlockPos blockPos, CommandBlockExecutor commandBlockExecutor, boolean bl) {
		if (bl) {
			commandBlockExecutor.method_8301(world);
		} else {
			commandBlockExecutor.setSuccessCount(0);
		}

		executeCommandChain(world, blockPos, blockState.method_11654(field_10791));
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 1;
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity && playerEntity.isCreativeLevelTwoOp()) {
			playerEntity.method_7323((CommandBlockBlockEntity)blockEntity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		BlockEntity blockEntity = world.method_8321(blockPos);
		if (blockEntity instanceof CommandBlockBlockEntity) {
			CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			if (itemStack.hasCustomName()) {
				commandBlockExecutor.setCustomName(itemStack.getName());
			}

			if (!world.isClient) {
				if (itemStack.getSubTag("BlockEntityTag") == null) {
					commandBlockExecutor.shouldTrackOutput(world.getGameRules().getBoolean(GameRules.field_19400));
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
	public BlockRenderType method_9604(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10791, blockRotation.rotate(blockState.method_11654(field_10791)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_10791)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10791, field_10793);
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		return this.method_9564().method_11657(field_10791, itemPlacementContext.getPlayerLookDirection().getOpposite());
	}

	private static void executeCommandChain(World world, BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		GameRules gameRules = world.getGameRules();
		int i = gameRules.getInt(GameRules.field_19408);

		while (i-- > 0) {
			mutable.setOffset(direction);
			BlockState blockState = world.method_8320(mutable);
			Block block = blockState.getBlock();
			if (block != Blocks.field_10395) {
				break;
			}

			BlockEntity blockEntity = world.method_8321(mutable);
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
					if (!commandBlockExecutor.method_8301(world)) {
						break;
					}

					world.method_8455(mutable, block);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			direction = blockState.method_11654(field_10791);
		}

		if (i <= 0) {
			int j = Math.max(gameRules.getInt(GameRules.field_19408), 0);
			LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
