package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
	private static final Map<DyeColor, DyeItem> DYES = Maps.newEnumMap(DyeColor.class);
	private final DyeColor color;

	public DyeItem(DyeColor color, Item.Settings settings) {
		super(settings);
		this.color = color;
		DYES.put(color, this);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof SheepEntity sheepEntity && sheepEntity.isAlive() && !sheepEntity.isSheared() && sheepEntity.getColor() != this.color) {
			sheepEntity.world.playSoundFromEntity(user, sheepEntity, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			if (!user.world.isClient) {
				sheepEntity.setColor(this.color);
				stack.decrement(1);
			}

			return ActionResult.success(user.world.isClient);
		}

		return ActionResult.PASS;
	}

	public DyeColor getColor() {
		return this.color;
	}

	public static DyeItem byColor(DyeColor color) {
		return (DyeItem)DYES.get(color);
	}
}
