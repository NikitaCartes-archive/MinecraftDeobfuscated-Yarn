package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.nbt.NbtCompound;

public class PlayerAbilities {
	public boolean invulnerable;
	public boolean flying;
	public boolean allowFlying;
	public boolean creativeMode;
	public boolean allowModifyWorld = true;
	private float flySpeed = 0.05F;
	private float walkSpeed = 0.1F;

	public void serialize(NbtCompound tag) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putBoolean("invulnerable", this.invulnerable);
		nbtCompound.putBoolean("flying", this.flying);
		nbtCompound.putBoolean("mayfly", this.allowFlying);
		nbtCompound.putBoolean("instabuild", this.creativeMode);
		nbtCompound.putBoolean("mayBuild", this.allowModifyWorld);
		nbtCompound.putFloat("flySpeed", this.flySpeed);
		nbtCompound.putFloat("walkSpeed", this.walkSpeed);
		tag.put("abilities", nbtCompound);
	}

	public void deserialize(NbtCompound tag) {
		if (tag.contains("abilities", NbtTypeIds.COMPOUND)) {
			NbtCompound nbtCompound = tag.getCompound("abilities");
			this.invulnerable = nbtCompound.getBoolean("invulnerable");
			this.flying = nbtCompound.getBoolean("flying");
			this.allowFlying = nbtCompound.getBoolean("mayfly");
			this.creativeMode = nbtCompound.getBoolean("instabuild");
			if (nbtCompound.contains("flySpeed", NbtTypeIds.NUMBER)) {
				this.flySpeed = nbtCompound.getFloat("flySpeed");
				this.walkSpeed = nbtCompound.getFloat("walkSpeed");
			}

			if (nbtCompound.contains("mayBuild", NbtTypeIds.BYTE)) {
				this.allowModifyWorld = nbtCompound.getBoolean("mayBuild");
			}
		}
	}

	public float getFlySpeed() {
		return this.flySpeed;
	}

	@Environment(EnvType.CLIENT)
	public void setFlySpeed(float flySpeed) {
		this.flySpeed = flySpeed;
	}

	public float getWalkSpeed() {
		return this.walkSpeed;
	}

	@Environment(EnvType.CLIENT)
	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
}
