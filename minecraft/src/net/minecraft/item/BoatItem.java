package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.FluidRayTraceMode;
import net.minecraft.world.World;

public class BoatItem extends Item {
	private final BoatEntity.Type type;

	public BoatItem(BoatEntity.Type type, Item.Settings settings) {
		super(settings);
		this.type = type;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		float f = 1.0F;
		float g = MathHelper.lerp(1.0F, playerEntity.prevPitch, playerEntity.pitch);
		float h = MathHelper.lerp(1.0F, playerEntity.prevYaw, playerEntity.yaw);
		double d = MathHelper.lerp(1.0, playerEntity.prevX, playerEntity.x);
		double e = MathHelper.lerp(1.0, playerEntity.prevY, playerEntity.y) + (double)playerEntity.getEyeHeight();
		double i = MathHelper.lerp(1.0, playerEntity.prevZ, playerEntity.z);
		Vec3d vec3d = new Vec3d(d, e, i);
		float j = MathHelper.cos(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float k = MathHelper.sin(-h * (float) (Math.PI / 180.0) - (float) Math.PI);
		float l = -MathHelper.cos(-g * (float) (Math.PI / 180.0));
		float m = MathHelper.sin(-g * (float) (Math.PI / 180.0));
		float n = k * l;
		float p = j * l;
		double q = 5.0;
		Vec3d vec3d2 = vec3d.add((double)n * 5.0, (double)m * 5.0, (double)p * 5.0);
		HitResult hitResult = world.rayTrace(vec3d, vec3d2, FluidRayTraceMode.field_1347);
		if (hitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			Vec3d vec3d3 = playerEntity.getRotationVec(1.0F);
			boolean bl = false;
			List<Entity> list = world.getVisibleEntities(playerEntity, playerEntity.getBoundingBox().stretch(vec3d3.x * 5.0, vec3d3.y * 5.0, vec3d3.z * 5.0).expand(1.0));

			for (int r = 0; r < list.size(); r++) {
				Entity entity = (Entity)list.get(r);
				if (entity.doesCollide()) {
					BoundingBox boundingBox = entity.getBoundingBox().expand((double)entity.method_5871());
					if (boundingBox.contains(vec3d)) {
						bl = true;
					}
				}
			}

			if (bl) {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			} else if (hitResult.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = hitResult.getBlockPos();
				Block block = world.getBlockState(blockPos).getBlock();
				BoatEntity boatEntity = new BoatEntity(world, hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
				boatEntity.setBoatType(this.type);
				boatEntity.yaw = playerEntity.yaw;
				if (!world.method_8587(boatEntity, boatEntity.getBoundingBox().expand(-0.1))) {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
				} else {
					if (!world.isRemote) {
						world.spawnEntity(boatEntity);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.incrementStat(Stats.field_15372.method_14956(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			} else {
				return new TypedActionResult<>(ActionResult.PASS, itemStack);
			}
		}
	}
}
