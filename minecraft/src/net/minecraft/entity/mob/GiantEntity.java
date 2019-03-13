package net.minecraft.entity.mob;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 10.440001F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(50.0);
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.method_8610(blockPos) - 0.5F;
	}
}
