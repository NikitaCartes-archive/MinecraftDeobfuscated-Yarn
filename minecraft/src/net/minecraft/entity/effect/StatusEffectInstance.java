package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final StatusEffect type;
	int duration;
	private int amplifier;
	private boolean ambient;
	private boolean permanent;
	private boolean showParticles;
	private boolean showIcon;
	/**
	 * The effect hidden when upgrading effects. Duration decreases with this
	 * effect.
	 * 
	 * <p>This exists so that long-duration low-amplifier effects reappears
	 * after short-duration high-amplifier effects run out.
	 */
	@Nullable
	private StatusEffectInstance hiddenEffect;
	private Optional<StatusEffectInstance.FactorCalculationData> factorCalculationData;

	public StatusEffectInstance(StatusEffect type) {
		this(type, 0, 0);
	}

	public StatusEffectInstance(StatusEffect type, int duration) {
		this(type, duration, 0);
	}

	public StatusEffectInstance(StatusEffect type, int duration, int amplifier) {
		this(type, duration, amplifier, false, true);
	}

	public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean visible) {
		this(type, duration, amplifier, ambient, visible, visible);
	}

	public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
		this(
			type,
			duration,
			amplifier,
			ambient,
			showParticles,
			showIcon,
			null,
			Optional.ofNullable((StatusEffectInstance.FactorCalculationData)type.getFactorCalculationDataSupplier().get())
		);
	}

	public StatusEffectInstance(
		StatusEffect type,
		int duration,
		int amplifier,
		boolean ambient,
		boolean showParticles,
		boolean showIcon,
		@Nullable StatusEffectInstance hiddenEffect,
		Optional<StatusEffectInstance.FactorCalculationData> factorCalculationData
	) {
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
		this.hiddenEffect = hiddenEffect;
		this.factorCalculationData = factorCalculationData;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.type = statusEffectInstance.type;
		this.factorCalculationData = Optional.ofNullable((StatusEffectInstance.FactorCalculationData)this.type.getFactorCalculationDataSupplier().get());
		this.copyFrom(statusEffectInstance);
	}

	public Optional<StatusEffectInstance.FactorCalculationData> getFactorCalculationData() {
		return this.factorCalculationData;
	}

	void copyFrom(StatusEffectInstance that) {
		this.duration = that.duration;
		this.amplifier = that.amplifier;
		this.ambient = that.ambient;
		this.showParticles = that.showParticles;
		this.showIcon = that.showIcon;
	}

	public boolean upgrade(StatusEffectInstance that) {
		if (this.type != that.type) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		int i = this.duration;
		boolean bl = false;
		if (that.amplifier > this.amplifier) {
			if (that.duration < this.duration) {
				StatusEffectInstance statusEffectInstance = this.hiddenEffect;
				this.hiddenEffect = new StatusEffectInstance(this);
				this.hiddenEffect.hiddenEffect = statusEffectInstance;
			}

			this.amplifier = that.amplifier;
			this.duration = that.duration;
			bl = true;
		} else if (that.duration > this.duration) {
			if (that.amplifier == this.amplifier) {
				this.duration = that.duration;
				bl = true;
			} else if (this.hiddenEffect == null) {
				this.hiddenEffect = new StatusEffectInstance(that);
			} else {
				this.hiddenEffect.upgrade(that);
			}
		}

		if (!that.ambient && this.ambient || bl) {
			this.ambient = that.ambient;
			bl = true;
		}

		if (that.showParticles != this.showParticles) {
			this.showParticles = that.showParticles;
			bl = true;
		}

		if (that.showIcon != this.showIcon) {
			this.showIcon = that.showIcon;
			bl = true;
		}

		if (i != this.duration) {
			this.factorCalculationData
				.ifPresent(factorCalculationData -> factorCalculationData.effectChangedTimestamp = factorCalculationData.effectChangedTimestamp + (this.duration - i));
			bl = true;
		}

		return bl;
	}

	public StatusEffect getEffectType() {
		return this.type;
	}

	public int getDuration() {
		return this.duration;
	}

	public int getAmplifier() {
		return this.amplifier;
	}

	public boolean isAmbient() {
		return this.ambient;
	}

	public boolean shouldShowParticles() {
		return this.showParticles;
	}

	public boolean shouldShowIcon() {
		return this.showIcon;
	}

	public boolean update(LivingEntity entity, Runnable overwriteCallback) {
		if (this.duration > 0) {
			if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
				this.applyUpdateEffect(entity);
			}

			this.updateDuration();
			if (this.duration == 0 && this.hiddenEffect != null) {
				this.copyFrom(this.hiddenEffect);
				this.hiddenEffect = this.hiddenEffect.hiddenEffect;
				overwriteCallback.run();
			}
		}

		this.factorCalculationData.ifPresent(factorCalculationData -> factorCalculationData.update(this));
		return this.duration > 0;
	}

	private int updateDuration() {
		if (this.hiddenEffect != null) {
			this.hiddenEffect.updateDuration();
		}

		return --this.duration;
	}

	public void applyUpdateEffect(LivingEntity entity) {
		if (this.duration > 0) {
			this.type.applyUpdateEffect(entity, this.amplifier);
		}
	}

	public String getTranslationKey() {
		return this.type.getTranslationKey();
	}

	public String toString() {
		String string;
		if (this.amplifier > 0) {
			string = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
		} else {
			string = this.getTranslationKey() + ", Duration: " + this.duration;
		}

		if (!this.showParticles) {
			string = string + ", Particles: false";
		}

		if (!this.showIcon) {
			string = string + ", Show Icon: false";
		}

		return string;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof StatusEffectInstance statusEffectInstance)
				? false
				: this.duration == statusEffectInstance.duration
					&& this.amplifier == statusEffectInstance.amplifier
					&& this.ambient == statusEffectInstance.ambient
					&& this.type.equals(statusEffectInstance.type);
		}
	}

	public int hashCode() {
		int i = this.type.hashCode();
		i = 31 * i + this.duration;
		i = 31 * i + this.amplifier;
		return 31 * i + (this.ambient ? 1 : 0);
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		nbt.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
		this.writeTypelessNbt(nbt);
		return nbt;
	}

	private void writeTypelessNbt(NbtCompound nbt) {
		nbt.putByte("Amplifier", (byte)this.getAmplifier());
		nbt.putInt("Duration", this.getDuration());
		nbt.putBoolean("Ambient", this.isAmbient());
		nbt.putBoolean("ShowParticles", this.shouldShowParticles());
		nbt.putBoolean("ShowIcon", this.shouldShowIcon());
		if (this.hiddenEffect != null) {
			NbtCompound nbtCompound = new NbtCompound();
			this.hiddenEffect.writeNbt(nbtCompound);
			nbt.put("HiddenEffect", nbtCompound);
		}

		this.factorCalculationData.ifPresent(factorCalculationData -> nbt.put("FactorCalculationData", factorCalculationData.writeNbt(new NbtCompound())));
	}

	@Nullable
	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		int i = nbt.getByte("Id");
		StatusEffect statusEffect = StatusEffect.byRawId(i);
		return statusEffect == null ? null : fromNbt(statusEffect, nbt);
	}

	private static StatusEffectInstance fromNbt(StatusEffect type, NbtCompound nbt) {
		int i = nbt.getByte("Amplifier");
		int j = nbt.getInt("Duration");
		boolean bl = nbt.getBoolean("Ambient");
		boolean bl2 = true;
		if (nbt.contains("ShowParticles", NbtElement.BYTE_TYPE)) {
			bl2 = nbt.getBoolean("ShowParticles");
		}

		boolean bl3 = bl2;
		if (nbt.contains("ShowIcon", NbtElement.BYTE_TYPE)) {
			bl3 = nbt.getBoolean("ShowIcon");
		}

		StatusEffectInstance statusEffectInstance = null;
		if (nbt.contains("HiddenEffect", NbtElement.COMPOUND_TYPE)) {
			statusEffectInstance = fromNbt(type, nbt.getCompound("HiddenEffect"));
		}

		Optional<StatusEffectInstance.FactorCalculationData> optional = Optional.empty();
		if (nbt.contains("FactorCalculationData", NbtElement.COMPOUND_TYPE)) {
			optional = Optional.of(StatusEffectInstance.FactorCalculationData.fromNbt(nbt.getCompound("FactorCalculationData")));
		}

		return new StatusEffectInstance(type, j, Math.max(i, 0), bl, bl2, bl3, statusEffectInstance, optional);
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	public boolean isPermanent() {
		return this.permanent;
	}

	public int compareTo(StatusEffectInstance statusEffectInstance) {
		int i = 32147;
		return (this.getDuration() <= 32147 || statusEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !statusEffectInstance.isAmbient())
			? ComparisonChain.start()
				.compare(this.isAmbient(), statusEffectInstance.isAmbient())
				.compare(this.getDuration(), statusEffectInstance.getDuration())
				.compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor())
				.result()
			: ComparisonChain.start()
				.compare(this.isAmbient(), statusEffectInstance.isAmbient())
				.compare(this.getEffectType().getColor(), statusEffectInstance.getEffectType().getColor())
				.result();
	}

	public static class FactorCalculationData {
		public int paddingDuration;
		public float factorTarget = 1.0F;
		public float factorCurrent;
		public int effectChangedTimestamp;
		public float factorPreviousFrame;
		public boolean hadEffectLastTick;

		public FactorCalculationData(int paddingDuration) {
			this.paddingDuration = paddingDuration;
		}

		public void update(StatusEffectInstance statusEffectInstance) {
			this.factorPreviousFrame = this.factorCurrent;
			boolean bl = statusEffectInstance.duration > this.paddingDuration;
			if (this.hadEffectLastTick) {
				if (!bl) {
					this.effectChangedTimestamp = statusEffectInstance.duration;
					this.hadEffectLastTick = false;
					this.factorTarget = 0.0F;
				}
			} else if (bl) {
				this.effectChangedTimestamp = statusEffectInstance.duration;
				this.hadEffectLastTick = true;
				this.factorTarget = 1.0F;
			}

			float f = MathHelper.clamp(((float)this.effectChangedTimestamp - (float)statusEffectInstance.duration) / (float)this.paddingDuration, 0.0F, 1.0F);
			this.factorCurrent = MathHelper.lerp(f, this.factorCurrent, this.factorTarget);
		}

		public float lerp(float delta) {
			return MathHelper.lerp(delta, this.factorPreviousFrame, this.factorCurrent);
		}

		public NbtCompound writeNbt(NbtCompound nbt) {
			nbt.putInt("padding_duration", this.paddingDuration);
			nbt.putFloat("factor_target", this.factorTarget);
			nbt.putFloat("factor_current", this.factorCurrent);
			nbt.putInt("effect_changed_timestamp", this.effectChangedTimestamp);
			nbt.putFloat("factor_previous_frame", this.factorPreviousFrame);
			nbt.putBoolean("had_effect_last_tick", this.hadEffectLastTick);
			return nbt;
		}

		public static StatusEffectInstance.FactorCalculationData fromNbt(NbtCompound nbt) {
			int i = nbt.getInt("padding_duration");
			StatusEffectInstance.FactorCalculationData factorCalculationData = new StatusEffectInstance.FactorCalculationData(i);
			factorCalculationData.factorTarget = nbt.getFloat("factor_target");
			factorCalculationData.factorCurrent = nbt.getFloat("factor_current");
			factorCalculationData.effectChangedTimestamp = nbt.getInt("effect_changed_timestamp");
			factorCalculationData.factorPreviousFrame = nbt.getFloat("factor_previous_frame");
			factorCalculationData.hadEffectLastTick = nbt.getBoolean("had_effect_last_tick");
			return factorCalculationData;
		}
	}
}
