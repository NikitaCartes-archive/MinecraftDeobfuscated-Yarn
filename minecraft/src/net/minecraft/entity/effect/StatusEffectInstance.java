package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final StatusEffect type;
	private int duration;
	private int amplifier;
	private boolean splash;
	private boolean ambient;
	@Environment(EnvType.CLIENT)
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

	public StatusEffectInstance(StatusEffect statusEffect) {
		this(statusEffect, 0, 0);
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
		this(type, duration, amplifier, ambient, showParticles, showIcon, null);
	}

	public StatusEffectInstance(
		StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect
	) {
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
		this.hiddenEffect = hiddenEffect;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.type = statusEffectInstance.type;
		this.copyFrom(statusEffectInstance);
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

		if (this.splash) {
			string = string + ", Splash: true";
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
		} else if (!(o instanceof StatusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)o;
			return this.duration == statusEffectInstance.duration
				&& this.amplifier == statusEffectInstance.amplifier
				&& this.splash == statusEffectInstance.splash
				&& this.ambient == statusEffectInstance.ambient
				&& this.type.equals(statusEffectInstance.type);
		}
	}

	public int hashCode() {
		int i = this.type.hashCode();
		i = 31 * i + this.duration;
		i = 31 * i + this.amplifier;
		i = 31 * i + (this.splash ? 1 : 0);
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
	}

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
		if (nbt.contains("ShowParticles", 1)) {
			bl2 = nbt.getBoolean("ShowParticles");
		}

		boolean bl3 = bl2;
		if (nbt.contains("ShowIcon", 1)) {
			bl3 = nbt.getBoolean("ShowIcon");
		}

		StatusEffectInstance statusEffectInstance = null;
		if (nbt.contains("HiddenEffect", 10)) {
			statusEffectInstance = fromNbt(type, nbt.getCompound("HiddenEffect"));
		}

		return new StatusEffectInstance(type, j, i < 0 ? 0 : i, bl, bl2, bl3, statusEffectInstance);
	}

	@Environment(EnvType.CLIENT)
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	@Environment(EnvType.CLIENT)
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
}
