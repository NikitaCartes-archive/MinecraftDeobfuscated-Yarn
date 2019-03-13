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
import net.minecraft.nbt.Tag;
import net.minecraft.sortme.JsonLikeTagParser;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;

public class NbtPredicate {
	public static final NbtPredicate ANY = new NbtPredicate(null);
	@Nullable
	private final CompoundTag field_9715;

	public NbtPredicate(@Nullable CompoundTag compoundTag) {
		this.field_9715 = compoundTag;
	}

	public boolean test(ItemStack itemStack) {
		return this == ANY ? true : this.method_9077(itemStack.method_7969());
	}

	public boolean test(Entity entity) {
		return this == ANY ? true : this.method_9077(method_9076(entity));
	}

	public boolean method_9077(@Nullable Tag tag) {
		return tag == null ? this == ANY : this.field_9715 == null || TagHelper.method_10687(this.field_9715, tag, true);
	}

	public JsonElement serialize() {
		return (JsonElement)(this != ANY && this.field_9715 != null ? new JsonPrimitive(this.field_9715.toString()) : JsonNull.INSTANCE);
	}

	public static NbtPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			CompoundTag compoundTag;
			try {
				compoundTag = JsonLikeTagParser.parse(JsonHelper.asString(jsonElement, "nbt"));
			} catch (CommandSyntaxException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new NbtPredicate(compoundTag);
		} else {
			return ANY;
		}
	}

	public static CompoundTag method_9076(Entity entity) {
		CompoundTag compoundTag = entity.method_5647(new CompoundTag());
		if (entity instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)entity).inventory.method_7391();
			if (!itemStack.isEmpty()) {
				compoundTag.method_10566("SelectedItem", itemStack.method_7953(new CompoundTag()));
			}
		}

		return compoundTag;
	}
}
