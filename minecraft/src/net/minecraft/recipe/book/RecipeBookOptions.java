package net.minecraft.recipe.book;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class RecipeBookOptions {
	public static final PacketCodec<PacketByteBuf, RecipeBookOptions> PACKET_CODEC = PacketCodec.of(RecipeBookOptions::toPacket, RecipeBookOptions::fromPacket);
	private static final Map<RecipeBookType, Pair<String, String>> CATEGORY_OPTION_NAMES = ImmutableMap.of(
		RecipeBookType.CRAFTING,
		Pair.of("isGuiOpen", "isFilteringCraftable"),
		RecipeBookType.FURNACE,
		Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"),
		RecipeBookType.BLAST_FURNACE,
		Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"),
		RecipeBookType.SMOKER,
		Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable")
	);
	private final Map<RecipeBookType, RecipeBookOptions.CategoryOption> categoryOptions;

	private RecipeBookOptions(Map<RecipeBookType, RecipeBookOptions.CategoryOption> categoryOptions) {
		this.categoryOptions = categoryOptions;
	}

	public RecipeBookOptions() {
		this(new EnumMap(RecipeBookType.class));
	}

	private RecipeBookOptions.CategoryOption getOption(RecipeBookType category) {
		return (RecipeBookOptions.CategoryOption)this.categoryOptions.getOrDefault(category, RecipeBookOptions.CategoryOption.DEFAULT);
	}

	private void apply(RecipeBookType category, UnaryOperator<RecipeBookOptions.CategoryOption> modifier) {
		this.categoryOptions.compute(category, (key, value) -> {
			if (value == null) {
				value = RecipeBookOptions.CategoryOption.DEFAULT;
			}

			value = (RecipeBookOptions.CategoryOption)modifier.apply(value);
			if (value.equals(RecipeBookOptions.CategoryOption.DEFAULT)) {
				value = null;
			}

			return value;
		});
	}

	public boolean isGuiOpen(RecipeBookType category) {
		return this.getOption(category).guiOpen;
	}

	public void setGuiOpen(RecipeBookType category, boolean open) {
		this.apply(category, option -> option.withGuiOpen(open));
	}

	public boolean isFilteringCraftable(RecipeBookType category) {
		return this.getOption(category).filteringCraftable;
	}

	public void setFilteringCraftable(RecipeBookType category, boolean filtering) {
		this.apply(category, option -> option.withFilteringCraftable(filtering));
	}

	private static RecipeBookOptions fromPacket(PacketByteBuf buf) {
		Map<RecipeBookType, RecipeBookOptions.CategoryOption> map = new EnumMap(RecipeBookType.class);

		for (RecipeBookType recipeBookType : RecipeBookType.values()) {
			boolean bl = buf.readBoolean();
			boolean bl2 = buf.readBoolean();
			if (bl || bl2) {
				map.put(recipeBookType, new RecipeBookOptions.CategoryOption(bl, bl2));
			}
		}

		return new RecipeBookOptions(map);
	}

	private void toPacket(PacketByteBuf buf) {
		for (RecipeBookType recipeBookType : RecipeBookType.values()) {
			RecipeBookOptions.CategoryOption categoryOption = (RecipeBookOptions.CategoryOption)this.categoryOptions
				.getOrDefault(recipeBookType, RecipeBookOptions.CategoryOption.DEFAULT);
			buf.writeBoolean(categoryOption.guiOpen);
			buf.writeBoolean(categoryOption.filteringCraftable);
		}
	}

	public static RecipeBookOptions fromNbt(NbtCompound nbt) {
		Map<RecipeBookType, RecipeBookOptions.CategoryOption> map = new EnumMap(RecipeBookType.class);
		CATEGORY_OPTION_NAMES.forEach((category, pair) -> {
			boolean bl = nbt.getBoolean((String)pair.getFirst());
			boolean bl2 = nbt.getBoolean((String)pair.getSecond());
			if (bl || bl2) {
				map.put(category, new RecipeBookOptions.CategoryOption(bl, bl2));
			}
		});
		return new RecipeBookOptions(map);
	}

	public void writeNbt(NbtCompound nbt) {
		CATEGORY_OPTION_NAMES.forEach(
			(category, pair) -> {
				RecipeBookOptions.CategoryOption categoryOption = (RecipeBookOptions.CategoryOption)this.categoryOptions
					.getOrDefault(category, RecipeBookOptions.CategoryOption.DEFAULT);
				nbt.putBoolean((String)pair.getFirst(), categoryOption.guiOpen);
				nbt.putBoolean((String)pair.getSecond(), categoryOption.filteringCraftable);
			}
		);
	}

	public RecipeBookOptions copy() {
		return new RecipeBookOptions(new EnumMap(this.categoryOptions));
	}

	public void copyFrom(RecipeBookOptions other) {
		this.categoryOptions.clear();
		this.categoryOptions.putAll(other.categoryOptions);
	}

	public boolean equals(Object o) {
		return this == o || o instanceof RecipeBookOptions && this.categoryOptions.equals(((RecipeBookOptions)o).categoryOptions);
	}

	public int hashCode() {
		return this.categoryOptions.hashCode();
	}

	static record CategoryOption(boolean guiOpen, boolean filteringCraftable) {
		public static final RecipeBookOptions.CategoryOption DEFAULT = new RecipeBookOptions.CategoryOption(false, false);

		public String toString() {
			return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + "]";
		}

		public RecipeBookOptions.CategoryOption withGuiOpen(boolean guiOpen) {
			return new RecipeBookOptions.CategoryOption(guiOpen, this.filteringCraftable);
		}

		public RecipeBookOptions.CategoryOption withFilteringCraftable(boolean filteringCraftable) {
			return new RecipeBookOptions.CategoryOption(this.guiOpen, filteringCraftable);
		}
	}
}
