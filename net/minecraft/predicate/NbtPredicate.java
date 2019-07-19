/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class NbtPredicate {
    public static final NbtPredicate ANY = new NbtPredicate(null);
    @Nullable
    private final CompoundTag tag;

    public NbtPredicate(@Nullable CompoundTag compoundTag) {
        this.tag = compoundTag;
    }

    public boolean test(ItemStack itemStack) {
        if (this == ANY) {
            return true;
        }
        return this.test(itemStack.getTag());
    }

    public boolean test(Entity entity) {
        if (this == ANY) {
            return true;
        }
        return this.test(NbtPredicate.entityToTag(entity));
    }

    public boolean test(@Nullable Tag tag) {
        if (tag == null) {
            return this == ANY;
        }
        return this.tag == null || NbtHelper.matches(this.tag, tag, true);
    }

    public JsonElement toJson() {
        if (this == ANY || this.tag == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(this.tag.toString());
    }

    public static NbtPredicate fromJson(@Nullable JsonElement jsonElement) {
        CompoundTag compoundTag;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        try {
            compoundTag = StringNbtReader.parse(JsonHelper.asString(jsonElement, "nbt"));
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
        return new NbtPredicate(compoundTag);
    }

    public static CompoundTag entityToTag(Entity entity) {
        ItemStack itemStack;
        CompoundTag compoundTag = entity.toTag(new CompoundTag());
        if (entity instanceof PlayerEntity && !(itemStack = ((PlayerEntity)entity).inventory.getMainHandStack()).isEmpty()) {
            compoundTag.put("SelectedItem", itemStack.toTag(new CompoundTag()));
        }
        return compoundTag;
    }
}

