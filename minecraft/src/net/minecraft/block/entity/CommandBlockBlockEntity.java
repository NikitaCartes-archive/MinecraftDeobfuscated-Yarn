package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockBlockEntity extends BlockEntity {
	private boolean powered;
	private boolean auto;
	private boolean conditionMet;
	private final CommandBlockExecutor commandExecutor = new CommandBlockExecutor() {
		@Override
		public void setCommand(String command) {
			super.setCommand(command);
			CommandBlockBlockEntity.this.markDirty();
		}

		@Override
		public ServerWorld getWorld() {
			return (ServerWorld)CommandBlockBlockEntity.this.world;
		}

		@Override
		public void markDirty() {
			BlockState blockState = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
			this.getWorld().updateListeners(CommandBlockBlockEntity.this.pos, blockState, blockState, Block.NOTIFY_ALL);
		}

		@Override
		public Vec3d getPos() {
			return Vec3d.ofCenter(CommandBlockBlockEntity.this.pos);
		}

		@Override
		public ServerCommandSource getSource() {
			Direction direction = CommandBlockBlockEntity.this.getCachedState().get(CommandBlock.FACING);
			return new ServerCommandSource(
				this,
				Vec3d.ofCenter(CommandBlockBlockEntity.this.pos),
				new Vec2f(0.0F, direction.asRotation()),
				this.getWorld(),
				2,
				this.getCustomName().getString(),
				this.getCustomName(),
				this.getWorld().getServer(),
				null
			);
		}

		@Override
		public boolean isEditable() {
			return !CommandBlockBlockEntity.this.isRemoved();
		}
	};

	public CommandBlockBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.COMMAND_BLOCK, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		this.commandExecutor.writeNbt(nbt, registryLookup);
		nbt.putBoolean("powered", this.isPowered());
		nbt.putBoolean("conditionMet", this.isConditionMet());
		nbt.putBoolean("auto", this.isAuto());
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.commandExecutor.readNbt(nbt, registryLookup);
		this.powered = nbt.getBoolean("powered");
		this.conditionMet = nbt.getBoolean("conditionMet");
		this.setAuto(nbt.getBoolean("auto"));
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	public boolean isPowered() {
		return this.powered;
	}

	public boolean isAuto() {
		return this.auto;
	}

	public void setAuto(boolean auto) {
		boolean bl = this.auto;
		this.auto = auto;
		if (!bl && auto && !this.powered && this.world != null && this.getCommandBlockType() != CommandBlockBlockEntity.Type.SEQUENCE) {
			this.scheduleAutoTick();
		}
	}

	public void updateCommandBlock() {
		CommandBlockBlockEntity.Type type = this.getCommandBlockType();
		if (type == CommandBlockBlockEntity.Type.AUTO && (this.powered || this.auto) && this.world != null) {
			this.scheduleAutoTick();
		}
	}

	private void scheduleAutoTick() {
		Block block = this.getCachedState().getBlock();
		if (block instanceof CommandBlock) {
			this.updateConditionMet();
			this.world.scheduleBlockTick(this.pos, block, 1);
		}
	}

	public boolean isConditionMet() {
		return this.conditionMet;
	}

	public boolean updateConditionMet() {
		this.conditionMet = true;
		if (this.isConditionalCommandBlock()) {
			BlockPos blockPos = this.pos.offset(((Direction)this.world.getBlockState(this.pos).get(CommandBlock.FACING)).getOpposite());
			if (this.world.getBlockState(blockPos).getBlock() instanceof CommandBlock) {
				BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
				this.conditionMet = blockEntity instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() > 0;
			} else {
				this.conditionMet = false;
			}
		}

		return this.conditionMet;
	}

	public CommandBlockBlockEntity.Type getCommandBlockType() {
		BlockState blockState = this.getCachedState();
		if (blockState.isOf(Blocks.COMMAND_BLOCK)) {
			return CommandBlockBlockEntity.Type.REDSTONE;
		} else if (blockState.isOf(Blocks.REPEATING_COMMAND_BLOCK)) {
			return CommandBlockBlockEntity.Type.AUTO;
		} else {
			return blockState.isOf(Blocks.CHAIN_COMMAND_BLOCK) ? CommandBlockBlockEntity.Type.SEQUENCE : CommandBlockBlockEntity.Type.REDSTONE;
		}
	}

	public boolean isConditionalCommandBlock() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.get(CommandBlock.CONDITIONAL) : false;
	}

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		this.commandExecutor.setCustomName(components.get(DataComponentTypes.CUSTOM_NAME));
	}

	@Override
	protected void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.commandExecutor.getCustomNameNullable());
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("CustomName");
	}

	public static enum Type {
		SEQUENCE,
		AUTO,
		REDSTONE;
	}
}
