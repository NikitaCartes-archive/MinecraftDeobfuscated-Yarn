package net.minecraft.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PotatoRefineryBlock;
import net.minecraft.component.type.LubricationComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.PotatoRefinementRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PotatoRefineryScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PotatoRefineryBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
	protected static final int field_50962 = 0;
	protected static final int field_50963 = 2;
	protected static final int field_50964 = 1;
	protected static final int field_50965 = 3;
	public static final int field_50966 = 0;
	private static final int[] TOP_SLOTS = new int[]{0};
	private static final int[] BOTTOM_SLOTS = new int[]{3, 1};
	private static final int[] SIDE_SLOTS = new int[]{1, 2};
	public static final int field_50967 = 1;
	public static final int field_50968 = 2;
	public static final int field_50969 = 3;
	public static final int field_50970 = 4;
	public static final int field_50971 = 200;
	public static final int field_50972 = 2;
	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
	int burnTime;
	int fuelTime;
	int cookTime;
	int cookTimeTotal;
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
				case 0:
					return PotatoRefineryBlockEntity.this.burnTime;
				case 1:
					return PotatoRefineryBlockEntity.this.fuelTime;
				case 2:
					return PotatoRefineryBlockEntity.this.cookTime;
				case 3:
					return PotatoRefineryBlockEntity.this.cookTimeTotal;
				default:
					return 0;
			}
		}

		@Override
		public void set(int index, int value) {
			switch (index) {
				case 0:
					PotatoRefineryBlockEntity.this.burnTime = value;
					break;
				case 1:
					PotatoRefineryBlockEntity.this.fuelTime = value;
					break;
				case 2:
					PotatoRefineryBlockEntity.this.cookTime = value;
					break;
				case 3:
					PotatoRefineryBlockEntity.this.cookTimeTotal = value;
			}
		}

		@Override
		public int size() {
			return 4;
		}
	};
	private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
	private float storedExperience;
	private final CachedMapper<PotatoRefineryBlockEntity.RecipeContext, PotatoRefineryBlockEntity.Recipe> recipeMapper;

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.potato_refinery");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new PotatoRefineryScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	public PotatoRefineryBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.POTATO_REFINERY, pos, state);
		RecipeManager.MatchGetter<Inventory, PotatoRefinementRecipe> matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.POTATO_REFINEMENT);
		this.recipeMapper = Util.cachedMapper(context -> {
			boolean bl = !context.inputStack.isEmpty() && !context.bottleInputStack.isEmpty();
			if (bl) {
				ItemStack itemStack = context.bottleInputStack;
				if (context.inputStack.isOf(Items.POTATO_OIL) && !itemStack.isEmpty()) {
					ItemStack itemStack2 = itemStack.copyWithCount(1);
					LubricationComponent.addLubricationLevel(itemStack2);
					return new PotatoRefineryBlockEntity.LubricationRecipe(itemStack.copyWithCount(1), itemStack2);
				}

				RecipeEntry<PotatoRefinementRecipe> recipeEntry = (RecipeEntry<PotatoRefinementRecipe>)matchGetter.getFirstMatch(this, context.level).orElse(null);
				if (recipeEntry != null) {
					return new PotatoRefineryBlockEntity.JsonRecipe(recipeEntry);
				}
			}

			return null;
		});
	}

	private PotatoRefineryBlockEntity.Recipe getRecipe(World world) {
		return this.recipeMapper.map(new PotatoRefineryBlockEntity.RecipeContext(world, this.inventory.get(0), this.inventory.get(2)));
	}

	public static Map<Item, Integer> createFuelTimeMap() {
		return FurnaceBlockEntity.createFuelTimeMap();
	}

	public float clearStoredExperience() {
		float f = this.storedExperience;
		this.storedExperience = 0.0F;
		return f;
	}

	private boolean isActive() {
		return this.burnTime > 0;
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory, registryLookup);
		this.burnTime = nbt.getShort("BurnTime");
		this.cookTime = nbt.getShort("CookTime");
		this.cookTimeTotal = nbt.getShort("CookTimeTotal");
		this.fuelTime = this.getFuelTime(this.inventory.get(1));
		NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");

		for (String string : nbtCompound.getKeys()) {
			this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
		}

		this.storedExperience = nbt.contains("StoredExperience") ? nbt.getFloat("StoredExperience") : 0.0F;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putShort("BurnTime", (short)this.burnTime);
		nbt.putShort("CookTime", (short)this.cookTime);
		nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		Inventories.writeNbt(nbt, this.inventory, registryLookup);
		NbtCompound nbtCompound = new NbtCompound();
		this.recipesUsed.forEach((recipe, count) -> nbtCompound.putInt(recipe.toString(), count));
		nbt.put("RecipesUsed", nbtCompound);
		nbt.putFloat("StoredExperience", this.storedExperience);
	}

	public static void tick(World world, BlockPos pos, BlockState state, PotatoRefineryBlockEntity blockEntity) {
		boolean bl = blockEntity.isActive();
		boolean bl2 = false;
		if (blockEntity.isActive()) {
			blockEntity.burnTime--;
		}

		ItemStack itemStack = blockEntity.inventory.get(1);
		boolean bl3 = blockEntity.hasItems();
		boolean bl4 = !itemStack.isEmpty();
		if (blockEntity.isActive() || bl4 && bl3) {
			PotatoRefineryBlockEntity.Recipe recipe = blockEntity.getRecipe(world);
			int i = blockEntity.getMaxCountPerStack();
			if (!blockEntity.isActive() && canCraft(world.getRegistryManager(), recipe, blockEntity.inventory, i)) {
				blockEntity.burnTime = blockEntity.getFuelTime(itemStack);
				blockEntity.fuelTime = blockEntity.burnTime;
				if (blockEntity.isActive()) {
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

			if (blockEntity.isActive() && canCraft(world.getRegistryManager(), recipe, blockEntity.inventory, i)) {
				blockEntity.cookTime++;
				if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
					blockEntity.cookTime = 0;
					blockEntity.cookTimeTotal = getRefinementTime(world, blockEntity);
					if (tryCraft(world.getRegistryManager(), recipe, blockEntity.inventory, i) && recipe instanceof PotatoRefineryBlockEntity.JsonRecipe jsonRecipe) {
						blockEntity.setLastRecipe(jsonRecipe.recipeHolder);
					}

					bl2 = true;
				}
			} else {
				blockEntity.cookTime = 0;
			}
		} else if (!blockEntity.isActive() && blockEntity.cookTime > 0) {
			blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
		}

		if (bl != blockEntity.isActive()) {
			bl2 = true;
			state = state.with(PotatoRefineryBlock.LIT, Boolean.valueOf(blockEntity.isActive()));
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
		}

		if (bl2) {
			markDirty(world, pos, state);
		}
	}

	private boolean hasItems() {
		return !this.inventory.get(0).isEmpty() && !this.inventory.get(2).isEmpty();
	}

	private static boolean canCraft(
		DynamicRegistryManager registryManager, @Nullable PotatoRefineryBlockEntity.Recipe recipe, DefaultedList<ItemStack> inventory, int i
	) {
		if (!inventory.get(0).isEmpty() && !inventory.get(2).isEmpty() && recipe != null) {
			ItemStack itemStack = recipe.craft(registryManager);
			if (itemStack.isEmpty()) {
				return false;
			} else {
				ItemStack itemStack2 = inventory.get(3);
				if (itemStack2.isEmpty()) {
					return true;
				} else if (!ItemStack.areItemsAndComponentsEqual(itemStack2, itemStack)) {
					return false;
				} else {
					return itemStack2.getCount() < i && itemStack2.getCount() < itemStack2.getMaxCount() ? true : itemStack2.getCount() < itemStack.getMaxCount();
				}
			}
		} else {
			return false;
		}
	}

	private static boolean tryCraft(
		DynamicRegistryManager registryManager, @Nullable PotatoRefineryBlockEntity.Recipe recipe, DefaultedList<ItemStack> inventory, int i
	) {
		if (recipe != null && canCraft(registryManager, recipe, inventory, i)) {
			ItemStack itemStack = inventory.get(0);
			ItemStack itemStack2 = inventory.get(2);
			ItemStack itemStack3 = recipe.craft(registryManager);
			ItemStack itemStack4 = inventory.get(3);
			if (itemStack4.isEmpty()) {
				inventory.set(3, itemStack3.copy());
			} else if (ItemStack.areItemsAndComponentsEqual(itemStack4, itemStack3)) {
				itemStack4.increment(1);
			}

			itemStack.decrement(1);
			itemStack2.decrement(1);
			return true;
		} else {
			return false;
		}
	}

	protected int getFuelTime(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();
			return (Integer)createFuelTimeMap().getOrDefault(item, 0);
		}
	}

	private static int getRefinementTime(World world, PotatoRefineryBlockEntity blockEntity) {
		PotatoRefineryBlockEntity.Recipe recipe = blockEntity.getRecipe(world);
		return recipe != null ? recipe.getRefinementTIme() : 20;
	}

	public static boolean isFuel(ItemStack stack) {
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
		boolean bl = stack.isEmpty() || !ItemStack.areItemsAndComponentsEqual(itemStack, stack);
		this.inventory.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}

		if ((slot == 0 || slot == 2) && bl) {
			this.cookTimeTotal = getRefinementTime(this.world, this);
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 3) {
			return false;
		} else if (slot != 1) {
			return true;
		} else {
			ItemStack itemStack = this.inventory.get(1);
			return isFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
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

	@Override
	public void provideRecipeInputs(RecipeMatcher finder) {
		for (ItemStack itemStack : this.inventory) {
			finder.addInput(itemStack);
		}
	}

	static record JsonRecipe(RecipeEntry<PotatoRefinementRecipe> recipeHolder) implements PotatoRefineryBlockEntity.Recipe {

		@Override
		public ItemStack craft(DynamicRegistryManager registryManager) {
			return this.recipeHolder.value().getResult(registryManager);
		}

		@Override
		public int getRefinementTIme() {
			return this.recipeHolder.value().getRefinementTime();
		}
	}

	static record LubricationRecipe(ItemStack itemStack, ItemStack result) implements PotatoRefineryBlockEntity.Recipe {
		@Override
		public ItemStack craft(DynamicRegistryManager registryManager) {
			return this.result;
		}

		@Override
		public int getRefinementTIme() {
			return 20;
		}
	}

	interface Recipe {
		ItemStack craft(DynamicRegistryManager registryManager);

		int getRefinementTIme();
	}

	static record RecipeContext(World level, ItemStack inputStack, ItemStack bottleInputStack) {
	}
}
