package net.minecraft.entity.player;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class PlayerAbilities {
	public boolean invulnerable;
	public boolean flying;
	public boolean allowFlying;
	public boolean creativeMode;
	public boolean allowModifyWorld = true;
	private float flySpeed = 0.05F;
	private float walkSpeed = 0.1F;

	public void writeNbt(NbtCompound nbt) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putBoolean("invulnerable", this.invulnerable);
		nbtCompound.putBoolean("flying", this.flying);
		nbtCompound.putBoolean("mayfly", this.allowFlying);
		nbtCompound.putBoolean("instabuild", this.creativeMode);
		nbtCompound.putBoolean("mayBuild", this.allowModifyWorld);
		nbtCompound.putFloat("flySpeed", this.flySpeed);
		nbtCompound.putFloat("walkSpeed", this.walkSpeed);
		nbt.put("abilities", nbtCompound);
	}

	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("abilities", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("abilities");
			this.invulnerable = nbtCompound.getBoolean("invulnerable");
			this.flying = nbtCompound.getBoolean("flying");
			this.allowFlying = nbtCompound.getBoolean("mayfly");
			this.creativeMode = nbtCompound.getBoolean("instabuild");
			if (nbtCompound.contains("flySpeed", NbtElement.NUMBER_TYPE)) {
				this.flySpeed = nbtCompound.getFloat("flySpeed");
				this.walkSpeed = nbtCompound.getFloat("walkSpeed");
			}

			if (nbtCompound.contains("mayBuild", NbtElement.BYTE_TYPE)) {
				this.allowModifyWorld = nbtCompound.getBoolean("mayBuild");
			}
		}
	}

	public float getFlySpeed() {
		return this.flySpeed;
	}

	public void setFlySpeed(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	public float getWalkSpeed() {
		return this.walkSpeed;
	}

	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
}
