package net.minecraft.block.entity;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
	private static final int field_31330 = 2;
	private static final int field_31331 = 4;
	private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private final int[] cookingTimes = new int[4];
	private final int[] cookingTotalTimes = new int[4];
	private final RecipeManager.MatchGetter<Inventory, CampfireCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

	public CampfireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CAMPFIRE, pos, state);
	}

	public static void litServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
		boolean bl = false;

		for (int i = 0; i < campfire.itemsBeingCooked.size(); i++) {
			ItemStack itemStack = campfire.itemsBeingCooked.get(i);
			if (!itemStack.isEmpty()) {
				bl = true;
				campfire.cookingTimes[i]++;
				if (campfire.cookingTimes[i] >= campfire.cookingTotalTimes[i]) {
					Inventory inventory = new SimpleInventory(itemStack);
					ItemStack itemStack2 = (ItemStack)campfire.matchGetter.getFirstMatch(inventory, world).map(recipe -> recipe.craft(inventory)).orElse(itemStack);
					ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack2);
					campfire.itemsBeingCooked.set(i, ItemStack.EMPTY);
					world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
				}
			}
		}

		if (bl) {
			markDirty(world, pos, state);
		}
	}

	public static void unlitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
		boolean bl = false;

		for (int i = 0; i < campfire.itemsBeingCooked.size(); i++) {
			if (campfire.cookingTimes[i] > 0) {
				bl = true;
				campfire.cookingTimes[i] = MathHelper.clamp(campfire.cookingTimes[i] - 2, 0, campfire.cookingTotalTimes[i]);
			}
		}

		if (bl) {
			markDirty(world, pos, state);
		}
	}

	public static void clientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire) {
		Random random = world.random;
		if (random.nextFloat() < 0.11F) {
			for (int i = 0; i < random.nextInt(2) + 2; i++) {
				CampfireBlock.spawnSmokeParticle(world, pos, (Boolean)state.get(CampfireBlock.SIGNAL_FIRE), false);
			}
		}

		int i = ((Direction)state.get(CampfireBlock.FACING)).getHorizontal();

		for (int j = 0; j < campfire.itemsBeingCooked.size(); j++) {
			if (!campfire.itemsBeingCooked.get(j).isEmpty() && random.nextFloat() < 0.2F) {
				Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
				float f = 0.3125F;
				double d = (double)pos.getX()
					+ 0.5
					- (double)((float)direction.getOffsetX() * 0.3125F)
					+ (double)((float)direction.rotateYClockwise().getOffsetX() * 0.3125F);
				double e = (double)pos.getY() + 0.5;
				double g = (double)pos.getZ()
					+ 0.5
					- (double)((float)direction.getOffsetZ() * 0.3125F)
					+ (double)((float)direction.rotateYClockwise().getOffsetZ() * 0.3125F);

				for (int k = 0; k < 4; k++) {
					world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
				}
			}
		}
	}

	public DefaultedList<ItemStack> getItemsBeingCooked() {
		return this.itemsBeingCooked;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.itemsBeingCooked.clear();
		Inventories.readNbt(nbt, this.itemsBeingCooked);
		if (nbt.contains("CookingTimes", NbtElement.INT_ARRAY_TYPE)) {
			int[] is = nbt.getIntArray("CookingTimes");
			System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
		}

		if (nbt.contains("CookingTotalTimes", NbtElement.INT_ARRAY_TYPE)) {
			int[] is = nbt.getIntArray("CookingTotalTimes");
			System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, this.itemsBeingCooked, true);
		nbt.putIntArray("CookingTimes", this.cookingTimes);
		nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		Inventories.writeNbt(nbtCompound, this.itemsBeingCooked, true);
		return nbtCompound;
	}

	public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack item) {
		return this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)
			? Optional.empty()
			: this.matchGetter.getFirstMatch(new SimpleInventory(item), this.world);
	}

	public boolean addItem(ItemStack item, int cookTime) {
		for (int i = 0; i < this.itemsBeingCooked.size(); i++) {
			ItemStack itemStack = this.itemsBeingCooked.get(i);
			if (itemStack.isEmpty()) {
				this.cookingTotalTimes[i] = cookTime;
				this.cookingTimes[i] = 0;
				this.itemsBeingCooked.set(i, item.split(1));
				this.updateListeners();
				return true;
			}
		}

		return false;
	}

	private void updateListeners() {
		this.markDirty();
		this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
	}

	@Override
	public void clear() {
		this.itemsBeingCooked.clear();
	}

	public void spawnItemsBeingCooked() {
		if (this.world != null) {
			this.updateListeners();
		}
	}
}
