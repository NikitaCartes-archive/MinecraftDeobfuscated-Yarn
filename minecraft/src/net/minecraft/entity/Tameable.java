package net.minecraft.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.EntityView;

public interface Tameable {
	@Nullable
	UUID getOwnerUuid();

	EntityView method_48926();

	@Nullable
	default LivingEntity getOwner() {
		UUID uUID = this.getOwnerUuid();
		return uUID == null ? null : this.method_48926().getPlayerByUuid(uUID);
	}
}
