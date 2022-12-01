package net.minecraft.entity.boss.dragon;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EnderDragonPart extends Entity {
	public final EnderDragonEntity owner;
	public final String name;
	private final EntityDimensions partDimensions;

	public EnderDragonPart(EnderDragonEntity owner, String name, float width, float height) {
		super(owner.getType(), owner.world);
		this.partDimensions = EntityDimensions.changing(width, height);
		this.calculateDimensions();
		this.owner = owner;
		this.name = name;
	}

	@Override
	protected void initDataTracker() {
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
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : this.owner.damagePart(this, source, amount);
	}

	@Override
	public boolean isPartOf(Entity entity) {
		return this == entity || this.owner == entity;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
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
