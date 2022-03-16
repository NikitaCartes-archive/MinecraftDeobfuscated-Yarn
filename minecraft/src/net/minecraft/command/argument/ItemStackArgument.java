package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

public class ItemStackArgument implements Predicate<ItemStack> {
	private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType(
		(item, maxCount) -> new TranslatableText("arguments.item.overstacked", item, maxCount)
	);
	private final RegistryEntry<Item> item;
	@Nullable
	private final NbtCompound nbt;

	public ItemStackArgument(RegistryEntry<Item> item, @Nullable NbtCompound nbt) {
		this.item = item;
		this.nbt = nbt;
	}

	public Item getItem() {
		return this.item.value();
	}

	public boolean test(ItemStack itemStack) {
		return itemStack.itemMatches(this.item) && NbtHelper.matches(this.nbt, itemStack.getNbt(), true);
	}

	public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.item, amount);
		if (this.nbt != null) {
			itemStack.setNbt(this.nbt);
		}

		if (checkOverstack && amount > itemStack.getMaxCount()) {
			throw OVERSTACKED_EXCEPTION.create(this.getIdString(), itemStack.getMaxCount());
		} else {
			return itemStack;
		}
	}

	public String asString() {
		StringBuilder stringBuilder = new StringBuilder(this.getIdString());
		if (this.nbt != null) {
			stringBuilder.append(this.nbt);
		}

		return stringBuilder.toString();
	}

	private String getIdString() {
		return this.item.getKey().map(RegistryKey::getValue).orElseGet(() -> "unknown[" + this.item + "]").toString();
	}
}
