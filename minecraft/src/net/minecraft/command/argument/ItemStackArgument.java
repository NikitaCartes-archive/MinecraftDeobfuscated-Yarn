package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
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
	private final ComponentChanges components;

	public ItemStackArgument(RegistryEntry<Item> item, ComponentChanges components) {
		this.item = item;
		this.components = components;
	}

	public Item getItem() {
		return this.item.value();
	}

	public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.item, amount);
		itemStack.applyUnvalidatedChanges(this.components);
		if (checkOverstack && amount > itemStack.getMaxCount()) {
			throw OVERSTACKED_EXCEPTION.create(this.getIdString(), itemStack.getMaxCount());
		} else {
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
		return (String)this.components.entrySet().stream().flatMap(entry -> {
			ComponentType<?> componentType = (ComponentType<?>)entry.getKey();
			Identifier identifier = Registries.DATA_COMPONENT_TYPE.getId(componentType);
			if (identifier == null) {
				return Stream.empty();
			} else {
				Optional<?> optional = (Optional<?>)entry.getValue();
				if (optional.isPresent()) {
					Component<?> component = Component.of(componentType, optional.get());
					return component.encode(dynamicOps).result().stream().map(value -> identifier.toString() + "=" + value);
				} else {
					return Stream.of("!" + identifier.toString());
				}
			}
		}).collect(Collectors.joining(String.valueOf(',')));
	}

	private String getIdString() {
		return this.item.getKey().map(RegistryKey::getValue).orElseGet(() -> "unknown[" + this.item + "]").toString();
	}
}
