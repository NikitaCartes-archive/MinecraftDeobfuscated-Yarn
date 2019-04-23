package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public abstract class Enchantment {
	private final EquipmentSlot[] slotTypes;
	private final Enchantment.Weight weight;
	@Nullable
	public EnchantmentTarget type;
	@Nullable
	protected String translationName;

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Enchantment byRawId(int i) {
		return Registry.ENCHANTMENT.get(i);
	}

	protected Enchantment(Enchantment.Weight weight, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) {
		this.weight = weight;
		this.type = enchantmentTarget;
		this.slotTypes = equipmentSlots;
	}

	public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity livingEntity) {
		Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

		for (EquipmentSlot equipmentSlot : this.slotTypes) {
			ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				map.put(equipmentSlot, itemStack);
			}
		}

		return map;
	}

	public Enchantment.Weight getWeight() {
		return this.weight;
	}

	public int getMinimumLevel() {
		return 1;
	}

	public int getMaximumLevel() {
		return 1;
	}

	public int getMinimumPower(int i) {
		return 1 + i * 10;
	}

	public int getProtectionAmount(int i, DamageSource damageSource) {
		return 0;
	}

	public float getAttackDamage(int i, EntityGroup entityGroup) {
		return 0.0F;
	}

	public final boolean isDifferent(Enchantment enchantment) {
		return this.differs(enchantment) && enchantment.differs(this);
	}

	protected boolean differs(Enchantment enchantment) {
		return this != enchantment;
	}

	protected String getOrCreateTranslationKey() {
		if (this.translationName == null) {
			this.translationName = SystemUtil.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
		}

		return this.translationName;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public Component getTextComponent(int i) {
		Component component = new TranslatableComponent(this.getTranslationKey());
		if (this.isCursed()) {
			component.applyFormat(ChatFormat.field_1061);
		} else {
			component.applyFormat(ChatFormat.field_1080);
		}

		if (i != 1 || this.getMaximumLevel() != 1) {
			component.append(" ").append(new TranslatableComponent("enchantment.level." + i));
		}

		return component;
	}

	public boolean isAcceptableItem(ItemStack itemStack) {
		return this.type.isAcceptableItem(itemStack.getItem());
	}

	public void onTargetDamaged(LivingEntity livingEntity, Entity entity, int i) {
	}

	public void onUserDamaged(LivingEntity livingEntity, Entity entity, int i) {
	}

	public boolean isTreasure() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public static enum Weight {
		field_9087(30),
		field_9090(10),
		field_9088(3),
		field_9091(1);

		private final int weight;

		private Weight(int j) {
			this.weight = j;
		}

		public int getWeight() {
			return this.weight;
		}
	}
}
