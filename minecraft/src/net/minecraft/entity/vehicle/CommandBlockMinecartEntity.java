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
import net.minecraft.sortme.CommandBlockExecutor;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity {
	private static final TrackedData<String> COMMAND = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<TextComponent> LAST_OUTPUT = DataTracker.registerData(
		CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT
	);
	private final CommandBlockExecutor commandExecutor = new CommandBlockMinecartEntity.class_1698();
	private int field_7742;

	public CommandBlockMinecartEntity(EntityType<? extends CommandBlockMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public CommandBlockMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.COMMAND_BLOCK_MINECART, world, d, e, f);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(COMMAND, "");
		this.getDataTracker().startTracking(LAST_OUTPUT, new StringTextComponent(""));
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.commandExecutor.deserialize(compoundTag);
		this.getDataTracker().set(COMMAND, this.getCommandExecutor().getCommand());
		this.getDataTracker().set(LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
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
		if (bl && this.age - this.field_7742 >= 4) {
			this.getCommandExecutor().execute(this.world);
			this.field_7742 = this.age;
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
				this.commandExecutor.setLastOutput(this.getDataTracker().get(LAST_OUTPUT));
			} catch (Throwable var3) {
			}
		} else if (COMMAND.equals(trackedData)) {
			this.commandExecutor.setCommand(this.getDataTracker().get(COMMAND));
		}
	}

	@Override
	public boolean method_5833() {
		return true;
	}

	public class class_1698 extends CommandBlockExecutor {
		@Override
		public ServerWorld getWorld() {
			return (ServerWorld)CommandBlockMinecartEntity.this.world;
		}

		@Override
		public void method_8295() {
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
			CommandBlockMinecartEntity.this.getDataTracker().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d getPos() {
			return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
		}

		@Environment(EnvType.CLIENT)
		public CommandBlockMinecartEntity method_7569() {
			return CommandBlockMinecartEntity.this;
		}

		@Override
		public ServerCommandSource markDirty() {
			return new ServerCommandSource(
				this,
				new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z),
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
