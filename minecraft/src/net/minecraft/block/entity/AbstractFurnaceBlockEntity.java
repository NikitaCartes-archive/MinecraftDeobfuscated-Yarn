package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider, Tickable {
	private static final int[] TOP_SLOTS = new int[]{0};
	private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
	private static final int[] SIDE_SLOTS = new int[]{1};
	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private int burnTime;
	private int fuelTime;
	private int cookTime;
	private int cookTimeTotal;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
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
		public void set(int index, int value) {
			switch (index) {
				case 0:
					AbstractFurnaceBlockEntity.this.burnTime = value;
					break;
				case 1:
					AbstractFurnaceBlockEntity.this.fuelTime = value;
					break;
				case 2:
					AbstractFurnaceBlockEntity.this.cookTime = value;
					break;
				case 3:
					AbstractFurnaceBlockEntity.this.cookTimeTotal = value;
			}
		}

		@Override
		public int size() {
			return 4;
		}
	};
	private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
	protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

	protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends AbstractCookingRecipe> recipeType) {
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
		addFuel(map, ItemTags.field_15536, 1200);
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
		addFuel(map, Blocks.field_16492, 400);
		addFuel(map, Blocks.field_10083, 300);
		addFuel(map, Blocks.field_16328, 300);
		addFuel(map, Blocks.field_16336, 300);
		addFuel(map, Blocks.field_16331, 300);
		addFuel(map, Blocks.field_16329, 300);
		addFuel(map, Blocks.field_17563, 300);
		return map;
	}

	private static boolean isNonFlammableWood(Item item) {
		return ItemTags.field_23211.contains(item);
	}

	private static void addFuel(Map<Item, Integer> fuelTimes, Tag<Item> tag, int fuelTime) {
		for (Item item : tag.values()) {
			if (!isNonFlammableWood(item)) {
				fuelTimes.put(item, fuelTime);
			}
		}
	}

	private static void addFuel(Map<Item, Integer> map, ItemConvertible item, int fuelTime) {
		Item item2 = item.asItem();
		if (isNonFlammableWood(item2)) {
			if (SharedConstants.isDevelopment) {
				throw (IllegalStateException)Util.throwOrPause(
					new IllegalStateException(
						"A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"
					)
				);
			}
		} else {
			map.put(item2, fuelTime);
		}
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.fromTag(tag, this.inventory);
		this.burnTime = tag.getShort("BurnTime");
		this.cookTime = tag.getShort("CookTime");
		this.cookTimeTotal = tag.getShort("CookTimeTotal");
		this.fuelTime = this.getFuelTime(this.inventory.get(1));
		CompoundTag compoundTag = tag.getCompound("RecipesUsed");

		for (String string : compoundTag.getKeys()) {
			this.recipesUsed.put(new Identifier(string), compoundTag.getInt(string));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putShort("BurnTime", (short)this.burnTime);
		tag.putShort("CookTime", (short)this.cookTime);
		tag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		Inventories.toTag(tag, this.inventory);
		CompoundTag compoundTag = new CompoundTag();
		this.recipesUsed.forEach((identifier, integer) -> compoundTag.putInt(identifier.toString(), integer));
		tag.put("RecipesUsed", compoundTag);
		return tag;
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
							itemStack.decrement(1);
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
				} else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
					return false;
				} else {
					return itemStack2.getCount() < this.getMaxCountPerStack() && itemStack2.getCount() < itemStack2.getMaxCount()
						? true
						: itemStack2.getCount() < itemStack.getMaxCount();
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
				itemStack3.increment(1);
			}

			if (!this.world.isClient) {
				this.setLastRecipe(recipe);
			}

			if (itemStack.getItem() == Blocks.field_10562.asItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.field_8550) {
				this.inventory.set(1, new ItemStack(Items.field_8705));
			}

			itemStack.decrement(1);
		}
	}

	protected int getFuelTime(ItemStack fuel) {
		if (fuel.isEmpty()) {
			return 0;
		} else {
			Item item = fuel.getItem();
			return (Integer)createFuelTimeMap().getOrDefault(item, 0);
		}
	}

	protected int getCookTime() {
		return (Integer)this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
	}

	public static boolean canUseAsFuel(ItemStack stack) {
		return createFuelTimeMap().containsKey(stack.getItem());
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		if (side == Direction.field_11033) {
			return BOTTOM_SLOTS;
		} else {
			return side == Direction.field_11036 ? TOP_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.field_11033 && slot == 1) {
			Item item = stack.getItem();
			if (item != Items.field_8705 && item != Items.field_8550) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.inventory.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.inventory, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return Inventories.removeStack(this.inventory, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		ItemStack itemStack = this.inventory.get(slot);
		boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areTagsEqual(stack, itemStack);
		this.inventory.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		if (slot == 0 && !bl) {
			this.cookTimeTotal = this.getCookTime();
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 2) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			ItemStack itemStack = this.inventory.get(1);
			return canUseAsFuel(stack) || stack.getItem() == Items.field_8550 && itemStack.getItem() != Items.field_8550;
		}
	}

	@Override
	public void clear() {
		this.inventory.clear();
	}

	@Override
	public void setLastRecipe(@Nullable Recipe<?> recipe) {
		if (recipe != null) {
			Identifier identifier = recipe.getId();
			this.recipesUsed.addTo(identifier, 1);
		}
	}

	@Nullable
	@Override
	public Recipe<?> getLastRecipe() {
		return null;
	}

	@Override
	public void unlockLastRecipe(PlayerEntity player) {
	}

	public void dropExperience(PlayerEntity player) {
		List<Recipe<?>> list = this.method_27354(player.world, player.getPos());
		player.unlockRecipes(list);
		this.recipesUsed.clear();
	}

	public List<Recipe<?>> method_27354(World world, Vec3d vec3d) {
		List<Recipe<?>> list = Lists.<Recipe<?>>newArrayList();

		for (Entry<Identifier> entry : this.recipesUsed.object2IntEntrySet()) {
			world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
				dropExperience(world, vec3d, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
			});
		}

		return list;
	}

	private static void dropExperience(World world, Vec3d vec3d, int i, float f) {
		int j = MathHelper.floor((float)i * f);
		float g = MathHelper.fractionalPart((float)i * f);
		if (g != 0.0F && Math.random() < (double)g) {
			j++;
		}

		while (j > 0) {
			int k = ExperienceOrbEntity.roundToOrbSize(j);
			j -= k;
			world.spawnEntity(new ExperienceOrbEntity(world, vec3d.x, vec3d.y, vec3d.z, k));
		}
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack itemStack : this.inventory) {
			finder.addItem(itemStack);
		}
	}
}
