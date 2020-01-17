package net.minecraft.container;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer extends Container {
	private final BlockContext context;
	private final Property selectedRecipe = Property.create();
	private final World world;
	private List<StonecuttingRecipe> availableRecipes = Lists.<StonecuttingRecipe>newArrayList();
	private ItemStack inputStack = ItemStack.EMPTY;
	private long lastTakeTime;
	final Slot inputSlot;
	final Slot outputSlot;
	private Runnable contentsChangedListener = () -> {
	};
	public final Inventory inventory = new BasicInventory(1) {
		@Override
		public void markDirty() {
			super.markDirty();
			StonecutterContainer.this.onContentChanged(this);
			StonecutterContainer.this.contentsChangedListener.run();
		}
	};
	private final CraftingResultInventory field_19173 = new CraftingResultInventory();

	public StonecutterContainer(int syncId, PlayerInventory playerInventory) {
		this(syncId, playerInventory, BlockContext.EMPTY);
	}

	public StonecutterContainer(int syncId, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.STONECUTTER, syncId);
		this.context = blockContext;
		this.world = playerInventory.player.world;
		this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
		this.outputSlot = this.addSlot(new Slot(this.field_19173, 1, 143, 33) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
				ItemStack itemStack = StonecutterContainer.this.inputSlot.takeStack(1);
				if (!itemStack.isEmpty()) {
					StonecutterContainer.this.populateResult();
				}

				stack.getItem().onCraft(stack, player.world, player);
				blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					long l = world.getTime();
					if (StonecutterContainer.this.lastTakeTime != l) {
						world.playSound(null, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
						StonecutterContainer.this.lastTakeTime = l;
					}
				}));
				return super.onTakeItem(player, stack);
			}
		});

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}

		this.addProperty(this.selectedRecipe);
	}

	@Environment(EnvType.CLIENT)
	public int getSelectedRecipe() {
		return this.selectedRecipe.get();
	}

	@Environment(EnvType.CLIENT)
	public List<StonecuttingRecipe> getAvailableRecipes() {
		return this.availableRecipes;
	}

	@Environment(EnvType.CLIENT)
	public int getAvailableRecipeCount() {
		return this.availableRecipes.size();
	}

	@Environment(EnvType.CLIENT)
	public boolean canCraft() {
		return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(this.context, player, Blocks.STONECUTTER);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (id >= 0 && id < this.availableRecipes.size()) {
			this.selectedRecipe.set(id);
			this.populateResult();
		}

		return true;
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		ItemStack itemStack = this.inputSlot.getStack();
		if (itemStack.getItem() != this.inputStack.getItem()) {
			this.inputStack = itemStack.copy();
			this.updateInput(inventory, itemStack);
		}
	}

	private void updateInput(Inventory inventory, ItemStack itemStack) {
		this.availableRecipes.clear();
		this.selectedRecipe.set(-1);
		this.outputSlot.setStack(ItemStack.EMPTY);
		if (!itemStack.isEmpty()) {
			this.availableRecipes = this.world.getRecipeManager().getAllMatches(RecipeType.STONECUTTING, inventory, this.world);
		}
	}

	private void populateResult() {
		if (!this.availableRecipes.isEmpty()) {
			StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe)this.availableRecipes.get(this.selectedRecipe.get());
			this.outputSlot.setStack(stonecuttingRecipe.craft(this.inventory));
		} else {
			this.outputSlot.setStack(ItemStack.EMPTY);
		}

		this.sendContentUpdates();
	}

	@Override
	public ContainerType<?> getType() {
		return ContainerType.STONECUTTER;
	}

	@Environment(EnvType.CLIENT)
	public void setContentsChangedListener(Runnable runnable) {
		this.contentsChangedListener = runnable;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.field_19173 && super.canInsertIntoSlot(stack, slot);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (invSlot == 1) {
				item.onCraft(itemStack2, player.world, player);
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 2, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.world.getRecipeManager().getFirstMatch(RecipeType.STONECUTTING, new BasicInventory(itemStack2), this.world).isPresent()) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 2 && invSlot < 29) {
				if (!this.insertItem(itemStack2, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 29 && invSlot < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(player, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.field_19173.removeInvStack(1);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.dropInventory(player, player.world, this.inventory)));
	}
}
