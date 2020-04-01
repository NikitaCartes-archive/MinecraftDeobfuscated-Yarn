package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class class_5036 extends OverworldDimension {
	private static final Vector3f field_23519 = new Vector3f(method_26541(129.0F), method_26541(185.0F), method_26541(0.0F));
	private static final Vector3f field_23520 = new Vector3f(method_26541(255.0F), method_26541(185.0F), method_26541(2.0F));
	private static final Vector3f field_23521 = new Vector3f(method_26541(1.0F), method_26541(164.0F), method_26541(239.0F));
	private static final Vector3f field_23522 = new Vector3f(method_26541(244.0F), method_26541(78.0F), method_26541(36.0F));

	private static float method_26541(float f) {
		return f / 255.0F;
	}

	public class_5036(World world, DimensionType dimensionType) {
		super(world, dimensionType);
	}

	@Environment(EnvType.CLIENT)
	private static Vector3f method_26542(BlockPos blockPos) {
		if (blockPos.getX() < 0) {
			return blockPos.getZ() > 0 ? field_23519 : field_23520;
		} else {
			return blockPos.getZ() > 0 ? field_23522 : field_23521;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return method_26542(blockPos);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return method_26542(livingEntity.getBlockPos());
	}
}
