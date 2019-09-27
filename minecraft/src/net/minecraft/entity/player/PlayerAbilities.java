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

	public void serialize(CompoundTag compoundTag) {
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag2.putBoolean("invulnerable", this.invulnerable);
		compoundTag2.putBoolean("flying", this.flying);
		compoundTag2.putBoolean("mayfly", this.allowFlying);
		compoundTag2.putBoolean("instabuild", this.creativeMode);
		compoundTag2.putBoolean("mayBuild", this.allowModifyWorld);
		compoundTag2.putFloat("flySpeed", this.flySpeed);
		compoundTag2.putFloat("walkSpeed", this.walkSpeed);
		compoundTag.put("abilities", compoundTag2);
	}

	public void deserialize(CompoundTag compoundTag) {
		if (compoundTag.contains("abilities", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("abilities");
			this.invulnerable = compoundTag2.getBoolean("invulnerable");
			this.flying = compoundTag2.getBoolean("flying");
			this.allowFlying = compoundTag2.getBoolean("mayfly");
			this.creativeMode = compoundTag2.getBoolean("instabuild");
			if (compoundTag2.contains("flySpeed", 99)) {
				this.flySpeed = compoundTag2.getFloat("flySpeed");
				this.walkSpeed = compoundTag2.getFloat("walkSpeed");
			}

			if (compoundTag2.contains("mayBuild", 1)) {
				this.allowModifyWorld = compoundTag2.getBoolean("mayBuild");
			}
		}
	}

	public float getFlySpeed() {
		return this.flySpeed;
	}

	@Environment(EnvType.CLIENT)
	public void setFlySpeed(float f) {
		this.flySpeed = f;
	}

	public float getWalkSpeed() {
		return this.walkSpeed;
	}

	@Environment(EnvType.CLIENT)
	public void setWalkSpeed(float f) {
		this.walkSpeed = f;
	}
}
