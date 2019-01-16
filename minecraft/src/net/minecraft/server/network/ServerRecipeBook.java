package net.minecraft.server.network;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.UnlockRecipesClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

	public int unlockRecipes(Collection<Recipe<?>> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		int i = 0;

		for (Recipe<?> recipe : collection) {
			Identifier identifier = recipe.getId();
			if (!this.recipes.contains(identifier) && !recipe.isIgnoredInRecipeBook()) {
				this.add(identifier);
				this.display(identifier);
				list.add(identifier);
				Criterions.RECIPE_UNLOCKED.handle(serverPlayerEntity, recipe);
				i++;
			}
		}

		this.sendUnlockRecipesPacket(UnlockRecipesClientPacket.Action.field_12415, serverPlayerEntity, list);
		return i;
	}

	public int lockRecipes(Collection<Recipe<?>> collection, ServerPlayerEntity serverPlayerEntity) {
		List<Identifier> list = Lists.<Identifier>newArrayList();
		int i = 0;

		for (Recipe<?> recipe : collection) {
			Identifier identifier = recipe.getId();
			if (this.recipes.contains(identifier)) {
				this.remove(identifier);
				list.add(identifier);
				i++;
			}
		}

		this.sendUnlockRecipesPacket(UnlockRecipesClientPacket.Action.field_12417, serverPlayerEntity, list);
		return i;
	}

	private void sendUnlockRecipesPacket(UnlockRecipesClientPacket.Action action, ServerPlayerEntity serverPlayerEntity, List<Identifier> list) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new UnlockRecipesClientPacket(
					action, list, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable
				)
			);
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putBoolean("isGuiOpen", this.guiOpen);
		compoundTag.putBoolean("isFilteringCraftable", this.filteringCraftable);
		compoundTag.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
		compoundTag.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
		ListTag listTag = new ListTag();

		for (Identifier identifier : this.recipes) {
			listTag.add(new StringTag(identifier.toString()));
		}

		compoundTag.put("recipes", listTag);
		ListTag listTag2 = new ListTag();

		for (Identifier identifier2 : this.toBeDisplayed) {
			listTag2.add(new StringTag(identifier2.toString()));
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
			Optional<? extends Recipe<?>> optional = this.manager.get(identifier);
			if (!optional.isPresent()) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier);
			} else {
				this.add((Recipe<?>)optional.get());
			}
		}

		ListTag listTag2 = compoundTag.getList("toBeDisplayed", 8);

		for (int j = 0; j < listTag2.size(); j++) {
			Identifier identifier2 = new Identifier(listTag2.getString(j));
			Optional<? extends Recipe<?>> optional2 = this.manager.get(identifier2);
			if (!optional2.isPresent()) {
				LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier2);
			} else {
				this.display((Recipe<?>)optional2.get());
			}
		}
	}

	public void sendInitRecipesPacket(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new UnlockRecipesClientPacket(
					UnlockRecipesClientPacket.Action.field_12416,
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
