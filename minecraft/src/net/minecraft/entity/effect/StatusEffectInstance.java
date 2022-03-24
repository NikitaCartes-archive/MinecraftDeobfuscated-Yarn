package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogUtils.getLogger();
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

	public StatusEffectInstance(StatusEffectInstance instance) {
		this.type = instance.type;
		this.factorCalculationData = Optional.ofNullable((StatusEffectInstance.FactorCalculationData)this.type.getFactorCalculationDataSupplier().get());
		this.copyFrom(instance);
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
		nbt.putInt("Id", StatusEffect.getRawId(this.getEffectType()));
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

		this.factorCalculationData
			.ifPresent(
				factorCalculationData -> StatusEffectInstance.FactorCalculationData.CODEC
						.encodeStart(NbtOps.INSTANCE, factorCalculationData)
						.resultOrPartial(LOGGER::error)
						.ifPresent(factorCalculationDataNbt -> nbt.put("FactorCalculationData", factorCalculationDataNbt))
			);
	}

	@Nullable
	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		int i = nbt.getInt("Id");
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

		Optional<StatusEffectInstance.FactorCalculationData> optional;
		if (nbt.contains("FactorCalculationData", NbtElement.COMPOUND_TYPE)) {
			optional = StatusEffectInstance.FactorCalculationData.CODEC
				.parse(new Dynamic<>(NbtOps.INSTANCE, nbt.getCompound("FactorCalculationData")))
				.resultOrPartial(LOGGER::error);
		} else {
			optional = Optional.empty();
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
		public static final Codec<StatusEffectInstance.FactorCalculationData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.NONNEGATIVE_INT.fieldOf("padding_duration").forGetter(data -> data.paddingDuration),
						Codec.FLOAT.fieldOf("factor_target").forGetter(data -> data.factorTarget),
						Codec.FLOAT.fieldOf("factor_current").forGetter(data -> data.factorCurrent),
						Codecs.NONNEGATIVE_INT.fieldOf("effect_changed_timestamp").forGetter(data -> data.effectChangedTimestamp),
						Codec.FLOAT.fieldOf("factor_previous_frame").forGetter(data -> data.factorPreviousFrame),
						Codec.BOOL.fieldOf("had_effect_last_tick").forGetter(data -> data.hadEffectLastTick)
					)
					.apply(instance, StatusEffectInstance.FactorCalculationData::new)
		);
		private int paddingDuration;
		private float factorTarget;
		private float factorCurrent;
		int effectChangedTimestamp;
		private float factorPreviousFrame;
		private boolean hadEffectLastTick;

		public FactorCalculationData(
			int paddingDuration, float factorTarget, float factorCurrent, int effectChangedTimestamp, float factorPreviousFrame, boolean hadEffectLastTick
		) {
			this.paddingDuration = paddingDuration;
			this.factorTarget = factorTarget;
			this.factorCurrent = factorCurrent;
			this.effectChangedTimestamp = effectChangedTimestamp;
			this.factorPreviousFrame = factorPreviousFrame;
			this.hadEffectLastTick = hadEffectLastTick;
		}

		public FactorCalculationData(int paddingDuration) {
			this(paddingDuration, 1.0F, 0.0F, 0, 0.0F, false);
		}

		public void update(StatusEffectInstance instance) {
			this.factorPreviousFrame = this.factorCurrent;
			boolean bl = instance.duration > this.paddingDuration;
			if (this.hadEffectLastTick) {
				if (!bl) {
					this.effectChangedTimestamp = instance.duration;
					this.hadEffectLastTick = false;
					this.factorTarget = 0.0F;
				}
			} else if (bl) {
				this.effectChangedTimestamp = instance.duration;
				this.hadEffectLastTick = true;
				this.factorTarget = 1.0F;
			}

			float f = MathHelper.clamp(((float)this.effectChangedTimestamp - (float)instance.duration) / (float)this.paddingDuration, 0.0F, 1.0F);
			this.factorCurrent = MathHelper.lerp(f, this.factorCurrent, this.factorTarget);
		}

		public float lerp(float factor) {
			return MathHelper.lerp(factor, this.factorPreviousFrame, this.factorCurrent);
		}
	}
}
