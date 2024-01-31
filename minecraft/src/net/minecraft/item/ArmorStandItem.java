package net.minecraft.item;

import java.util.function.Consumer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ArmorStandItem extends Item {
	public ArmorStandItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		Direction direction = context.getSide();
		if (direction == Direction.DOWN) {
			return ActionResult.FAIL;
		} else {
			World world = context.getWorld();
			ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
			Box box = EntityType.ARMOR_STAND.getDimensions().getBoxAt(vec3d.getX(), vec3d.getY(), vec3d.getZ());
			if (world.isSpaceEmpty(null, box) && world.getOtherEntities(null, box).isEmpty()) {
				if (world instanceof ServerWorld serverWorld) {
					Consumer<ArmorStandEntity> consumer = EntityType.copier(serverWorld, itemStack, context.getPlayer());
					ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND.create(serverWorld, consumer, blockPos, SpawnReason.SPAWN_EGG, true, true);
					if (armorStandEntity == null) {
						return ActionResult.FAIL;
					}

					float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
					armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0F);
					serverWorld.spawnEntityAndPassengers(armorStandEntity);
					world.playSound(
						null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F
					);
					armorStandEntity.emitGameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
				}

				itemStack.decrement(1);
				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.FAIL;
			}
		}
	}
}
