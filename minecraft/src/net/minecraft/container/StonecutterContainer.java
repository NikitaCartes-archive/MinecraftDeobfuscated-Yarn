package net.minecraft.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3914;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class StonecutterContainer extends Container {
	static final ImmutableList<Item> field_17626 = ImmutableList.of(
		Items.field_8352,
		Items.field_8709,
		Items.field_8134,
		Items.field_8738,
		Items.field_8724,
		Items.field_8061,
		Items.field_8421,
		Items.field_8796,
		Items.field_8079,
		Items.field_8553,
		Items.field_8044,
		Items.field_8784,
		Items.field_8718,
		Items.field_8082,
		Items.field_8123,
		Items.field_8532,
		Items.field_8642,
		Items.field_8413,
		Items.field_8221,
		Items.field_8195,
		Items.field_8555,
		Items.field_8813,
		Items.field_8339,
		Items.field_8590,
		Items.field_8518,
		Items.field_8314,
		Items.field_8216
	);
	private final class_3914 field_17630;
	private final Property selectedRecipe = Property.create();
	private final World world;
	private List<StonecuttingRecipe> availableRecipes = Lists.<StonecuttingRecipe>newArrayList();
	private ItemStack inputStack = ItemStack.EMPTY;
	private long lastTakeTime;
	final Slot inputSlot;
	final Slot outputSlot;
	private Runnable contentsChangedListener = () -> {
	};
	public final Inventory inventory = new BasicInventory(2) {
		@Override
		public void markDirty() {
			super.markDirty();
			StonecutterContainer.this.onContentChanged(this);
			StonecutterContainer.this.contentsChangedListener.run();
		}
	};

	public StonecutterContainer(int i, PlayerInventory playerInventory) {
		this(i, playerInventory, class_3914.field_17304);
	}

	public StonecutterContainer(int i, PlayerInventory playerInventory, class_3914 arg) {
		super(ContainerType.field_17625, i);
		this.field_17630 = arg;
		this.world = playerInventory.player.world;
		this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 20, 33));
		this.outputSlot = this.addSlot(new Slot(this.inventory, 1, 143, 33) {
			@Override
			public boolean canInsert(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
				ItemStack itemStack2 = StonecutterContainer.this.inputSlot.takeStack(1);
				if (!itemStack2.isEmpty()) {
					StonecutterContainer.this.populateResult();
				}

				itemStack.getItem().onCrafted(itemStack, playerEntity.world, playerEntity);
				arg.method_17393((world, blockPos) -> {
					long l = world.getTime();
					if (StonecutterContainer.this.lastTakeTime != l) {
						world.playSound(null, blockPos, SoundEvents.field_17710, SoundCategory.field_15245, 1.0F, 1.0F);
						StonecutterContainer.this.lastTakeTime = l;
					}
				});
				return super.onTakeItem(playerEntity, itemStack);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

		this.addProperty(this.selectedRecipe);
	}

	@Environment(EnvType.CLIENT)
	public int method_17862() {
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
	public boolean canUse(PlayerEntity playerEntity) {
		return canUse(this.field_17630, playerEntity, Blocks.field_16335);
	}

	@Override
	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		if (i >= 0 && i < this.availableRecipes.size()) {
			this.selectedRecipe.set(i);
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
			this.availableRecipes = this.world.getRecipeManager().method_17877(RecipeType.field_17641, inventory, this.world);
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
		return ContainerType.field_17625;
	}

	@Environment(EnvType.CLIENT)
	public void setContentsChangedListener(Runnable runnable) {
		this.contentsChangedListener = runnable;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 1) {
				item.onCrafted(itemStack2, playerEntity.world, playerEntity);
				if (!this.insertItem(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (i == 0) {
				if (!this.insertItem(itemStack2, 2, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (field_17626.contains(item)) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 2 && i < 29) {
				if (!this.insertItem(itemStack2, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 29 && i < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.onTakeItem(playerEntity, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.removeInvStack(1);
		this.field_17630.method_17393((world, blockPos) -> this.dropInventory(playerEntity, playerEntity.world, this.inventory));
	}
}
