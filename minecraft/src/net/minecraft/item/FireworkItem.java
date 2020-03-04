package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkItem extends Item {
	public FireworkItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!world.isClient) {
			ItemStack itemStack = context.getStack();
			Vec3d vec3d = context.getHitPos();
			Direction direction = context.getSide();
			FireworkEntity fireworkEntity = new FireworkEntity(
				world,
				context.getPlayer(),
				vec3d.x + (double)direction.getOffsetX() * 0.15,
				vec3d.y + (double)direction.getOffsetY() * 0.15,
				vec3d.z + (double)direction.getOffsetZ() * 0.15,
				itemStack
			);
			world.spawnEntity(fireworkEntity);
			itemStack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isFallFlying()) {
			ItemStack itemStack = user.getStackInHand(hand);
			if (!world.isClient) {
				world.spawnEntity(new FireworkEntity(world, itemStack, user));
				if (!user.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return TypedActionResult.success(user.getStackInHand(hand));
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CompoundTag compoundTag = stack.getSubTag("Fireworks");
		if (compoundTag != null) {
			if (compoundTag.contains("Flight", 99)) {
				tooltip.add(
					new TranslatableText("item.minecraft.firework_rocket.flight").append(" ").append(String.valueOf(compoundTag.getByte("Flight"))).formatted(Formatting.GRAY)
				);
			}

			ListTag listTag = compoundTag.getList("Explosions", 10);
			if (!listTag.isEmpty()) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompound(i);
					List<Text> list = Lists.<Text>newArrayList();
					FireworkChargeItem.appendFireworkTooltip(compoundTag2, list);
					if (!list.isEmpty()) {
						for (int j = 1; j < list.size(); j++) {
							list.set(j, new LiteralText("  ").append((Text)list.get(j)).formatted(Formatting.GRAY));
						}

						tooltip.addAll(list);
					}
				}
			}
		}
	}

	public static enum Type {
		SMALL_BALL(0, "small_ball"),
		LARGE_BALL(1, "large_ball"),
		STAR(2, "star"),
		CREEPER(3, "creeper"),
		BURST(4, "burst");

		private static final FireworkItem.Type[] TYPES = (FireworkItem.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(type -> type.id))
			.toArray(FireworkItem.Type[]::new);
		private final int id;
		private final String name;

		private Type(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		@Environment(EnvType.CLIENT)
		public String getName() {
			return this.name;
		}

		@Environment(EnvType.CLIENT)
		public static FireworkItem.Type byId(int id) {
			return id >= 0 && id < TYPES.length ? TYPES[id] : SMALL_BALL;
		}
	}
}
