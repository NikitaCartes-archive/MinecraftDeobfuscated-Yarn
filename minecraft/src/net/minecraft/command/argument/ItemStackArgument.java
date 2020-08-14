package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class ItemStackArgument implements Predicate<ItemStack> {
	private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("arguments.item.overstacked", object, object2)
	);
	private final Item item;
	@Nullable
	private final CompoundTag tag;

	public ItemStackArgument(Item item, @Nullable CompoundTag tag) {
		this.item = item;
		this.tag = tag;
	}

	public Item getItem() {
		return this.item;
	}

	public boolean test(ItemStack itemStack) {
		return itemStack.getItem() == this.item && NbtHelper.matches(this.tag, itemStack.getTag(), true);
	}

	public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.item, amount);
		if (this.tag != null) {
			itemStack.setTag(this.tag);
		}

		if (checkOverstack && amount > itemStack.getMaxCount()) {
			throw OVERSTACKED_EXCEPTION.create(Registry.ITEM.getId(this.item), itemStack.getMaxCount());
		} else {
			return itemStack;
		}
	}

	public String asString() {
		StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.item));
		if (this.tag != null) {
			stringBuilder.append(this.tag);
		}

		return stringBuilder.toString();
	}
}
