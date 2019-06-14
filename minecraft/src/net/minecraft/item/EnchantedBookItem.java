package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantedBookItem extends Item {
	public EnchantedBookItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack itemStack) {
		return false;
	}

	public static ListTag getEnchantmentTag(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null ? compoundTag.getList("StoredEnchantments", 10) : new ListTag();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		super.method_7851(itemStack, world, list, tooltipContext);
		ItemStack.appendEnchantments(list, getEnchantmentTag(itemStack));
	}

	public static void method_7807(ItemStack itemStack, InfoEnchantment infoEnchantment) {
		ListTag listTag = getEnchantmentTag(itemStack);
		boolean bl = true;
		Identifier identifier = Registry.ENCHANTMENT.getId(infoEnchantment.enchantment);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Identifier identifier2 = Identifier.tryParse(compoundTag.getString("id"));
			if (identifier2 != null && identifier2.equals(identifier)) {
				if (compoundTag.getInt("lvl") < infoEnchantment.level) {
					compoundTag.putShort("lvl", (short)infoEnchantment.level);
				}

				bl = false;
				break;
			}
		}

		if (bl) {
			CompoundTag compoundTag2 = new CompoundTag();
			compoundTag2.putString("id", String.valueOf(identifier));
			compoundTag2.putShort("lvl", (short)infoEnchantment.level);
			listTag.add(compoundTag2);
		}

		itemStack.getOrCreateTag().put("StoredEnchantments", listTag);
	}

	public static ItemStack method_7808(InfoEnchantment infoEnchantment) {
		ItemStack itemStack = new ItemStack(Items.field_8598);
		method_7807(itemStack, infoEnchantment);
		return itemStack;
	}

	@Override
	public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (itemGroup == ItemGroup.SEARCH) {
			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (enchantment.field_9083 != null) {
					for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
						defaultedList.add(method_7808(new InfoEnchantment(enchantment, i)));
					}
				}
			}
		} else if (itemGroup.method_7744().length != 0) {
			for (Enchantment enchantmentx : Registry.ENCHANTMENT) {
				if (itemGroup.method_7740(enchantmentx.field_9083)) {
					defaultedList.add(method_7808(new InfoEnchantment(enchantmentx, enchantmentx.getMaximumLevel())));
				}
			}
		}
	}
}
