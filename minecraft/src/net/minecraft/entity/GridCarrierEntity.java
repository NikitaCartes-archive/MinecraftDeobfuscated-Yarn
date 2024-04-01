package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.class_9517;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.AddSubGridS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Grid;
import net.minecraft.world.GridCarrierView;
import net.minecraft.world.World;

public class GridCarrierEntity extends Entity {
	public static final int field_50502 = 2;
	private static final TrackedData<Direction> MOVEMENT_DIRECTION = DataTracker.registerData(GridCarrierEntity.class, TrackedDataHandlerRegistry.FACING);
	private static final TrackedData<Float> MOVEMENT_SPEED = DataTracker.registerData(GridCarrierEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private final GridCarrierView view;
	@Nullable
	private class_9517 field_50506;
	@Nullable
	private GridCarrierEntity.class_9512 field_50507;
	private int field_50508;

	public GridCarrierEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.view = world.createGridCarrierView(this);
		this.ignoreCameraFrustum = true;
	}

	public void method_58950(Direction direction, float f) {
		this.getDataTracker().set(MOVEMENT_DIRECTION, direction);
		this.getDataTracker().set(MOVEMENT_SPEED, f);
		this.field_50506 = class_9517.method_58997(this.view.getGrid(), direction);
	}

	public void method_58952() {
		this.getDataTracker().set(MOVEMENT_SPEED, 0.0F);
		this.field_50506 = null;
	}

	public GridCarrierView method_58953() {
		return this.view;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		if (this.view != null) {
			this.view.setGridPosition(x, y, z);
		}
	}

	@Override
	public void tick() {
		super.tick();
		Direction direction = this.getGridMovementDirection();
		this.view.getGrid().tick(this.getWorld(), this.getPos(), direction);
		if (this.getWorld().isClient()) {
			this.method_58954();
		} else {
			this.method_58955();
		}
	}

	private void method_58954() {
		if (this.field_50507 != null) {
			this.field_50507.method_58958(this);
			if (--this.field_50507.field_50509 == 0) {
				this.field_50507 = null;
			}
		}
	}

	private void method_58955() {
		Direction direction = this.getGridMovementDirection();
		float f = this.getGridMovementSpeed();
		if (this.field_50508 == 0 && f == 0.0F) {
			this.field_50508 = 2;
		}

		if (this.field_50508 > 0) {
			this.field_50508--;
			if (this.field_50508 == 1) {
				this.view.getGrid().method_58976(this.getBlockPos(), this.getWorld());
			} else if (this.field_50508 == 0) {
				this.discard();
			}
		} else if (this.field_50506 != null) {
			this.method_58948(this.field_50506, direction, f);
		}
	}

	private void method_58948(class_9517 arg, Direction direction, float f) {
		Vec3d vec3d = this.getPos();
		Vec3d vec3d2 = vec3d.add(
			(double)((float)direction.getOffsetX() * f), (double)((float)direction.getOffsetY() * f), (double)((float)direction.getOffsetZ() * f)
		);
		BlockPos blockPos = this.method_58949(vec3d, direction);
		BlockPos blockPos2 = this.method_58949(vec3d2, direction);
		BlockPos.Mutable mutable = blockPos.mutableCopy();

		while (!mutable.equals(blockPos2)) {
			mutable.move(direction);
			if (arg.method_58998(this.getWorld(), mutable)) {
				BlockPos blockPos3 = mutable.offset(direction, -1);
				this.setPosition(Vec3d.of(blockPos3));
				this.method_58952();
				this.field_50508 = 5;
				return;
			}
		}

		this.setPosition(vec3d2);
	}

	private BlockPos method_58949(Vec3d vec3d, Direction direction) {
		BlockPos blockPos = BlockPos.ofFloored(vec3d);
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? blockPos.offset(direction) : blockPos;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(MOVEMENT_DIRECTION, Direction.NORTH);
		builder.add(MOVEMENT_SPEED, 0.0F);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.view.setGrid(Grid.readFromNbt(this.getRegistryManager().getWrapperOrThrow(RegistryKeys.BLOCK), nbt.getCompound("blocks")));
		if (nbt.contains("biome", NbtElement.STRING_TYPE)) {
			this.getRegistryManager().get(RegistryKeys.BIOME).getEntry(new Identifier(nbt.getString("biome"))).ifPresent(this.view::setBiome);
		}

		if (nbt.contains("movement_direction", NbtElement.STRING_TYPE)) {
			Direction direction = Direction.byName(nbt.getString("movement_direction"));
			if (direction != null) {
				this.method_58950(direction, nbt.getFloat("movement_speed"));
			}
		} else {
			this.method_58952();
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("blocks", this.view.getGrid().writeToNbt());
		this.view.getBiome().getKey().ifPresent(registryKey -> nbt.putString("biome", registryKey.getValue().toString()));
		nbt.putString("movement_direction", this.getGridMovementDirection().asString());
		nbt.putFloat("movement_speed", this.getGridMovementSpeed());
	}

	private float getGridMovementSpeed() {
		return this.getDataTracker().get(MOVEMENT_SPEED);
	}

	private Direction getGridMovementDirection() {
		return this.getDataTracker().get(MOVEMENT_DIRECTION);
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new AddSubGridS2CPacket(this);
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.field_50507 = new GridCarrierEntity.class_9512(2, x, y, z);
	}

	@Override
	public double getLerpTargetX() {
		return this.field_50507 != null ? this.field_50507.field_50510 : this.getX();
	}

	@Override
	public double getLerpTargetY() {
		return this.field_50507 != null ? this.field_50507.field_50511 : this.getY();
	}

	@Override
	public double getLerpTargetZ() {
		return this.field_50507 != null ? this.field_50507.field_50512 : this.getZ();
	}

	static class class_9512 {
		int field_50509;
		final double field_50510;
		final double field_50511;
		final double field_50512;

		class_9512(int i, double d, double e, double f) {
			this.field_50509 = i;
			this.field_50510 = d;
			this.field_50511 = e;
			this.field_50512 = f;
		}

		void method_58958(Entity entity) {
			entity.lerpPosAndRotation(this.field_50509, this.field_50510, this.field_50511, this.field_50512, 0.0, 0.0);
		}
	}
}
