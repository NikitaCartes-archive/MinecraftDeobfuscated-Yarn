package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import javax.annotation.Nullable;
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
	@Nullable
	private StatusEffectInstance field_21830;

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

	public StatusEffectInstance(StatusEffect statusEffect, int i, int j, boolean bl, boolean bl2, boolean bl3) {
		this(statusEffect, i, j, bl, bl2, bl3, null);
	}

	public StatusEffectInstance(
		StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance statusEffectInstance
	) {
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
		this.field_21830 = statusEffectInstance;
	}

	public StatusEffectInstance(StatusEffectInstance that) {
		this.type = that.type;
		this.method_24276(that);
	}

	void method_24276(StatusEffectInstance statusEffectInstance) {
		this.duration = statusEffectInstance.duration;
		this.amplifier = statusEffectInstance.amplifier;
		this.ambient = statusEffectInstance.ambient;
		this.showParticles = statusEffectInstance.showParticles;
		this.showIcon = statusEffectInstance.showIcon;
	}

	public boolean upgrade(StatusEffectInstance that) {
		if (this.type != that.type) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		boolean bl = false;
		if (that.amplifier > this.amplifier) {
			if (that.duration < this.duration) {
				this.field_21830 = new StatusEffectInstance(this);
			}

			this.amplifier = that.amplifier;
			this.duration = that.duration;
			bl = true;
		} else if (that.amplifier == this.amplifier && this.duration < that.duration) {
			this.duration = that.duration;
			bl = true;
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

	public boolean update(LivingEntity livingEntity) {
		if (this.duration > 0) {
			if (this.type.canApplyUpdateEffect(this.duration, this.amplifier)) {
				this.applyUpdateEffect(livingEntity);
			}

			this.updateDuration();
			if (this.duration == 0 && this.field_21830 != null) {
				this.method_24276(this.field_21830);
				this.field_21830 = this.field_21830.field_21830;
			}
		}

		return this.duration > 0;
	}

	private int updateDuration() {
		if (this.field_21830 != null) {
			this.field_21830.updateDuration();
		}

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
		this.method_24277(compoundTag);
		return compoundTag;
	}

	private void method_24277(CompoundTag compoundTag) {
		compoundTag.putByte("Amplifier", (byte)this.getAmplifier());
		compoundTag.putInt("Duration", this.getDuration());
		compoundTag.putBoolean("Ambient", this.isAmbient());
		compoundTag.putBoolean("ShowParticles", this.shouldShowParticles());
		compoundTag.putBoolean("ShowIcon", this.shouldShowIcon());
		if (this.field_21830 != null) {
			CompoundTag compoundTag2 = new CompoundTag();
			this.field_21830.serialize(compoundTag2);
			compoundTag.put("HiddenEffect", compoundTag2);
		}
	}

	public static StatusEffectInstance deserialize(CompoundTag tag) {
		int i = tag.getByte("Id");
		StatusEffect statusEffect = StatusEffect.byRawId(i);
		return statusEffect == null ? null : method_24275(statusEffect, tag);
	}

	private static StatusEffectInstance method_24275(StatusEffect statusEffect, CompoundTag compoundTag) {
		int i = compoundTag.getByte("Amplifier");
		int j = compoundTag.getInt("Duration");
		boolean bl = compoundTag.getBoolean("Ambient");
		boolean bl2 = true;
		if (compoundTag.contains("ShowParticles", 1)) {
			bl2 = compoundTag.getBoolean("ShowParticles");
		}

		boolean bl3 = bl2;
		if (compoundTag.contains("ShowIcon", 1)) {
			bl3 = compoundTag.getBoolean("ShowIcon");
		}

		StatusEffectInstance statusEffectInstance = null;
		if (compoundTag.contains("HiddenEffect", 10)) {
			statusEffectInstance = method_24275(statusEffect, compoundTag.getCompound("HiddenEffect"));
		}

		return new StatusEffectInstance(statusEffect, j, i < 0 ? 0 : i, bl, bl2, bl3, statusEffectInstance);
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
