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

	public CommandBlockMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6136, world, d, e, f);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(COMMAND, "");
		this.getDataTracker().startTracking(LAST_OUTPUT, new LiteralText(""));
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.commandExecutor.deserialize(compoundTag);
		this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
		this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().method_8292());
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		this.commandExecutor.serialize(compoundTag);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7681;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10525.getDefaultState();
	}

	public CommandBlockExecutor getCommandExecutor() {
		return this.commandExecutor;
	}

	@Override
	public void onActivatorRail(int i, int j, int k, boolean bl) {
		if (bl && this.age - this.lastExecuted >= 4) {
			this.getCommandExecutor().execute(this.world);
			this.lastExecuted = this.age;
		}
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		this.commandExecutor.interact(playerEntity);
		return true;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (LAST_OUTPUT.equals(trackedData)) {
			try {
				this.commandExecutor.method_8291(this.getDataTracker().get(LAST_OUTPUT));
			} catch (Throwable var3) {
			}
		} else if (COMMAND.equals(trackedData)) {
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
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.method_8292());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d getPos() {
			return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
		}

		@Environment(EnvType.CLIENT)
		public CommandBlockMinecartEntity getMinecart() {
			return CommandBlockMinecartEntity.this;
		}

		@Override
		public ServerCommandSource getSource() {
			return new ServerCommandSource(
				this,
				new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z),
				CommandBlockMinecartEntity.this.getRotationClient(),
				this.getWorld(),
				2,
				this.method_8299().getString(),
				CommandBlockMinecartEntity.this.method_5476(),
				this.getWorld().getServer(),
				CommandBlockMinecartEntity.this
			);
		}
	}
}
