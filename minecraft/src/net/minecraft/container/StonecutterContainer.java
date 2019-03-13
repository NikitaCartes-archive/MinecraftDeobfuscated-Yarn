package net.minecraft.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StonecutterContainer extends Container {
	static final ImmutableList<Item> field_17626 = ImmutableList.of(
		Items.STONE,
		Items.SANDSTONE,
		Items.RED_SANDSTONE,
		Items.QUARTZ_BLOCK,
		Items.COBBLESTONE,
		Items.STONE_BRICKS,
		Items.BRICKS,
		Items.NETHER_BRICKS,
		Items.RED_NETHER_BRICKS,
		Items.PURPUR_BLOCK,
		Items.PRISMARINE,
		Items.PRISMARINE_BRICKS,
		Items.DARK_PRISMARINE,
		Items.ANDESITE,
		Items.POLISHED_ANDESITE,
		Items.GRANITE,
		Items.POLISHED_GRANITE,
		Items.DIORITE,
		Items.POLISHED_DIORITE,
		Items.MOSSY_STONE_BRICKS,
		Items.MOSSY_COBBLESTONE,
		Items.SMOOTH_SANDSTONE,
		Items.SMOOTH_RED_SANDSTONE,
		Items.SMOOTH_QUARTZ,
		Items.END_STONE,
		Items.END_STONE_BRICKS,
		Items.SMOOTH_STONE
	);
	private final BlockContext context;
	private final Property selectedRecipe = Property.create();
	private final World field_17632;
	private List<StonecuttingRecipe> availableRecipes = Lists.<StonecuttingRecipe>newArrayList();
	private ItemStack field_17634 = ItemStack.EMPTY;
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
		this(i, playerInventory, BlockContext.EMPTY);
	}

	public StonecutterContainer(int i, PlayerInventory playerInventory, BlockContext blockContext) {
		super(ContainerType.field_17625, i);
		this.context = blockContext;
		this.field_17632 = playerInventory.field_7546.field_6002;
		this.inputSlot = this.method_7621(new Slot(this.inventory, 0, 20, 33));
		this.outputSlot = this.method_7621(new Slot(this.inventory, 1, 143, 33) {
			@Override
			public boolean method_7680(ItemStack itemStack) {
				return false;
			}

			@Override
			public ItemStack method_7667(PlayerEntity playerEntity, ItemStack itemStack) {
				ItemStack itemStack2 = StonecutterContainer.this.inputSlot.method_7671(1);
				if (!itemStack2.isEmpty()) {
					StonecutterContainer.this.populateResult();
				}

				itemStack.getItem().method_7843(itemStack, playerEntity.field_6002, playerEntity);
				blockContext.run((BiConsumer<World, BlockPos>)((world, blockPos) -> {
					long l = world.getTime();
					if (StonecutterContainer.this.lastTakeTime != l) {
						world.method_8396(null, blockPos, SoundEvents.field_17710, SoundCategory.field_15245, 1.0F, 1.0F);
						StonecutterContainer.this.lastTakeTime = l;
					}
				}));
				return super.method_7667(playerEntity, itemStack);
			}
		});

		for (int j = 0; j < 3; j++) {
			for (int k = 0; k < 9; k++) {
				this.method_7621(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			this.method_7621(new Slot(playerInventory, j, 8 + j * 18, 142));
		}

		this.method_17362(this.selectedRecipe);
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
	public boolean canUse(PlayerEntity playerEntity) {
		return method_17695(this.context, playerEntity, Blocks.field_16335);
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
		ItemStack itemStack = this.inputSlot.method_7677();
		if (itemStack.getItem() != this.field_17634.getItem()) {
			this.field_17634 = itemStack.copy();
			this.method_17855(inventory, itemStack);
		}
	}

	private void method_17855(Inventory inventory, ItemStack itemStack) {
		this.availableRecipes.clear();
		this.selectedRecipe.set(-1);
		this.outputSlot.method_7673(ItemStack.EMPTY);
		if (!itemStack.isEmpty()) {
			this.availableRecipes = this.field_17632.getRecipeManager().method_17877(RecipeType.field_17641, inventory, this.field_17632);
		}
	}

	private void populateResult() {
		if (!this.availableRecipes.isEmpty()) {
			StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe)this.availableRecipes.get(this.selectedRecipe.get());
			this.outputSlot.method_7673(stonecuttingRecipe.craft(this.inventory));
		} else {
			this.outputSlot.method_7673(ItemStack.EMPTY);
		}

		this.sendContentUpdates();
	}

	@Override
	public ContainerType<?> method_17358() {
		return ContainerType.field_17625;
	}

	@Environment(EnvType.CLIENT)
	public void setContentsChangedListener(Runnable runnable) {
		this.contentsChangedListener = runnable;
	}

	@Override
	public ItemStack method_7601(PlayerEntity playerEntity, int i) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slotList.get(i);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.method_7677();
			Item item = itemStack2.getItem();
			itemStack = itemStack2.copy();
			if (i == 1) {
				item.method_7843(itemStack2, playerEntity.field_6002, playerEntity);
				if (!this.method_7616(itemStack2, 2, 38, true)) {
					return ItemStack.EMPTY;
				}

				slot.method_7670(itemStack2, itemStack);
			} else if (i == 0) {
				if (!this.method_7616(itemStack2, 2, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (field_17626.contains(item)) {
				if (!this.method_7616(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 2 && i < 29) {
				if (!this.method_7616(itemStack2, 29, 38, false)) {
					return ItemStack.EMPTY;
				}
			} else if (i >= 29 && i < 38 && !this.method_7616(itemStack2, 2, 29, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.method_7673(ItemStack.EMPTY);
			}

			slot.markDirty();
			if (itemStack2.getAmount() == itemStack.getAmount()) {
				return ItemStack.EMPTY;
			}

			slot.method_7667(playerEntity, itemStack2);
			this.sendContentUpdates();
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity playerEntity) {
		super.close(playerEntity);
		this.inventory.method_5441(1);
		this.context.run((BiConsumer<World, BlockPos>)((world, blockPos) -> this.method_7607(playerEntity, playerEntity.field_6002, this.inventory)));
	}
}
