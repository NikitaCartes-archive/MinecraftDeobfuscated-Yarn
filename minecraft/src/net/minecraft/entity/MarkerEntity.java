package net.minecraft.entity;

import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.World;

public class MarkerEntity extends Entity {
	/**
	 * The name of the compound tag that stores the marker's custom data.
	 */
	private static final String DATA_KEY = "data";
	private NbtCompound data = new NbtCompound();

	public MarkerEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.noClip = true;
	}

	@Override
	public void tick() {
	}

	@Override
	protected void initDataTracker() {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		this.data = nbt.getCompound("data");
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.put("data", this.data.copy());
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		throw new IllegalStateException("Markers should never be sent");
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return false;
	}

	@Override
	protected boolean couldAcceptPassenger() {
		return false;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
	}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}
}
