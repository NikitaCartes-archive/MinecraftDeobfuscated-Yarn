package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class class_8336 extends class_8285<Item> {
	private final String field_43892;

	public class_8336(String string) {
		super(RegistryKeys.ITEM);
		this.field_43892 = string;
	}

	@Nullable
	public RegistryKey<Item> method_50389(Item item) {
		return (RegistryKey<Item>)this.field_43489.get(item.getRegistryEntry().registryKey());
	}

	public ItemStack method_50387(DynamicRegistryManager dynamicRegistryManager, ItemStack itemStack) {
		RegistryKey<Item> registryKey = (RegistryKey<Item>)this.field_43489.get(itemStack.getItem().getRegistryEntry().registryKey());
		if (registryKey != null) {
			Item item = dynamicRegistryManager.get(RegistryKeys.ITEM).get(registryKey);
			if (item != null) {
				ItemStack itemStack2 = new ItemStack(item, itemStack.getCount());
				itemStack2.setNbt(itemStack.getNbt());
				return itemStack2;
			}
		}

		return itemStack;
	}

	protected Text method_50162(RegistryKey<Item> registryKey, RegistryKey<Item> registryKey2) {
		Text text = Registries.ITEM.get(registryKey).getName();
		Text text2 = Registries.ITEM.get(registryKey2).getName();
		return Text.translatable(this.field_43892, text, text2);
	}
}
