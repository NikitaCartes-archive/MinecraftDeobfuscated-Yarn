package net.minecraft;

import java.util.Objects;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

public class class_8335 extends class_8336 {
	private int field_43891;

	public class_8335() {
		super("rule.replace_item_model");
	}

	public Item method_50386(Item item) {
		RegistryKey<Item> registryKey = this.method_50389(item);
		return registryKey != null ? (Item)Objects.requireNonNullElse(Registries.ITEM.get(registryKey), item) : item;
	}

	@Override
	protected void method_50138(RegistryKey<Item> registryKey, RegistryKey<Item> registryKey2) {
		this.field_43891++;
		super.method_50138(registryKey, registryKey2);
	}

	@Override
	protected void method_50136(RegistryKey<Item> registryKey) {
		this.field_43891++;
		super.method_50136(registryKey);
	}

	public int method_50385() {
		return this.field_43891;
	}
}
