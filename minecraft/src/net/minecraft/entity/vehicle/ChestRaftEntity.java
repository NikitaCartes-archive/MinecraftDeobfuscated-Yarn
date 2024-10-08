package net.minecraft.entity.vehicle;

import java.util.function.Supplier;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ChestRaftEntity extends AbstractChestBoatEntity {
	public ChestRaftEntity(EntityType<? extends ChestRaftEntity> entityType, World world, Supplier<Item> supplier) {
		super(entityType, world, supplier);
	}

	@Override
	protected double getPassengerAttachmentY(EntityDimensions dimensions) {
		return (double)(dimensions.height() * 0.8888889F);
	}
}