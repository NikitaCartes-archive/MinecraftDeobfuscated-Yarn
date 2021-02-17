package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Text> LAST_OUTPUT = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
	private final CommandBlockExecutor commandExecutor = new CommandBlockMinecartEntity.CommandExecutor();
	private int lastExecuted;

	public CommandBlockMinecartEntity(EntityType<? extends CommandBlockMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public CommandBlockMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.COMMAND_BLOCK_MINECART, world, x, y, z);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(COMMAND, "");
		this.getDataTracker().startTracking(LAST_OUTPUT, LiteralText.EMPTY);
	}

	@Override
	protected void readCustomDataFromNbt(CompoundTag tag) {
		super.readCustomDataFromNbt(tag);
		this.commandExecutor.deserialize(tag);
		this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
		this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
	}

	@Override
	protected void writeCustomDataToNbt(CompoundTag tag) {
		super.writeCustomDataToNbt(tag);
		this.commandExecutor.serialize(tag);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.COMMAND_BLOCK;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.COMMAND_BLOCK.getDefaultState();
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered && this.age - this.lastExecuted >= 4) {
			this.getCommandExecutor().execute(this.world);
			this.lastExecuted = this.age;
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		return this.commandExecutor.interact(player);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (LAST_OUTPUT.equals(data)) {
			try {
				this.commandExecutor.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
			} catch (Throwable var3) {
			}
		} else if (COMMAND.equals(data)) {
			this.commandExecutor.setCommand(this.getDataTracker().get(COMMAND));
		}
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}

	public class CommandExecutor extends CommandBlockExecutor {
		@Override
		public ServerWorld getWorld() {
			return (ServerWorld)CommandBlockMinecartEntity.this.world;
		}

		@Override
		public void markDirty() {
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d getPos() {
			return CommandBlockMinecartEntity.this.getPos();
		}

		@Environment(EnvType.CLIENT)
		public CommandBlockMinecartEntity getMinecart() {
			return CommandBlockMinecartEntity.this;
		}

		@Override
		public ServerCommandSource getSource() {
			return new ServerCommandSource(
				this,
				CommandBlockMinecartEntity.this.getPos(),
				CommandBlockMinecartEntity.this.getRotationClient(),
				this.getWorld(),
				2,
				this.getCustomName().getString(),
				CommandBlockMinecartEntity.this.getDisplayName(),
				this.getWorld().getServer(),
				CommandBlockMinecartEntity.this
			);
		}
	}
}
