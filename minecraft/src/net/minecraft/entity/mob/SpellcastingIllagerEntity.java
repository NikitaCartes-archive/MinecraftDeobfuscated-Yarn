package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class SpellcastingIllagerEntity extends IllagerEntity {
	private static final TrackedData<Byte> SPELL = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected int spellTicks;
	private SpellcastingIllagerEntity.Spell spell = SpellcastingIllagerEntity.Spell.field_7377;

	protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SPELL, (byte)0);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.spellTicks = tag.getInt("SpellTicks");
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("SpellTicks", this.spellTicks);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State getState() {
		if (this.isSpellcasting()) {
			return IllagerEntity.State.field_7212;
		} else {
			return this.isCelebrating() ? IllagerEntity.State.field_19012 : IllagerEntity.State.field_7207;
		}
	}

	public boolean isSpellcasting() {
		return this.world.isClient ? this.dataTracker.get(SPELL) > 0 : this.spellTicks > 0;
	}

	public void setSpell(SpellcastingIllagerEntity.Spell spell) {
		this.spell = spell;
		this.dataTracker.set(SPELL, (byte)spell.id);
	}

	protected SpellcastingIllagerEntity.Spell getSpell() {
		return !this.world.isClient ? this.spell : SpellcastingIllagerEntity.Spell.byId(this.dataTracker.get(SPELL));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (this.spellTicks > 0) {
			this.spellTicks--;
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && this.isSpellcasting()) {
			SpellcastingIllagerEntity.Spell spell = this.getSpell();
			double d = spell.particleVelocity[0];
			double e = spell.particleVelocity[1];
			double f = spell.particleVelocity[2];
			float g = this.bodyYaw * (float) (Math.PI / 180.0) + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
			float h = MathHelper.cos(g);
			float i = MathHelper.sin(g);
			this.world.addParticle(ParticleTypes.field_11226, this.getX() + (double)h * 0.6, this.getY() + 1.8, this.getZ() + (double)i * 0.6, d, e, f);
			this.world.addParticle(ParticleTypes.field_11226, this.getX() - (double)h * 0.6, this.getY() + 1.8, this.getZ() - (double)i * 0.6, d, e, f);
		}
	}

	protected int getSpellTicks() {
		return this.spellTicks;
	}

	protected abstract SoundEvent getCastSpellSound();

	public abstract class CastSpellGoal extends Goal {
		protected int spellCooldown;
		protected int startTime;

		protected CastSpellGoal() {
		}

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
			this.spellCooldown = this.getInitialCooldown();
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

	public class LookAtTargetGoal extends Goal {
		public LookAtTargetGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
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
			SpellcastingIllagerEntity.this.setSpell(SpellcastingIllagerEntity.Spell.field_7377);
		}

		@Override
		public void tick() {
			if (SpellcastingIllagerEntity.this.getTarget() != null) {
				SpellcastingIllagerEntity.this.getLookControl()
					.lookAt(
						SpellcastingIllagerEntity.this.getTarget(),
						(float)SpellcastingIllagerEntity.this.getBodyYawSpeed(),
						(float)SpellcastingIllagerEntity.this.getLookPitchSpeed()
					);
			}
		}
	}

	public static enum Spell {
		field_7377(0, 0.0, 0.0, 0.0),
		field_7379(1, 0.7, 0.7, 0.8),
		field_7380(2, 0.4, 0.3, 0.35),
		field_7381(3, 0.7, 0.5, 0.2),
		field_7382(4, 0.3, 0.3, 0.8),
		field_7378(5, 0.1, 0.1, 0.2);

		private final int id;
		private final double[] particleVelocity;

		private Spell(int id, double particleVelocityX, double particleVelocityY, double particleVelocityZ) {
			this.id = id;
			this.particleVelocity = new double[]{particleVelocityX, particleVelocityY, particleVelocityZ};
		}

		public static SpellcastingIllagerEntity.Spell byId(int id) {
			for (SpellcastingIllagerEntity.Spell spell : values()) {
				if (id == spell.id) {
					return spell;
				}
			}

			return field_7377;
		}
	}
}
