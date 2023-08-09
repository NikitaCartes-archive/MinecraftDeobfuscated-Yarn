package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.AttributeModifierCreator;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class StatusEffect {
	private final Map<EntityAttribute, AttributeModifierCreator> attributeModifiers = Maps.<EntityAttribute, AttributeModifierCreator>newHashMap();
	private final StatusEffectCategory category;
	private final int color;
	@Nullable
	private String translationKey;
	private Supplier<StatusEffectInstance.FactorCalculationData> factorCalculationDataSupplier = () -> null;
	private final RegistryEntry.Reference<StatusEffect> registryEntry = Registries.STATUS_EFFECT.createEntry(this);

	protected StatusEffect(StatusEffectCategory category, int color) {
		this.category = category;
		this.color = color;
	}

	public Optional<StatusEffectInstance.FactorCalculationData> getFactorCalculationDataSupplier() {
		return Optional.ofNullable((StatusEffectInstance.FactorCalculationData)this.factorCalculationDataSupplier.get());
	}

	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
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

	public StatusEffect addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
		this.attributeModifiers.put(attribute, new StatusEffect.EffectAttributeModifierCreator(UUID.fromString(uuid), amount, operation));
		return this;
	}

	public StatusEffect setFactorCalculationDataSupplier(Supplier<StatusEffectInstance.FactorCalculationData> factorCalculationDataSupplier) {
		this.factorCalculationDataSupplier = factorCalculationDataSupplier;
		return this;
	}

	public Map<EntityAttribute, AttributeModifierCreator> getAttributeModifiers() {
		return this.attributeModifiers;
	}

	public void onRemoved(AttributeContainer attributeContainer) {
		for (Entry<EntityAttribute, AttributeModifierCreator> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(((AttributeModifierCreator)entry.getValue()).getUuid());
			}
		}
	}

	public void onApplied(AttributeContainer attributeContainer, int amplifier) {
		for (Entry<EntityAttribute, AttributeModifierCreator> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier(((AttributeModifierCreator)entry.getValue()).getUuid());
				entityAttributeInstance.addPersistentModifier(((AttributeModifierCreator)entry.getValue()).createAttributeModifier(amplifier));
			}
		}
	}

	public boolean isBeneficial() {
		return this.category == StatusEffectCategory.BENEFICIAL;
	}

	@Deprecated
	public RegistryEntry.Reference<StatusEffect> getRegistryEntry() {
		return this.registryEntry;
	}

	class EffectAttributeModifierCreator implements AttributeModifierCreator {
		private final UUID uuid;
		private final double baseValue;
		private final EntityAttributeModifier.Operation operation;

		public EffectAttributeModifierCreator(UUID uuid, double baseValue, EntityAttributeModifier.Operation operation) {
			this.uuid = uuid;
			this.baseValue = baseValue;
			this.operation = operation;
		}

		@Override
		public UUID getUuid() {
			return this.uuid;
		}

		@Override
		public EntityAttributeModifier createAttributeModifier(int amplifier) {
			return new EntityAttributeModifier(
				this.uuid, StatusEffect.this.getTranslationKey() + " " + amplifier, this.baseValue * (double)(amplifier + 1), this.operation
			);
		}
	}
}
