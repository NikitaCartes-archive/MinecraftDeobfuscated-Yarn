package net.minecraft.entity.decoration;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapId;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ItemFrameEntity extends AbstractDecorationEntity {
	private static final Logger ITEM_FRAME_LOGGER = LogUtils.getLogger();
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final int field_30454 = 8;
	private float itemDropChance = 1.0F;
	private boolean fixed;

	public ItemFrameEntity(EntityType<? extends ItemFrameEntity> entityType, World world) {
		super(entityType, world);
	}

	public ItemFrameEntity(World world, BlockPos pos, Direction facing) {
		this(EntityType.ITEM_FRAME, world, pos, facing);
	}

	public ItemFrameEntity(EntityType<? extends ItemFrameEntity> type, World world, BlockPos pos, Direction facing) {
		super(type, world, pos);
		this.setFacing(facing);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
		this.getDataTracker().startTracking(ROTATION, 0);
	}

	@Override
	protected void setFacing(Direction facing) {
		Validate.notNull(facing);
		this.facing = facing;
		if (facing.getAxis().isHorizontal()) {
			this.setPitch(0.0F);
			this.setYaw((float)(this.facing.getHorizontal() * 90));
		} else {
			this.setPitch((float)(-90 * facing.getDirection().offset()));
			this.setYaw(0.0F);
		}

		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
		this.updateAttachmentPosition();
	}

	@Override
	protected void updateAttachmentPosition() {
		if (this.facing != null) {
			double d = 0.46875;
			double e = (double)this.attachmentPos.getX() + 0.5 - (double)this.facing.getOffsetX() * 0.46875;
			double f = (double)this.attachmentPos.getY() + 0.5 - (double)this.facing.getOffsetY() * 0.46875;
			double g = (double)this.attachmentPos.getZ() + 0.5 - (double)this.facing.getOffsetZ() * 0.46875;
			this.setPos(e, f, g);
			double h = (double)this.getWidthPixels();
			double i = (double)this.getHeightPixels();
			double j = (double)this.getWidthPixels();
			Direction.Axis axis = this.facing.getAxis();
			switch (axis) {
				case X:
					h = 1.0;
					break;
				case Y:
					i = 1.0;
					break;
				case Z:
					j = 1.0;
			}

			h /= 32.0;
			i /= 32.0;
			j /= 32.0;
			this.setBoundingBox(new Box(e - h, f - i, g - j, e + h, f + i, g + j));
		}
	}

	@Override
	public boolean canStayAttached() {
		if (this.fixed) {
			return true;
		} else if (!this.getWorld().isSpaceEmpty(this)) {
			return false;
		} else {
			BlockState blockState = this.getWorld().getBlockState(this.attachmentPos.offset(this.facing.getOpposite()));
			return blockState.isSolid() || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState)
				? this.getWorld().getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty()
				: false;
		}
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		if (!this.fixed) {
			super.move(movementType, movement);
		}
	}

	@Override
	public void addVelocity(double deltaX, double deltaY, double deltaZ) {
		if (!this.fixed) {
			super.addVelocity(deltaX, deltaY, deltaZ);
		}
	}

	@Override
	public void kill() {
		this.removeFromFrame(this.getHeldItemStack());
		super.kill();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.fixed) {
			return !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !source.isSourceCreativePlayer() ? false : super.damage(source, amount);
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!source.isIn(DamageTypeTags.IS_EXPLOSION) && !this.getHeldItemStack().isEmpty()) {
			if (!this.getWorld().isClient) {
				this.dropHeldStack(source.getAttacker(), false);
				this.emitGameEvent(GameEvent.BLOCK_CHANGE, source.getAttacker());
				this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.damage(source, amount);
		}
	}

	public SoundEvent getRemoveItemSound() {
		return SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
	}

	@Override
	public int getWidthPixels() {
		return 12;
	}

	@Override
	public int getHeightPixels() {
		return 12;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = 16.0;
		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	public void onBreak(@Nullable Entity entity) {
		this.playSound(this.getBreakSound(), 1.0F, 1.0F);
		this.dropHeldStack(entity, true);
		this.emitGameEvent(GameEvent.BLOCK_CHANGE, entity);
	}

	public SoundEvent getBreakSound() {
		return SoundEvents.ENTITY_ITEM_FRAME_BREAK;
	}

	@Override
	public void onPlace() {
		this.playSound(this.getPlaceSound(), 1.0F, 1.0F);
	}

	public SoundEvent getPlaceSound() {
		return SoundEvents.ENTITY_ITEM_FRAME_PLACE;
	}

	private void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
		if (!this.fixed) {
			ItemStack itemStack = this.getHeldItemStack();
			this.setHeldItemStack(ItemStack.EMPTY);
			if (!this.getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
				if (entity == null) {
					this.removeFromFrame(itemStack);
				}
			} else {
				if (entity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().creativeMode) {
					this.removeFromFrame(itemStack);
					return;
				}

				if (alwaysDrop) {
					this.dropStack(this.getAsItemStack());
				}

				if (!itemStack.isEmpty()) {
					itemStack = itemStack.copy();
					this.removeFromFrame(itemStack);
					if (this.random.nextFloat() < this.itemDropChance) {
						this.dropStack(itemStack);
					}
				}
			}
		}
	}

	private void removeFromFrame(ItemStack stack) {
		MapId mapId = this.getMapId();
		if (mapId != null) {
			MapState mapState = FilledMapItem.getMapState(mapId, this.getWorld());
			if (mapState != null) {
				mapState.removeFrame(this.attachmentPos, this.getId());
				mapState.setDirty(true);
			}
		}

		stack.setHolder(null);
	}

	public ItemStack getHeldItemStack() {
		return this.getDataTracker().get(ITEM_STACK);
	}

	@Nullable
	public MapId getMapId() {
		return FilledMapItem.getMapId(this.getHeldItemStack());
	}

	public boolean containsMap() {
		return this.getMapId() != null;
	}

	public void setHeldItemStack(ItemStack stack) {
		this.setHeldItemStack(stack, true);
	}

	public void setHeldItemStack(ItemStack value, boolean update) {
		if (!value.isEmpty()) {
			value = value.copyWithCount(1);
		}

		this.setAsStackHolder(value);
		this.getDataTracker().set(ITEM_STACK, value);
		if (!value.isEmpty()) {
			this.playSound(this.getAddItemSound(), 1.0F, 1.0F);
		}

		if (update && this.attachmentPos != null) {
			this.getWorld().updateComparators(this.attachmentPos, Blocks.AIR);
		}
	}

	public SoundEvent getAddItemSound() {
		return SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? new StackReference() {
			@Override
			public ItemStack get() {
				return ItemFrameEntity.this.getHeldItemStack();
			}

			@Override
			public boolean set(ItemStack stack) {
				ItemFrameEntity.this.setHeldItemStack(stack);
				return true;
			}
		} : super.getStackReference(mappedIndex);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (data.equals(ITEM_STACK)) {
			this.setAsStackHolder(this.getHeldItemStack());
		}
	}

	private void setAsStackHolder(ItemStack stack) {
		if (!stack.isEmpty() && stack.getFrame() != this) {
			stack.setHolder(this);
		}

		this.updateAttachmentPosition();
	}

	public int getRotation() {
		return this.getDataTracker().get(ROTATION);
	}

	public void setRotation(int value) {
		this.setRotation(value, true);
	}

	private void setRotation(int value, boolean updateComparators) {
		this.getDataTracker().set(ROTATION, value % 8);
		if (updateComparators && this.attachmentPos != null) {
			this.getWorld().updateComparators(this.attachmentPos, Blocks.AIR);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (!this.getHeldItemStack().isEmpty()) {
			nbt.put("Item", this.getHeldItemStack().writeNbt(new NbtCompound()));
			nbt.putByte("ItemRotation", (byte)this.getRotation());
			nbt.putFloat("ItemDropChance", this.itemDropChance);
		}

		nbt.putByte("Facing", (byte)this.facing.getId());
		nbt.putBoolean("Invisible", this.isInvisible());
		nbt.putBoolean("Fixed", this.fixed);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		NbtCompound nbtCompound = nbt.getCompound("Item");
		if (nbtCompound != null && !nbtCompound.isEmpty()) {
			ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
			if (itemStack.isEmpty()) {
				ITEM_FRAME_LOGGER.warn("Unable to load item from: {}", nbtCompound);
			}

			ItemStack itemStack2 = this.getHeldItemStack();
			if (!itemStack2.isEmpty() && !ItemStack.areEqual(itemStack, itemStack2)) {
				this.removeFromFrame(itemStack2);
			}

			this.setHeldItemStack(itemStack, false);
			this.setRotation(nbt.getByte("ItemRotation"), false);
			if (nbt.contains("ItemDropChance", NbtElement.NUMBER_TYPE)) {
				this.itemDropChance = nbt.getFloat("ItemDropChance");
			}
		}

		this.setFacing(Direction.byId(nbt.getByte("Facing")));
		this.setInvisible(nbt.getBoolean("Invisible"));
		this.fixed = nbt.getBoolean("Fixed");
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		boolean bl = !this.getHeldItemStack().isEmpty();
		boolean bl2 = !itemStack.isEmpty();
		if (this.fixed) {
			return ActionResult.PASS;
		} else if (!this.getWorld().isClient) {
			if (!bl) {
				if (bl2 && !this.isRemoved()) {
					if (itemStack.isOf(Items.FILLED_MAP)) {
						MapState mapState = FilledMapItem.getMapState(itemStack, this.getWorld());
						if (mapState != null && mapState.iconCountNotLessThan(256)) {
							return ActionResult.FAIL;
						}
					}

					this.setHeldItemStack(itemStack);
					this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
					if (!player.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}
				}
			} else {
				this.playSound(this.getRotateItemSound(), 1.0F, 1.0F);
				this.setRotation(this.getRotation() + 1);
				this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
			}

			return ActionResult.CONSUME;
		} else {
			return !bl && !bl2 ? ActionResult.PASS : ActionResult.SUCCESS;
		}
	}

	public SoundEvent getRotateItemSound() {
		return SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM;
	}

	public int getComparatorPower() {
		return this.getHeldItemStack().isEmpty() ? 0 : this.getRotation() % 8 + 1;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		this.setFacing(Direction.byId(packet.getEntityData()));
	}

	@Override
	public ItemStack getPickBlockStack() {
		ItemStack itemStack = this.getHeldItemStack();
		return itemStack.isEmpty() ? this.getAsItemStack() : itemStack.copy();
	}

	protected ItemStack getAsItemStack() {
		return new ItemStack(Items.ITEM_FRAME);
	}

	@Override
	public float getBodyYaw() {
		Direction direction = this.getHorizontalFacing();
		int i = direction.getAxis().isVertical() ? 90 * direction.getDirection().offset() : 0;
		return (float)MathHelper.wrapDegrees(180 + direction.getHorizontal() * 90 + this.getRotation() * 45 + i);
	}
}
