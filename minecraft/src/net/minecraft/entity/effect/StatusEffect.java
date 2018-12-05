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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class StatusEffect {
	private final Map<EntityAttribute, EntityAttributeModifier> attributes = Maps.<EntityAttribute, EntityAttributeModifier>newHashMap();
	private final boolean negative;
	private final int color;
	@Nullable
	private String translationKey;
	private int iconIndex = -1;
	private double field_5888;
	private boolean field_5882;

	@Nullable
	public static StatusEffect byRawId(int i) {
		return Registry.STATUS_EFFECT.getInt(i);
	}

	public static int getRawId(StatusEffect statusEffect) {
		return Registry.STATUS_EFFECT.getRawId(statusEffect);
	}

	protected StatusEffect(boolean bl, int i) {
		this.negative = bl;
		if (bl) {
			this.field_5888 = 0.5;
		} else {
			this.field_5888 = 1.0;
		}

		this.color = i;
	}

	protected StatusEffect setIcon(int i, int j) {
		this.iconIndex = i + j * 12;
		return this;
	}

	public void applyUpdateEffect(LivingEntity livingEntity, int i) {
		if (this == StatusEffects.field_5924) {
			if (livingEntity.getHealth() < livingEntity.getHealthMaximum()) {
				livingEntity.heal(1.0F);
			}
		} else if (this == StatusEffects.field_5899) {
			if (livingEntity.getHealth() > 1.0F) {
				livingEntity.damage(DamageSource.MAGIC, 1.0F);
			}
		} else if (this == StatusEffects.field_5920) {
			livingEntity.damage(DamageSource.WITHER, 1.0F);
		} else if (this == StatusEffects.field_5903 && livingEntity instanceof PlayerEntity) {
			((PlayerEntity)livingEntity).addExhaustion(0.005F * (float)(i + 1));
		} else if (this == StatusEffects.field_5922 && livingEntity instanceof PlayerEntity) {
			if (!livingEntity.world.isRemote) {
				((PlayerEntity)livingEntity).getHungerManager().add(i + 1, 1.0F);
			}
		} else if ((this != StatusEffects.field_5915 || livingEntity.isUndead()) && (this != StatusEffects.field_5921 || !livingEntity.isUndead())) {
			if (this == StatusEffects.field_5921 && !livingEntity.isUndead() || this == StatusEffects.field_5915 && livingEntity.isUndead()) {
				livingEntity.damage(DamageSource.MAGIC, (float)(6 << i));
			}
		} else {
			livingEntity.heal((float)Math.max(4 << i, 0));
		}
	}

	public void applyInstantEffect(@Nullable Entity entity, @Nullable Entity entity2, LivingEntity livingEntity, int i, double d) {
		if ((this != StatusEffects.field_5915 || livingEntity.isUndead()) && (this != StatusEffects.field_5921 || !livingEntity.isUndead())) {
			if (this == StatusEffects.field_5921 && !livingEntity.isUndead() || this == StatusEffects.field_5915 && livingEntity.isUndead()) {
				int j = (int)(d * (double)(6 << i) + 0.5);
				if (entity == null) {
					livingEntity.damage(DamageSource.MAGIC, (float)j);
				} else {
					livingEntity.damage(DamageSource.magic(entity, entity2), (float)j);
				}
			} else {
				this.applyUpdateEffect(livingEntity, i);
			}
		} else {
			int j = (int)(d * (double)(4 << i) + 0.5);
			livingEntity.heal((float)j);
		}
	}

	public boolean canApplyUpdateEffect(int i, int j) {
		if (this == StatusEffects.field_5924) {
			int k = 50 >> j;
			return k > 0 ? i % k == 0 : true;
		} else if (this == StatusEffects.field_5899) {
			int k = 25 >> j;
			return k > 0 ? i % k == 0 : true;
		} else if (this == StatusEffects.field_5920) {
			int k = 40 >> j;
			return k > 0 ? i % k == 0 : true;
		} else {
			return this == StatusEffects.field_5903;
		}
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

	public TextComponent method_5560() {
		return new TranslatableTextComponent(this.getTranslationKey());
	}

	@Environment(EnvType.CLIENT)
	public boolean hasIcon() {
		return this.iconIndex >= 0;
	}

	@Environment(EnvType.CLIENT)
	public int getIconIndex() {
		return this.iconIndex;
	}

	@Environment(EnvType.CLIENT)
	public boolean isNegative() {
		return this.negative;
	}

	protected StatusEffect method_5571(double d) {
		this.field_5888 = d;
		return this;
	}

	public int getColor() {
		return this.color;
	}

	public StatusEffect method_5566(EntityAttribute entityAttribute, String string, double d, EntityAttributeModifier.Operation operation) {
		EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(string), this::getTranslationKey, d, operation);
		this.attributes.put(entityAttribute, entityAttributeModifier);
		return this;
	}

	@Environment(EnvType.CLIENT)
	public Map<EntityAttribute, EntityAttributeModifier> method_5565() {
		return this.attributes;
	}

	public void method_5562(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		for (Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributes.entrySet()) {
			EntityAttributeInstance entityAttributeInstance = abstractEntityAttributeContainer.get((EntityAttribute)entry.getKey());
			if (entityAttributeInstance != null) {
				entityAttributeInstance.removeModifier((EntityAttributeModifier)entry.getValue());
			}
		}
	}

	public void method_5555(LivingEntity livingEntity, AbstractEntityAttributeContainer abstractEntityAttributeContainer, int i) {
		for (Entry<EntityAttribute, EntityAttributeModifier> entry : this.attributes.entrySet()) {
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
		return this.field_5882;
	}

	public StatusEffect method_5557() {
		this.field_5882 = true;
		return this;
	}
}
