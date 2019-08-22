/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.TagHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class ItemStackArgument
implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("arguments.item.overstacked", object, object2));
    private final Item item;
    @Nullable
    private final CompoundTag tag;

    public ItemStackArgument(Item item, @Nullable CompoundTag compoundTag) {
        this.item = item;
        this.tag = compoundTag;
    }

    public Item getItem() {
        return this.item;
    }

    public boolean method_9783(ItemStack itemStack) {
        return itemStack.getItem() == this.item && TagHelper.areTagsEqual(this.tag, itemStack.getTag(), true);
    }

    public ItemStack createStack(int i, boolean bl) throws CommandSyntaxException {
        ItemStack itemStack = new ItemStack(this.item, i);
        if (this.tag != null) {
            itemStack.setTag(this.tag);
        }
        if (bl && i > itemStack.getMaxCount()) {
            throw OVERSTACKED_EXCEPTION.create(Registry.ITEM.getId(this.item), itemStack.getMaxCount());
        }
        return itemStack;
    }

    public String method_9782() {
        StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.item));
        if (this.tag != null) {
            stringBuilder.append(this.tag);
        }
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.method_9783((ItemStack)object);
    }
}

