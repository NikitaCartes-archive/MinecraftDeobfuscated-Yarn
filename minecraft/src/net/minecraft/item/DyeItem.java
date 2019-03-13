package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
	private static final Map<DyeColor, DyeItem> dyes = Maps.newEnumMap(DyeColor.class);
	private final DyeColor color;

	public DyeItem(DyeColor dyeColor, Item.Settings settings) {
		super(settings);
		this.color = dyeColor;
		dyes.put(dyeColor, this);
	}

	@Override
	public boolean method_7847(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof SheepEntity) {
			SheepEntity sheepEntity = (SheepEntity)livingEntity;
			if (sheepEntity.isValid() && !sheepEntity.isSheared() && sheepEntity.method_6633() != this.color) {
				sheepEntity.method_6631(this.color);
				itemStack.subtractAmount(1);
			}

			return true;
		} else {
			return false;
		}
	}

	public DyeColor getColor() {
		return this.color;
	}

	public static DyeItem fromColor(DyeColor dyeColor) {
		return (DyeItem)dyes.get(dyeColor);
	}
}
