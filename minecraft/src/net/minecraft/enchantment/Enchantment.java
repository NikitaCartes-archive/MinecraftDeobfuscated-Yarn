package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public abstract class Enchantment {
	private final EquipmentSlot[] slotTypes;
	private final Enchantment.Weight weight;
	@Nullable
	public EnchantmentTarget type;
	@Nullable
	protected String translationKey;

	@Nullable
	@Environment(EnvType.CLIENT)
	public static Enchantment byRawId(int id) {
		return Registry.ENCHANTMENT.get(id);
	}

	protected Enchantment(Enchantment.Weight weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
		this.weight = weight;
		this.type = type;
		this.slotTypes = slotTypes;
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

	public int getMinimumPower(int level) {
		return 1 + level * 10;
	}

	public int getMaximumPower(int level) {
		return this.getMinimumPower(level) + 5;
	}

	public int getProtectionAmount(int level, DamageSource source) {
		return 0;
	}

	public float getAttackDamage(int level, LivingEntity livingEntity) {
		return 0.0F;
	}

	public final boolean isDifferent(Enchantment other) {
		return this.differs(other) && other.differs(this);
	}

	protected boolean differs(Enchantment other) {
		return this != other;
	}

	protected String getOrCreateTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public Text getName(int level) {
		Text text = new TranslatableText(this.getTranslationKey());
		if (this.isCursed()) {
			text.formatted(Formatting.RED);
		} else {
			text.formatted(Formatting.GRAY);
		}

		if (level != 1 || this.getMaximumLevel() != 1) {
			text.append(" ").append(new TranslatableText("enchantment.level." + level));
		}

		return text;
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.type.isAcceptableItem(stack.getItem());
	}

	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
	}

	public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
	}

	public boolean isTreasure() {
		return false;
	}

	public boolean isCursed() {
		return false;
	}

	public static enum Weight {
		COMMON(10),
		UNCOMMON(5),
		RARE(2),
		VERY_RARE(1);

		private final int weight;

		private Weight(int weight) {
			this.weight = weight;
		}

		public int getWeight() {
			return this.weight;
		}
	}
}
