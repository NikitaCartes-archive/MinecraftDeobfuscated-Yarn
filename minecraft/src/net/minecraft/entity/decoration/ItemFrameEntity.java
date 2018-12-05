package net.minecraft.entity.decoration;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends AbstractDecorationEntity {
	private static final Logger field_7131 = LogManager.getLogger();
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private float itemDropChance = 1.0F;

	public ItemFrameEntity(World world) {
		super(EntityType.ITEM_FRAME, world);
	}

	public ItemFrameEntity(World world, BlockPos blockPos, Direction direction) {
		super(EntityType.ITEM_FRAME, world, blockPos);
		this.method_6892(direction);
	}

	@Override
	public float getEyeHeight() {
		return 0.0F;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
		this.getDataTracker().startTracking(ROTATION, 0);
	}

	@Override
	protected void method_6892(Direction direction) {
		Validate.notNull(direction);
		this.field_7099 = direction;
		if (direction.getAxis().isHorizontal()) {
			this.pitch = 0.0F;
			this.yaw = (float)(this.field_7099.getHorizontal() * 90);
		} else {
			this.pitch = (float)(-90 * direction.getDirection().offset());
			this.yaw = 0.0F;
		}

		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		this.method_6895();
	}

	@Override
	protected void method_6895() {
		if (this.field_7099 != null) {
			double d = 0.46875;
			this.x = (double)this.blockPos.getX() + 0.5 - (double)this.field_7099.getOffsetX() * 0.46875;
			this.y = (double)this.blockPos.getY() + 0.5 - (double)this.field_7099.getOffsetY() * 0.46875;
			this.z = (double)this.blockPos.getZ() + 0.5 - (double)this.field_7099.getOffsetZ() * 0.46875;
			double e = (double)this.getWidthPixels();
			double f = (double)this.getHeightPixels();
			double g = (double)this.getWidthPixels();
			Direction.Axis axis = this.field_7099.getAxis();
			switch (axis) {
				case X:
					e = 1.0;
					break;
				case Y:
					f = 1.0;
					break;
				case Z:
					g = 1.0;
			}

			e /= 32.0;
			f /= 32.0;
			g /= 32.0;
			this.setBoundingBox(new BoundingBox(this.x - e, this.y - f, this.z - g, this.x + e, this.y + f, this.z + g));
		}
	}

	@Override
	public boolean method_6888() {
		if (!this.world.method_8587(this, this.getBoundingBox())) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(this.blockPos.method_10093(this.field_7099.getOpposite()));
			return blockState.getMaterial().method_15799() || this.field_7099.getAxis().isHorizontal() && AbstractRedstoneGateBlock.method_9999(blockState)
				? this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty()
				: false;
		}
	}

	@Override
	public float method_5871() {
		return 0.0F;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (!damageSource.isExplosive() && !this.getHeldItemStack().isEmpty()) {
			if (!this.world.isRemote) {
				this.method_6936(damageSource.getAttacker(), false);
				this.playSoundAtEntity(SoundEvents.field_14770, 1.0F, 1.0F);
			}

			return true;
		} else {
			return super.damage(damageSource, f);
		}
	}

	@Override
	public int getWidthPixels() {
		return 12;
	}

	@Override
	public int getHeightPixels() {
		return 12;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = 16.0;
		e *= 64.0 * getRenderDistanceMultiplier();
		return d < e * e;
	}

	@Override
	public void copyEntityData(@Nullable Entity entity) {
		this.playSoundAtEntity(SoundEvents.field_14585, 1.0F, 1.0F);
		this.method_6936(entity, true);
	}

	@Override
	public void onDecorationPlaced() {
		this.playSoundAtEntity(SoundEvents.field_14844, 1.0F, 1.0F);
	}

	public void method_6936(@Nullable Entity entity, boolean bl) {
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = this.getHeldItemStack();
			this.setHeldItemStack(ItemStack.EMPTY);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					this.method_6937(itemStack);
					return;
				}
			}

			if (bl) {
				this.dropItem(Items.field_8143);
			}

			if (!itemStack.isEmpty() && this.random.nextFloat() < this.itemDropChance) {
				itemStack = itemStack.copy();
				this.method_6937(itemStack);
				this.dropStack(itemStack);
			}
		}
	}

	private void method_6937(ItemStack itemStack) {
		if (itemStack.getItem() == Items.field_8204) {
			MapState mapState = FilledMapItem.method_8001(itemStack, this.world);
			mapState.method_104(this.blockPos, this.getEntityId());
		}

		itemStack.setHoldingItemFrame(null);
	}

	public ItemStack getHeldItemStack() {
		return this.getDataTracker().get(ITEM_STACK);
	}

	public void setHeldItemStack(ItemStack itemStack) {
		this.setHeldItemStack(itemStack, true);
	}

	public void setHeldItemStack(ItemStack itemStack, boolean bl) {
		if (!itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setAmount(1);
			itemStack.setHoldingItemFrame(this);
		}

		this.getDataTracker().set(ITEM_STACK, itemStack);
		if (!itemStack.isEmpty()) {
			this.playSoundAtEntity(SoundEvents.field_14667, 1.0F, 1.0F);
		}

		if (bl && this.blockPos != null) {
			this.world.updateHorizontalAdjacent(this.blockPos, Blocks.field_10124);
		}
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (i == 0) {
			this.setHeldItemStack(itemStack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (trackedData.equals(ITEM_STACK)) {
			ItemStack itemStack = this.getHeldItemStack();
			if (!itemStack.isEmpty() && itemStack.getHoldingItemFrame() != this) {
				itemStack.setHoldingItemFrame(this);
			}
		}
	}

	public int getRotation() {
		return this.getDataTracker().get(ROTATION);
	}

	public void setRotation(int i) {
		this.setRotation(i, true);
	}

	private void setRotation(int i, boolean bl) {
		this.getDataTracker().set(ROTATION, i % 8);
		if (bl && this.blockPos != null) {
			this.world.updateHorizontalAdjacent(this.blockPos, Blocks.field_10124);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (!this.getHeldItemStack().isEmpty()) {
			compoundTag.put("Item", this.getHeldItemStack().toTag(new CompoundTag()));
			compoundTag.putByte("ItemRotation", (byte)this.getRotation());
			compoundTag.putFloat("ItemDropChance", this.itemDropChance);
		}

		compoundTag.putByte("Facing", (byte)this.field_7099.getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		CompoundTag compoundTag2 = compoundTag.getCompound("Item");
		if (compoundTag2 != null && !compoundTag2.isEmpty()) {
			ItemStack itemStack = ItemStack.fromTag(compoundTag2);
			if (itemStack.isEmpty()) {
				field_7131.warn("Unable to load item from: {}", compoundTag2);
			}

			this.setHeldItemStack(itemStack, false);
			this.setRotation(compoundTag.getByte("ItemRotation"), false);
			if (compoundTag.containsKey("ItemDropChance", 99)) {
				this.itemDropChance = compoundTag.getFloat("ItemDropChance");
			}
		}

		this.method_6892(Direction.byId(compoundTag.getByte("Facing")));
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!this.world.isRemote) {
			if (this.getHeldItemStack().isEmpty()) {
				if (!itemStack.isEmpty()) {
					this.setHeldItemStack(itemStack);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}
				}
			} else {
				this.playSoundAtEntity(SoundEvents.field_15038, 1.0F, 1.0F);
				this.setRotation(this.getRotation() + 1);
			}
		}

		return true;
	}

	public int method_6938() {
		return this.getHeldItemStack().isEmpty() ? 0 : this.getRotation() % 8 + 1;
	}
}
