package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
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
		return Registry.ENCHANTMENT.getInt(i);
	}

	protected Enchantment(Enchantment.Weight weight, EnchantmentTarget enchantmentTarget, EquipmentSlot[] equipmentSlots) {
		this.weight = weight;
		this.type = enchantmentTarget;
		this.slotTypes = equipmentSlots;
	}

	public List<ItemStack> getEquipment(LivingEntity livingEntity) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		for (EquipmentSlot equipmentSlot : this.slotTypes) {
			ItemStack itemStack = livingEntity.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				list.add(itemStack);
			}
		}

		return list;
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

	public TextComponent getTextComponent(int i) {
		TextComponent textComponent = new TranslatableTextComponent(this.getTranslationKey());
		if (this.isCursed()) {
			textComponent.applyFormat(TextFormat.RED);
		} else {
			textComponent.applyFormat(TextFormat.GRAY);
		}

		if (i != 1 || this.getMaximumLevel() != 1) {
			textComponent.append(" ").append(new TranslatableTextComponent("enchantment.level." + i));
		}

		return textComponent;
	}

	public boolean isAcceptableItem(ItemStack itemStack) {
		return this.type.isAcceptableItem(itemStack.getItem());
	}

	public void onTargetDamaged(LivingEntity livingEntity, Entity entity, int i) {
	}

	public void onUserDamaged(LivingEntity livingEntity, Entity entity, int i) {
	}

	public boolean isLootOnly() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public static enum Weight {
		COMMON(30),
		UNCOMMON(10),
		RARE(3),
		LEGENDARY(1);

		private final int weight;

		private Weight(int j) {
			this.weight = j;
		}

		public int getWeight() {
			return this.weight;
		}
	}
}
