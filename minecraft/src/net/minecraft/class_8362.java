package net.minecraft;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class class_8362 extends class_8273<Item> {
	private static final RegistryKey<Item> field_43962 = RegistryKey.of(RegistryKeys.ITEM, new Identifier("emerald"));

	public class_8362() {
		super(RegistryKeys.ITEM, Text.translatable("rule.payment.seed_reshuffle"), Text.translatable("rule.payment.seed"), field_43962);
	}

	@Override
	protected Text method_50152(RegistryKey<Item> registryKey) {
		Item item = Registries.ITEM.get(registryKey);
		return Text.translatable("rule.payment.item", item.getName());
	}
}
