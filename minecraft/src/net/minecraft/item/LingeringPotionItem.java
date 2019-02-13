package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.TextComponent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LingeringPotionItem extends PotionItem {
	public LingeringPotionItem(Item.Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void buildTooltip(ItemStack itemStack, @Nullable World world, List<TextComponent> list, TooltipContext tooltipContext) {
		PotionUtil.buildTooltip(itemStack, list, 0.25F);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		ItemStack itemStack2 = playerEntity.abilities.creativeMode ? itemStack.copy() : itemStack.split(1);
		world.playSound(
			null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_14767, SoundCategory.field_15254, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
		);
		if (!world.isClient) {
			ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(world, playerEntity);
			thrownPotionEntity.setItemStack(itemStack2);
			thrownPotionEntity.calculateVelocity(playerEntity, playerEntity.pitch, playerEntity.yaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(thrownPotionEntity);
		}

		playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}
}
