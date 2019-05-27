package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;

public class DyeItem extends Item {
	private static final Map<DyeColor, DyeItem> DYES = Maps.newEnumMap(DyeColor.class);
	private final DyeColor color;

	public DyeItem(DyeColor dyeColor, Item.Settings settings) {
		super(settings);
		this.color = dyeColor;
		DYES.put(dyeColor, this);
	}

	@Override
	public boolean useOnEntity(ItemStack itemStack, PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
		if (livingEntity instanceof SheepEntity) {
			SheepEntity sheepEntity = (SheepEntity)livingEntity;
			if (sheepEntity.isAlive() && !sheepEntity.isSheared() && sheepEntity.getColor() != this.color) {
				sheepEntity.setColor(this.color);
				itemStack.decrement(1);
			}

			return true;
		} else {
			return false;
		}
	}

	public DyeColor getColor() {
		return this.color;
	}

	public static DyeItem byColor(DyeColor dyeColor) {
		return (DyeItem)DYES.get(dyeColor);
	}
}
