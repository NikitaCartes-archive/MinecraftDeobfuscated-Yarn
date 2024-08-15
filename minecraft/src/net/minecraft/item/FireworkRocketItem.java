package net.minecraft.item;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkRocketItem extends Item implements ProjectileItem {
	public static final byte[] FLIGHT_VALUES = new byte[]{1, 2, 3};
	public static final double OFFSET_POS_MULTIPLIER = 0.15;

	public FireworkRocketItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (world instanceof ServerWorld serverWorld) {
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = context.getHitPos();
			Direction direction = context.getSide();
			ProjectileEntity.spawn(
				new FireworkRocketEntity(
					world,
					context.getPlayer(),
					vec3d.x + (double)direction.getOffsetX() * 0.15,
					vec3d.y + (double)direction.getOffsetY() * 0.15,
					vec3d.z + (double)direction.getOffsetZ() * 0.15,
					itemStack
				),
				serverWorld,
				itemStack
			);
			itemStack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user.isFallFlying()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (world instanceof ServerWorld serverWorld) {
				ProjectileEntity.spawn(new FireworkRocketEntity(world, itemStack, user), serverWorld, itemStack);
				itemStack.decrementUnlessCreative(1, user);
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
		if (fireworksComponent != null) {
			fireworksComponent.appendTooltip(context, tooltip::add, type);
		}
	}

	@Override
	public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
		return new FireworkRocketEntity(world, stack.copyWithCount(1), pos.getX(), pos.getY(), pos.getZ(), true);
	}

	@Override
	public ProjectileItem.Settings getProjectileSettings() {
		return ProjectileItem.Settings.builder().positionFunction(FireworkRocketItem::position).uncertainty(1.0F).power(0.5F).overrideDispenseEvent(1004).build();
	}

	private static Vec3d position(BlockPointer pointer, Direction facing) {
		return pointer.centerPos()
			.add(
				(double)facing.getOffsetX() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getWidth() / 2.0),
				(double)facing.getOffsetY() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getHeight() / 2.0)
					- (double)EntityType.FIREWORK_ROCKET.getHeight() / 2.0,
				(double)facing.getOffsetZ() * (0.5000099999997474 - (double)EntityType.FIREWORK_ROCKET.getWidth() / 2.0)
			);
	}
}
