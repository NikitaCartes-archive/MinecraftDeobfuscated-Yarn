package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public abstract class class_3972 implements Recipe<Inventory> {
	protected final Ingredient input;
	protected final ItemStack output;
	private final RecipeType<?> type;
	private final RecipeSerializer<?> serializer;
	protected final Identifier id;
	protected final String group;

	public class_3972(
		RecipeType<?> recipeType, RecipeSerializer<?> recipeSerializer, Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack
	) {
		this.type = recipeType;
		this.serializer = recipeSerializer;
		this.id = identifier;
		this.group = string;
		this.input = ingredient;
		this.output = itemStack;
	}

	@Override
	public RecipeType<?> getType() {
		return this.type;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return this.serializer;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.create();
		defaultedList.add(this.input);
		return defaultedList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean fits(int i, int j) {
		return true;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return this.output.copy();
	}

	public static class class_3973<T extends class_3972> implements RecipeSerializer<T> {
		final class_3972.class_3973.class_3974<T> field_17648;

		public class_3973(class_3972.class_3973.class_3974<T> arg) {
			this.field_17648 = arg;
		}

		public T method_17881(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient ingredient;
			if (JsonHelper.hasArray(jsonObject, "ingredient")) {
				ingredient = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
			} else {
				ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			}

			String string2 = JsonHelper.getString(jsonObject, "result");
			int i = JsonHelper.getInt(jsonObject, "count");
			ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(string2)), i);
			return this.field_17648.create(identifier, string, ingredient, itemStack);
		}

		public T method_17882(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			return this.field_17648.create(identifier, string, ingredient, itemStack);
		}

		public void method_17880(PacketByteBuf packetByteBuf, T arg) {
			packetByteBuf.writeString(arg.group);
			arg.input.write(packetByteBuf);
			packetByteBuf.writeItemStack(arg.output);
		}

		interface class_3974<T extends class_3972> {
			T create(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack);
		}
	}
}
