package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1662;
import net.minecraft.class_1732;
import net.minecraft.class_1737;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.container.Container;
import net.minecraft.container.FurnaceContainer;
import net.minecraft.container.FurnaceFuelSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.smelting.SmeltingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.InventoryUtil;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, class_1732, class_1737, Tickable {
	private static final int[] field_11987 = new int[]{0};
	private static final int[] field_11982 = new int[]{2, 1};
	private static final int[] field_11983 = new int[]{1};
	private DefaultedList<ItemStack> inventory = DefaultedList.create(3, ItemStack.EMPTY);
	private int burnTime;
	private int fuelTime;
	private int cookTime;
	private int cookTimeTotal;
	private TextComponent customName;
	private final Map<Identifier, Integer> recipesUsed = Maps.<Identifier, Integer>newHashMap();

	private static void method_11194(Map<Item, Integer> map, Tag<Item> tag, int i) {
		for (Item item : tag.values()) {
			map.put(item, i);
		}
	}

	private static void method_11202(Map<Item, Integer> map, ItemContainer itemContainer, int i) {
		map.put(itemContainer.getItem(), i);
	}

	public static Map<Item, Integer> method_11196() {
		Map<Item, Integer> map = Maps.<Item, Integer>newLinkedHashMap();
		method_11202(map, Items.field_8187, 20000);
		method_11202(map, Blocks.field_10381, 16000);
		method_11202(map, Items.field_8894, 2400);
		method_11202(map, Items.field_8713, 1600);
		method_11202(map, Items.field_8665, 1600);
		method_11194(map, ItemTags.field_15539, 300);
		method_11194(map, ItemTags.field_15537, 300);
		method_11194(map, ItemTags.field_15557, 300);
		method_11194(map, ItemTags.field_15534, 150);
		method_11194(map, ItemTags.field_15550, 300);
		method_11194(map, ItemTags.field_15540, 300);
		method_11202(map, Blocks.field_10620, 300);
		method_11202(map, Blocks.field_10299, 300);
		method_11202(map, Blocks.field_10020, 300);
		method_11202(map, Blocks.field_10319, 300);
		method_11202(map, Blocks.field_10132, 300);
		method_11202(map, Blocks.field_10144, 300);
		method_11202(map, Blocks.field_10188, 300);
		method_11202(map, Blocks.field_10513, 300);
		method_11202(map, Blocks.field_10291, 300);
		method_11202(map, Blocks.field_10041, 300);
		method_11202(map, Blocks.field_10196, 300);
		method_11202(map, Blocks.field_10457, 300);
		method_11202(map, Blocks.field_10179, 300);
		method_11202(map, Blocks.field_10504, 300);
		method_11202(map, Blocks.field_10223, 300);
		method_11202(map, Blocks.field_10034, 300);
		method_11202(map, Blocks.field_10380, 300);
		method_11202(map, Blocks.field_9980, 300);
		method_11202(map, Blocks.field_10429, 300);
		method_11194(map, ItemTags.field_15556, 300);
		method_11202(map, Items.field_8102, 300);
		method_11202(map, Items.field_8378, 300);
		method_11202(map, Blocks.field_9983, 300);
		method_11194(map, ItemTags.field_15533, 200);
		method_11202(map, Items.field_8876, 200);
		method_11202(map, Items.field_8091, 200);
		method_11202(map, Items.field_8167, 200);
		method_11202(map, Items.field_8406, 200);
		method_11202(map, Items.field_8647, 200);
		method_11194(map, ItemTags.field_15552, 200);
		method_11194(map, ItemTags.field_15536, 200);
		method_11194(map, ItemTags.field_15544, 100);
		method_11194(map, ItemTags.field_15555, 100);
		method_11202(map, Items.field_8600, 100);
		method_11194(map, ItemTags.field_15528, 100);
		method_11202(map, Items.field_8428, 100);
		method_11194(map, ItemTags.field_15542, 67);
		method_11202(map, Blocks.field_10342, 4001);
		method_11202(map, Items.field_8399, 300);
		method_11202(map, Blocks.field_10211, 50);
		method_11202(map, Blocks.field_10428, 100);
		method_11202(map, Blocks.field_16492, 50);
		return map;
	}

	protected FurnaceBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public FurnaceBlockEntity() {
		this(BlockEntityType.FURNACE);
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
		return InventoryUtil.splitStack(this.inventory, i, j);
	}

	@Override
	public ItemStack removeInvStack(int i) {
		return InventoryUtil.removeStack(this.inventory, i);
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
			this.cookTimeTotal = this.getRecipeCookTime();
			this.cookTime = 0;
			this.markDirty();
		}
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.furnace"));
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null;
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		InventoryUtil.deserialize(compoundTag, this.inventory);
		this.burnTime = compoundTag.getShort("BurnTime");
		this.cookTime = compoundTag.getShort("CookTime");
		this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
		this.fuelTime = method_11200(this.inventory.get(1));
		int i = compoundTag.getShort("RecipesUsedSize");

		for (int j = 0; j < i; j++) {
			Identifier identifier = new Identifier(compoundTag.getString("RecipeLocation" + j));
			int k = compoundTag.getInt("RecipeAmount" + j);
			this.recipesUsed.put(identifier, k);
		}

		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putShort("BurnTime", (short)this.burnTime);
		compoundTag.putShort("CookTime", (short)this.cookTime);
		compoundTag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
		InventoryUtil.serialize(compoundTag, this.inventory);
		compoundTag.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
		int i = 0;

		for (Entry<Identifier, Integer> entry : this.recipesUsed.entrySet()) {
			compoundTag.putString("RecipeLocation" + i, ((Identifier)entry.getKey()).toString());
			compoundTag.putInt("RecipeAmount" + i, (Integer)entry.getValue());
			i++;
		}

		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	private boolean isBurning() {
		return this.burnTime > 0;
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_11199(Inventory inventory) {
		return inventory.getInvProperty(0) > 0;
	}

	@Override
	public void tick() {
		boolean bl = this.isBurning();
		boolean bl2 = false;
		if (this.isBurning()) {
			this.burnTime--;
		}

		if (!this.world.isRemote) {
			ItemStack itemStack = this.inventory.get(1);
			if (this.isBurning() || !itemStack.isEmpty() && !this.inventory.get(0).isEmpty()) {
				Recipe recipe = this.world.getRecipeManager().get(this, this.world);
				if (!this.isBurning() && this.method_11192(recipe)) {
					this.burnTime = method_11200(itemStack);
					this.fuelTime = this.burnTime;
					if (this.isBurning()) {
						bl2 = true;
						if (!itemStack.isEmpty()) {
							Item item = itemStack.getItem();
							itemStack.subtractAmount(1);
							if (itemStack.isEmpty()) {
								Item item2 = item.getContainerItem();
								this.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
							}
						}
					}
				}

				if (this.isBurning() && this.method_11192(recipe)) {
					this.cookTime++;
					if (this.cookTime == this.cookTimeTotal) {
						this.cookTime = 0;
						this.cookTimeTotal = this.getRecipeCookTime();
						this.method_11203(recipe);
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
				this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(FurnaceBlock.field_11105, Boolean.valueOf(this.isBurning())), 3);
			}
		}

		if (bl2) {
			this.markDirty();
		}
	}

	private int getRecipeCookTime() {
		SmeltingRecipe smeltingRecipe = (SmeltingRecipe)this.world.getRecipeManager().get(this, this.world);
		return smeltingRecipe != null ? smeltingRecipe.getCookTime() : 200;
	}

	private boolean method_11192(@Nullable Recipe recipe) {
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

	private void method_11203(@Nullable Recipe recipe) {
		if (recipe != null && this.method_11192(recipe)) {
			ItemStack itemStack = this.inventory.get(0);
			ItemStack itemStack2 = recipe.getOutput();
			ItemStack itemStack3 = this.inventory.get(2);
			if (itemStack3.isEmpty()) {
				this.inventory.set(2, itemStack2.copy());
			} else if (itemStack3.getItem() == itemStack2.getItem()) {
				itemStack3.addAmount(1);
			}

			if (!this.world.isRemote) {
				this.method_7665(this.world, null, recipe);
			}

			if (itemStack.getItem() == Blocks.field_10562.getItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.field_8550) {
				this.inventory.set(1, new ItemStack(Items.field_8705));
			}

			itemStack.subtractAmount(1);
		}
	}

	private static int method_11200(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return 0;
		} else {
			Item item = itemStack.getItem();
			return (Integer)method_11196().getOrDefault(item, 0);
		}
	}

	public static boolean canUseAsFuel(ItemStack itemStack) {
		return method_11196().containsKey(itemStack.getItem());
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		if (i == 2) {
			return false;
		} else if (i != 1) {
			return true;
		} else {
			ItemStack itemStack2 = this.inventory.get(1);
			return canUseAsFuel(itemStack) || FurnaceFuelSlot.isBucket(itemStack) && itemStack2.getItem() != Items.field_8550;
		}
	}

	@Override
	public int[] method_5494(Direction direction) {
		if (direction == Direction.DOWN) {
			return field_11982;
		} else {
			return direction == Direction.UP ? field_11987 : field_11983;
		}
	}

	@Override
	public boolean method_5492(int i, ItemStack itemStack, @Nullable Direction direction) {
		return this.isValidInvStack(i, itemStack);
	}

	@Override
	public boolean method_5493(int i, ItemStack itemStack, Direction direction) {
		if (direction == Direction.DOWN && i == 1) {
			Item item = itemStack.getItem();
			if (item != Items.field_8705 && item != Items.field_8550) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getContainerId() {
		return "minecraft:furnace";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new FurnaceContainer(playerInventory, this);
	}

	@Override
	public int getInvProperty(int i) {
		switch (i) {
			case 0:
				return this.burnTime;
			case 1:
				return this.fuelTime;
			case 2:
				return this.cookTime;
			case 3:
				return this.cookTimeTotal;
			default:
				return 0;
		}
	}

	@Override
	public void setInvProperty(int i, int j) {
		switch (i) {
			case 0:
				this.burnTime = j;
				break;
			case 1:
				this.fuelTime = j;
				break;
			case 2:
				this.cookTime = j;
				break;
			case 3:
				this.cookTimeTotal = j;
		}
	}

	@Override
	public int getInvPropertyCount() {
		return 4;
	}

	@Override
	public void clearInv() {
		this.inventory.clear();
	}

	@Override
	public void method_7683(class_1662 arg) {
		for (ItemStack itemStack : this.inventory) {
			arg.method_7400(itemStack);
		}
	}

	@Override
	public void method_7662(Recipe recipe) {
		if (this.recipesUsed.containsKey(recipe.getId())) {
			this.recipesUsed.put(recipe.getId(), (Integer)this.recipesUsed.get(recipe.getId()) + 1);
		} else {
			this.recipesUsed.put(recipe.getId(), 1);
		}
	}

	@Nullable
	@Override
	public Recipe method_7663() {
		return null;
	}

	public Map<Identifier, Integer> method_11198() {
		return this.recipesUsed;
	}

	@Override
	public boolean method_7665(World world, ServerPlayerEntity serverPlayerEntity, @Nullable Recipe recipe) {
		if (recipe != null) {
			this.method_7662(recipe);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_7664(PlayerEntity playerEntity) {
		if (!this.world.getGameRules().getBoolean("doLimitedCrafting")) {
			List<Recipe> list = Lists.<Recipe>newArrayList();

			for (Identifier identifier : this.recipesUsed.keySet()) {
				Recipe recipe = playerEntity.world.getRecipeManager().get(identifier);
				if (recipe != null) {
					list.add(recipe);
				}
			}

			playerEntity.method_7254(list);
		}

		this.recipesUsed.clear();
	}
}
