package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkRocketItem extends Item {
	public static final String FIREWORKS_KEY = "Fireworks";
	public static final String EXPLOSION_KEY = "Explosion";
	public static final String EXPLOSIONS_KEY = "Explosions";
	public static final String FLIGHT_KEY = "Flight";
	public static final String TYPE_KEY = "Type";
	public static final String TRAIL_KEY = "Trail";
	public static final String FLICKER_KEY = "Flicker";
	public static final String COLORS_KEY = "Colors";
	public static final String FADE_COLORS_KEY = "FadeColors";
	public static final double field_30884 = 0.15;

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
				if (!user.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}

				user.incrementStat(Stats.USED.getOrCreateStat(this));
			}

			return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
		} else {
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound nbtCompound = stack.getSubNbt("Fireworks");
		if (nbtCompound != null) {
			if (nbtCompound.contains("Flight", NbtElement.NUMBER_TYPE)) {
				tooltip.add(
					Text.translatable("item.minecraft.firework_rocket.flight").append(" ").append(String.valueOf(nbtCompound.getByte("Flight"))).formatted(Formatting.GRAY)
				);
			}

			NbtList nbtList = nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE);
			if (!nbtList.isEmpty()) {
				for (int i = 0; i < nbtList.size(); i++) {
					NbtCompound nbtCompound2 = nbtList.getCompound(i);
					List<Text> list = Lists.<Text>newArrayList();
					FireworkStarItem.appendFireworkTooltip(nbtCompound2, list);
					if (!list.isEmpty()) {
						for (int j = 1; j < list.size(); j++) {
							list.set(j, Text.literal("  ").append((Text)list.get(j)).formatted(Formatting.GRAY));
						}

						tooltip.addAll(list);
					}
				}
			}
		}
	}

	@Override
	public ItemStack getDefaultStack() {
		ItemStack itemStack = new ItemStack(this);
		itemStack.getOrCreateNbt().putByte("Flight", (byte)1);
		return itemStack;
	}

	public static enum Type {
		SMALL_BALL(0, "small_ball"),
		LARGE_BALL(1, "large_ball"),
		STAR(2, "star"),
		CREEPER(3, "creeper"),
		BURST(4, "burst");

		private static final FireworkRocketItem.Type[] TYPES = (FireworkRocketItem.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(type -> type.id))
			.toArray(FireworkRocketItem.Type[]::new);
		private final int id;
		private final String name;

		private Type(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public static FireworkRocketItem.Type byId(int id) {
			return id >= 0 && id < TYPES.length ? TYPES[id] : SMALL_BALL;
		}
	}
}
