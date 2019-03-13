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
import net.minecraft.text.TextComponent;
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
	public boolean method_7886(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean method_7870(ItemStack itemStack) {
		return false;
	}

	public static ListTag method_7806(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7969();
		return compoundTag != null ? compoundTag.method_10554("StoredEnchantments", 10) : new ListTag();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		super.method_7851(itemStack, world, list, tooltipContext);
		ItemStack.method_17870(list, method_7806(itemStack));
	}

	public static void method_7807(ItemStack itemStack, InfoEnchantment infoEnchantment) {
		ListTag listTag = method_7806(itemStack);
		boolean bl = true;
		Identifier identifier = Registry.ENCHANTMENT.method_10221(infoEnchantment.enchantment);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Identifier identifier2 = Identifier.create(compoundTag.getString("id"));
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

		itemStack.method_7948().method_10566("StoredEnchantments", listTag);
	}

	public static ItemStack method_7808(InfoEnchantment infoEnchantment) {
		ItemStack itemStack = new ItemStack(Items.field_8598);
		method_7807(itemStack, infoEnchantment);
		return itemStack;
	}

	@Override
	public void method_7850(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
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
