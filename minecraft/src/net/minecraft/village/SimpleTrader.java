package net.minecraft.village;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SimpleTrader implements Trader {
	private final TraderInventory field_7443;
	private final PlayerEntity field_7441;
	private TraderRecipeList field_7442 = new TraderRecipeList();
	private int field_18525;

	public SimpleTrader(PlayerEntity playerEntity) {
		this.field_7441 = playerEntity;
		this.field_7443 = new TraderInventory(this);
	}

	@Nullable
	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.field_7441;
	}

	@Override
	public void setCurrentCustomer(@Nullable PlayerEntity playerEntity) {
	}

	@Override
	public TraderRecipeList method_8264() {
		return this.field_7442;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_8261(@Nullable TraderRecipeList traderRecipeList) {
		this.field_7442 = traderRecipeList;
	}

	@Override
	public void method_8262(TraderRecipe traderRecipe) {
		traderRecipe.use();
	}

	@Override
	public void onSellingItem(ItemStack itemStack) {
	}

	@Override
	public World method_8260() {
		return this.field_7441.field_6002;
	}

	@Override
	public int method_19269() {
		return this.field_18525;
	}

	@Override
	public void method_19271(int i) {
		this.field_18525 = i;
	}

	@Override
	public boolean method_19270() {
		return true;
	}
}
