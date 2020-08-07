package net.minecraft.entity.mob;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GiantEntity extends HostileEntity {
	public GiantEntity(EntityType<? extends GiantEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 10.440001F;
	}

	public static DefaultAttributeContainer.Builder createGiantAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.field_23716, 100.0)
			.add(EntityAttributes.field_23719, 0.5)
			.add(EntityAttributes.field_23721, 50.0);
	}

	@Override
	public float getPathfindingFavor(BlockPos pos, WorldView world) {
		return world.getBrightness(pos) - 0.5F;
	}
}
