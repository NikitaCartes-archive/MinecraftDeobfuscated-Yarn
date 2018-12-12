package net.minecraft.recipe;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.recipe.crafting.ArmorDyeRecipe;
import net.minecraft.recipe.crafting.BannerDuplicateRecipe;
import net.minecraft.recipe.crafting.BookCloningRecipe;
import net.minecraft.recipe.crafting.FireworkRocketRecipe;
import net.minecraft.recipe.crafting.FireworkStarFadeRecipe;
import net.minecraft.recipe.crafting.FireworkStarRecipe;
import net.minecraft.recipe.crafting.MapCloningRecipe;
import net.minecraft.recipe.crafting.MapExtendRecipe;
import net.minecraft.recipe.crafting.ShapedRecipe;
import net.minecraft.recipe.crafting.ShapelessRecipe;
import net.minecraft.recipe.crafting.ShieldDecorationRecipe;
import net.minecraft.recipe.crafting.ShulkerBoxColoringRecipe;
import net.minecraft.recipe.crafting.SuspiciousStewRecipe;
import net.minecraft.recipe.crafting.TippedArrowRecipe;
import net.minecraft.recipe.smelting.BlastingRecipe;
import net.minecraft.recipe.smelting.SmeltingRecipe;
import net.minecraft.recipe.smelting.SmokingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class RecipeSerializers {
	private static final Map<String, RecipeSerializer<?>> serializers = Maps.<String, RecipeSerializer<?>>newHashMap();
	public static final RecipeSerializer<ShapedRecipe> SHAPED = register(new ShapedRecipe.Serializer());
	public static final RecipeSerializer<ShapelessRecipe> SHAPELESS = register(new ShapelessRecipe.Serializer());
	public static final RecipeSerializers.Dummy<ArmorDyeRecipe> ARMOR_DYE = register(
		new RecipeSerializers.Dummy<>("crafting_special_armordye", ArmorDyeRecipe::new)
	);
	public static final RecipeSerializers.Dummy<BookCloningRecipe> BOOK_CLONING = register(
		new RecipeSerializers.Dummy<>("crafting_special_bookcloning", BookCloningRecipe::new)
	);
	public static final RecipeSerializers.Dummy<MapCloningRecipe> MAP_CLONING = register(
		new RecipeSerializers.Dummy<>("crafting_special_mapcloning", MapCloningRecipe::new)
	);
	public static final RecipeSerializers.Dummy<MapExtendRecipe> MAP_EXTEND = register(
		new RecipeSerializers.Dummy<>("crafting_special_mapextending", MapExtendRecipe::new)
	);
	public static final RecipeSerializers.Dummy<FireworkRocketRecipe> FIREWORK_ROCKET = register(
		new RecipeSerializers.Dummy<>("crafting_special_firework_rocket", FireworkRocketRecipe::new)
	);
	public static final RecipeSerializers.Dummy<FireworkStarRecipe> FIREWORK_STAR = register(
		new RecipeSerializers.Dummy<>("crafting_special_firework_star", FireworkStarRecipe::new)
	);
	public static final RecipeSerializers.Dummy<FireworkStarFadeRecipe> FIREWORK_STAR_FADE = register(
		new RecipeSerializers.Dummy<>("crafting_special_firework_star_fade", FireworkStarFadeRecipe::new)
	);
	public static final RecipeSerializers.Dummy<TippedArrowRecipe> TIPPED_ARROW = register(
		new RecipeSerializers.Dummy<>("crafting_special_tippedarrow", TippedArrowRecipe::new)
	);
	public static final RecipeSerializers.Dummy<BannerDuplicateRecipe> BANNER_DUPLICATE = register(
		new RecipeSerializers.Dummy<>("crafting_special_bannerduplicate", BannerDuplicateRecipe::new)
	);
	public static final RecipeSerializers.Dummy<ShieldDecorationRecipe> SHIELD_DECORATION = register(
		new RecipeSerializers.Dummy<>("crafting_special_shielddecoration", ShieldDecorationRecipe::new)
	);
	public static final RecipeSerializers.Dummy<ShulkerBoxColoringRecipe> SHULKER_BOX = register(
		new RecipeSerializers.Dummy<>("crafting_special_shulkerboxcoloring", ShulkerBoxColoringRecipe::new)
	);
	public static final RecipeSerializers.Dummy<SuspiciousStewRecipe> SUSPICIOUS_STEW = register(
		new RecipeSerializers.Dummy<>("crafting_special_suspiciousstew", SuspiciousStewRecipe::new)
	);
	public static final RecipeSerializer<SmeltingRecipe> SMELTING = register(new SmeltingRecipe.Serializer());
	public static final RecipeSerializer<BlastingRecipe> field_17084 = register(new BlastingRecipe.class_3860());
	public static final RecipeSerializer<SmokingRecipe> field_17085 = register(new SmokingRecipe.class_3863());

	public static <S extends RecipeSerializer<T>, T extends Recipe> S register(S recipeSerializer) {
		if (serializers.containsKey(recipeSerializer.getId())) {
			throw new IllegalArgumentException("Duplicate recipe serializer " + recipeSerializer.getId());
		} else {
			serializers.put(recipeSerializer.getId(), recipeSerializer);
			return recipeSerializer;
		}
	}

	public static Recipe fromJson(Identifier identifier, JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "type");
		RecipeSerializer<?> recipeSerializer = (RecipeSerializer<?>)serializers.get(string);
		if (recipeSerializer == null) {
			throw new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'");
		} else {
			return recipeSerializer.read(identifier, jsonObject);
		}
	}

	public static Recipe fromPacket(PacketByteBuf packetByteBuf) {
		Identifier identifier = packetByteBuf.readIdentifier();
		String string = packetByteBuf.readString(32767);
		RecipeSerializer<?> recipeSerializer = (RecipeSerializer<?>)serializers.get(string);
		if (recipeSerializer == null) {
			throw new IllegalArgumentException("Unknown recipe serializer " + string);
		} else {
			return recipeSerializer.read(identifier, packetByteBuf);
		}
	}

	public static <T extends Recipe> void toPacket(T recipe, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeIdentifier(recipe.getId());
		packetByteBuf.writeString(recipe.getSerializer().getId());
		RecipeSerializer<T> recipeSerializer = (RecipeSerializer<T>)recipe.getSerializer();
		recipeSerializer.write(packetByteBuf, recipe);
	}

	public static final class Dummy<T extends Recipe> implements RecipeSerializer<T> {
		private final String id;
		private final Function<Identifier, T> supplier;

		public Dummy(String string, Function<Identifier, T> function) {
			this.id = string;
			this.supplier = function;
		}

		@Override
		public T read(Identifier identifier, JsonObject jsonObject) {
			return (T)this.supplier.apply(identifier);
		}

		@Override
		public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
			return (T)this.supplier.apply(identifier);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf, T recipe) {
		}

		@Override
		public String getId() {
			return this.id;
		}
	}
}
