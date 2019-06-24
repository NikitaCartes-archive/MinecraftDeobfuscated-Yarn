package net.minecraft.server.network;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.client.network.packet.UnlockRecipesS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
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

		this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, serverPlayerEntity, list);
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

		this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.REMOVE, serverPlayerEntity, list);
		return i;
	}

	private void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity serverPlayerEntity, List<Identifier> list) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new UnlockRecipesS2CPacket(
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
		this.method_20732(listTag, this::add);
		ListTag listTag2 = compoundTag.getList("toBeDisplayed", 8);
		this.method_20732(listTag2, this::display);
	}

	private void method_20732(ListTag listTag, Consumer<Recipe<?>> consumer) {
		for (int i = 0; i < listTag.size(); i++) {
			String string = listTag.getString(i);

			try {
				Identifier identifier = new Identifier(string);
				Optional<? extends Recipe<?>> optional = this.manager.get(identifier);
				if (!optional.isPresent()) {
					LOGGER.error("Tried to load unrecognized recipe: {} removed now.", identifier);
				} else {
					consumer.accept(optional.get());
				}
			} catch (InvalidIdentifierException var7) {
				LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", string);
			}
		}
	}

	public void sendInitRecipesPacket(ServerPlayerEntity serverPlayerEntity) {
		serverPlayerEntity.networkHandler
			.sendPacket(
				new UnlockRecipesS2CPacket(
					UnlockRecipesS2CPacket.Action.INIT,
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
