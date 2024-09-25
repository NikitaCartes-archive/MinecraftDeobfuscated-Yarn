package net.minecraft.entity.boss.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;

public class EnderDragonPart extends Entity {
	public final EnderDragonEntity owner;
	public final String name;
	private final EntityDimensions partDimensions;

	public EnderDragonPart(EnderDragonEntity owner, String name, float width, float height) {
		super(owner.getType(), owner.getWorld());
		this.partDimensions = EntityDimensions.changing(width, height);
		this.calculateDimensions();
		this.owner = owner;
		this.name = name;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Nullable
	@Override
	public ItemStack getPickBlockStack() {
		return this.owner.getPickBlockStack();
	}

	@Override
	public final boolean damage(ServerWorld world, DamageSource source, float amount) {
		return this.isAlwaysInvulnerableTo(source) ? false : this.owner.damagePart(world, this, source, amount);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.partDimensions;
	}

	@Override
	public boolean shouldSave() {
		return false;
	}
}
