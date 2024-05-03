package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentsPredicate implements ComponentSubPredicate<ItemEnchantmentsComponent> {
	private final List<EnchantmentPredicate> enchantments;

	protected EnchantmentsPredicate(List<EnchantmentPredicate> enchantments) {
		this.enchantments = enchantments;
	}

	public static <T extends EnchantmentsPredicate> Codec<T> createCodec(Function<List<EnchantmentPredicate>, T> predicateFunction) {
		return EnchantmentPredicate.CODEC.listOf().xmap(predicateFunction, EnchantmentsPredicate::getEnchantments);
	}

	protected List<EnchantmentPredicate> getEnchantments() {
		return this.enchantments;
	}

	public boolean test(ItemStack itemStack, ItemEnchantmentsComponent itemEnchantmentsComponent) {
		for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
			if (!enchantmentPredicate.test(itemEnchantmentsComponent)) {
				return false;
			}
		}

		return true;
	}

	public static EnchantmentsPredicate.Enchantments enchantments(List<EnchantmentPredicate> enchantments) {
		return new EnchantmentsPredicate.Enchantments(enchantments);
	}

	public static EnchantmentsPredicate.StoredEnchantments storedEnchantments(List<EnchantmentPredicate> storedEnchantments) {
		return new EnchantmentsPredicate.StoredEnchantments(storedEnchantments);
	}

	public static class Enchantments extends EnchantmentsPredicate {
		public static final Codec<EnchantmentsPredicate.Enchantments> CODEC = createCodec(EnchantmentsPredicate.Enchantments::new);

		protected Enchantments(List<EnchantmentPredicate> list) {
			super(list);
		}

		@Override
		public ComponentType<ItemEnchantmentsComponent> getComponentType() {
			return DataComponentTypes.ENCHANTMENTS;
		}
	}

	public static class StoredEnchantments extends EnchantmentsPredicate {
		public static final Codec<EnchantmentsPredicate.StoredEnchantments> CODEC = createCodec(EnchantmentsPredicate.StoredEnchantments::new);

		protected StoredEnchantments(List<EnchantmentPredicate> list) {
			super(list);
		}

		@Override
		public ComponentType<ItemEnchantmentsComponent> getComponentType() {
			return DataComponentTypes.STORED_ENCHANTMENTS;
		}
	}
}
