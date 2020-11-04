package net.minecraft;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface class_5568 {
	int getEntityId();

	UUID getUuid();

	BlockPos getBlockPos();

	Box getBoundingBox();

	void method_31744(class_5569 arg);

	Stream<? extends class_5568> method_31748();

	void setRemoved(Entity.RemovalReason reason);

	boolean method_31746();

	boolean isPlayer();
}
