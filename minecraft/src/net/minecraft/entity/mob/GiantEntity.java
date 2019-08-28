package net.minecraft.entity.mob;

import net.minecraft.class_4538;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GiantEntity extends HostileEntity {
	public GiantEntity(EntityType<? extends GiantEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
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
	public float getPathfindingFavor(BlockPos blockPos, class_4538 arg) {
		return arg.method_22349(blockPos) - 0.5F;
	}
}
