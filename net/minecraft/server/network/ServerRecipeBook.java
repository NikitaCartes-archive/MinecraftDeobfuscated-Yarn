/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.slf4j.Logger;

public class ServerRecipeBook
extends RecipeBook {
    public static final String RECIPE_BOOK_KEY = "recipeBook";
    private static final Logger LOGGER = LogUtils.getLogger();

    public int unlockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList<Identifier> list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> recipe : recipes) {
            Identifier identifier = recipe.getId();
            if (this.recipes.contains(identifier) || recipe.isIgnoredInRecipeBook()) continue;
            this.add(identifier);
            this.display(identifier);
            list.add(identifier);
            Criteria.RECIPE_UNLOCKED.trigger(player, recipe);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.ADD, player, list);
        return i;
    }

    public int lockRecipes(Collection<Recipe<?>> recipes, ServerPlayerEntity player) {
        ArrayList<Identifier> list = Lists.newArrayList();
        int i = 0;
        for (Recipe<?> recipe : recipes) {
            Identifier identifier = recipe.getId();
            if (!this.recipes.contains(identifier)) continue;
            this.remove(identifier);
            list.add(identifier);
            ++i;
        }
        this.sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action.REMOVE, player, list);
        return i;
    }

    private void sendUnlockRecipesPacket(UnlockRecipesS2CPacket.Action action, ServerPlayerEntity player, List<Identifier> recipeIds) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(action, recipeIds, Collections.emptyList(), this.getOptions()));
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.getOptions().writeNbt(nbtCompound);
        NbtList nbtList = new NbtList();
        for (Identifier identifier : this.recipes) {
            nbtList.add(NbtString.of(identifier.toString()));
        }
        nbtCompound.put("recipes", nbtList);
        NbtList nbtList2 = new NbtList();
        for (Identifier identifier2 : this.toBeDisplayed) {
            nbtList2.add(NbtString.of(identifier2.toString()));
        }
        nbtCompound.put("toBeDisplayed", nbtList2);
        return nbtCompound;
    }

    public void readNbt(NbtCompound nbt, RecipeManager recipeManager) {
        this.setOptions(RecipeBookOptions.fromNbt(nbt));
        NbtList nbtList = nbt.getList("recipes", NbtElement.STRING_TYPE);
        this.handleList(nbtList, this::add, recipeManager);
        NbtList nbtList2 = nbt.getList("toBeDisplayed", NbtElement.STRING_TYPE);
        this.handleList(nbtList2, this::display, recipeManager);
    }

    private void handleList(NbtList list, Consumer<Recipe<?>> handler, RecipeManager recipeManager) {
        for (int i = 0; i < list.size(); ++i) {
            String string = list.getString(i);
            try {
                Identifier identifier = new Identifier(string);
                Optional<Recipe<?>> optional = recipeManager.get(identifier);
                if (!optional.isPresent()) {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)identifier);
                    continue;
                }
                handler.accept(optional.get());
                continue;
            } catch (InvalidIdentifierException invalidIdentifierException) {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)string);
            }
        }
    }

    public void sendInitRecipesPacket(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new UnlockRecipesS2CPacket(UnlockRecipesS2CPacket.Action.INIT, this.recipes, this.toBeDisplayed, this.getOptions()));
    }
}

