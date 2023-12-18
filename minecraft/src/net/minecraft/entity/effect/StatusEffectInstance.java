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
	private final StatusEffectInstance.class_9063 field_47739 = new StatusEffectInstance.class_9063();

	public StatusEffectInstance(RegistryEntry<StatusEffect> registryEntry) {
		this(registryEntry, 0, 0);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> registryEntry, int duration) {
		this(registryEntry, duration, 0);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> registryEntry, int duration, int amplifier) {
		this(registryEntry, duration, amplifier, false, true);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> registryEntry, int duration, int amplifier, boolean ambient, boolean visible) {
		this(registryEntry, duration, amplifier, ambient, visible, visible);
	}

	public StatusEffectInstance(RegistryEntry<StatusEffect> registryEntry, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
		this(registryEntry, duration, amplifier, ambient, showParticles, showIcon, null);
	}

	public StatusEffectInstance(
		RegistryEntry<StatusEffect> registryEntry,
		int duration,
		int amplifier,
		boolean ambient,
		boolean showParticles,
		boolean showIcon,
		@Nullable StatusEffectInstance hiddenEffect
	) {
		this.type = registryEntry;
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

	public float method_55653(LivingEntity livingEntity, float f) {
		return this.field_47739.method_55660(livingEntity, f);
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

		this.field_47739.method_55661(this);
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
		return identifier == null
			? null
			: (StatusEffectInstance)Registries.STATUS_EFFECT.method_55841(identifier).map(reference -> fromNbt(reference, nbt)).orElse(null);
	}

	private static StatusEffectInstance fromNbt(RegistryEntry<StatusEffect> registryEntry, NbtCompound nbt) {
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
			statusEffectInstance = fromNbt(registryEntry, nbt.getCompound("hidden_effect"));
		}

		return new StatusEffectInstance(registryEntry, j, Math.max(i, 0), bl, bl2, bl3, statusEffectInstance);
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

	public boolean method_55654(RegistryEntry<StatusEffect> registryEntry) {
		return this.type.equals(registryEntry);
	}

	public void method_55656(StatusEffectInstance statusEffectInstance) {
		this.field_47739.method_55658(statusEffectInstance.field_47739);
	}

	public void method_55657() {
		this.field_47739.method_55659(this);
	}

	static class class_9063 {
		private float field_47740;
		private float field_47741;

		public void method_55659(StatusEffectInstance statusEffectInstance) {
			this.field_47740 = method_55662(statusEffectInstance);
			this.field_47741 = this.field_47740;
		}

		public void method_55658(StatusEffectInstance.class_9063 arg) {
			this.field_47740 = arg.field_47740;
			this.field_47741 = arg.field_47741;
		}

		public void method_55661(StatusEffectInstance statusEffectInstance) {
			this.field_47741 = this.field_47740;
			int i = method_55663(statusEffectInstance);
			if (i == 0) {
				this.field_47740 = 1.0F;
			} else {
				float f = method_55662(statusEffectInstance);
				if (this.field_47740 != f) {
					float g = 1.0F / (float)i;
					this.field_47740 = this.field_47740 + MathHelper.clamp(f - this.field_47740, -g, g);
				}
			}
		}

		private static float method_55662(StatusEffectInstance statusEffectInstance) {
			boolean bl = !statusEffectInstance.isDurationBelow(method_55663(statusEffectInstance));
			return bl ? 1.0F : 0.0F;
		}

		private static int method_55663(StatusEffectInstance statusEffectInstance) {
			return statusEffectInstance.getEffectType().value().method_55652();
		}

		public float method_55660(LivingEntity livingEntity, float f) {
			if (livingEntity.isRemoved()) {
				this.field_47741 = this.field_47740;
			}

			return MathHelper.lerp(f, this.field_47741, this.field_47740);
		}
	}
}
