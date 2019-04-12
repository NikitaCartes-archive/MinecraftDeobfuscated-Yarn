package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.cooking.CookingRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider, Tickable {
	private static final int[] TOP_SLOTS = new int[]{0};
	private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
	private static final int[] SIDE_SLOTS = new int[]{1};
	protected DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
	private int burnTime;
	private int fuelTime;
	private int cookTime;
	private int cookTimeTotal;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			switch (i) {
				case 0:
					return AbstractFurnaceBlockEntity.this.burnTime;
				case 1:
					return AbstractFurnaceBlockEntity.this.fuelTime;
				case 2:
					return AbstractFurnaceBlockEntity.this.cookTime;
				case 3:
					return AbstractFurnaceBlockEntity.this.cookTimeTotal;
				default:
					return 0;
			}
		}

		@Override
		public void set(int i, int j) {
			switch (i) {
				case 0:
					AbstractFurnaceBlockEntity.this.burnTime = j;
					break;
				case 1:
					AbstractFurnaceBlockEntity.this.fuelTime = j;
					break;
				case 2:
					AbstractFurnaceBlockEntity.this.cookTime = j;
					break;
				case 3:
					AbstractFurnaceBlockEntity.this.cookTimeTotal = j;
			}
		}

		@Override
		public int size() {
			return 4;
		}
	};
	private final Map<Identifier, Integer> recipesUsed = Maps.<Identifier, Integer>newHashMap();
	protected final RecipeType<? extends CookingRecipe> recipeType;

	protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends CookingRecipe> recipeType) {
		super(blockEntityType);
		this.recipeType = recipeType;
	}

	public static Map<Item, Integer> createFuelTimeMap() {
		Map<Item, Integer> map = Maps.<Item, Integer>newLinkedHashMap();
		addFuel(map, Items.field_8187, 20000);
		addFuel(map, Blocks.field_10381, 16000);
		addFuel(map, Items.field_8894, 2400);
		addFuel(map, Items.field_8713, 1600);
		addFuel(map, Items.field_8665, 1600);
		addFuel(map, ItemTags.field_15539, 300);
		addFuel(map, ItemTags.field_15537, 300);
		addFuel(map, ItemTags.field_15557, 300);
		addFuel(map, ItemTags.field_15534, 150);
		addFuel(map, ItemTags.field_15550, 300);
		addFuel(map, ItemTags.field_15540, 300);
		addFuel(map, Blocks.field_10620, 300);
		addFuel(map, Blocks.field_10299, 300);
		addFuel(map, Blocks.field_10020, 300);
		addFuel(map, Blocks.field_10319, 300);
		addFuel(map, Blocks.field_10132, 300);
		addFuel(map, Blocks.field_10144, 300);
		addFuel(map, Blocks.field_10188, 300);
		addFuel(map, Blocks.field_10513, 300);
		addFuel(map, Blocks.field_10291, 300);
		addFuel(map, Blocks.field_10041, 300);
		addFuel(map, Blocks.field_10196, 300);
		addFuel(map, Blocks.field_10457, 300);
		addFuel(map, Blocks.field_10179, 300);
		addFuel(map, Blocks.field_10504, 300);
		addFuel(map, Blocks.field_16330, 300);
		addFuel(map, Blocks.field_10223, 300);
		addFuel(map, Blocks.field_10034, 300);
		addFuel(map, Blocks.field_10380, 300);
		addFuel(map, Blocks.field_9980, 300);
		addFuel(map, Blocks.field_10429, 300);
		addFuel(map, ItemTags.field_15556, 300);
		addFuel(map, Items.field_8102, 300);
		addFuel(map, Items.field_8378, 300);
		addFuel(map, Blocks.field_9983, 300);
		addFuel(map, ItemTags.field_15533, 200);
		addFuel(map, Items.field_8876, 200);
		addFuel(map, Items.field_8091, 200);
		addFuel(map, Items.field_8167, 200);
		addFuel(map, Items.field_8406, 200);
		addFuel(map, Items.field_8647, 200);
		addFuel(map, ItemTags.field_15552, 200);
		addFuel(map, ItemTags.field_15536, 200);
		addFuel(map, ItemTags.field_15544, 100);
		addFuel(map, ItemTags.field_15555, 100);
		addFuel(map, Items.field_8600, 100);
		addFuel(map, ItemTags.field_15528, 100);
		addFuel(map, Items.field_8428, 100);
		addFuel(map, ItemTags.field_15542, 67);
		addFuel(map, Blocks.field_10342, 4001);
		addFuel(map, Items.field_8399, 300);
		addFuel(map, Blocks.field_10211, 50);
		addFuel(map, Blocks.field_10428, 100);
		addFuel(map, Blocks.field_16492, 50);
		addFuel(map, Blocks.field_10083, 300);
		addFuel(map, Blocks.field_16328, 300);
		addFuel(map, Blocks.field_16336, 300);
		addFuel(map, Blocks.field_16331, 300);
		addFuel(map, Blocks.field_16329, 300);
		addFuel(map, Blocks.field_17563, 300);
		return map;
	}

	private static void addFuel(Map<Item, Integer> map, Tag<Item> tag, int i) {
		for (Item item : tag.values()) {
			map.put(item, i);
		}
	}

	private static void addFuel(Map<Item, Integer> map, ItemProvider itemProvider, int i) {
		map.put(itemProvider.getItem(), i);
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		Inventories.fromTag(compoundTag, this.inventory);
		this.burnTime = compoundTag.getShort("BurnTime");
		this.cookTime = compoundTag.getShort("CookTime");
		this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
		this.fuelTime = this.getFuelTime(this.inventory.get(1));
		int i = compoundTag.getShort("RecipesUsedSize");

		for (int j = 0; j < i; j++) {
			Identifier identifier = new Identifier(compoundTag.getString("RecipeLocation" + j));
			int k = compoundTag.getInt("RecipeAmount" + j);
			this.recipesUsed.put(identifier, k);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putShort("BurnTime", (short)this.burnTime);
		compoundTag.putShort("CookTime", (short)this.cookTime);
		compoundTag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		Inventories.toTag(compoundTag, this.inventory);
		compoundTag.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
		int i = 0;

		for (Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
			compoundTag.putString("RecipeLocation" + i, ((Identifier)entry.getKey()).toString());
			compoundTag.putInt("RecipeAmount" + i, (Integer)entry.getValue());
			i++;
		}

		return compoundTag;
	}

	@Override
	public void tick() {
		boolean bl = this.isBurning();
		boolean bl2 = false;
		if (this.isBurning()) {
			this.burnTime--;
		}

		if (!this.world.isClient) {
			ItemStack itemStack = this.inventory.get(1);
			if (this.isBurning() || !itemStack.isEmpty() && !this.inventory.get(0).isEmpty()) {
				Recipe<?> recipe = (Recipe<?>)this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).orElse(null);
				if (!this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
					this.burnTime = this.getFuelTime(itemStack);
					this.fuelTime = this.burnTime;
					if (this.isBurning()) {
						bl2 = true;
						if (!itemStack.isEmpty()) {
							Item item = itemStack.getItem();
							itemStack.subtractAmount(1);
							if (itemStack.isEmpty()) {
								Item item2 = item.getRecipeRemainder();
								this.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
							}
						}
					}
				}

				if (this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
					this.cookTime++;
					if (this.cookTime == this.cookTimeTotal) {
						this.cookTime = 0;
						this.cookTimeTotal = this.getCookTime();
						this.craftRecipe(recipe);
						bl2 = true;
					}
				} else {
					this.cookTime = 0;
				}
			} else if (!this.isBurning() && this.cookTime > 0) {
				this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
			}

			if (bl != this.isBurning()) {
				bl2 = true;
				this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
			}
		}

		if (bl2) {
			this.markDirty();
		}
	}

	protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
		if (!this.inventory.get(0).isEmpty() && recipe != null) {
			ItemStack itemStack = recipe.getOutput();
			if (itemStack.isEmpty()) {
				return false;
			} else {
				ItemStack itemStack2 = this.inventory.get(2);
				if (itemStack2.isEmpty()) {
					return true;
				} else if (!itemStack2.isEqualIgnoreTags(itemStack)) {
					return false;
				} else {
					return itemStack2.getAmount() < this.getInvMaxStackAmount() && itemStack2.getAmount() < itemStack2.getMaxAmount()
						? true
						: itemStack2.getAmount() < itemStack.getMaxAmount();
				}
			}
		} else {
			return false;
		}
	}

	private void craftRecipe(@Nullable Recipe<?> recipe) {
		if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
			ItemStack itemStack = this.inventory.get(0);
			ItemStack itemStack2 = recipe.getOutput();
			ItemStack itemStack3 = this.inventory.get(2);
			if (itemStack3.isEmpty()) {
				this.inventory.set(2, itemStack2.copy());
			} else if (itemStack3.getItem() == itemStack2.getItem()) {
				itemStack3.addAmount(1);
			}

			if (!this.world.isClient) {
				this.setLastRecipe(recipe);
			}

			if (itemStack.getItem() == Blocks.field_10562.getItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.field_8550) {
				this.inventory.set(1, new ItemStack(Items.field_8705));
			}

			itemStack.subtractAmount(1);
		}
	}

	protected int getFuelTime(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		} else {
			Item item = itemStack.getItem();
			return (Integer)createFuelTimeMap().getOrDefault(item, 0);
		}
	}

	protected int getCookTime() {
		return (Integer)this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).map(CookingRecipe::getCookTime).orElse(200);
	}

	public static boolean canUseAsFuel(ItemStack itemStack) {
		return createFuelTimeMap().containsKey(itemStack.getItem());
	}

	@Override
	public int[] getInvAvailableSlots(Direction direction) {
		if (direction == Direction.DOWN) {
			return BOTTOM_SLOTS;
		} else {
			return direction == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean canInsertInvStack(int i, ItemStack itemStack, @Nullable Direction direction) {
		return this.isValidInvStack(i, itemStack);
	}

	@Override
	public boolean canExtractInvStack(int i, ItemStack itemStack, Direction direction) {
		if (direction == Direction.DOWN && i == 1) {
			Item item = itemStack.getItem();
			if (item != Items.field_8705 && item != Items.field_8550) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int getInvSize() {
		return this.inventory.size();
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int i) {
		return this.inventory.get(i);
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		return Inventories.splitStack(this.inventory, i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return Inventories.removeStack(this.inventory, i);
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		ItemStack itemStack2 = this.inventory.get(i);
		boolean bl = !itemStack.isEmpty() && itemStack.isEqualIgnoreTags(itemStack2) && ItemStack.areTagsEqual(itemStack, itemStack2);
		this.inventory.set(i, itemStack);
		if (itemStack.getAmount() > this.getInvMaxStackAmount()) {
			itemStack.setAmount(this.getInvMaxStackAmount());
		}

		if (i == 0 && !bl) {
			this.cookTimeTotal = this.getCookTime();
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		if (i == 2) {
			return false;
		} else if (i != 1) {
			return true;
		} else {
			ItemStack itemStack2 = this.inventory.get(1);
			return canUseAsFuel(itemStack) || itemStack.getItem() == Items.field_8550 && itemStack2.getItem() != Items.field_8550;
		}
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public void setLastRecipe(@Nullable Recipe<?> recipe) {
		if (recipe != null) {
			this.recipesUsed.compute(recipe.getId(), (identifier, integer) -> 1 + (integer == null ? 0 : integer));
		}
	}

	@Nullable
	@Override
	public Recipe<?> getLastRecipe() {
		return null;
	}

	@Override
	public void unlockLastRecipe(PlayerEntity playerEntity) {
	}

	public void dropExperience(PlayerEntity playerEntity) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
			playerEntity.world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
				dropExperience(playerEntity, (Integer)entry.getValue(), ((CookingRecipe)recipe).getExperience());
			});
		}

		playerEntity.unlockRecipes(list);
		this.recipesUsed.clear();
	}

	private static void dropExperience(PlayerEntity playerEntity, int i, float f) {
		if (f == 0.0F) {
			i = 0;
		} else if (f < 1.0F) {
			int j = MathHelper.floor((float)i * f);
			if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
				j++;
			}

			i = j;
		}

		while (i > 0) {
			int j = ExperienceOrbEntity.roundToOrbSize(i);
			i -= j;
			playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.x, playerEntity.y + 0.5, playerEntity.z + 0.5, j));
		}
	}

	@Override
	public void provideRecipeInputs(RecipeFinder recipeFinder) {
		for (ItemStack itemStack : this.inventory) {
			recipeFinder.addItem(itemStack);
		}
	}
}
