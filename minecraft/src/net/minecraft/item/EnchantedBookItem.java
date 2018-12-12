package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipOptions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
	public boolean hasEnchantmentGlow(ItemStack itemStack) {
		return true;
	}

	@Override
	public boolean isTool(ItemStack itemStack) {
		return false;
	}

	public static ListTag getEnchantmentTag(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		return compoundTag != null ? compoundTag.getList("StoredEnchantments", 10) : new ListTag();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		super.buildTooltip(itemStack, world, list, tooltipOptions);
		ListTag listTag = getEnchantmentTag(itemStack);

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompoundTag(i);
			Enchantment enchantment = Registry.ENCHANTMENT.get(Identifier.create(compoundTag.getString("id")));
			if (enchantment != null) {
				list.add(enchantment.getTextComponent(compoundTag.getInt("lvl")));
			}
		}
	}

	public static void addEnchantment(ItemStack itemStack, InfoEnchantment infoEnchantment) {
		ListTag listTag = getEnchantmentTag(itemStack);
		boolean bl = true;
		Identifier identifier = Registry.ENCHANTMENT.getId(infoEnchantment.enchantment);

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
			listTag.add((Tag)compoundTag2);
		}

		itemStack.getOrCreateTag().put("StoredEnchantments", listTag);
	}

	public static ItemStack method_7808(InfoEnchantment infoEnchantment) {
		ItemStack itemStack = new ItemStack(Items.field_8598);
		addEnchantment(itemStack, infoEnchantment);
		return itemStack;
	}

	@Override
	public void addStacksForDisplay(ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (itemGroup == ItemGroup.SEARCH) {
			for (Enchantment enchantment : Registry.ENCHANTMENT) {
				if (enchantment.type != null) {
					for (int i = enchantment.getMinimumLevel(); i <= enchantment.getMaximumLevel(); i++) {
						defaultedList.add(method_7808(new InfoEnchantment(enchantment, i)));
					}
				}
			}
		} else if (itemGroup.getEnchantmentTypes().length != 0) {
			for (Enchantment enchantmentx : Registry.ENCHANTMENT) {
				if (itemGroup.containsEnchantmentType(enchantmentx.type)) {
					defaultedList.add(method_7808(new InfoEnchantment(enchantmentx, enchantmentx.getMaximumLevel())));
				}
			}
		}
	}
}
