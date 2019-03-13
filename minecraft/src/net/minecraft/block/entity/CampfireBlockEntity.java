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
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.sortme.ItemScatterer;
import net.minecraft.util.Clearable;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireBlockEntity extends BlockEntity implements Clearable, Tickable {
	private final DefaultedList<ItemStack> field_17383 = DefaultedList.create(4, ItemStack.EMPTY);
	private final int[] cookingTimes = new int[4];
	private final int[] cookingTotalTimes = new int[4];

	public CampfireBlockEntity() {
		super(BlockEntityType.CAMPFIRE);
	}

	@Override
	public void tick() {
		boolean bl = (Boolean)this.method_11010().method_11654(CampfireBlock.field_17352);
		boolean bl2 = this.world.isClient;
		if (bl2) {
			if (bl) {
				this.spawnSmokeParticles();
			}
		} else {
			if (bl) {
				this.updateItemsBeingCooked();
			} else {
				for (int i = 0; i < this.field_17383.size(); i++) {
					if (this.cookingTimes[i] > 0) {
						this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
					}
				}
			}
		}
	}

	private void updateItemsBeingCooked() {
		for (int i = 0; i < this.field_17383.size(); i++) {
			ItemStack itemStack = this.field_17383.get(i);
			if (!itemStack.isEmpty()) {
				this.cookingTimes[i]++;
				if (this.cookingTimes[i] >= this.cookingTotalTimes[i]) {
					Inventory inventory = new BasicInventory(itemStack);
					ItemStack itemStack2 = (ItemStack)this.world
						.getRecipeManager()
						.method_8132(RecipeType.CAMPFIRE_COOKING, inventory, this.world)
						.map(campfireCookingRecipe -> campfireCookingRecipe.craft(inventory))
						.orElse(itemStack);
					BlockPos blockPos = this.method_11016();
					ItemScatterer.method_5449(this.world, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack2);
					this.field_17383.set(i, ItemStack.EMPTY);
					this.updateListeners();
				}
			}
		}
	}

	private void spawnSmokeParticles() {
		World world = this.getWorld();
		if (world != null) {
			BlockPos blockPos = this.method_11016();
			Random random = world.random;
			if (random.nextFloat() < 0.11F) {
				for (int i = 0; i < random.nextInt(2) + 2; i++) {
					CampfireBlock.method_17455(world, blockPos, (Boolean)this.method_11010().method_11654(CampfireBlock.field_17353), false);
				}
			}

			int i = ((Direction)this.method_11010().method_11654(CampfireBlock.field_17564)).getHorizontal();

			for (int j = 0; j < this.field_17383.size(); j++) {
				if (!this.field_17383.get(j).isEmpty() && random.nextFloat() < 0.2F) {
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
						world.method_8406(ParticleTypes.field_11251, d, e, g, 0.0, 5.0E-4, 0.0);
					}
				}
			}
		}
	}

	public DefaultedList<ItemStack> method_17505() {
		return this.field_17383;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_17383.clear();
		Inventories.method_5429(compoundTag, this.field_17383);
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
	public CompoundTag method_11007(CompoundTag compoundTag) {
		this.method_17507(compoundTag);
		compoundTag.putIntArray("CookingTimes", this.cookingTimes);
		compoundTag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
		return compoundTag;
	}

	private CompoundTag method_17507(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		Inventories.method_5427(compoundTag, this.field_17383, true);
		return compoundTag;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 13, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_17507(new CompoundTag());
	}

	public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack itemStack) {
		return this.field_17383.stream().noneMatch(ItemStack::isEmpty)
			? Optional.empty()
			: this.world.getRecipeManager().method_8132(RecipeType.CAMPFIRE_COOKING, new BasicInventory(itemStack), this.world);
	}

	public boolean addItem(ItemStack itemStack, int i) {
		for (int j = 0; j < this.field_17383.size(); j++) {
			ItemStack itemStack2 = this.field_17383.get(j);
			if (itemStack2.isEmpty()) {
				this.cookingTotalTimes[j] = i;
				this.cookingTimes[j] = 0;
				this.field_17383.set(j, itemStack.split(1));
				this.updateListeners();
				return true;
			}
		}

		return false;
	}

	private void updateListeners() {
		this.markDirty();
		this.getWorld().method_8413(this.method_11016(), this.method_11010(), this.method_11010(), 3);
	}

	@Override
	public void clear() {
		this.field_17383.clear();
	}

	public void spawnItemsBeingCooked() {
		if (!this.getWorld().isClient) {
			ItemScatterer.method_17349(this.getWorld(), this.method_11016(), this.method_17505());
		}

		this.updateListeners();
	}
}
