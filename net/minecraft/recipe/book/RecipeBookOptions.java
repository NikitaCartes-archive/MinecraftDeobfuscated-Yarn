/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.recipe.book;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.util.Util;

public final class RecipeBookOptions {
    private static final Map<RecipeBookCategory, Pair<String, String>> CATEGORY_OPTION_NAMES = ImmutableMap.of(RecipeBookCategory.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookCategory.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookCategory.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookCategory.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
    private final Map<RecipeBookCategory, CategoryOption> categoryOptions;

    private RecipeBookOptions(Map<RecipeBookCategory, CategoryOption> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public RecipeBookOptions() {
        this(Util.make(Maps.newEnumMap(RecipeBookCategory.class), enumMap -> {
            for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
                enumMap.put(recipeBookCategory, new CategoryOption(false, false));
            }
        }));
    }

    public boolean isGuiOpen(RecipeBookCategory category) {
        return this.categoryOptions.get((Object)((Object)category)).guiOpen;
    }

    public void setGuiOpen(RecipeBookCategory category, boolean open) {
        this.categoryOptions.get((Object)((Object)category)).guiOpen = open;
    }

    public boolean isFilteringCraftable(RecipeBookCategory category) {
        return this.categoryOptions.get((Object)((Object)category)).filteringCraftable;
    }

    public void setFilteringCraftable(RecipeBookCategory category, boolean filtering) {
        this.categoryOptions.get((Object)((Object)category)).filteringCraftable = filtering;
    }

    public static RecipeBookOptions fromPacket(PacketByteBuf buf) {
        EnumMap<RecipeBookCategory, CategoryOption> map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            boolean bl = buf.readBoolean();
            boolean bl2 = buf.readBoolean();
            map.put(recipeBookCategory, new CategoryOption(bl, bl2));
        }
        return new RecipeBookOptions(map);
    }

    public void toPacket(PacketByteBuf buf) {
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = this.categoryOptions.get((Object)recipeBookCategory);
            if (categoryOption == null) {
                buf.writeBoolean(false);
                buf.writeBoolean(false);
                continue;
            }
            buf.writeBoolean(categoryOption.guiOpen);
            buf.writeBoolean(categoryOption.filteringCraftable);
        }
    }

    public static RecipeBookOptions fromNbt(NbtCompound nbt) {
        EnumMap<RecipeBookCategory, CategoryOption> map = Maps.newEnumMap(RecipeBookCategory.class);
        CATEGORY_OPTION_NAMES.forEach((category, pair) -> {
            boolean bl = nbt.getBoolean((String)pair.getFirst());
            boolean bl2 = nbt.getBoolean((String)pair.getSecond());
            map.put((RecipeBookCategory)((Object)category), new CategoryOption(bl, bl2));
        });
        return new RecipeBookOptions(map);
    }

    public void writeNbt(NbtCompound nbt) {
        CATEGORY_OPTION_NAMES.forEach((category, pair) -> {
            CategoryOption categoryOption = this.categoryOptions.get(category);
            nbt.putBoolean((String)pair.getFirst(), categoryOption.guiOpen);
            nbt.putBoolean((String)pair.getSecond(), categoryOption.filteringCraftable);
        });
    }

    public RecipeBookOptions copy() {
        EnumMap<RecipeBookCategory, CategoryOption> map = Maps.newEnumMap(RecipeBookCategory.class);
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = this.categoryOptions.get((Object)recipeBookCategory);
            map.put(recipeBookCategory, categoryOption.copy());
        }
        return new RecipeBookOptions(map);
    }

    public void copyFrom(RecipeBookOptions other) {
        this.categoryOptions.clear();
        for (RecipeBookCategory recipeBookCategory : RecipeBookCategory.values()) {
            CategoryOption categoryOption = other.categoryOptions.get((Object)recipeBookCategory);
            this.categoryOptions.put(recipeBookCategory, categoryOption.copy());
        }
    }

    public boolean equals(Object o) {
        return this == o || o instanceof RecipeBookOptions && this.categoryOptions.equals(((RecipeBookOptions)o).categoryOptions);
    }

    public int hashCode() {
        return this.categoryOptions.hashCode();
    }

    static final class CategoryOption {
        boolean guiOpen;
        boolean filteringCraftable;

        public CategoryOption(boolean guiOpen, boolean filteringCraftable) {
            this.guiOpen = guiOpen;
            this.filteringCraftable = filteringCraftable;
        }

        public CategoryOption copy() {
            return new CategoryOption(this.guiOpen, this.filteringCraftable);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof CategoryOption) {
                CategoryOption categoryOption = (CategoryOption)o;
                return this.guiOpen == categoryOption.guiOpen && this.filteringCraftable == categoryOption.filteringCraftable;
            }
            return false;
        }

        public int hashCode() {
            int i = this.guiOpen ? 1 : 0;
            i = 31 * i + (this.filteringCraftable ? 1 : 0);
            return i;
        }

        public String toString() {
            return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + "]";
        }
    }
}

