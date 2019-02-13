package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SimpleTrader implements Trader {
	private final TraderInventory traderInventory;
	private final PlayerEntity player;
	private TraderRecipeList recipeList = new TraderRecipeList();

	public SimpleTrader(PlayerEntity playerEntity) {
		this.player = playerEntity;
		this.traderInventory = new TraderInventory(this);
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.player;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
	}

	@Override
	public TraderRecipeList getRecipes() {
		return this.recipeList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setServerRecipes(@Nullable TraderRecipeList traderRecipeList) {
		this.recipeList = traderRecipeList;
	}

	@Override
	public void useRecipe(TraderRecipe traderRecipe) {
		traderRecipe.use();
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
	}

	@Override
	public World getTraderWorld() {
		return this.player.world;
	}

	@Override
	public BlockPos getTraderPos() {
		return new BlockPos(this.player);
	}
}
