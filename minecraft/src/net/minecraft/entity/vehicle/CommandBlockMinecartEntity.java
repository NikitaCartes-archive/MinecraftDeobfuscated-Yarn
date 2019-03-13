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
	private static final TrackedData<String> field_7743 = DataTracker.registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<TextComponent> field_7741 = DataTracker.registerData(
		CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT
	);
	private final CommandBlockExecutor field_7744 = new CommandBlockMinecartEntity.class_1698();
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
		this.method_5841().startTracking(field_7743, "");
		this.method_5841().startTracking(field_7741, new StringTextComponent(""));
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.field_7744.method_8305(compoundTag);
		this.method_5841().set(field_7743, this.method_7567().getCommand());
		this.method_5841().set(field_7741, this.method_7567().method_8292());
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		this.field_7744.method_8297(compoundTag);
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
		if (bl && this.age - this.field_7742 >= 4) {
			this.method_7567().method_8301(this.field_6002);
			this.field_7742 = this.age;
		}
	}

	@Override
	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		this.field_7744.interact(playerEntity);
		return true;
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		super.method_5674(trackedData);
		if (field_7741.equals(trackedData)) {
			try {
				this.field_7744.method_8291(this.method_5841().get(field_7741));
			} catch (Throwable var3) {
			}
		} else if (field_7743.equals(trackedData)) {
			this.field_7744.setCommand(this.method_5841().get(field_7743));
		}
	}

	@Override
	public boolean method_5833() {
		return true;
	}

	public class class_1698 extends CommandBlockExecutor {
		@Override
		public ServerWorld method_8293() {
			return (ServerWorld)CommandBlockMinecartEntity.this.field_6002;
		}

		@Override
		public void method_8295() {
			CommandBlockMinecartEntity.this.method_5841().set(CommandBlockMinecartEntity.field_7743, this.getCommand());
			CommandBlockMinecartEntity.this.method_5841().set(CommandBlockMinecartEntity.field_7741, this.method_8292());
		}

		@Environment(EnvType.CLIENT)
		@Override
		public Vec3d method_8300() {
			return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
		}

		@Environment(EnvType.CLIENT)
		public CommandBlockMinecartEntity method_7569() {
			return CommandBlockMinecartEntity.this;
		}

		@Override
		public ServerCommandSource method_8303() {
			return new ServerCommandSource(
				this,
				new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z),
				CommandBlockMinecartEntity.this.method_5802(),
				this.method_8293(),
				2,
				this.method_8299().getString(),
				CommandBlockMinecartEntity.this.method_5476(),
				this.method_8293().getServer(),
				CommandBlockMinecartEntity.this
			);
		}
	}
}
