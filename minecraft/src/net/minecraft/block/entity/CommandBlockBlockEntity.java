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
		public ServerWorld method_8293() {
			return (ServerWorld)CommandBlockBlockEntity.this.world;
		}

		@Override
		public void method_8295() {
			BlockState blockState = CommandBlockBlockEntity.this.world.method_8320(CommandBlockBlockEntity.this.field_11867);
			this.method_8293().method_8413(CommandBlockBlockEntity.this.field_11867, blockState, blockState, 3);
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d method_8300() {
			return new Vec3d(
				(double)CommandBlockBlockEntity.this.field_11867.getX() + 0.5,
				(double)CommandBlockBlockEntity.this.field_11867.getY() + 0.5,
				(double)CommandBlockBlockEntity.this.field_11867.getZ() + 0.5
			);
		}

		@Override
		public ServerCommandSource method_8303() {
			return new ServerCommandSource(
				this,
				new Vec3d(
					(double)CommandBlockBlockEntity.this.field_11867.getX() + 0.5,
					(double)CommandBlockBlockEntity.this.field_11867.getY() + 0.5,
					(double)CommandBlockBlockEntity.this.field_11867.getZ() + 0.5
				),
				Vec2f.ZERO,
				this.method_8293(),
				2,
				this.method_8299().getString(),
				this.method_8299(),
				this.method_8293().getServer(),
				null
			);
		}
	};

	public CommandBlockBlockEntity() {
		super(BlockEntityType.COMMAND_BLOCK);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		this.commandExecutor.method_8297(compoundTag);
		compoundTag.putBoolean("powered", this.isPowered());
		compoundTag.putBoolean("conditionMet", this.isConditionMet());
		compoundTag.putBoolean("auto", this.isAuto());
		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.commandExecutor.method_8305(compoundTag);
		this.powered = compoundTag.getBoolean("powered");
		this.conditionMet = compoundTag.getBoolean("conditionMet");
		this.setAuto(compoundTag.getBoolean("auto"));
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		if (this.needsUpdatePacket()) {
			this.setNeedsUpdatePacket(false);
			CompoundTag compoundTag = this.method_11007(new CompoundTag());
			return new BlockEntityUpdateS2CPacket(this.field_11867, 2, compoundTag);
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
			Block block = this.method_11010().getBlock();
			if (block instanceof CommandBlock) {
				this.updateConditionMet();
				this.world.method_8397().method_8676(this.field_11867, block, block.getTickRate(this.world));
			}
		}
	}

	public boolean isConditionMet() {
		return this.conditionMet;
	}

	public boolean updateConditionMet() {
		this.conditionMet = true;
		if (this.isConditionalCommandBlock()) {
			BlockPos blockPos = this.field_11867
				.method_10093(((Direction)this.world.method_8320(this.field_11867).method_11654(CommandBlock.field_10791)).getOpposite());
			if (this.world.method_8320(blockPos).getBlock() instanceof CommandBlock) {
				BlockEntity blockEntity = this.world.method_8321(blockPos);
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
		Block block = this.method_11010().getBlock();
		if (block == Blocks.field_10525) {
			return CommandBlockBlockEntity.Type.field_11924;
		} else if (block == Blocks.field_10263) {
			return CommandBlockBlockEntity.Type.field_11923;
		} else {
			return block == Blocks.field_10395 ? CommandBlockBlockEntity.Type.field_11922 : CommandBlockBlockEntity.Type.field_11924;
		}
	}

	public boolean isConditionalCommandBlock() {
		BlockState blockState = this.world.method_8320(this.method_11016());
		return blockState.getBlock() instanceof CommandBlock ? (Boolean)blockState.method_11654(CommandBlock.field_10793) : false;
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
