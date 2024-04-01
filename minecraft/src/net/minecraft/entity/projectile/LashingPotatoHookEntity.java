package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LashingPotatoHookEntity extends ProjectileEntity {
	public static final TrackedData<Boolean> IN_BLOCK = DataTracker.registerData(LashingPotatoHookEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public static final TrackedData<Float> LENGTH = DataTracker.registerData(LashingPotatoHookEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final float field_50487 = 100.0F;
	private static final double field_50488 = 5.0;

	public LashingPotatoHookEntity(EntityType<? extends LashingPotatoHookEntity> entityType, World world) {
		super(entityType, world);
		this.ignoreCameraFrustum = true;
	}

	public LashingPotatoHookEntity(World world, PlayerEntity player) {
		this(EntityType.LASHING_POTATO_HOOK, world);
		this.setOwner(player);
		this.setPosition(player.getX(), player.getEyeY() - 0.1, player.getZ());
		this.setVelocity(player.getRotationVec(1.0F).multiply(5.0));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(IN_BLOCK, false);
		builder.add(LENGTH, 0.0F);
	}

	@Override
	public boolean shouldRender(double distance) {
		return true;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
	}

	@Override
	public void tick() {
		super.tick();
		PlayerEntity playerEntity = this.getPlayer();
		if (playerEntity != null && (this.getWorld().isClient() || !this.discardIfInvalid(playerEntity))) {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
			if (hitResult.getType() != HitResult.Type.MISS) {
				this.onCollision(hitResult);
			}

			this.setPosition(hitResult.getPos());
			this.checkBlockCollision();
		} else {
			this.discard();
		}
	}

	private boolean discardIfInvalid(PlayerEntity player) {
		if (!player.isRemoved() && player.isAlive() && player.isHolding(Items.LASHING_POTATO) && !(this.squaredDistanceTo(player) > 10000.0)) {
			return false;
		} else {
			this.discard();
			return true;
		}
	}

	@Override
	protected boolean canHit(Entity entity) {
		return false;
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.setVelocity(Vec3d.ZERO);
		this.setInBlock(true);
		PlayerEntity playerEntity = this.getPlayer();
		if (playerEntity != null) {
			double d = playerEntity.getEyePos().subtract(blockHitResult.getPos()).length();
			this.setLength(Math.max((float)d * 0.5F - 3.0F, 1.5F));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putBoolean("in_block", this.isInBlock());
		nbt.putFloat("length", this.getLength());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setInBlock(nbt.getBoolean("in_block"));
		this.setLength(nbt.getFloat("length"));
	}

	private void setInBlock(boolean inBlock) {
		this.getDataTracker().set(IN_BLOCK, inBlock);
	}

	private void setLength(float length) {
		this.getDataTracker().set(LENGTH, length);
	}

	public boolean isInBlock() {
		return this.getDataTracker().get(IN_BLOCK);
	}

	public float getLength() {
		return this.getDataTracker().get(LENGTH);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		this.setHookForPlayer(null);
		super.remove(reason);
	}

	@Override
	public void onRemoved() {
		this.setHookForPlayer(null);
	}

	@Override
	public void setOwner(@Nullable Entity entity) {
		super.setOwner(entity);
		this.setHookForPlayer(this);
	}

	private void setHookForPlayer(@Nullable LashingPotatoHookEntity lashingPotatoHookEntity) {
		PlayerEntity playerEntity = this.getPlayer();
		if (playerEntity != null) {
			playerEntity.lashingPotatoHook = lashingPotatoHookEntity;
		}
	}

	@Nullable
	public PlayerEntity getPlayer() {
		Entity entity = this.getOwner();
		return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entity == null ? this.getId() : entity.getId());
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		if (this.getPlayer() == null) {
			this.kill();
		}
	}
}
