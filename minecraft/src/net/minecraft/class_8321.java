package net.minecraft;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class class_8321 extends class_8269<RegistryKey<Item>> {
	private final String field_43807;

	public class_8321(String string, int i, int j) {
		super(RegistryKey.createCodec(RegistryKeys.ITEM), i, j);
		this.field_43807 = string;
	}

	@Override
	protected Stream<RegistryKey<Item>> method_50140(MinecraftServer minecraftServer, Random random) {
		Registry<Item> registry = minecraftServer.getRegistryManager().get(RegistryKeys.ITEM);
		return Stream.generate(() -> registry.getRandom(random).map(RegistryEntry.Reference::registryKey)).flatMap(Optional::stream);
	}

	public ItemStack method_50348(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return itemStack;
		} else {
			Item item = itemStack.getItem();
			float f = this.method_50142(item.getRegistryEntry().registryKey());
			if ((double)f == 1.0) {
				return itemStack;
			} else {
				int i = Math.round((float)itemStack.getCount() * f);
				return itemStack.copyWithCount(MathHelper.clamp(i, 1, item.getMaxCount()));
			}
		}
	}

	protected Text method_50162(RegistryKey<Item> registryKey, Integer integer) {
		Text text = Registries.ITEM.get(registryKey.getValue()).getName();
		return Text.translatable(this.field_43807, text, Text.literal(method_50134(integer)));
	}
}
