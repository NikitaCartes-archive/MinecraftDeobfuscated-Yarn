package net.minecraft;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class class_8312 extends class_8273<Item> {
	private static final RegistryKey<Item> field_43766 = RegistryKey.of(RegistryKeys.ITEM, new Identifier("egg"));

	public class_8312() {
		super(RegistryKeys.ITEM, Text.translatable("rule.egg_free.seed_reshuffle"), Text.translatable("rule.egg_free.seed"), field_43766);
	}

	@Override
	protected Text method_50152(RegistryKey<Item> registryKey) {
		Item item = Registries.ITEM.get(registryKey);
		return Text.translatable("rule.egg_free.item", item.getName());
	}
}
