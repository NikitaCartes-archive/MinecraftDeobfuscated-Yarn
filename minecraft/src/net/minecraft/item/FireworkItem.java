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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworkItem extends Item {
	public FireworkItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (!world.isClient) {
			ItemStack itemStack = itemUsageContext.getStack();
			Vec3d vec3d = itemUsageContext.getHitPos();
			FireworkEntity fireworkEntity = new FireworkEntity(world, vec3d.x, vec3d.y, vec3d.z, itemStack);
			world.spawnEntity(fireworkEntity);
			itemStack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.isFallFlying()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (!world.isClient) {
				world.spawnEntity(new FireworkEntity(world, itemStack, playerEntity));
				if (!playerEntity.abilities.creativeMode) {
					itemStack.decrement(1);
				}
			}

			return TypedActionResult.method_22427(playerEntity.getStackInHand(hand));
		} else {
			return TypedActionResult.method_22430(playerEntity.getStackInHand(hand));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
		CompoundTag compoundTag = itemStack.getSubTag("Fireworks");
		if (compoundTag != null) {
			if (compoundTag.containsKey("Flight", 99)) {
				list.add(
					new TranslatableText("item.minecraft.firework_rocket.flight").append(" ").append(String.valueOf(compoundTag.getByte("Flight"))).formatted(Formatting.GRAY)
				);
			}

			ListTag listTag = compoundTag.getList("Explosions", 10);
			if (!listTag.isEmpty()) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompoundTag(i);
					List<Text> list2 = Lists.<Text>newArrayList();
					FireworkChargeItem.appendFireworkTooltip(compoundTag2, list2);
					if (!list2.isEmpty()) {
						for (int j = 1; j < list2.size(); j++) {
							list2.set(j, new LiteralText("  ").append((Text)list2.get(j)).formatted(Formatting.GRAY));
						}

						list.addAll(list2);
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

		private Type(int j, String string2) {
			this.id = j;
			this.name = string2;
		}

		public int getId() {
			return this.id;
		}

		@Environment(EnvType.CLIENT)
		public String getName() {
			return this.name;
		}

		@Environment(EnvType.CLIENT)
		public static FireworkItem.Type byId(int i) {
			return i >= 0 && i < TYPES.length ? TYPES[i] : SMALL_BALL;
		}
	}
}
