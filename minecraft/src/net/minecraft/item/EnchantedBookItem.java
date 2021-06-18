package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantedBookItem extends Item {
	public static final String STORED_ENCHANTMENTS_KEY = "StoredEnchantments";

	public EnchantedBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static NbtList getEnchantmentNbt(ItemStack stack) {
		NbtCompound nbtCompound = stack.getTag();
		return nbtCompound != null ? nbtCompound.getList("StoredEnchantments", NbtElement.COMPOUND_TYPE) : new NbtList();
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		ItemStack.appendEnchantments(tooltip, getEnchantmentNbt(stack));
	}

	public static void addEnchantment(ItemStack stack, EnchantmentLevelEntry entry) {
		NbtList nbtList = getEnchantmentNbt(stack);
		boolean bl = true;
		Identifier identifier = EnchantmentHelper.getEnchantmentId(entry.enchantment);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			Identifier identifier2 = EnchantmentHelper.getIdFromNbt(nbtCompound);
			if (identifier2 != null && identifier2.equals(identifier)) {
				if (EnchantmentHelper.getLevelFromNbt(nbtCompound) < entry.level) {
					EnchantmentHelper.writeLevelToNbt(nbtCompound, entry.level);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			nbtList.add(EnchantmentHelper.createNbt(identifier, entry.level));
		}

		stack.getOrCreateTag().put("StoredEnchantments", nbtList);
	}

	public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
		ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
		addEnchantment(itemStack, info);
		return itemStack;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
		if (group == ItemGroup.SEARCH) {
			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (enchantment.type != null) {
					for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
						stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
					}
				}
			}
		} else if (group.getEnchantments().length != 0) {
			for (Enchantment enchantmentx : Registry.ENCHANTMENT) {
				if (group.containsEnchantments(enchantmentx.type)) {
					stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantmentx, enchantmentx.getMaxLevel())));
				}
			}
		}
	}
}
