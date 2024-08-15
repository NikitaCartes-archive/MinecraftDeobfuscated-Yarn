package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
	protected static final int INPUT_SLOT_INDEX = 0;
	protected static final int FUEL_SLOT_INDEX = 1;
	protected static final int OUTPUT_SLOT_INDEX = 2;
	public static final int BURN_TIME_PROPERTY_INDEX = 0;
	private static final int[] TOP_SLOTS = new int[]{0};
	private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
	private static final int[] SIDE_SLOTS = new int[]{1};
	public static final int FUEL_TIME_PROPERTY_INDEX = 1;
	public static final int COOK_TIME_PROPERTY_INDEX = 2;
	public static final int COOK_TIME_TOTAL_PROPERTY_INDEX = 3;
	public static final int PROPERTY_COUNT = 4;
	public static final int DEFAULT_COOK_TIME = 200;
	public static final int field_31295 = 2;
	public static final int field_52634 = 0;
	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	int burnTime;
	int fuelTime = 0;
	int cookTime;
	int cookTimeTotal;
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
	private final RecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;

	protected AbstractFurnaceBlockEntity(
		BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType
	) {
		super(blockEntityType, pos, state);
		this.matchGetter = RecipeManager.createCachedMatchGetter(recipeType);
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory, registryLookup);
		this.burnTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.cookTimeTotal = nbt.getShort("CookTimeTotal");
		this.fuelTime = 0;
		NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");

		for (String string : nbtCompound.getKeys()) {
			this.recipesUsed.put(Identifier.of(string), nbtCompound.getInt(string));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putShort("BurnTime", (short)this.burnTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		Inventories.writeNbt(nbt, this.inventory, registryLookup);
		NbtCompound nbtCompound = new NbtCompound();
		this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
		nbt.put("RecipesUsed", nbtCompound);
	}

	public static void tick(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity) {
		boolean bl = blockEntity.isBurning();
		boolean bl2 = false;
		if (blockEntity.isBurning()) {
			blockEntity.burnTime--;
		}

		ItemStack itemStack = blockEntity.inventory.get(1);
		ItemStack itemStack2 = blockEntity.inventory.get(0);
		boolean bl3 = !itemStack2.isEmpty();
		boolean bl4 = !itemStack.isEmpty();
		if (blockEntity.fuelTime == 0) {
			blockEntity.fuelTime = blockEntity.getFuelTime(world.getFuelRegistry(), itemStack);
		}

		if (blockEntity.isBurning() || bl4 && bl3) {
			RecipeEntry<?> recipeEntry;
			if (bl3) {
				recipeEntry = (RecipeEntry<?>)blockEntity.matchGetter.getFirstMatch(new SingleStackRecipeInput(itemStack2), world).orElse(null);
			} else {
				recipeEntry = null;
			}

			int i = blockEntity.getMaxCountPerStack();
			if (!blockEntity.isBurning() && canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, blockEntity.inventory, i)) {
				blockEntity.burnTime = blockEntity.getFuelTime(world.getFuelRegistry(), itemStack);
				blockEntity.fuelTime = blockEntity.burnTime;
				if (blockEntity.isBurning()) {
					bl2 = true;
					if (bl4) {
						Item item = itemStack.getItem();
						itemStack.decrement(1);
						if (itemStack.isEmpty()) {
							Item item2 = item.getRecipeRemainder();
							blockEntity.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
						}
					}
				}
			}

			if (blockEntity.isBurning() && canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, blockEntity.inventory, i)) {
				blockEntity.cookTime++;
				if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
					blockEntity.cookTime = 0;
					blockEntity.cookTimeTotal = getCookTime(world, blockEntity);
					if (craftRecipe(world.getRegistryManager(), recipeEntry, blockEntity.inventory, i)) {
						blockEntity.setLastRecipe(recipeEntry);
					}

					bl2 = true;
				}
			} else {
				blockEntity.cookTime = 0;
			}
		} else if (!blockEntity.isBurning() && blockEntity.cookTime > 0) {
			blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
		}

		if (bl != blockEntity.isBurning()) {
			bl2 = true;
			state = state.with(AbstractFurnaceBlock.LIT, Boolean.valueOf(blockEntity.isBurning()));
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
		}

		if (bl2) {
			markDirty(world, pos, state);
		}
	}

	private static boolean canAcceptRecipeOutput(
		DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count
	) {
		if (!slots.get(0).isEmpty() && recipe != null) {
			ItemStack itemStack = recipe.value().getResult(registryManager);
			if (itemStack.isEmpty()) {
				return false;
			} else {
				ItemStack itemStack2 = slots.get(2);
				if (itemStack2.isEmpty()) {
					return true;
				} else if (!ItemStack.areItemsAndComponentsEqual(itemStack2, itemStack)) {
					return false;
				} else {
					return itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount() ? true : itemStack2.getCount() < itemStack.getMaxCount();
				}
			}
		} else {
			return false;
		}
	}

	private static boolean craftRecipe(DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count) {
		if (recipe != null && canAcceptRecipeOutput(registryManager, recipe, slots, count)) {
			ItemStack itemStack = slots.get(0);
			ItemStack itemStack2 = recipe.value().getResult(registryManager);
			ItemStack itemStack3 = slots.get(2);
			if (itemStack3.isEmpty()) {
				slots.set(2, itemStack2.copy());
			} else if (ItemStack.areItemsAndComponentsEqual(itemStack3, itemStack2)) {
				itemStack3.increment(1);
			}

			if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !slots.get(1).isEmpty() && slots.get(1).isOf(Items.BUCKET)) {
				slots.set(1, new ItemStack(Items.WATER_BUCKET));
			}

			itemStack.decrement(1);
			return true;
		} else {
			return false;
		}
	}

	protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack itemStack) {
		return fuelRegistry.getFuelTicks(itemStack);
	}

	private static int getCookTime(World world, AbstractFurnaceBlockEntity furnace) {
		SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(furnace.getStack(0));
		return (Integer)furnace.matchGetter
			.getFirstMatch(singleStackRecipeInput, world)
			.map(recipe -> ((AbstractCookingRecipe)recipe.value()).getCookingTime())
			.orElse(200);
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
		return dir == Direction.DOWN && slot == 1 ? stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET) : true;
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		ItemStack itemStack = this.inventory.get(slot);
		boolean bl = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, stack);
		this.inventory.set(slot, stack);
		stack.capCount(this.getMaxCount(stack));
		if (slot == 0 && !bl) {
			this.cookTimeTotal = getCookTime(this.world, this);
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 2) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			ItemStack itemStack = this.inventory.get(1);
			return this.world.getFuelRegistry().isFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
		}
	}

	@Override
	public void setLastRecipe(@Nullable RecipeEntry<?> recipe) {
		if (recipe != null) {
			Identifier identifier = recipe.id();
			this.recipesUsed.addTo(identifier, 1);
		}
	}

	@Nullable
	@Override
	public RecipeEntry<?> getLastRecipe() {
		return null;
	}

	@Override
	public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
	}

	public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
		List<RecipeEntry<?>> list = this.getRecipesUsedAndDropExperience(player.getServerWorld(), player.getPos());
		player.unlockRecipes(list);

		for (RecipeEntry<?> recipeEntry : list) {
			if (recipeEntry != null) {
				player.onRecipeCrafted(recipeEntry, this.inventory);
			}
		}

		this.recipesUsed.clear();
	}

	public List<RecipeEntry<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
		List<RecipeEntry<?>> list = Lists.<RecipeEntry<?>>newArrayList();

		for (Entry<Identifier> entry : this.recipesUsed.object2IntEntrySet()) {
			world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
				list.add(recipe);
				dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe.value()).getExperience());
			});
		}

		return list;
	}

	private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
		int i = MathHelper.floor((float)multiplier * experience);
		float f = MathHelper.fractionalPart((float)multiplier * experience);
		if (f != 0.0F && Math.random() < (double)f) {
			i++;
		}

		ExperienceOrbEntity.spawn(world, pos, i);
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder) {
		for (ItemStack itemStack : this.inventory) {
			finder.addInput(itemStack);
		}
	}
}
