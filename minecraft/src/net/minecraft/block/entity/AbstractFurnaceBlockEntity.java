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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
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
		addFuel(map, Items.LAVA_BUCKET, 20000);
		addFuel(map, Blocks.COAL_BLOCK, 16000);
		addFuel(map, Items.BLAZE_ROD, 2400);
		addFuel(map, Items.COAL, 1600);
		addFuel(map, Items.CHARCOAL, 1600);
		addFuel(map, ItemTags.LOGS, 300);
		addFuel(map, ItemTags.PLANKS, 300);
		addFuel(map, ItemTags.WOODEN_STAIRS, 300);
		addFuel(map, ItemTags.WOODEN_SLABS, 150);
		addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
		addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
		addFuel(map, Blocks.OAK_FENCE, 300);
		addFuel(map, Blocks.BIRCH_FENCE, 300);
		addFuel(map, Blocks.SPRUCE_FENCE, 300);
		addFuel(map, Blocks.JUNGLE_FENCE, 300);
		addFuel(map, Blocks.DARK_OAK_FENCE, 300);
		addFuel(map, Blocks.ACACIA_FENCE, 300);
		addFuel(map, Blocks.OAK_FENCE_GATE, 300);
		addFuel(map, Blocks.BIRCH_FENCE_GATE, 300);
		addFuel(map, Blocks.SPRUCE_FENCE_GATE, 300);
		addFuel(map, Blocks.JUNGLE_FENCE_GATE, 300);
		addFuel(map, Blocks.DARK_OAK_FENCE_GATE, 300);
		addFuel(map, Blocks.ACACIA_FENCE_GATE, 300);
		addFuel(map, Blocks.NOTE_BLOCK, 300);
		addFuel(map, Blocks.BOOKSHELF, 300);
		addFuel(map, Blocks.LECTERN, 300);
		addFuel(map, Blocks.JUKEBOX, 300);
		addFuel(map, Blocks.CHEST, 300);
		addFuel(map, Blocks.TRAPPED_CHEST, 300);
		addFuel(map, Blocks.CRAFTING_TABLE, 300);
		addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
		addFuel(map, ItemTags.BANNERS, 300);
		addFuel(map, Items.BOW, 300);
		addFuel(map, Items.FISHING_ROD, 300);
		addFuel(map, Blocks.LADDER, 300);
		addFuel(map, ItemTags.SIGNS, 200);
		addFuel(map, Items.WOODEN_SHOVEL, 200);
		addFuel(map, Items.WOODEN_SWORD, 200);
		addFuel(map, Items.WOODEN_HOE, 200);
		addFuel(map, Items.WOODEN_AXE, 200);
		addFuel(map, Items.WOODEN_PICKAXE, 200);
		addFuel(map, ItemTags.WOODEN_DOORS, 200);
		addFuel(map, ItemTags.BOATS, 1200);
		addFuel(map, ItemTags.WOOL, 100);
		addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
		addFuel(map, Items.STICK, 100);
		addFuel(map, ItemTags.SAPLINGS, 100);
		addFuel(map, Items.BOWL, 100);
		addFuel(map, ItemTags.CARPETS, 67);
		addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
		addFuel(map, Items.CROSSBOW, 300);
		addFuel(map, Blocks.BAMBOO, 50);
		addFuel(map, Blocks.DEAD_BUSH, 100);
		addFuel(map, Blocks.SCAFFOLDING, 400);
		addFuel(map, Blocks.LOOM, 300);
		addFuel(map, Blocks.BARREL, 300);
		addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
		addFuel(map, Blocks.FLETCHING_TABLE, 300);
		addFuel(map, Blocks.SMITHING_TABLE, 300);
		addFuel(map, Blocks.COMPOSTER, 300);
		return map;
	}

	/**
	 * Returns whether the provided {@code item} is in the {@link
	 * net.minecraft.tag.ItemTags#NON_FLAMMABLE_WOOD non_flammable_wood} tag.
	 */
	private static boolean isNonFlammableWood(Item item) {
		return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
	}

	private static void addFuel(Map<Item, Integer> fuelTimes, Tag<Item> tag, int fuelTime) {
		for (Item item : tag.values()) {
			if (!isNonFlammableWood(item)) {
				fuelTimes.put(item, fuelTime);
			}
		}
	}

	private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
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
			fuelTimes.put(item2, fuelTime);
		}
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(tag, this.inventory);
		this.burnTime = tag.getShort("BurnTime");
		this.cookTime = tag.getShort("CookTime");
		this.cookTimeTotal = tag.getShort("CookTimeTotal");
		this.fuelTime = this.getFuelTime(this.inventory.get(1));
		NbtCompound nbtCompound = tag.getCompound("RecipesUsed");

		for (String string : nbtCompound.getKeys()) {
			this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
		}
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putShort("BurnTime", (short)this.burnTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		Inventories.writeNbt(nbt, this.inventory);
		NbtCompound nbtCompound = new NbtCompound();
		this.recipesUsed.forEach((identifier, integer) -> nbtCompound.putInt(identifier.toString(), integer));
		nbt.put("RecipesUsed", nbtCompound);
		return nbt;
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

			if (itemStack.getItem() == Blocks.WET_SPONGE.asItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.BUCKET) {
				this.inventory.set(1, new ItemStack(Items.WATER_BUCKET));
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
		if (side == Direction.DOWN) {
			return BOTTOM_SLOTS;
		} else {
			return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
		}
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		if (dir == Direction.DOWN && slot == 1) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
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
			return canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
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
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : this.inventory) {
			finder.addInput(itemStack);
		}
	}
}
