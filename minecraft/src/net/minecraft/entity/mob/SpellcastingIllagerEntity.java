package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
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
	private static final TrackedData<Byte> field_7373 = DataTracker.registerData(SpellcastingIllagerEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected int spellTicks;
	private SpellcastingIllagerEntity.class_1618 field_7371 = SpellcastingIllagerEntity.class_1618.field_7377;

	protected SpellcastingIllagerEntity(EntityType<? extends SpellcastingIllagerEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_7373, (byte)0);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.spellTicks = compoundTag.getInt("SpellTicks");
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("SpellTicks", this.spellTicks);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State method_6990() {
		return this.method_7137() ? IllagerEntity.State.field_7212 : IllagerEntity.State.field_7207;
	}

	public boolean method_7137() {
		return this.world.isClient ? this.dataTracker.get(field_7373) > 0 : this.spellTicks > 0;
	}

	public void method_7138(SpellcastingIllagerEntity.class_1618 arg) {
		this.field_7371 = arg;
		this.dataTracker.set(field_7373, (byte)arg.field_7375);
	}

	protected SpellcastingIllagerEntity.class_1618 method_7140() {
		return !this.world.isClient ? this.field_7371 : SpellcastingIllagerEntity.class_1618.method_7144(this.dataTracker.get(field_7373));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		if (this.spellTicks > 0) {
			this.spellTicks--;
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.world.isClient && this.method_7137()) {
			SpellcastingIllagerEntity.class_1618 lv = this.method_7140();
			double d = lv.field_7374[0];
			double e = lv.field_7374[1];
			double f = lv.field_7374[2];
			float g = this.field_6283 * (float) (Math.PI / 180.0) + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
			float h = MathHelper.cos(g);
			float i = MathHelper.sin(g);
			this.world.addParticle(ParticleTypes.field_11226, this.x + (double)h * 0.6, this.y + 1.8, this.z + (double)i * 0.6, d, e, f);
			this.world.addParticle(ParticleTypes.field_11226, this.x - (double)h * 0.6, this.y + 1.8, this.z - (double)i * 0.6, d, e, f);
		}
	}

	protected int method_7139() {
		return this.spellTicks;
	}

	protected abstract SoundEvent method_7142();

	public abstract class CastSpellGoal extends Goal {
		protected int spellCooldown;
		protected int startTime;

		protected CastSpellGoal() {
		}

		@Override
		public boolean canStart() {
			if (SpellcastingIllagerEntity.this.getTarget() == null) {
				return false;
			} else {
				return SpellcastingIllagerEntity.this.method_7137() ? false : SpellcastingIllagerEntity.this.age >= this.startTime;
			}
		}

		@Override
		public boolean shouldContinue() {
			return SpellcastingIllagerEntity.this.getTarget() != null && this.spellCooldown > 0;
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

			SpellcastingIllagerEntity.this.method_7138(this.method_7147());
		}

		@Override
		public void tick() {
			this.spellCooldown--;
			if (this.spellCooldown == 0) {
				this.castSpell();
				SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.method_7142(), 1.0F, 1.0F);
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

		protected abstract SpellcastingIllagerEntity.class_1618 method_7147();
	}

	public class LookAtTargetGoal extends Goal {
		public LookAtTargetGoal() {
			this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406));
		}

		@Override
		public boolean canStart() {
			return SpellcastingIllagerEntity.this.method_7139() > 0;
		}

		@Override
		public void start() {
			super.start();
			SpellcastingIllagerEntity.this.navigation.stop();
		}

		@Override
		public void onRemove() {
			super.onRemove();
			SpellcastingIllagerEntity.this.method_7138(SpellcastingIllagerEntity.class_1618.field_7377);
		}

		@Override
		public void tick() {
			if (SpellcastingIllagerEntity.this.getTarget() != null) {
				SpellcastingIllagerEntity.this.getLookControl()
					.lookAt(
						SpellcastingIllagerEntity.this.getTarget(), (float)SpellcastingIllagerEntity.this.method_5986(), (float)SpellcastingIllagerEntity.this.method_5978()
					);
			}
		}
	}

	public static enum class_1618 {
		field_7377(0, 0.0, 0.0, 0.0),
		field_7379(1, 0.7, 0.7, 0.8),
		field_7380(2, 0.4, 0.3, 0.35),
		field_7381(3, 0.7, 0.5, 0.2),
		field_7382(4, 0.3, 0.3, 0.8),
		field_7378(5, 0.1, 0.1, 0.2);

		private final int field_7375;
		private final double[] field_7374;

		private class_1618(int j, double d, double e, double f) {
			this.field_7375 = j;
			this.field_7374 = new double[]{d, e, f};
		}

		public static SpellcastingIllagerEntity.class_1618 method_7144(int i) {
			for (SpellcastingIllagerEntity.class_1618 lv : values()) {
				if (i == lv.field_7375) {
					return lv;
				}
			}

			return field_7377;
		}
	}
}
