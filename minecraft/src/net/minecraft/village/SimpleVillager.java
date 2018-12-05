package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class SimpleVillager implements Villager {
	private final VillagerInventory villagerInventory;
	private final PlayerEntity player;
	private VillagerRecipeList recipeList;
	private final TextComponent displayName;

	public SimpleVillager(PlayerEntity playerEntity, TextComponent textComponent) {
		this.player = playerEntity;
		this.displayName = textComponent;
		this.villagerInventory = new VillagerInventory(playerEntity, this);
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.player;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
	}

	@Nullable
	@Override
	public VillagerRecipeList getRecipes(PlayerEntity playerEntity) {
		return this.recipeList;
	}

	@Override
	public void setRecipeList(@Nullable VillagerRecipeList villagerRecipeList) {
		this.recipeList = villagerRecipeList;
	}

	@Override
	public void useRecipe(VillagerRecipe villagerRecipe) {
		villagerRecipe.use();
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
	}

	@Override
	public TextComponent getDisplayName() {
		return (TextComponent)(this.displayName != null ? this.displayName : new TranslatableTextComponent("entity.Villager.name"));
	}

	@Override
	public World getVillagerWorld() {
		return this.player.world;
	}

	@Override
	public BlockPos getVillagerPos() {
		return new BlockPos(this.player);
	}
}
