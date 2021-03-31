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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;

public class NbtPredicate {
	public static final NbtPredicate ANY = new NbtPredicate(null);
	@Nullable
	private final NbtCompound nbt;

	public NbtPredicate(@Nullable NbtCompound nbt) {
		this.nbt = nbt;
	}

	public boolean test(ItemStack stack) {
		return this == ANY ? true : this.test(stack.getTag());
	}

	public boolean test(Entity entity) {
		return this == ANY ? true : this.test(entityToNbt(entity));
	}

	public boolean test(@Nullable NbtElement element) {
		return element == null ? this == ANY : this.nbt == null || NbtHelper.matches(this.nbt, element, true);
	}

	public JsonElement toJson() {
		return (JsonElement)(this != ANY && this.nbt != null ? new JsonPrimitive(this.nbt.toString()) : JsonNull.INSTANCE);
	}

	public static NbtPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			NbtCompound nbtCompound;
			try {
				nbtCompound = StringNbtReader.parse(JsonHelper.asString(json, "nbt"));
			} catch (CommandSyntaxException var3) {
				throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
			}

			return new NbtPredicate(nbtCompound);
		} else {
			return ANY;
		}
	}

	public static NbtCompound entityToNbt(Entity entity) {
		NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
		if (entity instanceof PlayerEntity) {
			ItemStack itemStack = ((PlayerEntity)entity).getInventory().getMainHandStack();
			if (!itemStack.isEmpty()) {
				nbtCompound.put("SelectedItem", itemStack.writeNbt(new NbtCompound()));
			}
		}

		return nbtCompound;
	}
}
