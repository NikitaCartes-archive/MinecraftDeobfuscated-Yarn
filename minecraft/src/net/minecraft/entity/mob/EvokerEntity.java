package net.minecraft.entity.mob;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_1361;
import net.minecraft.class_1379;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class EvokerEntity extends SpellcastingIllagerEntity {
	private SheepEntity wololoTarget;

	public EvokerEntity(World world) {
		super(EntityType.EVOKER, world);
		this.setSize(0.6F, 1.95F);
		this.experiencePoints = 10;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EvokerEntity.LookAtTargetOrWololoTarget());
		this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
		this.goalSelector.add(4, new EvokerEntity.SummonVexGoal());
		this.goalSelector.add(5, new EvokerEntity.ConjureFangsGoal());
		this.goalSelector.add(6, new EvokerEntity.WololoGoal());
		this.goalSelector.add(8, new class_1379(this, 0.6));
		this.goalSelector.add(9, new class_1361(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 8.0F));
		this.targetSelector.add(1, new class_1399(this, IllagerEntity.class).method_6318());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, VillagerEntity.class, false).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
	}

	@Override
	protected void mobTick() {
		super.mobTick();
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (entity == null) {
			return false;
		} else if (entity == this) {
			return true;
		} else if (super.isTeammate(entity)) {
			return true;
		} else if (entity instanceof VexEntity) {
			return this.isTeammate(((VexEntity)entity).getOwner());
		} else {
			return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14782;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14599;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15111;
	}

	private void setWololoTarget(@Nullable SheepEntity sheepEntity) {
		this.wololoTarget = sheepEntity;
	}

	@Nullable
	private SheepEntity getWololoTarget() {
		return this.wololoTarget;
	}

	@Override
	protected SoundEvent method_7142() {
		return SoundEvents.field_14858;
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	class ConjureFangsGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private ConjureFangsGoal() {
		}

		@Override
		protected int getSpellTicks() {
			return 40;
		}

		@Override
		protected int startTimeDelay() {
			return 100;
		}

		@Override
		protected void castSpell() {
			LivingEntity livingEntity = EvokerEntity.this.getTarget();
			double d = Math.min(livingEntity.y, EvokerEntity.this.y);
			double e = Math.max(livingEntity.y, EvokerEntity.this.y) + 1.0;
			float f = (float)MathHelper.atan2(livingEntity.z - EvokerEntity.this.z, livingEntity.x - EvokerEntity.this.x);
			if (EvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
				for (int i = 0; i < 5; i++) {
					float g = f + (float)i * (float) Math.PI * 0.4F;
					this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.z + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
				}

				for (int i = 0; i < 8; i++) {
					float g = f + (float)i * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
					this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(g) * 2.5, EvokerEntity.this.z + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
				}
			} else {
				for (int i = 0; i < 16; i++) {
					double h = 1.25 * (double)(i + 1);
					int j = 1 * i;
					this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(f) * h, EvokerEntity.this.z + (double)MathHelper.sin(f) * h, d, e, f, j);
				}
			}
		}

		private void conjureFangs(double d, double e, double f, double g, float h, int i) {
			BlockPos blockPos = new BlockPos(d, g, e);
			boolean bl = false;
			double j = 0.0;

			do {
				if (!EvokerEntity.this.world.doesBlockHaveSolidTopSurface(blockPos) && EvokerEntity.this.world.doesBlockHaveSolidTopSurface(blockPos.down())) {
					if (!EvokerEntity.this.world.isAir(blockPos)) {
						BlockState blockState = EvokerEntity.this.world.getBlockState(blockPos);
						VoxelShape voxelShape = blockState.getCollisionShape(EvokerEntity.this.world, blockPos);
						if (!voxelShape.isEmpty()) {
							j = voxelShape.getMaximum(Direction.Axis.Y);
						}
					}

					bl = true;
					break;
				}

				blockPos = blockPos.down();
			} while (blockPos.getY() >= MathHelper.floor(f) - 1);

			if (bl) {
				EvokerFangsEntity evokerFangsEntity = new EvokerFangsEntity(EvokerEntity.this.world, d, (double)blockPos.getY() + j, e, h, i, EvokerEntity.this);
				EvokerEntity.this.world.spawnEntity(evokerFangsEntity);
			}
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.field_14908;
		}

		@Override
		protected SpellcastingIllagerEntity.class_1618 method_7147() {
			return SpellcastingIllagerEntity.class_1618.field_7380;
		}
	}

	class LookAtTargetOrWololoTarget extends SpellcastingIllagerEntity.LookAtTargetGoal {
		private LookAtTargetOrWololoTarget() {
		}

		@Override
		public void tick() {
			if (EvokerEntity.this.getTarget() != null) {
				EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.method_5978());
			} else if (EvokerEntity.this.getWololoTarget() != null) {
				EvokerEntity.this.getLookControl()
					.lookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.method_5978());
			}
		}
	}

	class SummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private SummonVexGoal() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else {
				int i = EvokerEntity.this.world.getVisibleEntities(VexEntity.class, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
				return EvokerEntity.this.random.nextInt(8) + 1 > i;
			}
		}

		@Override
		protected int getSpellTicks() {
			return 100;
		}

		@Override
		protected int startTimeDelay() {
			return 340;
		}

		@Override
		protected void castSpell() {
			for (int i = 0; i < 3; i++) {
				BlockPos blockPos = new BlockPos(EvokerEntity.this).add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
				VexEntity vexEntity = new VexEntity(EvokerEntity.this.world);
				vexEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				vexEntity.prepareEntityData(EvokerEntity.this.world, EvokerEntity.this.world.getLocalDifficulty(blockPos), SpawnType.field_16471, null, null);
				vexEntity.setOwner(EvokerEntity.this);
				vexEntity.setBounds(blockPos);
				vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
				EvokerEntity.this.world.spawnEntity(vexEntity);
			}
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.field_15193;
		}

		@Override
		protected SpellcastingIllagerEntity.class_1618 method_7147() {
			return SpellcastingIllagerEntity.class_1618.field_7379;
		}
	}

	public class WololoGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private final Predicate<SheepEntity> purpleSheepPredicate = sheepEntity -> sheepEntity.getColor() == DyeColor.BLUE;

		@Override
		public boolean canStart() {
			if (EvokerEntity.this.getTarget() != null) {
				return false;
			} else if (EvokerEntity.this.method_7137()) {
				return false;
			} else if (EvokerEntity.this.age < this.startTime) {
				return false;
			} else if (!EvokerEntity.this.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else {
				List<SheepEntity> list = EvokerEntity.this.world
					.getEntities(SheepEntity.class, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0), this.purpleSheepPredicate);
				if (list.isEmpty()) {
					return false;
				} else {
					EvokerEntity.this.setWololoTarget((SheepEntity)list.get(EvokerEntity.this.random.nextInt(list.size())));
					return true;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
		}

		@Override
		public void onRemove() {
			super.onRemove();
			EvokerEntity.this.setWololoTarget(null);
		}

		@Override
		protected void castSpell() {
			SheepEntity sheepEntity = EvokerEntity.this.getWololoTarget();
			if (sheepEntity != null && sheepEntity.isValid()) {
				sheepEntity.setColor(DyeColor.RED);
			}
		}

		@Override
		protected int getInitialCooldown() {
			return 40;
		}

		@Override
		protected int getSpellTicks() {
			return 60;
		}

		@Override
		protected int startTimeDelay() {
			return 140;
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.field_15058;
		}

		@Override
		protected SpellcastingIllagerEntity.class_1618 method_7147() {
			return SpellcastingIllagerEntity.class_1618.field_7381;
		}
	}
}
