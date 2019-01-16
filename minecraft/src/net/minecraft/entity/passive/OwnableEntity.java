package net.minecraft.entity.passive;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;

public interface OwnableEntity {
	@Nullable
	UUID getOwnerUuid();

	@Nullable
	Entity getOwner();
}
