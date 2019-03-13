package net.minecraft.container;

import java.util.Optional;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.GuiSlotUpdateS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableContainer extends CraftingContainer<CraftingInventory> {
	private final CraftingInventory craftingInv = new CraftingInventory(this, 3, 3);
	private final CraftingResultInventory field_7800 = new CraftingResultInventory();
	private final BlockContext context;
	private final PlayerEntity player;

	public CraftingTableContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public CraftingTableContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.CRAFTING, i);
		this.context = blockContext;
		this.player = playerInventory.field_7546;
		this.method_7621(new CraftingResultSlot(playerInventory.field_7546, this.craftingInv, this.field_7800, 0, 124, 35));

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 3; k++) {
				this.method_7621(new Slot(this.craftingInv, k + j * 3, 30 + k * 18, 17 + j * 18));
			}
		}

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}
	}

	protected static void method_17399(
		int i, World world, PlayerEntity playerEntity, CraftingInventory craftingInventory, CraftingResultInventory craftingResultInventory
	) {
		if (!world.isClient) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
			ItemStack itemStack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().method_8132(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()) {
				CraftingRecipe craftingRecipe = (CraftingRecipe)optional.get();
				if (craftingResultInventory.method_7665(world, serverPlayerEntity, craftingRecipe)) {
					itemStack = craftingRecipe.craft(craftingInventory);
				}
			}

			craftingResultInventory.method_5447(0, itemStack);
			serverPlayerEntity.field_13987.sendPacket(new GuiSlotUpdateS2CPacket(i, 0, itemStack));
		}
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> method_17399(this.syncId, world, this.player, this.craftingInv, this.field_7800)));
	}

	@Override
	public void populateRecipeFinder(RecipeFinder recipeFinder) {
		this.craftingInv.provideRecipeInputs(recipeFinder);
	}

	@Override
	public void clearCraftingSlots() {
		this.craftingInv.clear();
		this.field_7800.clear();
	}

	@Override
	public boolean method_7652(Recipe<? super CraftingInventory> recipe) {
		return recipe.method_8115(this.craftingInv, this.player.field_6002);
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.method_7607(playerEntity, world, this.craftingInv)));
	}

	@Override
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.context, playerEntity, Blocks.field_9980);
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			itemStack = itemStack2.copy();
			if (i == 0) {
				this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> itemStack2.getItem().method_7843(itemStack2, world, playerEntity)));
				if (!this.method_7616(itemStack2, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (i >= 10 && i < 37) {
				if (!this.method_7616(itemStack2, 37, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 37 && i < 46) {
				if (!this.method_7616(itemStack2, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.method_7616(itemStack2, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.method_7667(playerEntity, itemStack2);
			if (i == 0) {
				playerEntity.method_7328(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean method_7613(ItemStack itemStack, Slot slot) {
		return slot.inventory != this.field_7800 && super.method_7613(itemStack, slot);
	}

	@Override
	public int getCraftingResultSlotIndex() {
		return 0;
	}

	@Override
	public int getCraftingWidth() {
		return this.craftingInv.getWidth();
	}

	@Override
	public int getCraftingHeight() {
		return this.craftingInv.getHeight();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getCraftingSlotCount() {
		return 10;
	}
}
