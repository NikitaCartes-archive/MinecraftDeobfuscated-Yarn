package net.minecraft.server.network;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_2713;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RecipeManager manager;

	public ServerRecipeBook(RecipeManager recipeManager) {
		this.manager = recipeManager;
	}

	public int method_14903(Collection<Recipe> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		int i = 0;

		for (Recipe recipe : collection) {
			Identifier identifier = recipe.getId();
			if (!this.recipes.contains(identifier) && !recipe.isIgnoredInRecipeBook()) {
				this.method_14881(identifier);
				this.method_14877(identifier);
				list.add(identifier);
				CriterionCriterions.RECIPE_UNLOCKED.handle(serverPlayerEntity, recipe);
				i++;
			}
		}

		this.method_14899(class_2713.class_2714.field_12415, serverPlayerEntity, list);
		return i;
	}

	public int method_14900(Collection<Recipe> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		int i = 0;

		for (Recipe recipe : collection) {
			Identifier identifier = recipe.getId();
			if (this.recipes.contains(identifier)) {
				this.method_14879(identifier);
				list.add(identifier);
				i++;
			}
		}

		this.method_14899(class_2713.class_2714.field_12417, serverPlayerEntity, list);
		return i;
	}

	private void method_14899(class_2713.class_2714 arg, ServerPlayerEntity serverPlayerEntity, List<Identifier> list) {
		serverPlayerEntity.networkHandler
			.sendPacket(new class_2713(arg, list, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putBoolean("isGuiOpen", this.guiOpen);
		compoundTag.putBoolean("isFilteringCraftable", this.filteringCraftable);
		compoundTag.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
		compoundTag.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
		ListTag listTag = new ListTag();

		for (Identifier identifier : this.recipes) {
			listTag.add((Tag)(new StringTag(identifier.toString())));
		}

		compoundTag.put("recipes", listTag);
		ListTag listTag2 = new ListTag();

		for (Identifier identifier2 : this.toBeDisplayed) {
			listTag2.add((Tag)(new StringTag(identifier2.toString())));
		}

		compoundTag.put("toBeDisplayed", listTag2);
		return compoundTag;
	}

	public void fromTag(CompoundTag compoundTag) {
		this.guiOpen = compoundTag.getBoolean("isGuiOpen");
		this.filteringCraftable = compoundTag.getBoolean("isFilteringCraftable");
		this.furnaceGuiOpen = compoundTag.getBoolean("isFurnaceGuiOpen");
		this.furnaceFilteringCraftable = compoundTag.getBoolean("isFurnaceFilteringCraftable");
		ListTag listTag = compoundTag.getList("recipes", 8);

		for (int i = 0; i < listTag.size(); i++) {
			Identifier identifier = new Identifier(listTag.getString(i));
			Recipe recipe = this.manager.get(identifier);
			if (recipe == null) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier);
			} else {
				this.method_14876(recipe);
			}
		}

		ListTag listTag2 = compoundTag.getList("toBeDisplayed", 8);

		for (int j = 0; j < listTag2.size(); j++) {
			Identifier identifier2 = new Identifier(listTag2.getString(j));
			Recipe recipe2 = this.manager.get(identifier2);
			if (recipe2 == null) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier2);
			} else {
				this.method_14885(recipe2);
			}
		}
	}

	public void method_14904(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new class_2713(
					class_2713.class_2714.field_12416,
					this.recipes,
					this.toBeDisplayed,
					this.guiOpen,
					this.filteringCraftable,
					this.furnaceGuiOpen,
					this.furnaceFilteringCraftable
				)
			);
	}
}
