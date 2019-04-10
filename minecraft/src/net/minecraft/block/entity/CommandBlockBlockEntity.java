package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class CommandBlockBlockEntity extends BlockEntity {
	private boolean powered;
	private boolean auto;
	private boolean conditionMet;
	private boolean needsUpdatePacket;
	private final CommandBlockExecutor commandExecutor = new CommandBlockExecutor() {
		@Override
		public void setCommand(String string) {
			super.setCommand(string);
			CommandBlockBlockEntity.this.markDirty();
		}

		@Override
		public ServerWorld getWorld() {
			return (ServerWorld)CommandBlockBlockEntity.this.world;
		}

		@Override
		public void method_8295() {
			BlockState blockState = CommandBlockBlockEntity.this.world.getBlockState(CommandBlockBlockEntity.this.pos);
			this.getWorld().updateListeners(CommandBlockBlockEntity.this.pos, blockState, blockState, 3);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d getPos() {
			return new Vec3d(
				(double)CommandBlockBlockEntity.this.pos.getX() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getY() + 0.5,
				(double)CommandBlockBlockEntity.this.pos.getZ() + 0.5
			);
		}

		@Override
		public ServerCommandSource markDirty() {
			return new ServerCommandSource(
				this,
				new Vec3d(
					(double)CommandBlockBlockEntity.this.pos.getX() + 0.5,
					(double)CommandBlockBlockEntity.this.pos.getY() + 0.5,
					(double)CommandBlockBlockEntity.this.pos.getZ() + 0.5
				),
				Vec2f.ZERO,
				this.getWorld(),
				2,
				this.getCustomName().getString(),
				this.getCustomName(),
				this.getWorld().getServer(),
				null
			);
		}
	};

	public CommandBlockBlockEntity() {
		super(BlockEntityType.COMMAND_BLOCK);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		this.commandExecutor.serialize(compoundTag);
		compoundTag.putBoolean("powered", this.isPowered());
		compoundTag.putBoolean("conditionMet", this.isConditionMet());
		compoundTag.putBoolean("auto", this.isAuto());
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.commandExecutor.deserialize(compoundTag);
		this.powered = compoundTag.getBoolean("powered");
		this.conditionMet = compoundTag.getBoolean("conditionMet");
		this.setAuto(compoundTag.getBoolean("auto"));
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		if (this.needsUpdatePacket()) {
			this.setNeedsUpdatePacket(false);
			CompoundTag compoundTag = this.toTag(new CompoundTag());
			return new BlockEntityUpdateS2CPacket(this.pos, 2, compoundTag);
		} else {
			return null;
		}
	}

	@Override
	public boolean shouldNotCopyTagFromItem() {
		return true;
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	public void setPowered(boolean bl) {
		this.powered = bl;
	}

	public boolean isPowered() {
		return this.powered;
	}

	public boolean isAuto() {
		return this.auto;
	}

	public void setAuto(boolean bl) {
		boolean bl2 = this.auto;
		this.auto = bl;
		if (!bl2 && bl && !this.powered && this.world != null && this.getType() != CommandBlockBlockEntity.Type.field_11922) {
			Block block = this.getCachedState().getBlock();
			if (block instanceof CommandBlock) {
				this.updateConditionMet();
				this.world.getBlockTickScheduler().schedule(this.pos, block, block.getTickRate(this.world));
			}
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

	public boolean needsUpdatePacket() {
		return this.needsUpdatePacket;
	}

	public void setNeedsUpdatePacket(boolean bl) {
		this.needsUpdatePacket = bl;
	}

	public CommandBlockBlockEntity.Type getType() {
		Block block = this.getCachedState().getBlock();
		if (block == Blocks.field_10525) {
			return CommandBlockBlockEntity.Type.field_11924;
		} else if (block == Blocks.field_10263) {
			return CommandBlockBlockEntity.Type.field_11923;
		} else {
			return block == Blocks.field_10395 ? CommandBlockBlockEntity.Type.field_11922 : CommandBlockBlockEntity.Type.field_11924;
		}
	}

	public boolean isConditionalCommandBlock() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.get(CommandBlock.CONDITIONAL) : false;
	}

	@Override
	public void validate() {
		this.resetBlock();
		super.validate();
	}

	public static enum Type {
		field_11922,
		field_11923,
		field_11924;
	}
}
