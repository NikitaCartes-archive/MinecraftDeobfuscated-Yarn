package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipOptions;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireworksItem extends Item {
	public FireworksItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		if (!world.isRemote) {
			BlockPos blockPos = itemUsageContext.getPos();
			ItemStack itemStack = itemUsageContext.getItemStack();
			FireworkEntity fireworkEntity = new FireworkEntity(
				world,
				(double)((float)blockPos.getX() + itemUsageContext.getHitX()),
				(double)((float)blockPos.getY() + itemUsageContext.getHitY()),
				(double)((float)blockPos.getZ() + itemUsageContext.getHitZ()),
				itemStack
			);
			world.spawnEntity(fireworkEntity);
			itemStack.subtractAmount(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.isFallFlying()) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (!world.isRemote) {
				FireworkEntity fireworkEntity = new FireworkEntity(world, itemStack, playerEntity);
				world.spawnEntity(fireworkEntity);
				if (!playerEntity.abilities.creativeMode) {
					itemStack.subtractAmount(1);
				}
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, playerEntity.getStackInHand(hand));
		} else {
			return new TypedActionResult<>(ActionResult.PASS, playerEntity.getStackInHand(hand));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipOptions tooltipOptions) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("Fireworks");
		if (compoundTag != null) {
			if (compoundTag.containsKey("Flight", 99)) {
				list.add(
					new TranslatableTextComponent("item.minecraft.firework_rocket.flight")
						.append(" ")
						.append(String.valueOf(compoundTag.getByte("Flight")))
						.applyFormat(TextFormat.GRAY)
				);
			}

			ListTag listTag = compoundTag.getList("Explosions", 10);
			if (!listTag.isEmpty()) {
				for (int i = 0; i < listTag.size(); i++) {
					CompoundTag compoundTag2 = listTag.getCompoundTag(i);
					List<TextComponent> list2 = Lists.<TextComponent>newArrayList();
					FireworkChargeItem.method_7809(compoundTag2, list2);
					if (!list2.isEmpty()) {
						for (int j = 1; j < list2.size(); j++) {
							list2.set(j, new StringTextComponent("  ").append((TextComponent)list2.get(j)).applyFormat(TextFormat.GRAY));
						}

						list.addAll(list2);
					}
				}
			}
		}
	}

	public static enum class_1782 {
		field_7976(0, "small_ball"),
		field_7977(1, "large_ball"),
		field_7973(2, "star"),
		field_7974(3, "creeper"),
		field_7970(4, "burst");

		private static final FireworksItem.class_1782[] field_7975 = (FireworksItem.class_1782[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(arg -> arg.field_7972))
			.toArray(FireworksItem.class_1782[]::new);
		private final int field_7972;
		private final String field_7971;

		private class_1782(int j, String string2) {
			this.field_7972 = j;
			this.field_7971 = string2;
		}

		public int method_7816() {
			return this.field_7972;
		}

		@Environment(EnvType.CLIENT)
		public String method_7812() {
			return this.field_7971;
		}

		@Environment(EnvType.CLIENT)
		public static FireworksItem.class_1782 method_7813(int i) {
			return i >= 0 && i < field_7975.length ? field_7975[i] : field_7976;
		}
	}
}
