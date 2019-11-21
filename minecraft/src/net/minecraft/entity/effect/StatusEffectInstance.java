package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
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

	public StatusEffectInstance(StatusEffect statusEffect) {
		this(statusEffect, 0, 0);
	}

	public StatusEffectInstance(StatusEffect type, int i) {
		this(type, i, 0);
	}

	public StatusEffectInstance(StatusEffect type, int duration, int i) {
		this(type, duration, i, false, true);
	}

	public StatusEffectInstance(StatusEffect effect, int duration, int amplifier, boolean bl, boolean showParticles) {
		this(effect, duration, amplifier, bl, showParticles, showParticles);
	}

	public StatusEffectInstance(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
	}

	public StatusEffectInstance(StatusEffectInstance statusEffectInstance) {
		this.type = statusEffectInstance.type;
		this.duration = statusEffectInstance.duration;
		this.amplifier = statusEffectInstance.amplifier;
		this.ambient = statusEffectInstance.ambient;
		this.showParticles = statusEffectInstance.showParticles;
		this.showIcon = statusEffectInstance.showIcon;
	}

	public boolean upgrade(StatusEffectInstance statusEffectInstance) {
		if (this.type != statusEffectInstance.type) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		boolean bl = false;
		if (statusEffectInstance.amplifier > this.amplifier) {
			this.amplifier = statusEffectInstance.amplifier;
			this.duration = statusEffectInstance.duration;
			bl = true;
		} else if (statusEffectInstance.amplifier == this.amplifier && this.duration < statusEffectInstance.duration) {
			this.duration = statusEffectInstance.duration;
			bl = true;
		}

		if (!statusEffectInstance.ambient && this.ambient || bl) {
			this.ambient = statusEffectInstance.ambient;
			bl = true;
		}

		if (statusEffectInstance.showParticles != this.showParticles) {
			this.showParticles = statusEffectInstance.showParticles;
			bl = true;
		}

		if (statusEffectInstance.showIcon != this.showIcon) {
			this.showIcon = statusEffectInstance.showIcon;
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

	public boolean update(LivingEntity livingEntity) {
		if (this.duration > 0) {
			if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
				this.applyUpdateEffect(livingEntity);
			}

			this.updateDuration();
		}

		return this.duration > 0;
	}

	private int updateDuration() {
		return --this.duration;
	}

	public void applyUpdateEffect(LivingEntity livingEntity) {
		if (this.duration > 0) {
			this.type.applyUpdateEffect(livingEntity, this.amplifier);
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

	public CompoundTag serialize(CompoundTag compoundTag) {
		compoundTag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
		compoundTag.putByte("Amplifier", (byte)this.getAmplifier());
		compoundTag.putInt("Duration", this.getDuration());
		compoundTag.putBoolean("Ambient", this.isAmbient());
		compoundTag.putBoolean("ShowParticles", this.shouldShowParticles());
		compoundTag.putBoolean("ShowIcon", this.shouldShowIcon());
		return compoundTag;
	}

	public static StatusEffectInstance deserialize(CompoundTag tag) {
		int i = tag.getByte("Id");
		StatusEffect statusEffect = StatusEffect.byRawId(i);
		if (statusEffect == null) {
			return null;
		} else {
			int j = tag.getByte("Amplifier");
			int k = tag.getInt("Duration");
			boolean bl = tag.getBoolean("Ambient");
			boolean bl2 = true;
			if (tag.contains("ShowParticles", 1)) {
				bl2 = tag.getBoolean("ShowParticles");
			}

			boolean bl3 = bl2;
			if (tag.contains("ShowIcon", 1)) {
				bl3 = tag.getBoolean("ShowIcon");
			}

			return new StatusEffectInstance(statusEffect, k, j < 0 ? 0 : j, bl, bl2, bl3);
		}
	}

	@Environment(EnvType.CLIENT)
	public void setPermanent(boolean bl) {
		this.permanent = bl;
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
