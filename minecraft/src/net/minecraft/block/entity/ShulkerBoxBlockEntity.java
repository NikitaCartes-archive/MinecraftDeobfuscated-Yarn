package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory {
	public static final int field_31354 = 9;
	public static final int field_31355 = 3;
	public static final int field_31356 = 27;
	public static final int field_31357 = 1;
	public static final int field_31358 = 10;
	public static final float field_31359 = 0.5F;
	public static final float field_31360 = 270.0F;
	public static final String ITEMS_KEY = "Items";
	private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private int viewerCount;
	private ShulkerBoxBlockEntity.AnimationStage animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
	private float animationProgress;
	private float prevAnimationProgress;
	@Nullable
	private final DyeColor cachedColor;

	public ShulkerBoxBlockEntity(@Nullable DyeColor color, BlockPos pos, BlockState state) {
		super(BlockEntityType.SHULKER_BOX, pos, state);
		this.cachedColor = color;
	}

	public ShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SHULKER_BOX, pos, state);
		this.cachedColor = ShulkerBoxBlock.getColor(state.getBlock());
	}

	public static void tick(World world, BlockPos pos, BlockState state, ShulkerBoxBlockEntity blockEntity) {
		blockEntity.updateAnimation(world, pos, state);
	}

	private void updateAnimation(World world, BlockPos pos, BlockState state) {
		this.prevAnimationProgress = this.animationProgress;
		switch (this.animationStage) {
			case CLOSED:
				this.animationProgress = 0.0F;
				break;
			case OPENING:
				this.animationProgress += 0.1F;
				if (this.animationProgress >= 1.0F) {
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENED;
					this.animationProgress = 1.0F;
					updateNeighborStates(world, pos, state);
				}

				this.pushEntities(world, pos, state);
				break;
			case CLOSING:
				this.animationProgress -= 0.1F;
				if (this.animationProgress <= 0.0F) {
					this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSED;
					this.animationProgress = 0.0F;
					updateNeighborStates(world, pos, state);
				}
				break;
			case OPENED:
				this.animationProgress = 1.0F;
		}
	}

	public ShulkerBoxBlockEntity.AnimationStage getAnimationStage() {
		return this.animationStage;
	}

	public Box getBoundingBox(BlockState state) {
		return ShulkerEntity.method_33346(state.get(ShulkerBoxBlock.FACING), 0.5F * this.getAnimationProgress(1.0F));
	}

	private void pushEntities(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof ShulkerBoxBlock) {
			Direction direction = state.get(ShulkerBoxBlock.FACING);
			Box box = ShulkerEntity.method_33347(direction, this.prevAnimationProgress, this.animationProgress).offset(pos);
			List<Entity> list = world.getOtherEntities(null, box);
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = (Entity)list.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						double d = 0.0;
						double e = 0.0;
						double f = 0.0;
						Box box2 = entity.getBoundingBox();
						switch (direction.getAxis()) {
							case X:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									d = box.maxX - box2.minX;
								} else {
									d = box2.maxX - box.minX;
								}

								d += 0.01;
								break;
							case Y:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									e = box.maxY - box2.minY;
								} else {
									e = box2.maxY - box.minY;
								}

								e += 0.01;
								break;
							case Z:
								if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
									f = box.maxZ - box2.minZ;
								} else {
									f = box2.maxZ - box.minZ;
								}

								f += 0.01;
						}

						entity.move(
							MovementType.SHULKER_BOX, new Vec3d(d * (double)direction.getOffsetX(), e * (double)direction.getOffsetY(), f * (double)direction.getOffsetZ())
						);
					}
				}
			}
		}
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public boolean onSyncedBlockEvent(int type, int data) {
		if (type == 1) {
			this.viewerCount = data;
			if (data == 0) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.CLOSING;
				updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
			}

			if (data == 1) {
				this.animationStage = ShulkerBoxBlockEntity.AnimationStage.OPENING;
				updateNeighborStates(this.getWorld(), this.pos, this.getCachedState());
			}

			return true;
		} else {
			return super.onSyncedBlockEvent(type, data);
		}
	}

	private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
		state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
			if (this.viewerCount == 1) {
				this.world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, this.pos);
				this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!player.isSpectator()) {
			this.viewerCount--;
			this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
			if (this.viewerCount <= 0) {
				this.world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, this.pos);
				this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}
		}
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.shulkerBox");
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.readInventoryNbt(nbt);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		return this.writeInventoryNbt(nbt);
	}

	public void readInventoryNbt(NbtCompound nbt) {
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(nbt) && nbt.contains("Items", NbtElement.LIST_TYPE)) {
			Inventories.readNbt(nbt, this.inventory);
		}
	}

	public NbtCompound writeInventoryNbt(NbtCompound nbt) {
		if (!this.serializeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory, false);
		}

		return nbt;
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list) {
		this.inventory = list;
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return AVAILABLE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return true;
	}

	public float getAnimationProgress(float delta) {
		return MathHelper.lerp(delta, this.prevAnimationProgress, this.animationProgress);
	}

	@Nullable
	public DyeColor getColor() {
		return this.cachedColor;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
	}

	public boolean suffocates() {
		return this.animationStage == ShulkerBoxBlockEntity.AnimationStage.CLOSED;
	}

	public static enum AnimationStage {
		CLOSED,
		OPENING,
		OPENED,
		CLOSING;
	}
}
