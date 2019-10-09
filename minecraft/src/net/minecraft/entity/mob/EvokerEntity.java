package net.minecraft.entity.mob;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EvokerEntity extends SpellcastingIllagerEntity {
	private SheepEntity wololoTarget;

	public EvokerEntity(EntityType<? extends EvokerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 10;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EvokerEntity.LookAtTargetOrWololoTarget());
		this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
		this.goalSelector.add(4, new EvokerEntity.SummonVexGoal());
		this.goalSelector.add(5, new EvokerEntity.ConjureFangsGoal());
		this.goalSelector.add(6, new EvokerEntity.WololoGoal());
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
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
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_EVOKER_CELEBRATE;
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
		return SoundEvents.ENTITY_EVOKER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_EVOKER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_EVOKER_HURT;
	}

	private void setWololoTarget(@Nullable SheepEntity sheepEntity) {
		this.wololoTarget = sheepEntity;
	}

	@Nullable
	private SheepEntity getWololoTarget() {
		return this.wololoTarget;
	}

	@Override
	protected SoundEvent getCastSpellSound() {
		return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
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
			double d = Math.min(livingEntity.getY(), EvokerEntity.this.getY());
			double e = Math.max(livingEntity.getY(), EvokerEntity.this.getY()) + 1.0;
			float f = (float)MathHelper.atan2(livingEntity.getZ() - EvokerEntity.this.getZ(), livingEntity.getX() - EvokerEntity.this.getX());
			if (EvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
				for (int i = 0; i < 5; i++) {
					float g = f + (float)i * (float) Math.PI * 0.4F;
					this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
				}

				for (int i = 0; i < 8; i++) {
					float g = f + (float)i * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
					this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(g) * 2.5, EvokerEntity.this.getZ() + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
				}
			} else {
				for (int i = 0; i < 16; i++) {
					double h = 1.25 * (double)(i + 1);
					int j = 1 * i;
					this.conjureFangs(EvokerEntity.this.getX() + (double)MathHelper.cos(f) * h, EvokerEntity.this.getZ() + (double)MathHelper.sin(f) * h, d, e, f, j);
				}
			}
		}

		private void conjureFangs(double d, double e, double f, double g, float h, int i) {
			BlockPos blockPos = new BlockPos(d, g, e);
			boolean bl = false;
			double j = 0.0;

			do {
				BlockPos blockPos2 = blockPos.method_10074();
				BlockState blockState = EvokerEntity.this.world.getBlockState(blockPos2);
				if (blockState.isSideSolidFullSquare(EvokerEntity.this.world, blockPos2, Direction.UP)) {
					if (!EvokerEntity.this.world.isAir(blockPos)) {
						BlockState blockState2 = EvokerEntity.this.world.getBlockState(blockPos);
						VoxelShape voxelShape = blockState2.getCollisionShape(EvokerEntity.this.world, blockPos);
						if (!voxelShape.isEmpty()) {
							j = voxelShape.getMaximum(Direction.Axis.Y);
						}
					}

					bl = true;
					break;
				}

				blockPos = blockPos.method_10074();
			} while (blockPos.getY() >= MathHelper.floor(f) - 1);

			if (bl) {
				EvokerEntity.this.world.spawnEntity(new EvokerFangsEntity(EvokerEntity.this.world, d, (double)blockPos.getY() + j, e, h, i, EvokerEntity.this));
			}
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.FANGS;
		}
	}

	class LookAtTargetOrWololoTarget extends SpellcastingIllagerEntity.LookAtTargetGoal {
		private LookAtTargetOrWololoTarget() {
		}

		@Override
		public void tick() {
			if (EvokerEntity.this.getTarget() != null) {
				EvokerEntity.this.getLookControl()
					.lookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.getLookPitchSpeed());
			} else if (EvokerEntity.this.getWololoTarget() != null) {
				EvokerEntity.this.getLookControl()
					.lookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.getLookPitchSpeed());
			}
		}
	}

	class SummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private final TargetPredicate closeVexPredicate = new TargetPredicate()
			.setBaseMaxDistance(16.0)
			.includeHidden()
			.ignoreDistanceScalingFactor()
			.includeInvulnerable()
			.includeTeammates();

		private SummonVexGoal() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else {
				int i = EvokerEntity.this.world
					.getTargets(VexEntity.class, this.closeVexPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0))
					.size();
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
				VexEntity vexEntity = EntityType.VEX.create(EvokerEntity.this.world);
				vexEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				vexEntity.initialize(EvokerEntity.this.world, EvokerEntity.this.world.getLocalDifficulty(blockPos), SpawnType.MOB_SUMMONED, null, null);
				vexEntity.setOwner(EvokerEntity.this);
				vexEntity.setBounds(blockPos);
				vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
				EvokerEntity.this.world.spawnEntity(vexEntity);
			}
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
		}
	}

	public class WololoGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private final TargetPredicate purpleSheepPredicate = new TargetPredicate()
			.setBaseMaxDistance(16.0)
			.includeInvulnerable()
			.setPredicate(livingEntity -> ((SheepEntity)livingEntity).getColor() == DyeColor.BLUE);

		@Override
		public boolean canStart() {
			if (EvokerEntity.this.getTarget() != null) {
				return false;
			} else if (EvokerEntity.this.isSpellcasting()) {
				return false;
			} else if (EvokerEntity.this.age < this.startTime) {
				return false;
			} else if (!EvokerEntity.this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
				return false;
			} else {
				List<SheepEntity> list = EvokerEntity.this.world
					.getTargets(SheepEntity.class, this.purpleSheepPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
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
		public void stop() {
			super.stop();
			EvokerEntity.this.setWololoTarget(null);
		}

		@Override
		protected void castSpell() {
			SheepEntity sheepEntity = EvokerEntity.this.getWololoTarget();
			if (sheepEntity != null && sheepEntity.isAlive()) {
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
			return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.WOLOLO;
		}
	}
}
