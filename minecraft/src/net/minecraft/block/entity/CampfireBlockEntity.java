package net.minecraft.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CampfireBlockEntity extends BlockEntity implements Clearable {
	private static final int field_31330 = 2;
	private static final int field_31331 = 4;
	private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private final int[] cookingTimes = new int[4];
	private final int[] cookingTotalTimes = new int[4];

	public CampfireBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.CAMPFIRE, pos, state);
	}

	public static void litServerTick(
		ServerWorld world,
		BlockPos pos,
		BlockState state,
		CampfireBlockEntity blockEntity,
		ServerRecipeManager.MatchGetter<SingleStackRecipeInput, CampfireCookingRecipe> recipeMatchGetter
	) {
		boolean bl = false;

		for (int i = 0; i < blockEntity.itemsBeingCooked.size(); i++) {
			ItemStack itemStack = blockEntity.itemsBeingCooked.get(i);
			if (!itemStack.isEmpty()) {
				bl = true;
				blockEntity.cookingTimes[i]++;
				if (blockEntity.cookingTimes[i] >= blockEntity.cookingTotalTimes[i]) {
					SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(itemStack);
					ItemStack itemStack2 = (ItemStack)recipeMatchGetter.getFirstMatch(singleStackRecipeInput, world)
						.map(recipe -> ((CampfireCookingRecipe)recipe.value()).craft(singleStackRecipeInput, world.getRegistryManager()))
						.orElse(itemStack);
					if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
						ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack2);
						blockEntity.itemsBeingCooked.set(i, ItemStack.EMPTY);
						world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
						world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
					}
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
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		this.itemsBeingCooked.clear();
		Inventories.readNbt(nbt, this.itemsBeingCooked, registries);
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
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		Inventories.writeNbt(nbt, this.itemsBeingCooked, true, registries);
		nbt.putIntArray("CookingTimes", this.cookingTimes);
		nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		NbtCompound nbtCompound = new NbtCompound();
		Inventories.writeNbt(nbtCompound, this.itemsBeingCooked, true, registries);
		return nbtCompound;
	}

	public boolean addItem(ServerWorld world, @Nullable LivingEntity entity, ItemStack stack) {
		for (int i = 0; i < this.itemsBeingCooked.size(); i++) {
			ItemStack itemStack = this.itemsBeingCooked.get(i);
			if (itemStack.isEmpty()) {
				Optional<RecipeEntry<CampfireCookingRecipe>> optional = world.getRecipeManager()
					.getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SingleStackRecipeInput(stack), world);
				if (optional.isEmpty()) {
					return false;
				}

				this.cookingTotalTimes[i] = ((CampfireCookingRecipe)((RecipeEntry)optional.get()).value()).getCookingTime();
				this.cookingTimes[i] = 0;
				this.itemsBeingCooked.set(i, stack.splitUnlessCreative(1, entity));
				world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(entity, this.getCachedState()));
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

	@Override
	protected void readComponents(BlockEntity.ComponentsAccess components) {
		super.readComponents(components);
		components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyTo(this.getItemsBeingCooked());
	}

	@Override
	protected void addComponents(ComponentMap.Builder builder) {
		super.addComponents(builder);
		builder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(this.getItemsBeingCooked()));
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("Items");
	}
}
