package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;

public class NbtPredicate {
	public static final NbtPredicate ANY = new NbtPredicate(null);
	@Nullable
	private final CompoundTag tag;

	public NbtPredicate(@Nullable CompoundTag compoundTag) {
		this.tag = compoundTag;
	}

	public boolean test(ItemStack itemStack) {
		return this == ANY ? true : this.test(itemStack.getTag());
	}

	public boolean test(Entity entity) {
		return this == ANY ? true : this.test(entityToTag(entity));
	}

	public boolean test(@Nullable Tag tag) {
		return tag == null ? this == ANY : this.tag == null || TagHelper.areTagsEqual(this.tag, tag, true);
	}

	public JsonElement serialize() {
		return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
	}

	public static NbtPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			CompoundTag compoundTag;
			try {
				compoundTag = StringNbtReader.parse(JsonHelper.asString(jsonElement, "nbt"));
			} catch (CommandSyntaxException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new NbtPredicate(compoundTag);
		} else {
			return ANY;
		}
	}

	public static CompoundTag entityToTag(Entity entity) {
		CompoundTag compoundTag = entity.toTag(new CompoundTag());
		if (entity instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)entity).inventory.getMainHandStack();
			if (!itemStack.isEmpty()) {
				compoundTag.put("SelectedItem", itemStack.toTag(new CompoundTag()));
			}
		}

		return compoundTag;
	}
}
