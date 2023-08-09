package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public abstract class Enchantment {
	private final EquipmentSlot[] slotTypes;
	private final Enchantment.Rarity rarity;
	public final EnchantmentTarget target;
	@Nullable
	protected String translationKey;
	private final RegistryEntry.Reference<Enchantment> registryEntry = Registries.ENCHANTMENT.createEntry(this);

	@Nullable
	public static Enchantment byRawId(int id) {
		return Registries.ENCHANTMENT.get(id);
	}

	protected Enchantment(Enchantment.Rarity rarity, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
		this.rarity = rarity;
		this.target = target;
		this.slotTypes = slotTypes;
	}

	public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
		Map<EquipmentSlot, ItemStack> map = Maps.newEnumMap(EquipmentSlot.class);

		for (EquipmentSlot equipmentSlot : this.slotTypes) {
			ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				map.put(equipmentSlot, itemStack);
			}
		}

		return map;
	}

	public Enchantment.Rarity getRarity() {
		return this.rarity;
	}

	public int getMinLevel() {
		return 1;
	}

	public int getMaxLevel() {
		return 1;
	}

	public int getMinPower(int level) {
		return 1 + level * 10;
	}

	public int getMaxPower(int level) {
		return this.getMinPower(level) + 5;
	}

	public int getProtectionAmount(int level, DamageSource source) {
		return 0;
	}

	public float getAttackDamage(int level, EntityGroup group) {
		return 0.0F;
	}

	/**
	 * {@return whether this enchantment can exist on an item stack with the
	 * {@code other} enchantment and the {@code other} enchantment can exist
	 * with this enchantment}
	 */
	public final boolean canCombine(Enchantment other) {
		return this.canAccept(other) && other.canAccept(this);
	}

	/**
	 * {@return whether this enchantment can exist on an item stack with the
	 * {@code other} enchantment}
	 */
	protected boolean canAccept(Enchantment other) {
		return this != other;
	}

	protected String getOrCreateTranslationKey() {
		if (this.translationKey == null) {
			this.translationKey = Util.createTranslationKey("enchantment", Registries.ENCHANTMENT.getId(this));
		}

		return this.translationKey;
	}

	public String getTranslationKey() {
		return this.getOrCreateTranslationKey();
	}

	public Text getName(int level) {
		MutableText mutableText = Text.translatable(this.getTranslationKey());
		if (this.isCursed()) {
			mutableText.formatted(Formatting.RED);
		} else {
			mutableText.formatted(Formatting.GRAY);
		}

		if (level != 1 || this.getMaxLevel() != 1) {
			mutableText.append(ScreenTexts.SPACE).append(Text.translatable("enchantment.level." + level));
		}

		return mutableText;
	}

	public boolean isAcceptableItem(ItemStack stack) {
		return this.target.isAcceptableItem(stack.getItem());
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

	/**
	 * {@return whether this enchantment will appear in the enchanted book trade
	 * offers of librarian villagers}
	 */
	public boolean isAvailableForEnchantedBookOffer() {
		return true;
	}

	/**
	 * {@return whether this enchantment will appear in the enchanting table or
	 * loots with random enchant function}
	 */
	public boolean isAvailableForRandomSelection() {
		return true;
	}

	@Deprecated
	public RegistryEntry.Reference<Enchantment> getRegistryEntry() {
		return this.registryEntry;
	}

	/**
	 * The rarity is an attribute of an enchantment.
	 * 
	 * <p>It affects the chance of getting an enchantment from enchanting or
	 * loots as well as the combination cost in anvil.
	 */
	public static enum Rarity {
		COMMON(10),
		UNCOMMON(5),
		RARE(2),
		VERY_RARE(1);

		private final int weight;

		private Rarity(int weight) {
			this.weight = weight;
		}

		/**
		 * {@return the weight of an enchantment in weighted pickers}
		 */
		public int getWeight() {
			return this.weight;
		}
	}
}
