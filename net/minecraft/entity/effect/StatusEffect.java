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
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class StatusEffect {
    private final Map<EntityAttribute, EntityAttributeModifier> attributeModifiers = Maps.newHashMap();
    private final StatusEffectType type;
    private final int color;
    @Nullable
    private String translationKey;

    @Nullable
    public static StatusEffect byRawId(int i) {
        return (StatusEffect)Registry.STATUS_EFFECT.get(i);
    }

    public static int getRawId(StatusEffect statusEffect) {
        return Registry.STATUS_EFFECT.getRawId(statusEffect);
    }

    protected StatusEffect(StatusEffectType statusEffectType, int i) {
        this.type = statusEffectType;
        this.color = i;
    }

    public void applyUpdateEffect(LivingEntity livingEntity, int i) {
        if (this == StatusEffects.REGENERATION) {
            if (livingEntity.getHealth() < livingEntity.getHealthMaximum()) {
                livingEntity.heal(1.0f);
            }
        } else if (this == StatusEffects.POISON) {
            if (livingEntity.getHealth() > 1.0f) {
                livingEntity.damage(DamageSource.MAGIC, 1.0f);
            }
        } else if (this == StatusEffects.WITHER) {
            livingEntity.damage(DamageSource.WITHER, 1.0f);
        } else if (this == StatusEffects.HUNGER && livingEntity instanceof PlayerEntity) {
            ((PlayerEntity)livingEntity).addExhaustion(0.005f * (float)(i + 1));
        } else if (this == StatusEffects.SATURATION && livingEntity instanceof PlayerEntity) {
            if (!livingEntity.world.isClient) {
                ((PlayerEntity)livingEntity).getHungerManager().add(i + 1, 1.0f);
            }
        } else if (this == StatusEffects.INSTANT_HEALTH && !livingEntity.isUndead() || this == StatusEffects.INSTANT_DAMAGE && livingEntity.isUndead()) {
            livingEntity.heal(Math.max(4 << i, 0));
        } else if (this == StatusEffects.INSTANT_DAMAGE && !livingEntity.isUndead() || this == StatusEffects.INSTANT_HEALTH && livingEntity.isUndead()) {
            livingEntity.damage(DamageSource.MAGIC, 6 << i);
        }
    }

    public void applyInstantEffect(@Nullable Entity entity, @Nullable Entity entity2, LivingEntity livingEntity, int i, double d) {
        if (this == StatusEffects.INSTANT_HEALTH && !livingEntity.isUndead() || this == StatusEffects.INSTANT_DAMAGE && livingEntity.isUndead()) {
            int j = (int)(d * (double)(4 << i) + 0.5);
            livingEntity.heal(j);
        } else if (this == StatusEffects.INSTANT_DAMAGE && !livingEntity.isUndead() || this == StatusEffects.INSTANT_HEALTH && livingEntity.isUndead()) {
            int j = (int)(d * (double)(6 << i) + 0.5);
            if (entity == null) {
                livingEntity.damage(DamageSource.MAGIC, j);
            } else {
                livingEntity.damage(DamageSource.magic(entity, entity2), j);
            }
        } else {
            this.applyUpdateEffect(livingEntity, i);
        }
    }

    public boolean canApplyUpdateEffect(int i, int j) {
        if (this == StatusEffects.REGENERATION) {
            int k = 50 >> j;
            if (k > 0) {
                return i % k == 0;
            }
            return true;
        }
        if (this == StatusEffects.POISON) {
            int k = 25 >> j;
            if (k > 0) {
                return i % k == 0;
            }
            return true;
        }
        if (this == StatusEffects.WITHER) {
            int k = 40 >> j;
            if (k > 0) {
                return i % k == 0;
            }
            return true;
        }
        return this == StatusEffects.HUNGER;
    }

    public boolean isInstant() {
        return false;
    }

    protected String method_5559() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("effect", Registry.STATUS_EFFECT.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.method_5559();
    }

    public Text method_5560() {
        return new TranslatableText(this.getTranslationKey(), new Object[0]);
    }

    @Environment(value=EnvType.CLIENT)
    public StatusEffectType getType() {
        return this.type;
    }

    public int getColor() {
        return this.color;
    }

    public StatusEffect addAttributeModifier(EntityAttribute entityAttribute, String string, double d, EntityAttributeModifier.Operation operation) {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(string), this::getTranslationKey, d, operation);
        this.attributeModifiers.put(entityAttribute, entityAttributeModifier);
        return this;
    }

    @Environment(value=EnvType.CLIENT)
    public Map<EntityAttribute, EntityAttributeModifier> getAttributeModifiers() {
        return this.attributeModifiers;
    }

    public void method_5562(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            entityAttributeInstance.removeModifier(entry.getValue());
        }
    }

    public void method_5555(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
        for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get(entry.getKey());
            if (entityAttributeInstance == null) continue;
            EntityAttributeModifier entityAttributeModifier = entry.getValue();
            entityAttributeInstance.removeModifier(entityAttributeModifier);
            entityAttributeInstance.addModifier(new EntityAttributeModifier(entityAttributeModifier.getId(), this.getTranslationKey() + " " + i, this.method_5563(i, entityAttributeModifier), entityAttributeModifier.getOperation()));
        }
    }

    public double method_5563(int i, EntityAttributeModifier entityAttributeModifier) {
        return entityAttributeModifier.getAmount() * (double)(i + 1);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_5573() {
        return this.type == StatusEffectType.BENEFICIAL;
    }
}

