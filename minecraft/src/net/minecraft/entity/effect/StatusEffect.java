package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class StatusEffect {
	private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.<EntityAttribute, EntityAttributeModifier>newHashMap();
	private final StatusEffectType type;
	private final int color;
	@Nullable
	private String translationKey;

	@Nullable
	public static StatusEffect byRawId(int rawId) {
		return Registry.STATUS_EFFECT.get(rawId);
	}

	public static int getRawId(StatusEffect type) {
		return Registry.STATUS_EFFECT.getRawId(type);
	}

	protected StatusEffect(StatusEffectType statusEffectType, int i) {
		this.type = statusEffectType;
		this.color = i;
	}

	public void applyUpdateEffect(LivingEntity entity, int i) {
		if (this == StatusEffects.REGENERATION) {
			if (entity.getHealth() < entity.getMaximumHealth()) {
				entity.heal(1.0F);
			}
		} else if (this == StatusEffects.POISON) {
			if (entity.getHealth() > 1.0F) {
				entity.damage(DamageSource.MAGIC, 1.0F);
			}
		} else if (this == StatusEffects.WITHER) {
			entity.damage(DamageSource.WITHER, 1.0F);
		} else if (this == StatusEffects.HUNGER && entity instanceof PlayerEntity) {
			((PlayerEntity)entity).addExhaustion(0.005F * (float)(i + 1));
		} else if (this == StatusEffects.SATURATION && entity instanceof PlayerEntity) {
			if (!entity.world.isClient) {
				((PlayerEntity)entity).getHungerManager().add(i + 1, 1.0F);
			}
		} else if ((this != StatusEffects.INSTANT_HEALTH || entity.isUndead()) && (this != StatusEffects.INSTANT_DAMAGE || !entity.isUndead())) {
			if (this == StatusEffects.INSTANT_DAMAGE && !entity.isUndead() || this == StatusEffects.INSTANT_HEALTH && entity.isUndead()) {
				entity.damage(DamageSource.MAGIC, (float)(6 << i));
			}
		} else {
			entity.heal((float)Math.max(4 << i, 0));
		}
	}

	public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double d) {
		if ((this != StatusEffects.INSTANT_HEALTH || target.isUndead()) && (this != StatusEffects.INSTANT_DAMAGE || !target.isUndead())) {
			if (this == StatusEffects.INSTANT_DAMAGE && !target.isUndead() || this == StatusEffects.INSTANT_HEALTH && target.isUndead()) {
				int i = (int)(d * (double)(6 << amplifier) + 0.5);
				if (source == null) {
					target.damage(DamageSource.MAGIC, (float)i);
				} else {
					target.damage(DamageSource.magic(source, attacker), (float)i);
				}
			} else {
				this.applyUpdateEffect(target, amplifier);
			}
		} else {
			int i = (int)(d * (double)(4 << amplifier) + 0.5);
			target.heal((float)i);
		}
	}

	public boolean canApplyUpdateEffect(int duration, int i) {
		if (this == StatusEffects.REGENERATION) {
			int j = 50 >> i;
			return j > 0 ? duration % j == 0 : true;
		} else if (this == StatusEffects.POISON) {
			int j = 25 >> i;
			return j > 0 ? duration % j == 0 : true;
		} else if (this == StatusEffects.WITHER) {
			int j = 40 >> i;
			return j > 0 ? duration % j == 0 : true;
		} else {
			return this == StatusEffects.HUNGER;
		}
	}

	public boolean isInstant() {
		return false;
	}

	protected String loadTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("effect", Registry.STATUS_EFFECT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.loadTranslationKey();
	}

	public Text getName() {
		return new TranslatableText(this.getTranslationKey());
	}

	@Environment(EnvType.CLIENT)
	public StatusEffectType getType() {
		return this.type;
	}

	public int getColor() {
		return this.color;
	}

	public StatusEffect addAttributeModifier(EntityAttribute attribute, String uuid, double amount, EntityAttributeModifier.Operation operation) {
		EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(uuid), this::getTranslationKey, amount, operation);
		this.attributeModifiers.put(attribute, entityAttributeModifier);
		return this;
	}

	@Environment(EnvType.CLIENT)
	public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
		return this.attributeModifiers;
	}

	public void onRemoved(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		for (Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	public void onApplied(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		for (Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)entry.getValue();
				entityAttributeInstance.removeModifier(entityAttributeModifier);
				entityAttributeInstance.addModifier(
					new EntityAttributeModifier(
						entityAttributeModifier.getId(), this.getTranslationKey() + " " + i, this.method_5563(i, entityAttributeModifier), entityAttributeModifier.getOperation()
					)
				);
			}
		}
	}

	public double method_5563(int i, EntityAttributeModifier entityAttributeModifier) {
		return entityAttributeModifier.getAmount() * (double)(i + 1);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5573() {
		return this.type == StatusEffectType.BENEFICIAL;
	}
}
