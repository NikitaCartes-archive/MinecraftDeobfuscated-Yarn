package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3730;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.Raid;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;

public abstract class PatrolEntity extends HostileEntity {
	private BlockPos field_16478;
	private boolean field_16479;
	private boolean field_16477;

	protected PatrolEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(4, new PatrolEntity.PatrolGoal<>(this, 0.7, 0.595));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.field_16478 != null) {
			compoundTag.put("PatrolTarget", TagHelper.serializeBlockPos(this.field_16478));
		}

		compoundTag.putBoolean("PatrolLeader", this.field_16479);
		compoundTag.putBoolean("Patrolling", this.field_16477);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("PatrolTarget")) {
			this.field_16478 = TagHelper.deserializeBlockPos(compoundTag.getCompound("PatrolTarget"));
		}

		this.field_16479 = compoundTag.getBoolean("PatrolLeader");
		this.field_16477 = compoundTag.getBoolean("Patrolling");
	}

	@Override
	public double getHeightOffset() {
		return -0.45;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (this.method_16219()) {
			this.setEquippedStack(EquipmentSlot.HEAD, Raid.illagerBanner);
			this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
		}

		if (arg == class_3730.field_16527) {
			this.field_16477 = true;
		}

		return super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
	}

	@Override
	public boolean method_5974(double d) {
		return !this.field_16477 || d > 16384.0;
	}

	public void method_16216(BlockPos blockPos) {
		this.field_16478 = blockPos;
		this.field_16477 = true;
	}

	public BlockPos method_16215() {
		return this.field_16478;
	}

	public boolean method_16220() {
		return this.field_16478 != null;
	}

	public void method_16217(boolean bl) {
		this.field_16479 = bl;
		this.field_16477 = true;
	}

	public boolean method_16219() {
		return this.field_16479;
	}

	public boolean method_16472() {
		return true;
	}

	public void method_16218() {
		this.field_16478 = new BlockPos(this).add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
		this.field_16477 = true;
	}

	public static class PatrolGoal<T extends PatrolEntity> extends Goal {
		private final T field_16481;
		private final double field_16480;
		private final double field_16535;

		public PatrolGoal(T patrolEntity, double d, double e) {
			this.field_16481 = patrolEntity;
			this.field_16480 = d;
			this.field_16535 = e;
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			return this.field_16481.getTarget() == null && !this.field_16481.hasPassengers() && this.field_16481.method_16220();
		}

		@Override
		public void start() {
		}

		@Override
		public void onRemove() {
		}

		@Override
		public void tick() {
			boolean bl = this.field_16481.method_16219();
			EntityNavigation entityNavigation = this.field_16481.getNavigation();
			if (entityNavigation.method_6357()) {
				double d = this.field_16481.squaredDistanceTo(this.field_16481.method_16215());
				if (bl && !(d >= 100.0)) {
					this.field_16481.method_16218();
				} else {
					Vec3d vec3d = new Vec3d(this.field_16481.method_16215());
					Vec3d vec3d2 = new Vec3d(this.field_16481.x, this.field_16481.y, this.field_16481.z);
					Vec3d vec3d3 = vec3d2.subtract(vec3d);
					vec3d = vec3d3.rotateY(90.0F).multiply(0.4).add(vec3d);
					Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
					BlockPos blockPos = new BlockPos((int)vec3d4.x, (int)vec3d4.y, (int)vec3d4.z);
					blockPos = this.field_16481.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
					if (!entityNavigation.method_6337((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.field_16535 : this.field_16480)) {
						this.method_16222();
					} else if (bl) {
						for (PatrolEntity patrolEntity : this.field_16481
							.world
							.getEntities(
								PatrolEntity.class, this.field_16481.getBoundingBox().expand(16.0), patrolEntityx -> !patrolEntityx.method_16219() && patrolEntityx.method_16472()
							)) {
							patrolEntity.method_16216(blockPos);
						}
					}
				}
			}
		}

		private void method_16222() {
			Random random = this.field_16481.getRand();
			BlockPos blockPos = this.field_16481
				.world
				.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.field_16481).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
			this.field_16481.getNavigation().method_6337((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.field_16480);
		}
	}
}
