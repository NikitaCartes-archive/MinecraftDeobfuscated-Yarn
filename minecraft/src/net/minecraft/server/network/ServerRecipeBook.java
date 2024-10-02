package net.minecraft.server.network;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
	public static final String RECIPE_BOOK_KEY = "recipeBook";
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ServerRecipeBook.DisplayCollector collector;
	@VisibleForTesting
	protected final Set<RegistryKey<Recipe<?>>> unlocked = Sets.newIdentityHashSet();
	/**
	 * Contains recipes that play an animation when first viewed on the recipe book.
	 * 
	 * <p>This is saved under {@code toBeDisplayed} key in the player NBT data.
	 */
	@VisibleForTesting
	protected final Set<RegistryKey<Recipe<?>>> highlighted = Sets.newIdentityHashSet();

	public ServerRecipeBook(ServerRecipeBook.DisplayCollector collector) {
		this.collector = collector;
	}

	public void unlock(RegistryKey<Recipe<?>> recipeKey) {
		this.unlocked.add(recipeKey);
	}

	public boolean isUnlocked(RegistryKey<Recipe<?>> recipeKey) {
		return this.unlocked.contains(recipeKey);
	}

	public void lock(RegistryKey<Recipe<?>> recipeKey) {
		this.unlocked.remove(recipeKey);
		this.highlighted.remove(recipeKey);
	}

	public void unmarkHighlighted(RegistryKey<Recipe<?>> recipeKey) {
		this.highlighted.remove(recipeKey);
	}

	private void markHighlighted(RegistryKey<Recipe<?>> recipeKey) {
		this.highlighted.add(recipeKey);
	}

	public int unlockRecipes(Collection<RecipeEntry<?>> recipes, ServerPlayerEntity player) {
		List<RecipeBookAddS2CPacket.Entry> list = new ArrayList();

		for (RecipeEntry<?> recipeEntry : recipes) {
			RegistryKey<Recipe<?>> registryKey = recipeEntry.id();
			if (!this.unlocked.contains(registryKey) && !recipeEntry.value().isIgnoredInRecipeBook()) {
				this.unlock(registryKey);
				this.markHighlighted(registryKey);
				this.collector.displaysForRecipe(registryKey, display -> list.add(new RecipeBookAddS2CPacket.Entry(display, recipeEntry.value().showNotification(), true)));
				Criteria.RECIPE_UNLOCKED.trigger(player, recipeEntry);
			}
		}

		if (!list.isEmpty()) {
			player.networkHandler.sendPacket(new RecipeBookAddS2CPacket(list));
		}

		return list.size();
	}

	public int lockRecipes(Collection<RecipeEntry<?>> recipes, ServerPlayerEntity player) {
		List<NetworkRecipeId> list = Lists.<NetworkRecipeId>newArrayList();

		for (RecipeEntry<?> recipeEntry : recipes) {
			RegistryKey<Recipe<?>> registryKey = recipeEntry.id();
			if (this.unlocked.contains(registryKey)) {
				this.lock(registryKey);
				this.collector.displaysForRecipe(registryKey, display -> list.add(display.id()));
			}
		}

		if (!list.isEmpty()) {
			player.networkHandler.sendPacket(new RecipeBookRemoveS2CPacket(list));
		}

		return list.size();
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		this.getOptions().writeNbt(nbtCompound);
		NbtList nbtList = new NbtList();

		for (RegistryKey<Recipe<?>> registryKey : this.unlocked) {
			nbtList.add(NbtString.of(registryKey.getValue().toString()));
		}

		nbtCompound.put("recipes", nbtList);
		NbtList nbtList2 = new NbtList();

		for (RegistryKey<Recipe<?>> registryKey2 : this.highlighted) {
			nbtList2.add(NbtString.of(registryKey2.getValue().toString()));
		}

		nbtCompound.put("toBeDisplayed", nbtList2);
		return nbtCompound;
	}

	public void readNbt(NbtCompound nbt, Predicate<RegistryKey<Recipe<?>>> validPredicate) {
		this.setOptions(RecipeBookOptions.fromNbt(nbt));
		NbtList nbtList = nbt.getList("recipes", NbtElement.STRING_TYPE);
		this.handleList(nbtList, this::unlock, validPredicate);
		NbtList nbtList2 = nbt.getList("toBeDisplayed", NbtElement.STRING_TYPE);
		this.handleList(nbtList2, this::markHighlighted, validPredicate);
	}

	private void handleList(NbtList list, Consumer<RegistryKey<Recipe<?>>> handler, Predicate<RegistryKey<Recipe<?>>> validPredicate) {
		for (int i = 0; i < list.size(); i++) {
			String string = list.getString(i);

			try {
				RegistryKey<Recipe<?>> registryKey = RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(string));
				if (!validPredicate.test(registryKey)) {
					LOGGER.error("Tried to load unrecognized recipe: {} removed now.", registryKey);
				} else {
					handler.accept(registryKey);
				}
			} catch (InvalidIdentifierException var7) {
				LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", string);
			}
		}
	}

	public void sendInitRecipesPacket(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new RecipeBookSettingsS2CPacket(this.getOptions()));
		List<RecipeBookAddS2CPacket.Entry> list = new ArrayList(this.unlocked.size());

		for (RegistryKey<Recipe<?>> registryKey : this.unlocked) {
			this.collector.displaysForRecipe(registryKey, display -> list.add(new RecipeBookAddS2CPacket.Entry(display, false, this.highlighted.contains(registryKey))));
		}

		player.networkHandler.sendPacket(new RecipeBookAddS2CPacket(list));
	}

	public void copyFrom(ServerRecipeBook recipeBook) {
		this.unlocked.clear();
		this.highlighted.clear();
		this.options.copyFrom(recipeBook.options);
		this.unlocked.addAll(recipeBook.unlocked);
		this.highlighted.addAll(recipeBook.highlighted);
	}

	@FunctionalInterface
	public interface DisplayCollector {
		void displaysForRecipe(RegistryKey<Recipe<?>> recipeKey, Consumer<RecipeDisplayEntry> adder);
	}
}
