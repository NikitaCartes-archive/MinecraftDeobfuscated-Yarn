package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class CommandBlock extends BlockWithEntity implements OperatorBlock {
	public static final MapCodec<CommandBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.BOOL.fieldOf("automatic").forGetter(block -> block.auto), createSettingsCodec()).apply(instance, CommandBlock::new)
	);
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty CONDITIONAL = Properties.CONDITIONAL;
	private final boolean auto;

	@Override
	public MapCodec<CommandBlock> getCodec() {
		return CODEC;
	}

	public CommandBlock(boolean auto, AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(CONDITIONAL, Boolean.valueOf(false)));
		this.auto = auto;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		CommandBlockBlockEntity commandBlockBlockEntity = new CommandBlockBlockEntity(pos, state);
		commandBlockBlockEntity.setAuto(this.auto);
		return commandBlockBlockEntity;
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		if (!world.isClient) {
			if (world.getBlockEntity(pos) instanceof CommandBlockBlockEntity commandBlockBlockEntity) {
				boolean bl = world.isReceivingRedstonePower(pos);
				boolean bl2 = commandBlockBlockEntity.isPowered();
				commandBlockBlockEntity.setPowered(bl);
				if (!bl2 && !commandBlockBlockEntity.isAuto() && commandBlockBlockEntity.getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) {
					if (bl) {
						commandBlockBlockEntity.updateConditionMet();
						world.scheduleBlockTick(pos, this, 1);
					}
				}
			}
		}
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBlockEntity(pos) instanceof CommandBlockBlockEntity commandBlockBlockEntity) {
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			boolean bl = !StringHelper.isEmpty(commandBlockExecutor.getCommand());
			CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
			boolean bl2 = commandBlockBlockEntity.isConditionMet();
			if (type == CommandBlockBlockEntity.Type.AUTO) {
				commandBlockBlockEntity.updateConditionMet();
				if (bl2) {
					this.execute(state, world, pos, commandBlockExecutor, bl);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}

				if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
					world.scheduleBlockTick(pos, this, 1);
				}
			} else if (type == CommandBlockBlockEntity.Type.REDSTONE) {
				if (bl2) {
					this.execute(state, world, pos, commandBlockExecutor, bl);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			world.updateComparators(pos, this);
		}
	}

	private void execute(BlockState state, World world, BlockPos pos, CommandBlockExecutor executor, boolean hasCommand) {
		if (hasCommand) {
			executor.execute(world);
		} else {
			executor.setSuccessCount(0);
		}

		executeCommandChain(world, pos, state.get(FACING));
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
			player.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity);
			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof CommandBlockBlockEntity ? ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() : 0;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		if (world.getBlockEntity(pos) instanceof CommandBlockBlockEntity commandBlockBlockEntity) {
			CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			if (!world.isClient) {
				if (!itemStack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
					commandBlockExecutor.setTrackOutput(world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
					commandBlockBlockEntity.setAuto(this.auto);
				}

				boolean bl = world.isReceivingRedstonePower(pos);
				commandBlockBlockEntity.setPowered(bl);
			}
		}
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
		builder.add(FACING, CONDITIONAL);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	private static void executeCommandChain(World world, BlockPos pos, Direction facing) {
		BlockPos.Mutable mutable = pos.mutableCopy();
		GameRules gameRules = world.getGameRules();
		int i = gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);

		while (i-- > 0) {
			mutable.move(facing);
			BlockState blockState = world.getBlockState(mutable);
			Block block = blockState.getBlock();
			if (!blockState.isOf(Blocks.CHAIN_COMMAND_BLOCK)
				|| !(world.getBlockEntity(mutable) instanceof CommandBlockBlockEntity commandBlockBlockEntity)
				|| commandBlockBlockEntity.getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) {
				break;
			}

			if (commandBlockBlockEntity.isPowered() || commandBlockBlockEntity.isAuto()) {
				CommandBlockExecutor commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
				if (commandBlockBlockEntity.updateConditionMet()) {
					if (!commandBlockExecutor.execute(world)) {
						break;
					}

					world.updateComparators(mutable, block);
				} else if (commandBlockBlockEntity.isConditionalCommandBlock()) {
					commandBlockExecutor.setSuccessCount(0);
				}
			}

			facing = blockState.get(FACING);
		}

		if (i <= 0) {
			int j = Math.max(gameRules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
			LOGGER.warn("Command Block chain tried to execute more than {} steps!", j);
		}
	}
}
