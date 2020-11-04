package net.minecraft.entity.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

public class PlayerAbilities {
	public boolean invulnerable;
	public boolean flying;
	public boolean allowFlying;
	public boolean creativeMode;
	public boolean allowModifyWorld = true;
	private float flySpeed = 0.05F;
	private float walkSpeed = 0.1F;

	public void serialize(CompoundTag tag) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putBoolean("invulnerable", this.invulnerable);
		compoundTag.putBoolean("flying", this.flying);
		compoundTag.putBoolean("mayfly", this.allowFlying);
		compoundTag.putBoolean("instabuild", this.creativeMode);
		compoundTag.putBoolean("mayBuild", this.allowModifyWorld);
		compoundTag.putFloat("flySpeed", this.flySpeed);
		compoundTag.putFloat("walkSpeed", this.walkSpeed);
		tag.put("abilities", compoundTag);
	}

	public void deserialize(CompoundTag tag) {
		if (tag.contains("abilities", 10)) {
			CompoundTag compoundTag = tag.getCompound("abilities");
			this.invulnerable = compoundTag.getBoolean("invulnerable");
			this.flying = compoundTag.getBoolean("flying");
			this.allowFlying = compoundTag.getBoolean("mayfly");
			this.creativeMode = compoundTag.getBoolean("instabuild");
			if (compoundTag.contains("flySpeed", 99)) {
				this.flySpeed = compoundTag.getFloat("flySpeed");
				this.walkSpeed = compoundTag.getFloat("walkSpeed");
			}

			if (compoundTag.contains("mayBuild", 1)) {
				this.allowModifyWorld = compoundTag.getBoolean("mayBuild");
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
