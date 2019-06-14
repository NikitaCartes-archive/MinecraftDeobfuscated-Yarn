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
	private final CommandBlockExecutor field_7744 = new CommandBlockMinecartEntity.CommandExecutor();
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
		this.field_7744.deserialize(compoundTag);
		this.getDataTracker().set(COMMAND, this.method_7567().getCommand());
		this.getDataTracker().set(LAST_OUTPUT, this.method_7567().getLastOutput());
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		this.field_7744.serialize(compoundTag);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7681;
	}

	@Override
	public BlockState method_7517() {
		return Blocks.field_10525.method_9564();
	}

	public CommandBlockExecutor method_7567() {
		return this.field_7744;
	}

	@Override
	public void onActivatorRail(int i, int j, int k, boolean bl) {
		if (bl && this.age - this.lastExecuted >= 4) {
			this.method_7567().method_8301(this.field_6002);
			this.lastExecuted = this.age;
		}
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		this.field_7744.interact(playerEntity);
		return true;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (LAST_OUTPUT.equals(trackedData)) {
			try {
				this.field_7744.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
			} catch (Throwable var3) {
			}
		} else if (COMMAND.equals(trackedData)) {
			this.field_7744.setCommand(this.getDataTracker().get(COMMAND));
		}
	}

	@Override
	public boolean entityDataRequiresOperator() {
		return true;
	}

	public class CommandExecutor extends CommandBlockExecutor {
		@Override
		public ServerWorld getWorld() {
			return (ServerWorld)CommandBlockMinecartEntity.this.field_6002;
		}

		@Override
		public void markDirty() {
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d method_8300() {
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
				CommandBlockMinecartEntity.this.method_5802(),
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
