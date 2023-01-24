package net.minecraft.entity;

import javax.annotation.Nullable;

public interface Ownable {
	@Nullable
	Entity getOwner();
}
