/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class StatusEffect {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();
    private final StatusEffectType type;
    private final int color;
    @Nullable
    private String translationKey;

    @Nullable
    public static StatusEffect byRawId(int rawId) {
        return (StatusEffect)Registry.STATUS_EFFECT.get(rawId);
    }

    public static int getRawId(StatusEffect type) {
        return Registry.STATUS_EFFECT.getRawId(type);
    }

    protected StatusEffect(StatusEffectType type, int color) {
        this.type = type;
        this.color = color;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (this == StatusEffects.REGENERATION) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0f);
            }
        } else if (this == StatusEffects.POISON) {
            if (entity.getHealth() > 1.0f) {
                entity.damage(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == StatusEffects.WITHER) {
            entity.damage(DamageSource.WITHER, 1.0f);
        } else if (this == StatusEffects.HUNGER && entity instanceof PlayerEntity) {
            ((PlayerEntity)entity).addExhaustion(0.005f * (float)(amplifier + 1));
        } else if (this == StatusEffects.SATURATION && entity instanceof PlayerEntity) {
            if (!entity.world.isClient) {
                ((PlayerEntity)entity).getHungerManager().add(amplifier + 1, 1.0f);
            }
        } else if (this == StatusEffects.INSTANT_HEALTH && !entity.isUndead() || this == StatusEffects.INSTANT_DAMAGE && entity.isUndead()) {
            entity.heal(Math.max(4 << amplifier, 0));
        } else if (this == StatusEffects.INSTANT_DAMAGE && !entity.isUndead() || this == StatusEffects.INSTANT_HEALTH && entity.isUndead()) {
            entity.damage(DamageSource.MAGIC, 6 << amplifier);
        }
    }

    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if (this == StatusEffects.INSTANT_HEALTH && !target.isUndead() || this == StatusEffects.INSTANT_DAMAGE && target.isUndead()) {
            int i = (int)(proximity * (double)(4 << amplifier) + 0.5);
            target.heal(i);
        } else if (this == StatusEffects.INSTANT_DAMAGE && !target.isUndead() || this == StatusEffects.INSTANT_HEALTH && target.isUndead()) {
            int i = (int)(proximity * (double)(6 << amplifier) + 0.5);
            if (source == null) {
                target.damage(DamageSource.MAGIC, i);
            } else {
                target.damage(DamageSource.magic(source, attacker), i);
            }
        } else {
            this.applyUpdateEffect(target, amplifier);
        }
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (this == StatusEffects.REGENERATION) {
            int i = 50 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        if (this == StatusEffects.POISON) {
            int i = 25 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        if (this == StatusEffects.WITHER) {
            int i = 40 >> amplifier;
            if (i > 0) {
                return duration % i == 0;
            }
            return true;
        }
        return this == StatusEffects.HUNGER;
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

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
        }
    }

    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + amplifier, this.adjustModifierAmount(amplifier, entityAttributeModifier), entityAttributeModifier.getOperation()));
        }
    }

    public double adjustModifierAmount(int amplifier, EntityAttributeModifier modifier) {
        return modifier.getValue() * (double)(amplifier + 1);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isBeneficial() {
        return this.type == StatusEffectType.BENEFICIAL;
    }
}

