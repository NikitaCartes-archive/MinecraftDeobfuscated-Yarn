package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
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
	private boolean field_11916;
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
		public ServerCommandSource method_8303() {
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
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		if (this.method_11036()) {
			this.method_11037(false);
			CompoundTag compoundTag = this.toTag(new CompoundTag());
			return new BlockEntityUpdateClientPacket(this.pos, 2, compoundTag);
		} else {
			return null;
		}
	}

	@Override
	public boolean method_11011() {
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
		if (!bl2 && bl && !this.powered && this.world != null && this.getType() != CommandBlockBlockEntity.Type.CHAIN) {
			Block block = this.getCachedState().getBlock();
			if (block instanceof CommandBlock) {
				this.method_11045();
				this.world.getBlockTickScheduler().schedule(this.pos, block, block.getTickRate(this.world));
			}
		}
	}

	public boolean isConditionMet() {
		return this.conditionMet;
	}

	public boolean method_11045() {
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

	public boolean method_11036() {
		return this.field_11916;
	}

	public void method_11037(boolean bl) {
		this.field_11916 = bl;
	}

	public CommandBlockBlockEntity.Type getType() {
		Block block = this.getCachedState().getBlock();
		if (block == Blocks.field_10525) {
			return CommandBlockBlockEntity.Type.NORMAL;
		} else if (block == Blocks.field_10263) {
			return CommandBlockBlockEntity.Type.REPEATING;
		} else {
			return block == Blocks.field_10395 ? CommandBlockBlockEntity.Type.CHAIN : CommandBlockBlockEntity.Type.NORMAL;
		}
	}

	public boolean isConditionalCommandBlock() {
		BlockState blockState = this.world.getBlockState(this.getPos());
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.get(CommandBlock.field_10793) : false;
	}

	@Override
	public void validate() {
		this.resetBlock();
		super.validate();
	}

	public static enum Type {
		CHAIN,
		REPEATING,
		NORMAL;
	}
}
