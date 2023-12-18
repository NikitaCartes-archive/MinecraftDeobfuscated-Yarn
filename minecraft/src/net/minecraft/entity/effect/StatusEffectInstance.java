package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int INFINITE = -1;
	private static final String ID_NBT_KEY = "id";
	private static final String AMBIENT_NBT_KEY = "ambient";
	private static final String HIDDEN_EFFECT_NBT_KEY = "hidden_effect";
	private static final String AMPLIFIER_NBT_KEY = "amplifier";
	private static final String DURATION_NBT_KEY = "duration";
	private static final String SHOW_PARTICLES_NBT_KEY = "show_particles";
	private static final String SHOW_ICON_NBT_KEY = "show_icon";
	private final RegistryEntry<StatusEffect> type;
	private int duration;
	private int amplifier;
	private boolean ambient;
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
	private final StatusEffectInstance.Fading fading = new StatusEffectInstance.Fading();

	public StatusEffectInstance(RegistryEntry<StatusEffect> effect) {
		this(effect, 0, 0);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> effect, int duration) {
		this(effect, duration, 0);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
		this(effect, duration, amplifier, false, true);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> effect, int duration, int amplifier, boolean ambient, boolean visible) {
		this(effect, duration, amplifier, ambient, visible, visible);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> effect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
		this(effect, duration, amplifier, ambient, showParticles, showIcon, null);
	}

	public StatusEffectInstance(
		RegistryEntry<StatusEffect> effect,
		int duration,
		int amplifier,
		boolean ambient,
		boolean showParticles,
		boolean showIcon,
		@Nullable StatusEffectInstance hiddenEffect
	) {
		this.type = effect;
		this.duration = duration;
		this.amplifier = amplifier;
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
		this.hiddenEffect = hiddenEffect;
	}

	public StatusEffectInstance(StatusEffectInstance instance) {
		this.type = instance.type;
		this.copyFrom(instance);
	}

	/**
	 * {@return the factor (multiplier) for effect fade-in and fade-out}
	 * 
	 * <p>The return value is between {@code 0.0f} and {@code 1.0f} (both inclusive).
	 * 
	 * @see StatusEffect#fadeTicks(int)
	 */
	public float getFadeFactor(LivingEntity entity, float tickDelta) {
		return this.fading.calculate(entity, tickDelta);
	}

	void copyFrom(StatusEffectInstance that) {
		this.duration = that.duration;
		this.amplifier = that.amplifier;
		this.ambient = that.ambient;
		this.showParticles = that.showParticles;
		this.showIcon = that.showIcon;
	}

	public boolean upgrade(StatusEffectInstance that) {
		if (!this.type.equals(that.type)) {
			LOGGER.warn("This method should only be called for matching effects!");
		}

		boolean bl = false;
		if (that.amplifier > this.amplifier) {
			if (that.lastsShorterThan(this)) {
				StatusEffectInstance statusEffectInstance = this.hiddenEffect;
				this.hiddenEffect = new StatusEffectInstance(this);
				this.hiddenEffect.hiddenEffect = statusEffectInstance;
			}

			this.amplifier = that.amplifier;
			this.duration = that.duration;
			bl = true;
		} else if (this.lastsShorterThan(that)) {
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

	private boolean lastsShorterThan(StatusEffectInstance effect) {
		return !this.isInfinite() && (this.duration < effect.duration || effect.isInfinite());
	}

	public boolean isInfinite() {
		return this.duration == -1;
	}

	public boolean isDurationBelow(int duration) {
		return !this.isInfinite() && this.duration <= duration;
	}

	public int mapDuration(Int2IntFunction mapper) {
		return !this.isInfinite() && this.duration != 0 ? mapper.applyAsInt(this.duration) : this.duration;
	}

	public RegistryEntry<StatusEffect> getEffectType() {
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
		if (this.isActive()) {
			int i = this.isInfinite() ? entity.age : this.duration;
			if (this.type.value().canApplyUpdateEffect(i, this.amplifier) && !this.type.value().applyUpdateEffect(entity, this.amplifier)) {
				entity.removeStatusEffect(this.type);
			}

			this.updateDuration();
			if (this.duration == 0 && this.hiddenEffect != null) {
				this.copyFrom(this.hiddenEffect);
				this.hiddenEffect = this.hiddenEffect.hiddenEffect;
				overwriteCallback.run();
			}
		}

		this.fading.update(this);
		return this.isActive();
	}

	private boolean isActive() {
		return this.isInfinite() || this.duration > 0;
	}

	private int updateDuration() {
		if (this.hiddenEffect != null) {
			this.hiddenEffect.updateDuration();
		}

		return this.duration = this.mapDuration(duration -> duration - 1);
	}

	public void onApplied(LivingEntity entity) {
		this.type.value().onApplied(entity, this.amplifier);
	}

	public String getTranslationKey() {
		return this.type.value().getTranslationKey();
	}

	public String toString() {
		String string;
		if (this.amplifier > 0) {
			string = this.getTranslationKey() + " x " + (this.amplifier + 1) + ", Duration: " + this.getDurationString();
		} else {
			string = this.getTranslationKey() + ", Duration: " + this.getDurationString();
		}

		if (!this.showParticles) {
			string = string + ", Particles: false";
		}

		if (!this.showIcon) {
			string = string + ", Show Icon: false";
		}

		return string;
	}

	private String getDurationString() {
		return this.isInfinite() ? "infinite" : Integer.toString(this.duration);
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
		Identifier identifier = ((RegistryKey)this.type.getKey().orElseThrow()).getValue();
		nbt.putString("id", identifier.toString());
		this.writeTypelessNbt(nbt);
		return nbt;
	}

	private void writeTypelessNbt(NbtCompound nbt) {
		nbt.putByte("amplifier", (byte)this.getAmplifier());
		nbt.putInt("duration", this.getDuration());
		nbt.putBoolean("ambient", this.isAmbient());
		nbt.putBoolean("show_particles", this.shouldShowParticles());
		nbt.putBoolean("show_icon", this.shouldShowIcon());
		if (this.hiddenEffect != null) {
			NbtCompound nbtCompound = new NbtCompound();
			this.hiddenEffect.writeNbt(nbtCompound);
			nbt.put("hidden_effect", nbtCompound);
		}
	}

	@Nullable
	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		Identifier identifier = Identifier.tryParse(nbt.getString("id"));
		return identifier == null ? null : (StatusEffectInstance)Registries.STATUS_EFFECT.getEntry(identifier).map(effect -> fromNbt(effect, nbt)).orElse(null);
	}

	private static StatusEffectInstance fromNbt(RegistryEntry<StatusEffect> effect, NbtCompound nbt) {
		int i = nbt.getByte("amplifier");
		int j = nbt.getInt("duration");
		boolean bl = nbt.getBoolean("ambient");
		boolean bl2 = true;
		if (nbt.contains("show_particles", NbtElement.BYTE_TYPE)) {
			bl2 = nbt.getBoolean("show_particles");
		}

		boolean bl3 = bl2;
		if (nbt.contains("show_icon", NbtElement.BYTE_TYPE)) {
			bl3 = nbt.getBoolean("show_icon");
		}

		StatusEffectInstance statusEffectInstance = null;
		if (nbt.contains("hidden_effect", NbtElement.COMPOUND_TYPE)) {
			statusEffectInstance = fromNbt(effect, nbt.getCompound("hidden_effect"));
		}

		return new StatusEffectInstance(effect, j, Math.max(i, 0), bl, bl2, bl3, statusEffectInstance);
	}

	public int compareTo(StatusEffectInstance statusEffectInstance) {
		int i = 32147;
		return (this.getDuration() <= 32147 || statusEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !statusEffectInstance.isAmbient())
			? ComparisonChain.start()
				.compareFalseFirst(this.isAmbient(), statusEffectInstance.isAmbient())
				.compareFalseFirst(this.isInfinite(), statusEffectInstance.isInfinite())
				.compare(this.getDuration(), statusEffectInstance.getDuration())
				.compare(this.getEffectType().value().getColor(), statusEffectInstance.getEffectType().value().getColor())
				.result()
			: ComparisonChain.start()
				.compare(this.isAmbient(), statusEffectInstance.isAmbient())
				.compare(this.getEffectType().value().getColor(), statusEffectInstance.getEffectType().value().getColor())
				.result();
	}

	public boolean equals(RegistryEntry<StatusEffect> effect) {
		return this.type.equals(effect);
	}

	public void copyFadingFrom(StatusEffectInstance effect) {
		this.fading.copyFrom(effect.fading);
	}

	/**
	 * Skips fade-in or fade-out currently in progress, instantly setting it
	 * to the final state (factor {@code 1.0f} or {@code 0.0f}, depending on the
	 * effect's duration).
	 */
	public void skipFading() {
		this.fading.skipFading(this);
	}

	/**
	 * Computes the factor (multiplier) for effect fade-in and fade-out.
	 * 
	 * <p>This is used by {@link StatusEffects#DARKNESS} in vanilla.
	 * 
	 * @see StatusEffect#fadeTicks(int)
	 * @see StatusEffect#getFadeTicks
	 */
	static class Fading {
		private float factor;
		private float prevFactor;

		/**
		 * Skips fade-in or fade-out currently in progress, instantly setting it
		 * to the final state (factor {@code 1.0f} or {@code 0.0f}, depending on the
		 * effect's duration).
		 */
		public void skipFading(StatusEffectInstance effect) {
			this.factor = getTarget(effect);
			this.prevFactor = this.factor;
		}

		public void copyFrom(StatusEffectInstance.Fading fading) {
			this.factor = fading.factor;
			this.prevFactor = fading.prevFactor;
		}

		public void update(StatusEffectInstance effect) {
			this.prevFactor = this.factor;
			int i = getFadeTicks(effect);
			if (i == 0) {
				this.factor = 1.0F;
			} else {
				float f = getTarget(effect);
				if (this.factor != f) {
					float g = 1.0F / (float)i;
					this.factor = this.factor + MathHelper.clamp(f - this.factor, -g, g);
				}
			}
		}

		private static float getTarget(StatusEffectInstance effect) {
			boolean bl = !effect.isDurationBelow(getFadeTicks(effect));
			return bl ? 1.0F : 0.0F;
		}

		private static int getFadeTicks(StatusEffectInstance effect) {
			return effect.getEffectType().value().getFadeOutTicks();
		}

		public float calculate(LivingEntity entity, float tickDelta) {
			if (entity.isRemoved()) {
				this.prevFactor = this.factor;
			}

			return MathHelper.lerp(tickDelta, this.prevFactor, this.factor);
		}
	}
}
