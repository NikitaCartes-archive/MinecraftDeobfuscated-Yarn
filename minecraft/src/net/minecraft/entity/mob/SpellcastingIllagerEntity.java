package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class SpellcastingIllagerEntity extends IllagerEntity {
	private static final TrackedData<Byte> SPELL = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected int spellTicks;
	private SpellcastingIllagerEntity.Spell spell = SpellcastingIllagerEntity.Spell.NONE;

	protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SPELL, (byte)0);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.spellTicks = nbt.getInt("SpellTicks");
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("SpellTicks", this.spellTicks);
	}

	@Override
	public IllagerEntity.State getState() {
		if (this.isSpellcasting()) {
			return IllagerEntity.State.SPELLCASTING;
		} else {
			return this.isCelebrating() ? IllagerEntity.State.CELEBRATING : IllagerEntity.State.CROSSED;
		}
	}

	public boolean isSpellcasting() {
		return this.getWorld().isClient ? this.dataTracker.get(SPELL) > 0 : this.spellTicks > 0;
	}

	public void setSpell(SpellcastingIllagerEntity.Spell spell) {
		this.spell = spell;
		this.dataTracker.set(SPELL, (byte)spell.id);
	}

	protected SpellcastingIllagerEntity.Spell getSpell() {
		return !this.getWorld().isClient ? this.spell : SpellcastingIllagerEntity.Spell.byId(this.dataTracker.get(SPELL));
	}

	@Override
	protected void mobTick(ServerWorld world) {
		super.mobTick(world);
		if (this.spellTicks > 0) {
			this.spellTicks--;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient && this.isSpellcasting()) {
			SpellcastingIllagerEntity.Spell spell = this.getSpell();
			float f = (float)spell.particleVelocity[0];
			float g = (float)spell.particleVelocity[1];
			float h = (float)spell.particleVelocity[2];
			float i = this.bodyYaw * (float) (Math.PI / 180.0) + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
			float j = MathHelper.cos(i);
			float k = MathHelper.sin(i);
			double d = 0.6 * (double)this.getScale();
			double e = 1.8 * (double)this.getScale();
			this.getWorld()
				.addParticle(
					EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h),
					this.getX() + (double)j * d,
					this.getY() + e,
					this.getZ() + (double)k * d,
					0.0,
					0.0,
					0.0
				);
			this.getWorld()
				.addParticle(
					EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, f, g, h),
					this.getX() - (double)j * d,
					this.getY() + e,
					this.getZ() - (double)k * d,
					0.0,
					0.0,
					0.0
				);
		}
	}

	protected int getSpellTicks() {
		return this.spellTicks;
	}

	protected abstract SoundEvent getCastSpellSound();

	protected abstract class CastSpellGoal extends Goal {
		protected int spellCooldown;
		protected int startTime;

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
			if (livingEntity == null || !livingEntity.isAlive()) {
				return false;
			} else {
				return SpellcastingIllagerEntity.this.isSpellcasting() ? false : SpellcastingIllagerEntity.this.age >= this.startTime;
			}
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
			return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
		}

		@Override
		public void start() {
			this.spellCooldown = this.getTickCount(this.getInitialCooldown());
			SpellcastingIllagerEntity.this.spellTicks = this.getSpellTicks();
			this.startTime = SpellcastingIllagerEntity.this.age + this.startTimeDelay();
			SoundEvent soundEvent = this.getSoundPrepare();
			if (soundEvent != null) {
				SpellcastingIllagerEntity.this.playSound(soundEvent, 1.0F, 1.0F);
			}

			SpellcastingIllagerEntity.this.setSpell(this.getSpell());
		}

		@Override
		public void tick() {
			this.spellCooldown--;
			if (this.spellCooldown == 0) {
				this.castSpell();
				SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastSpellSound(), 1.0F, 1.0F);
			}
		}

		protected abstract void castSpell();

		protected int getInitialCooldown() {
			return 20;
		}

		protected abstract int getSpellTicks();

		protected abstract int startTimeDelay();

		@Nullable
		protected abstract SoundEvent getSoundPrepare();

		protected abstract SpellcastingIllagerEntity.Spell getSpell();
	}

	protected class LookAtTargetGoal extends Goal {
		public LookAtTargetGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
		}

		@Override
		public void start() {
			super.start();
			SpellcastingIllagerEntity.this.navigation.stop();
		}

		@Override
		public void stop() {
			super.stop();
			SpellcastingIllagerEntity.this.setSpell(SpellcastingIllagerEntity.Spell.NONE);
		}

		@Override
		public void tick() {
			if (SpellcastingIllagerEntity.this.getTarget() != null) {
				SpellcastingIllagerEntity.this.getLookControl()
					.lookAt(
						SpellcastingIllagerEntity.this.getTarget(),
						(float)SpellcastingIllagerEntity.this.getMaxHeadRotation(),
						(float)SpellcastingIllagerEntity.this.getMaxLookPitchChange()
					);
			}
		}
	}

	protected static enum Spell {
		NONE(0, 0.0, 0.0, 0.0),
		SUMMON_VEX(1, 0.7, 0.7, 0.8),
		FANGS(2, 0.4, 0.3, 0.35),
		WOLOLO(3, 0.7, 0.5, 0.2),
		DISAPPEAR(4, 0.3, 0.3, 0.8),
		BLINDNESS(5, 0.1, 0.1, 0.2);

		private static final IntFunction<SpellcastingIllagerEntity.Spell> BY_ID = ValueLists.createIdToValueFunction(
			spell -> spell.id, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		final int id;
		final double[] particleVelocity;

		private Spell(final int id, final double particleVelocityX, final double particleVelocityY, final double particleVelocityZ) {
			this.id = id;
			this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
		}

		public static SpellcastingIllagerEntity.Spell byId(int id) {
			return (SpellcastingIllagerEntity.Spell)BY_ID.apply(id);
		}
	}
}
