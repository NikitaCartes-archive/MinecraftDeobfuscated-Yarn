package net.minecraft.entity.mob;

import net.minecraft.class_4048;
import net.minecraft.class_4050;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class GiantEntity extends HostileEntity {
	public GiantEntity(EntityType<? extends GiantEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected float method_18394(class_4050 arg, class_4048 arg2) {
		return 10.440001F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(50.0);
	}

	@Override
	public float getPathfindingFavor(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.getBrightness(blockPos) - 0.5F;
	}
}
