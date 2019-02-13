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
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireworksItem extends Item {
	public FireworksItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (!world.isClient) {
			ItemStack itemStack = itemUsageContext.getItemStack();
			Vec3d vec3d = itemUsageContext.getPos();
			FireworkEntity fireworkEntity = new FireworkEntity(world, vec3d.x, vec3d.y, vec3d.z, itemStack);
			world.spawnEntity(fireworkEntity);
			itemStack.subtractAmount(1);
		}

		return ActionResult.field_5812;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.isFallFlying()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (!world.isClient) {
				world.spawnEntity(new FireworkEntity(world, itemStack, playerEntity));
				if (!playerEntity.abilities.creativeMode) {
					itemStack.subtractAmount(1);
				}
			}

			return new TypedActionResult<>(ActionResult.field_5812, playerEntity.getStackInHand(hand));
		} else {
			return new TypedActionResult<>(ActionResult.PASS, playerEntity.getStackInHand(hand));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("Fireworks");
		if (compoundTag != null) {
			if (compoundTag.containsKey("Flight", 99)) {
				list.add(
					new TranslatableTextComponent("item.minecraft.firework_rocket.flight")
						.append(" ")
						.append(String.valueOf(compoundTag.getByte("Flight")))
						.applyFormat(TextFormat.field_1080)
				);
			}

			ListTag listTag = compoundTag.getList("Explosions", 10);
			if (!listTag.isEmpty()) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompoundTag(i);
					List<TextComponent> list2 = Lists.<TextComponent>newArrayList();
					FireworkChargeItem.buildTooltip(compoundTag2, list2);
					if (!list2.isEmpty()) {
						for (int j = 1; j < list2.size(); j++) {
							list2.set(j, new StringTextComponent("  ").append((TextComponent)list2.get(j)).applyFormat(TextFormat.field_1080));
						}

						list.addAll(list2);
					}
				}
			}
		}
	}

	public static enum Type {
		field_7976(0, "small_ball"),
		field_7977(1, "large_ball"),
		field_7973(2, "star"),
		field_7974(3, "creeper"),
		field_7970(4, "burst");

		private static final FireworksItem.Type[] TYPES = (FireworksItem.Type[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(type -> type.id))
			.toArray(FireworksItem.Type[]::new);
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
		public static FireworksItem.Type fromId(int i) {
			return i >= 0 && i < TYPES.length ? TYPES[i] : field_7976;
		}
	}
}
