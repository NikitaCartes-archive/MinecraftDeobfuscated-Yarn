package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

public class StatusEffectInstance implements Comparable<StatusEffectInstance> {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int INFINITE = -1;
	public static final int MIN_AMPLIFIER = 0;
	public static final int MAX_AMPLIFIER = 255;
	public static final Codec<StatusEffectInstance> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(StatusEffectInstance::getEffectType),
					StatusEffectInstance.Parameters.CODEC.forGetter(StatusEffectInstance::asParameters)
				)
				.apply(instance, StatusEffectInstance::new)
	);
	public static final PacketCodec<RegistryByteBuf, StatusEffectInstance> PACKET_CODEC = PacketCodec.tuple(
		StatusEffect.ENTRY_PACKET_CODEC,
		StatusEffectInstance::getEffectType,
		StatusEffectInstance.Parameters.PACKET_CODEC,
		StatusEffectInstance::asParameters,
		StatusEffectInstance::new
	);
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
		this.amplifier = MathHelper.clamp(amplifier, 0, 255);
		this.ambient = ambient;
		this.showParticles = showParticles;
		this.showIcon = showIcon;
		this.hiddenEffect = hiddenEffect;
	}

	public StatusEffectInstance(StatusEffectInstance instance) {
		this.type = instance.type;
		this.copyFrom(instance);
	}

	private StatusEffectInstance(RegistryEntry<StatusEffect> effect, StatusEffectInstance.Parameters parameters) {
		this(
			effect,
			parameters.duration(),
			parameters.amplifier(),
			parameters.ambient(),
			parameters.showParticles(),
			parameters.showIcon(),
			(StatusEffectInstance)parameters.hiddenEffect().map(parametersx -> new StatusEffectInstance(effect, parametersx)).orElse(null)
		);
	}

	private StatusEffectInstance.Parameters asParameters() {
		return new StatusEffectInstance.Parameters(
			this.getAmplifier(),
			this.getDuration(),
			this.isAmbient(),
			this.shouldShowParticles(),
			this.shouldShowIcon(),
			Optional.ofNullable(this.hiddenEffect).map(StatusEffectInstance::asParameters)
		);
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

	public ParticleEffect createParticle() {
		return this.type.value().createParticle(this);
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

	public void onEntityRemoval(LivingEntity entity, Entity.RemovalReason reason) {
		this.type.value().onEntityRemoval(entity, this.amplifier, reason);
	}

	public void onEntityDamage(LivingEntity entity, DamageSource source, float amount) {
		this.type.value().onEntityDamage(entity, this.amplifier, source, amount);
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
					&& this.showParticles == statusEffectInstance.showParticles
					&& this.showIcon == statusEffectInstance.showIcon
					&& this.type.equals(statusEffectInstance.type);
		}
	}

	public int hashCode() {
		int i = this.type.hashCode();
		i = 31 * i + this.duration;
		i = 31 * i + this.amplifier;
		i = 31 * i + (this.ambient ? 1 : 0);
		i = 31 * i + (this.showParticles ? 1 : 0);
		return 31 * i + (this.showIcon ? 1 : 0);
	}

	public NbtElement writeNbt() {
		return CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
	}

	@Nullable
	public static StatusEffectInstance fromNbt(NbtCompound nbt) {
		return (StatusEffectInstance)CODEC.parse(NbtOps.INSTANCE, nbt).resultOrPartial(LOGGER::error).orElse(null);
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

	public void playApplySound(LivingEntity entity) {
		this.type.value().playApplySound(entity, this.amplifier);
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
			return effect.getEffectType().value().getFadeTicks();
		}

		public float calculate(LivingEntity entity, float tickDelta) {
			if (entity.isRemoved()) {
				this.prevFactor = this.factor;
			}

			return MathHelper.lerp(tickDelta, this.prevFactor, this.factor);
		}
	}

	static record Parameters(
		int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional<StatusEffectInstance.Parameters> hiddenEffect
	) {
		public static final MapCodec<StatusEffectInstance.Parameters> CODEC = MapCodec.recursive(
			"MobEffectInstance.Details",
			codec -> RecordCodecBuilder.mapCodec(
					instance -> instance.group(
								Codecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0).forGetter(StatusEffectInstance.Parameters::amplifier),
								Codec.INT.optionalFieldOf("duration", Integer.valueOf(0)).forGetter(StatusEffectInstance.Parameters::duration),
								Codec.BOOL.optionalFieldOf("ambient", Boolean.valueOf(false)).forGetter(StatusEffectInstance.Parameters::ambient),
								Codec.BOOL.optionalFieldOf("show_particles", Boolean.valueOf(true)).forGetter(StatusEffectInstance.Parameters::showParticles),
								Codec.BOOL.optionalFieldOf("show_icon").forGetter(parameters -> Optional.of(parameters.showIcon())),
								codec.optionalFieldOf("hidden_effect").forGetter(StatusEffectInstance.Parameters::hiddenEffect)
							)
							.apply(instance, StatusEffectInstance.Parameters::create)
				)
		);
		public static final PacketCodec<ByteBuf, StatusEffectInstance.Parameters> PACKET_CODEC = PacketCodec.recursive(
			packetCodec -> PacketCodec.tuple(
					PacketCodecs.VAR_INT,
					StatusEffectInstance.Parameters::amplifier,
					PacketCodecs.VAR_INT,
					StatusEffectInstance.Parameters::duration,
					PacketCodecs.BOOL,
					StatusEffectInstance.Parameters::ambient,
					PacketCodecs.BOOL,
					StatusEffectInstance.Parameters::showParticles,
					PacketCodecs.BOOL,
					StatusEffectInstance.Parameters::showIcon,
					packetCodec.collect(PacketCodecs::optional),
					StatusEffectInstance.Parameters::hiddenEffect,
					StatusEffectInstance.Parameters::new
				)
		);

		private static StatusEffectInstance.Parameters create(
			int amplifier, int duration, boolean ambient, boolean showParticles, Optional<Boolean> showIcon, Optional<StatusEffectInstance.Parameters> hiddenEffect
		) {
			return new StatusEffectInstance.Parameters(amplifier, duration, ambient, showParticles, (Boolean)showIcon.orElse(showParticles), hiddenEffect);
		}
	}
}
