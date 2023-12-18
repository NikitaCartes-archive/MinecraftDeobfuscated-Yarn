package net.minecraft.entity.effect;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class StatusEffect {
	private final Map<RegistryEntry<EntityAttribute>, StatusEffect.EffectAttributeModifierCreator> attributeModifiers = new Object2ObjectOpenHashMap<>();
	private final StatusEffectCategory category;
	private final int color;
	@Nullable
	private String translationKey;
	private int fadeTicks;

	protected StatusEffect(StatusEffectCategory category, int color) {
		this.category = category;
		this.color = color;
	}

	public int getFadeOutTicks() {
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

	static record EffectAttributeModifierCreator(UUID uuid, double baseValue, EntityAttributeModifier.Operation operation) {
		public EntityAttributeModifier createAttributeModifier(String translationKey, int amplifier) {
			return new EntityAttributeModifier(this.uuid, translationKey + " " + amplifier, this.baseValue * (double)(amplifier + 1), this.operation);
		}
	}
}
