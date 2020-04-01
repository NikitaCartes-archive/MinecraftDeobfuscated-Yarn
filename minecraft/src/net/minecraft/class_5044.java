package net.minecraft;

import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;

public class class_5044 extends OverworldDimension {
	private final Vector3f field_23530;
	private final Vec3d field_23531;

	public class_5044(World world, DimensionType dimensionType, Vector3f vector3f) {
		super(world, dimensionType);
		this.field_23530 = vector3f;
		this.field_23531 = new Vec3d(vector3f);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26495(BlockState blockState, BlockPos blockPos) {
		return this.field_23530;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public <T extends LivingEntity> Vector3f method_26494(T livingEntity) {
		return this.field_23530;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
		return this.field_23531;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_26493(int i, int j, Vector3f vector3f) {
		vector3f.method_26698(this.field_23530);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26502() {
		return this.field_23530;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vector3f method_26503() {
		return this.field_23530;
	}

	public static BiFunction<World, DimensionType, ? extends Dimension> method_26547(Vector3f vector3f) {
		return (world, dimensionType) -> new class_5044(world, dimensionType, vector3f);
	}
}
