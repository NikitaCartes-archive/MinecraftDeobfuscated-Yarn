package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkRocketItem extends Item {
	public static final byte[] FLIGHT_VALUES = new byte[]{1, 2, 3};
	public static final double OFFSET_POS_MULTIPLIER = 0.15;

	public FireworkRocketItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!world.isClient) {
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = context.getHitPos();
			Direction direction = context.getSide();
			FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(
				world,
				context.getPlayer(),
				vec3d.x + (double)direction.getOffsetX() * 0.15,
				vec3d.y + (double)direction.getOffsetY() * 0.15,
				vec3d.z + (double)direction.getOffsetZ() * 0.15,
				itemStack
			);
			world.spawnEntity(fireworkRocketEntity);
			itemStack.decrement(1);
		}

		return ActionResult.success(world.isClient);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isFallFlying()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (!world.isClient) {
				FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(world, itemStack, user);
				world.spawnEntity(fireworkRocketEntity);
				itemStack.decrementUnlessCreative(1, user);
				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
		if (fireworksComponent != null) {
			fireworksComponent.appendTooltip(tooltip::add, context);
		}
	}
}
