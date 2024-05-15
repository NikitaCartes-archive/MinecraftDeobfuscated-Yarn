package net.minecraft.entity.effect;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class StatusEffect implements ToggleableFeature {
	public static final Codec<RegistryEntry<StatusEffect>> ENTRY_CODEC = Registries.STATUS_EFFECT.getEntryCodec();
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<StatusEffect>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.STATUS_EFFECT);
	private static final int AMBIENT_PARTICLE_ALPHA = MathHelper.floor(38.25F);
	private final Map<RegistryEntry<EntityAttribute>, StatusEffect.EffectAttributeModifierCreator> attributeModifiers = new Object2ObjectOpenHashMap<>();
	private final StatusEffectCategory category;
	private final int color;
	private final Function<StatusEffectInstance, ParticleEffect> particleFactory;
	@Nullable
	private String translationKey;
	private int fadeTicks;
	private Optional<SoundEvent> applySound = Optional.empty();
	private FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;

	protected StatusEffect(StatusEffectCategory category, int color) {
		this.category = category;
		this.color = color;
		this.particleFactory = effect -> {
			int j = effect.isAmbient() ? AMBIENT_PARTICLE_ALPHA : 255;
			return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, ColorHelper.Argb.withAlpha(j, color));
		};
	}

	protected StatusEffect(StatusEffectCategory category, int color, ParticleEffect particleEffect) {
		this.category = category;
		this.color = color;
		this.particleFactory = effect -> particleEffect;
	}

	public int getFadeTicks() {
		return this.fadeTicks;
	}

	public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
		return true;
	}

	public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
		this.applyUpdateEffect(target, amplifier);
	}

	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return false;
	}

	public void onApplied(LivingEntity entity, int amplifier) {
	}

	public void playApplySound(LivingEntity entity, int amplifier) {
		this.applySound
			.ifPresent(sound -> entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, entity.getSoundCategory(), 1.0F, 1.0F));
	}

	public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
	}

	public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
	}

	public boolean isInstant() {
		return false;
	}

	protected String loadTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("effect", Registries.STATUS_EFFECT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.loadTranslationKey();
	}

	public Text getName() {
		return Text.translatable(this.getTranslationKey());
	}

	public StatusEffectCategory getCategory() {
		return this.category;
	}

	public int getColor() {
		return this.color;
	}

	public StatusEffect addAttributeModifier(RegistryEntry<EntityAttribute> attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
		this.attributeModifiers.put(attribute, new StatusEffect.EffectAttributeModifierCreator(UUID.fromString(uuid), amount, operation));
		return this;
	}

	/**
	 * Sets the duration of effect fade-in and fade-out.
	 * 
	 * @return this effect, for chaining
	 * @see StatusEffectInstance#getFadeFactor
	 */
	public StatusEffect fadeTicks(int fadeTicks) {
		this.fadeTicks = fadeTicks;
		return this;
	}

	public void forEachAttributeModifier(int amplifier, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer) {
		this.attributeModifiers.forEach((attribute, modifier) -> consumer.accept(attribute, modifier.createAttributeModifier(this.getTranslationKey(), amplifier)));
	}

	public void onRemoved(AttributeContainer attributeContainer) {
		for (Entry<RegistryEntry<EntityAttribute>, StatusEffect.EffectAttributeModifierCreator> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((RegistryEntry<EntityAttribute>)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(((StatusEffect.EffectAttributeModifierCreator)entry.getValue()).uuid());
			}
		}
	}

	public void onApplied(AttributeContainer attributeContainer, int amplifier) {
		for (Entry<RegistryEntry<EntityAttribute>, StatusEffect.EffectAttributeModifierCreator> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((RegistryEntry<EntityAttribute>)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(((StatusEffect.EffectAttributeModifierCreator)entry.getValue()).uuid());
				entityAttributeInstance.addPersistentModifier(
					((StatusEffect.EffectAttributeModifierCreator)entry.getValue()).createAttributeModifier(this.getTranslationKey(), amplifier)
				);
			}
		}
	}

	public boolean isBeneficial() {
		return this.category == StatusEffectCategory.BENEFICIAL;
	}

	public ParticleEffect createParticle(StatusEffectInstance effect) {
		return (ParticleEffect)this.particleFactory.apply(effect);
	}

	public StatusEffect applySound(SoundEvent sound) {
		this.applySound = Optional.of(sound);
		return this;
	}

	public StatusEffect requires(FeatureFlag... requiredFeatures) {
		this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures);
		return this;
	}

	@Override
	public FeatureSet getRequiredFeatures() {
		return this.requiredFeatures;
	}

	static record EffectAttributeModifierCreator(UUID uuid, double baseValue, EntityAttributeModifier.Operation operation) {
		public EntityAttributeModifier createAttributeModifier(String translationKey, int amplifier) {
			return new EntityAttributeModifier(this.uuid, translationKey + " " + amplifier, this.baseValue * (double)(amplifier + 1), this.operation);
		}
	}
}
