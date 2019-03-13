package net.minecraft.entity.mob;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_1399;
import net.minecraft.class_4051;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
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

	public EvokerEntity(EntityType<? extends EvokerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 10;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new EvokerEntity.LookAtTargetOrWololoTarget());
		this.field_6201.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 0.6, 1.0));
		this.field_6201.add(4, new EvokerEntity.SummonVexGoal());
		this.field_6201.add(5, new EvokerEntity.ConjureFangsGoal());
		this.field_6201.add(6, new EvokerEntity.WololoGoal());
		this.field_6201.add(8, new WanderAroundGoal(this, 0.6));
		this.field_6201.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.field_6201.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.field_6185.add(1, new class_1399(this, IllagerEntity.class).method_6318());
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
		this.field_6185.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
		this.field_6185.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
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
			return entity instanceof LivingEntity && ((LivingEntity)entity).method_6046() == EntityGroup.ILLAGER
				? this.method_5781() == null && entity.method_5781() == null
				: false;
		}
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14782;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14599;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
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
				if (!EvokerEntity.this.field_6002.method_8515(blockPos) && EvokerEntity.this.field_6002.method_8515(blockPos.down())) {
					if (!EvokerEntity.this.field_6002.method_8623(blockPos)) {
						BlockState blockState = EvokerEntity.this.field_6002.method_8320(blockPos);
						VoxelShape voxelShape = blockState.method_11628(EvokerEntity.this.field_6002, blockPos);
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
				EvokerEntity.this.field_6002.spawnEntity(new EvokerFangsEntity(EvokerEntity.this.field_6002, d, (double)blockPos.getY() + j, e, h, i, EvokerEntity.this));
			}
		}

		@Override
		protected SoundEvent method_7150() {
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
				EvokerEntity.this.method_5988().lookAt(EvokerEntity.this.getTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.method_5978());
			} else if (EvokerEntity.this.getWololoTarget() != null) {
				EvokerEntity.this.method_5988().lookAt(EvokerEntity.this.getWololoTarget(), (float)EvokerEntity.this.method_5986(), (float)EvokerEntity.this.method_5978());
			}
		}
	}

	class SummonVexGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private final class_4051 field_18129 = new class_4051().method_18418(16.0).method_18422().method_18424().method_18417().method_18421();

		private SummonVexGoal() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else {
				int i = EvokerEntity.this.field_6002
					.method_18466(VexEntity.class, this.field_18129, EvokerEntity.this, EvokerEntity.this.method_5829().expand(16.0))
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
				VexEntity vexEntity = EntityType.VEX.method_5883(EvokerEntity.this.field_6002);
				vexEntity.method_5725(blockPos, 0.0F, 0.0F);
				vexEntity.method_5943(EvokerEntity.this.field_6002, EvokerEntity.this.field_6002.method_8404(blockPos), SpawnType.field_16471, null, null);
				vexEntity.setOwner(EvokerEntity.this);
				vexEntity.method_7188(blockPos);
				vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
				EvokerEntity.this.field_6002.spawnEntity(vexEntity);
			}
		}

		@Override
		protected SoundEvent method_7150() {
			return SoundEvents.field_15193;
		}

		@Override
		protected SpellcastingIllagerEntity.class_1618 method_7147() {
			return SpellcastingIllagerEntity.class_1618.field_7379;
		}
	}

	public class WololoGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private final class_4051 purpleSheepPredicate = new class_4051()
			.method_18418(16.0)
			.method_18417()
			.method_18420(livingEntity -> ((SheepEntity)livingEntity).method_6633() == DyeColor.field_7966);

		@Override
		public boolean canStart() {
			if (EvokerEntity.this.getTarget() != null) {
				return false;
			} else if (EvokerEntity.this.method_7137()) {
				return false;
			} else if (EvokerEntity.this.age < this.startTime) {
				return false;
			} else if (!EvokerEntity.this.field_6002.getGameRules().getBoolean("mobGriefing")) {
				return false;
			} else {
				List<SheepEntity> list = EvokerEntity.this.field_6002
					.method_18466(SheepEntity.class, this.purpleSheepPredicate, EvokerEntity.this, EvokerEntity.this.method_5829().expand(16.0, 4.0, 16.0));
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
				sheepEntity.method_6631(DyeColor.field_7964);
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
		protected SoundEvent method_7150() {
			return SoundEvents.field_15058;
		}

		@Override
		protected SpellcastingIllagerEntity.class_1618 method_7147() {
			return SpellcastingIllagerEntity.class_1618.field_7381;
		}
	}
}
