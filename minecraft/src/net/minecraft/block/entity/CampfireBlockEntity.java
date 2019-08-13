package net.minecraft.block.entity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireBlockEntity extends BlockEntity implements Clearable, Tickable {
	private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private final int[] cookingTimes = new int[4];
	private final int[] cookingTotalTimes = new int[4];

	public CampfireBlockEntity() {
		super(BlockEntityType.field_17380);
	}

	@Override
	public void tick() {
		boolean bl = (Boolean)this.getCachedState().get(CampfireBlock.LIT);
		boolean bl2 = this.world.isClient;
		if (bl2) {
			if (bl) {
				this.spawnSmokeParticles();
			}
		} else {
			if (bl) {
				this.updateItemsBeingCooked();
			} else {
				for (int i = 0; i < this.itemsBeingCooked.size(); i++) {
					if (this.cookingTimes[i] > 0) {
						this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
					}
				}
			}
		}
	}

	private void updateItemsBeingCooked() {
		for (int i = 0; i < this.itemsBeingCooked.size(); i++) {
			ItemStack itemStack = this.itemsBeingCooked.get(i);
			if (!itemStack.isEmpty()) {
				this.cookingTimes[i]++;
				if (this.cookingTimes[i] >= this.cookingTotalTimes[i]) {
					Inventory inventory = new BasicInventory(itemStack);
					ItemStack itemStack2 = (ItemStack)this.world
						.getRecipeManager()
						.getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory, this.world)
						.map(campfireCookingRecipe -> campfireCookingRecipe.craft(inventory))
						.orElse(itemStack);
					BlockPos blockPos = this.getPos();
					ItemScatterer.spawn(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
					this.itemsBeingCooked.set(i, ItemStack.EMPTY);
					this.updateListeners();
				}
			}
		}
	}

	private void spawnSmokeParticles() {
		World world = this.getWorld();
		if (world != null) {
			BlockPos blockPos = this.getPos();
			Random random = world.random;
			if (random.nextFloat() < 0.11F) {
				for (int i = 0; i < random.nextInt(2) + 2; i++) {
					CampfireBlock.spawnSmokeParticle(world, blockPos, (Boolean)this.getCachedState().get(CampfireBlock.SIGNAL_FIRE), false);
				}
			}

			int i = ((Direction)this.getCachedState().get(CampfireBlock.FACING)).getHorizontal();

			for (int j = 0; j < this.itemsBeingCooked.size(); j++) {
				if (!this.itemsBeingCooked.get(j).isEmpty() && random.nextFloat() < 0.2F) {
					Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
					float f = 0.3125F;
					double d = (double)blockPos.getX()
						+ 0.5
						- (double)((float)direction.getOffsetX() * 0.3125F)
						+ (double)((float)direction.rotateYClockwise().getOffsetX() * 0.3125F);
					double e = (double)blockPos.getY() + 0.5;
					double g = (double)blockPos.getZ()
						+ 0.5
						- (double)((float)direction.getOffsetZ() * 0.3125F)
						+ (double)((float)direction.rotateYClockwise().getOffsetZ() * 0.3125F);

					for (int k = 0; k < 4; k++) {
						world.addParticle(ParticleTypes.field_11251, d, e, g, 0.0, 5.0E-4, 0.0);
					}
				}
			}
		}
	}

	public DefaultedList<ItemStack> getItemsBeingCooked() {
		return this.itemsBeingCooked;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.itemsBeingCooked.clear();
		Inventories.fromTag(compoundTag, this.itemsBeingCooked);
		if (compoundTag.containsKey("CookingTimes", 11)) {
			int[] is = compoundTag.getIntArray("CookingTimes");
			System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
		}

		if (compoundTag.containsKey("CookingTotalTimes", 11)) {
			int[] is = compoundTag.getIntArray("CookingTotalTimes");
			System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		this.saveInitialChunkData(compoundTag);
		compoundTag.putIntArray("CookingTimes", this.cookingTimes);
		compoundTag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
		return compoundTag;
	}

	private CompoundTag saveInitialChunkData(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		Inventories.toTag(compoundTag, this.itemsBeingCooked, true);
		return compoundTag;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 13, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.saveInitialChunkData(new CompoundTag());
	}

	public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack itemStack) {
		return this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)
			? Optional.empty()
			: this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new BasicInventory(itemStack), this.world);
	}

	public boolean addItem(ItemStack itemStack, int i) {
		for (int j = 0; j < this.itemsBeingCooked.size(); j++) {
			ItemStack itemStack2 = this.itemsBeingCooked.get(j);
			if (itemStack2.isEmpty()) {
				this.cookingTotalTimes[j] = i;
				this.cookingTimes[j] = 0;
				this.itemsBeingCooked.set(j, itemStack.split(1));
				this.updateListeners();
				return true;
			}
		}

		return false;
	}

	private void updateListeners() {
		this.markDirty();
		this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
	}

	@Override
	public void clear() {
		this.itemsBeingCooked.clear();
	}

	public void spawnItemsBeingCooked() {
		if (!this.getWorld().isClient) {
			ItemScatterer.spawn(this.getWorld(), this.getPos(), this.getItemsBeingCooked());
		}

		this.updateListeners();
	}
}
