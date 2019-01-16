package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import net.minecraft.util.registry.Registry;

public class class_2290 implements Predicate<ItemStack> {
	private static final Dynamic2CommandExceptionType field_10797 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableTextComponent("arguments.item.overstacked", object, object2)
	);
	private final Item field_10796;
	@Nullable
	private final CompoundTag field_10798;

	public class_2290(Item item, @Nullable CompoundTag compoundTag) {
		this.field_10796 = item;
		this.field_10798 = compoundTag;
	}

	public Item method_9785() {
		return this.field_10796;
	}

	public boolean method_9783(ItemStack itemStack) {
		return itemStack.getItem() == this.field_10796 && TagHelper.areTagsEqual(this.field_10798, itemStack.getTag(), true);
	}

	public ItemStack method_9781(int i, boolean bl) throws CommandSyntaxException {
		ItemStack itemStack = new ItemStack(this.field_10796, i);
		if (this.field_10798 != null) {
			itemStack.setTag(this.field_10798);
		}

		if (bl && i > itemStack.getMaxAmount()) {
			throw field_10797.create(Registry.ITEM.getId(this.field_10796), itemStack.getMaxAmount());
		} else {
			return itemStack;
		}
	}

	public String method_9782() {
		StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.field_10796));
		if (this.field_10798 != null) {
			stringBuilder.append(this.field_10798);
		}

		return stringBuilder.toString();
	}
}
