package net.minecraft.entity.decoration;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
import net.minecraft.network.Packet;
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
	private static final TrackedData<ItemStack> field_7130 = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Integer> field_7132 = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private float itemDropChance = 1.0F;

	public ItemFrameEntity(EntityType<? extends ItemFrameEntity> entityType, World world) {
		super(entityType, world);
	}

	public ItemFrameEntity(World world, BlockPos blockPos, Direction direction) {
		super(EntityType.ITEM_FRAME, world, blockPos);
		this.method_6892(direction);
	}

	@Override
	protected float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return 0.0F;
	}

	@Override
	protected void initDataTracker() {
		this.method_5841().startTracking(field_7130, ItemStack.EMPTY);
		this.method_5841().startTracking(field_7132, 0);
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
			this.x = (double)this.field_7100.getX() + 0.5 - (double)this.field_7099.getOffsetX() * 0.46875;
			this.y = (double)this.field_7100.getY() + 0.5 - (double)this.field_7099.getOffsetY() * 0.46875;
			this.z = (double)this.field_7100.getZ() + 0.5 - (double)this.field_7099.getOffsetZ() * 0.46875;
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
			this.method_5857(new BoundingBox(this.x - e, this.y - f, this.z - g, this.x + e, this.y + f, this.z + g));
		}
	}

	@Override
	public boolean method_6888() {
		if (!this.field_6002.method_17892(this)) {
			return false;
		} else {
			BlockState blockState = this.field_6002.method_8320(this.field_7100.method_10093(this.field_7099.getOpposite()));
			return blockState.method_11620().method_15799() || this.field_7099.getAxis().isHorizontal() && AbstractRedstoneGateBlock.method_9999(blockState)
				? this.field_6002.method_8333(this, this.method_5829(), PREDICATE).isEmpty()
				: false;
		}
	}

	@Override
	public float getBoundingBoxMarginForTargeting() {
		return 0.0F;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (!damageSource.isExplosive() && !this.method_6940().isEmpty()) {
			if (!this.field_6002.isClient) {
				this.method_6936(damageSource.method_5529(), false);
				this.method_5783(SoundEvents.field_14770, 1.0F, 1.0F);
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
		this.method_5783(SoundEvents.field_14585, 1.0F, 1.0F);
		this.method_6936(entity, true);
	}

	@Override
	public void method_6894() {
		this.method_5783(SoundEvents.field_14844, 1.0F, 1.0F);
	}

	public void method_6936(@Nullable Entity entity, boolean bl) {
		if (this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = this.method_6940();
			this.method_6935(ItemStack.EMPTY);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				if (playerEntity.abilities.creativeMode) {
					this.method_6937(itemStack);
					return;
				}
			}

			if (bl) {
				this.method_5706(Items.field_8143);
			}

			if (!itemStack.isEmpty() && this.random.nextFloat() < this.itemDropChance) {
				itemStack = itemStack.copy();
				this.method_6937(itemStack);
				this.method_5775(itemStack);
			}
		}
	}

	private void method_6937(ItemStack itemStack) {
		if (itemStack.getItem() == Items.field_8204) {
			MapState mapState = FilledMapItem.method_8001(itemStack, this.field_6002);
			mapState.method_104(this.field_7100, this.getEntityId());
		}

		itemStack.setHoldingItemFrame(null);
	}

	public ItemStack method_6940() {
		return this.method_5841().get(field_7130);
	}

	public void method_6935(ItemStack itemStack) {
		this.method_6933(itemStack, true);
	}

	public void method_6933(ItemStack itemStack, boolean bl) {
		if (!itemStack.isEmpty()) {
			itemStack = itemStack.copy();
			itemStack.setAmount(1);
			itemStack.setHoldingItemFrame(this);
		}

		this.method_5841().set(field_7130, itemStack);
		if (!itemStack.isEmpty()) {
			this.method_5783(SoundEvents.field_14667, 1.0F, 1.0F);
		}

		if (bl && this.field_7100 != null) {
			this.field_6002.method_8455(this.field_7100, Blocks.field_10124);
		}
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		if (i == 0) {
			this.method_6935(itemStack);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (trackedData.equals(field_7130)) {
			ItemStack itemStack = this.method_6940();
			if (!itemStack.isEmpty() && itemStack.getHoldingItemFrame() != this) {
				itemStack.setHoldingItemFrame(this);
			}
		}
	}

	public int getRotation() {
		return this.method_5841().get(field_7132);
	}

	public void setRotation(int i) {
		this.setRotation(i, true);
	}

	private void setRotation(int i, boolean bl) {
		this.method_5841().set(field_7132, i % 8);
		if (bl && this.field_7100 != null) {
			this.field_6002.method_8455(this.field_7100, Blocks.field_10124);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (!this.method_6940().isEmpty()) {
			compoundTag.method_10566("Item", this.method_6940().method_7953(new CompoundTag()));
			compoundTag.putByte("ItemRotation", (byte)this.getRotation());
			compoundTag.putFloat("ItemDropChance", this.itemDropChance);
		}

		compoundTag.putByte("Facing", (byte)this.field_7099.getId());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		CompoundTag compoundTag2 = compoundTag.getCompound("Item");
		if (compoundTag2 != null && !compoundTag2.isEmpty()) {
			ItemStack itemStack = ItemStack.method_7915(compoundTag2);
			if (itemStack.isEmpty()) {
				field_7131.warn("Unable to load item from: {}", compoundTag2);
			}

			this.method_6933(itemStack, false);
			this.setRotation(compoundTag.getByte("ItemRotation"), false);
			if (compoundTag.containsKey("ItemDropChance", 99)) {
				this.itemDropChance = compoundTag.getFloat("ItemDropChance");
			}
		}

		this.method_6892(Direction.byId(compoundTag.getByte("Facing")));
	}

	@Override
	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (!this.field_6002.isClient) {
			if (this.method_6940().isEmpty()) {
				if (!itemStack.isEmpty()) {
					this.method_6935(itemStack);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}
				}
			} else {
				this.method_5783(SoundEvents.field_15038, 1.0F, 1.0F);
				this.setRotation(this.getRotation() + 1);
			}
		}

		return true;
	}

	public int method_6938() {
		return this.method_6940().isEmpty() ? 0 : this.getRotation() % 8 + 1;
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this, this.method_5864(), this.field_7099.getId(), this.method_6896());
	}
}
