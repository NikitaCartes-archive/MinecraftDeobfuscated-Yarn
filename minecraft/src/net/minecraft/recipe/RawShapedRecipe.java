package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

public record RawShapedRecipe(int width, int height, DefaultedList<Ingredient> ingredients, Optional<RawShapedRecipe.Data> data) {
	private static final int MAX_WIDTH_AND_HEIGHT = 3;
	public static final MapCodec<RawShapedRecipe> CODEC = RawShapedRecipe.Data.CODEC
		.flatXmap(
			RawShapedRecipe::fromData,
			recipe -> (DataResult)recipe.data().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
		);
	public static final PacketCodec<RegistryByteBuf, RawShapedRecipe> PACKET_CODEC = PacketCodec.of(RawShapedRecipe::writeToBuf, RawShapedRecipe::readFromBuf);

	public static RawShapedRecipe create(Map<Character, Ingredient> key, String... pattern) {
		return create(key, List.of(pattern));
	}

	public static RawShapedRecipe create(Map<Character, Ingredient> key, List<String> pattern) {
		RawShapedRecipe.Data data = new RawShapedRecipe.Data(key, pattern);
		return Util.getResult(fromData(data), IllegalArgumentException::new);
	}

	private static DataResult<RawShapedRecipe> fromData(RawShapedRecipe.Data data) {
		String[] strings = removePadding(data.pattern);
		int i = strings[0].length();
		int j = strings.length;
		DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
		CharSet charSet = new CharArraySet(data.key.keySet());

		for (int k = 0; k < strings.length; k++) {
			String string = strings[k];

			for (int l = 0; l < string.length(); l++) {
				char c = string.charAt(l);
				Ingredient ingredient = c == ' ' ? Ingredient.EMPTY : (Ingredient)data.key.get(c);
				if (ingredient == null) {
					return DataResult.error(() -> "Pattern references symbol '" + c + "' but it's not defined in the key");
				}

				charSet.remove(c);
				defaultedList.set(l + i * k, ingredient);
			}
		}

		return !charSet.isEmpty()
			? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + charSet)
			: DataResult.success(new RawShapedRecipe(i, j, defaultedList, Optional.of(data)));
	}

	/**
	 * Removes empty space from around the recipe pattern.
	 * 
	 * <p>Turns patterns such as:
	 * <pre>
	 * {@code
	 * "   o"
	 * "   a"
	 * "    "
	 * }
	 * </pre>
	 * Into:
	 * <pre>
	 * {@code
	 * "o"
	 * "a"
	 * }
	 * </pre>
	 * 
	 * @return a new recipe pattern with all leading and trailing empty rows/columns removed
	 */
	@VisibleForTesting
	static String[] removePadding(List<String> pattern) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int m = 0; m < pattern.size(); m++) {
			String string = (String)pattern.get(m);
			i = Math.min(i, findFirstSymbol(string));
			int n = findLastSymbol(string);
			j = Math.max(j, n);
			if (n < 0) {
				if (k == m) {
					k++;
				}

				l++;
			} else {
				l = 0;
			}
		}

		if (pattern.size() == l) {
			return new String[0];
		} else {
			String[] strings = new String[pattern.size() - l - k];

			for (int o = 0; o < strings.length; o++) {
				strings[o] = ((String)pattern.get(o + k)).substring(i, j + 1);
			}

			return strings;
		}
	}

	private static int findFirstSymbol(String line) {
		int i = 0;

		while (i < line.length() && line.charAt(i) == ' ') {
			i++;
		}

		return i;
	}

	private static int findLastSymbol(String line) {
		int i = line.length() - 1;

		while (i >= 0 && line.charAt(i) == ' ') {
			i--;
		}

		return i;
	}

	public boolean matches(RecipeInputInventory inventory) {
		for (int i = 0; i <= inventory.getWidth() - this.width; i++) {
			for (int j = 0; j <= inventory.getHeight() - this.height; j++) {
				if (this.matches(inventory, i, j, true)) {
					return true;
				}

				if (this.matches(inventory, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean matches(RecipeInputInventory inventory, int offsetX, int offsetY, boolean flipped) {
		for (int i = 0; i < inventory.getWidth(); i++) {
			for (int j = 0; j < inventory.getHeight(); j++) {
				int k = i - offsetX;
				int l = j - offsetY;
				Ingredient ingredient = Ingredient.EMPTY;
				if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
					if (flipped) {
						ingredient = this.ingredients.get(this.width - k - 1 + l * this.width);
					} else {
						ingredient = this.ingredients.get(k + l * this.width);
					}
				}

				if (!ingredient.test(inventory.getStack(i + j * inventory.getWidth()))) {
					return false;
				}
			}
		}

		return true;
	}

	private void writeToBuf(RegistryByteBuf buf) {
		buf.writeVarInt(this.width);
		buf.writeVarInt(this.height);

		for (Ingredient ingredient : this.ingredients) {
			Ingredient.PACKET_CODEC.encode(buf, ingredient);
		}
	}

	private static RawShapedRecipe readFromBuf(RegistryByteBuf buf) {
		int i = buf.readVarInt();
		int j = buf.readVarInt();
		DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.EMPTY);
		defaultedList.replaceAll(ingredient -> Ingredient.PACKET_CODEC.decode(buf));
		return new RawShapedRecipe(i, j, defaultedList, Optional.empty());
	}

	public static record Data(Map<Character, Ingredient> key, List<String> pattern) {
		private static final Codec<List<String>> PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap(pattern -> {
			if (pattern.size() > 3) {
				return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
			} else if (pattern.isEmpty()) {
				return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
			} else {
				int i = ((String)pattern.get(0)).length();

				for (String string : pattern) {
					if (string.length() > 3) {
						return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
					}

					if (i != string.length()) {
						return DataResult.error(() -> "Invalid pattern: each row must be the same width");
					}
				}

				return DataResult.success(pattern);
			}
		}, Function.identity());
		private static final Codec<Character> KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap(keyEntry -> {
			if (keyEntry.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).");
			} else {
				return " ".equals(keyEntry) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(keyEntry.charAt(0));
			}
		}, String::valueOf);
		public static final MapCodec<RawShapedRecipe.Data> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Codecs.strictUnboundedMap(KEY_ENTRY_CODEC, Ingredient.DISALLOW_EMPTY_CODEC).fieldOf("key").forGetter(data -> data.key),
						PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern)
					)
					.apply(instance, RawShapedRecipe.Data::new)
		);
	}
}
