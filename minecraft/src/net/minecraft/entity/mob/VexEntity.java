package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1399;
import net.minecraft.class_4051;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VexEntity extends HostileEntity {
	protected static final TrackedData<Byte> field_7410 = DataTracker.registerData(VexEntity.class, TrackedDataHandlerRegistry.BYTE);
	private MobEntity owner;
	@Nullable
	private BlockPos field_7407;
	private boolean alive;
	private int lifeTicks;

	public VexEntity(EntityType<? extends VexEntity> entityType, World world) {
		super(entityType, world);
		this.fireImmune = true;
		this.field_6207 = new VexEntity.VexMoveControl(this);
		this.experiencePoints = 3;
	}

	@Override
	public void method_5784(MovementType movementType, Vec3d vec3d) {
		super.method_5784(movementType, vec3d);
		this.checkBlockCollision();
	}

	@Override
	public void update() {
		this.noClip = true;
		super.update();
		this.noClip = false;
		this.setUnaffectedByGravity(true);
		if (this.alive && --this.lifeTicks <= 0) {
			this.lifeTicks = 20;
			this.damage(DamageSource.STARVE, 1.0F);
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(4, new VexEntity.ChargeTargetGoal());
		this.field_6201.add(8, new VexEntity.LookAtTargetGoal());
		this.field_6201.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.field_6201.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.field_6185.add(1, new class_1399(this, IllagerEntity.class).method_6318());
		this.field_6185.add(2, new VexEntity.TrackOwnerTargetGoal(this));
		this.field_6185.add(3, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(14.0);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7410, (byte)0);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("BoundX")) {
			this.field_7407 = new BlockPos(compoundTag.getInt("BoundX"), compoundTag.getInt("BoundY"), compoundTag.getInt("BoundZ"));
		}

		if (compoundTag.containsKey("LifeTicks")) {
			this.setLifeTicks(compoundTag.getInt("LifeTicks"));
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (this.field_7407 != null) {
			compoundTag.putInt("BoundX", this.field_7407.getX());
			compoundTag.putInt("BoundY", this.field_7407.getY());
			compoundTag.putInt("BoundZ", this.field_7407.getZ());
		}

		if (this.alive) {
			compoundTag.putInt("LifeTicks", this.lifeTicks);
		}
	}

	public MobEntity getOwner() {
		return this.owner;
	}

	@Nullable
	public BlockPos method_7186() {
		return this.field_7407;
	}

	public void method_7188(@Nullable BlockPos blockPos) {
		this.field_7407 = blockPos;
	}

	private boolean method_7184(int i) {
		int j = this.field_6011.get(field_7410);
		return (j & i) != 0;
	}

	private void method_7189(int i, boolean bl) {
		int j = this.field_6011.get(field_7410);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.field_6011.set(field_7410, (byte)(j & 0xFF));
	}

	public boolean method_7176() {
		return this.method_7184(1);
	}

	public void method_7177(boolean bl) {
		this.method_7189(1, bl);
	}

	public void setOwner(MobEntity mobEntity) {
		this.owner = mobEntity;
	}

	public void setLifeTicks(int i) {
		this.alive = true;
		this.lifeTicks = i;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14812;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14964;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15072;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8371));
		this.setEquipmentDropChance(EquipmentSlot.HAND_MAIN, 0.0F);
	}

	class ChargeTargetGoal extends Goal {
		public ChargeTargetGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			return VexEntity.this.getTarget() != null && !VexEntity.this.method_5962().isMoving() && VexEntity.this.random.nextInt(7) == 0
				? VexEntity.this.squaredDistanceTo(VexEntity.this.getTarget()) > 4.0
				: false;
		}

		@Override
		public boolean shouldContinue() {
			return VexEntity.this.method_5962().isMoving() && VexEntity.this.method_7176() && VexEntity.this.getTarget() != null && VexEntity.this.getTarget().isValid();
		}

		@Override
		public void start() {
			LivingEntity livingEntity = VexEntity.this.getTarget();
			Vec3d vec3d = livingEntity.method_5836(1.0F);
			VexEntity.this.field_6207.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
			VexEntity.this.method_7177(true);
			VexEntity.this.method_5783(SoundEvents.field_14898, 1.0F, 1.0F);
		}

		@Override
		public void onRemove() {
			VexEntity.this.method_7177(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = VexEntity.this.getTarget();
			if (VexEntity.this.method_5829().intersects(livingEntity.method_5829())) {
				VexEntity.this.attack(livingEntity);
				VexEntity.this.method_7177(false);
			} else {
				double d = VexEntity.this.squaredDistanceTo(livingEntity);
				if (d < 9.0) {
					Vec3d vec3d = livingEntity.method_5836(1.0F);
					VexEntity.this.field_6207.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
				}
			}
		}
	}

	class LookAtTargetGoal extends Goal {
		public LookAtTargetGoal() {
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			return !VexEntity.this.method_5962().isMoving() && VexEntity.this.random.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void tick() {
			BlockPos blockPos = VexEntity.this.method_7186();
			if (blockPos == null) {
				blockPos = new BlockPos(VexEntity.this);
			}

			for (int i = 0; i < 3; i++) {
				BlockPos blockPos2 = blockPos.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
				if (VexEntity.this.field_6002.method_8623(blockPos2)) {
					VexEntity.this.field_6207.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
					if (VexEntity.this.getTarget() == null) {
						VexEntity.this.method_5988().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0F, 20.0F);
					}
					break;
				}
			}
		}
	}

	class TrackOwnerTargetGoal extends TrackTargetGoal {
		private final class_4051 field_18132 = new class_4051().method_18422().method_18424();

		public TrackOwnerTargetGoal(MobEntityWithAi mobEntityWithAi) {
			super(mobEntityWithAi, false);
		}

		@Override
		public boolean canStart() {
			return VexEntity.this.owner != null && VexEntity.this.owner.getTarget() != null && this.method_6328(VexEntity.this.owner.getTarget(), this.field_18132);
		}

		@Override
		public void start() {
			VexEntity.this.setTarget(VexEntity.this.owner.getTarget());
			super.start();
		}
	}

	class VexMoveControl extends MoveControl {
		public VexMoveControl(VexEntity vexEntity2) {
			super(vexEntity2);
		}

		@Override
		public void tick() {
			if (this.state == MoveControl.State.field_6378) {
				Vec3d vec3d = new Vec3d(this.targetX - VexEntity.this.x, this.targetY - VexEntity.this.y, this.targetZ - VexEntity.this.z);
				double d = vec3d.length();
				if (d < VexEntity.this.method_5829().averageDimension()) {
					this.state = MoveControl.State.field_6377;
					VexEntity.this.method_18799(VexEntity.this.method_18798().multiply(0.5));
				} else {
					VexEntity.this.method_18799(VexEntity.this.method_18798().add(vec3d.multiply(this.speed * 0.05 / d)));
					if (VexEntity.this.getTarget() == null) {
						Vec3d vec3d2 = VexEntity.this.method_18798();
						VexEntity.this.yaw = -((float)MathHelper.atan2(vec3d2.x, vec3d2.z)) * (180.0F / (float)Math.PI);
						VexEntity.this.field_6283 = VexEntity.this.yaw;
					} else {
						double e = VexEntity.this.getTarget().x - VexEntity.this.x;
						double f = VexEntity.this.getTarget().z - VexEntity.this.z;
						VexEntity.this.yaw = -((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI);
						VexEntity.this.field_6283 = VexEntity.this.yaw;
					}
				}
			}
		}
	}
}
