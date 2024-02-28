package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemStackArgument {
	private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType(
		(item, maxCount) -> Text.stringifiedTranslatable("arguments.item.overstacked", item, maxCount)
	);
	private final RegistryEntry<Item> item;
	private final ComponentMap components;

	public ItemStackArgument(RegistryEntry<Item> item, ComponentMap components) {
		this.item = item;
		this.components = components;
	}

	public Item getItem() {
		return this.item.value();
	}

	public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.item, amount);
		if (checkOverstack && amount > itemStack.getMaxCount()) {
			throw OVERSTACKED_EXCEPTION.create(this.getIdString(), itemStack.getMaxCount());
		} else {
			itemStack.applyComponentsFrom(this.components);
			return itemStack;
		}
	}

	public String asString(RegistryWrapper.WrapperLookup registries) {
		StringBuilder stringBuilder = new StringBuilder(this.getIdString());
		String string = this.componentsAsString(registries);
		if (!string.isEmpty()) {
			stringBuilder.append('[');
			stringBuilder.append(string);
			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	private String componentsAsString(RegistryWrapper.WrapperLookup registries) {
		DynamicOps<NbtElement> dynamicOps = registries.getOps(NbtOps.INSTANCE);
		return (String)this.components.stream().flatMap(component -> {
			DataComponentType<?> dataComponentType = component.type();
			Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(dataComponentType);
			Optional<NbtElement> optional = component.encode(dynamicOps).result();
			return identifier != null && !optional.isEmpty() ? Stream.of(identifier.toString() + "=" + optional.get()) : Stream.empty();
		}).collect(Collectors.joining(String.valueOf(',')));
	}

	private String getIdString() {
		return this.item.getKey().map(RegistryKey::getValue).orElseGet(() -> "unknown[" + this.item + "]").toString();
	}
}
