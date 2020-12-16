package net.minecraft.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.class_5569;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface EntityLike {
	int getId();

	UUID getUuid();

	BlockPos getBlockPos();

	Box getBoundingBox();

	void method_31744(class_5569 arg);

	Stream<? extends EntityLike> streamPassengers();

	void setRemoved(Entity.RemovalReason reason);

	boolean shouldSave();

	boolean isPlayer();
}
