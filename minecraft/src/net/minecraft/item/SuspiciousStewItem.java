package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class SuspiciousStewItem extends Item {
	public static final int DEFAULT_DURATION = 160;

	public SuspiciousStewItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		super.appendTooltip(stack, world, tooltip, context);
		if (context.isCreative()) {
			List<StatusEffectInstance> list = new ArrayList();
			SuspiciousStewEffectsComponent suspiciousStewEffectsComponent = stack.getOrDefault(
				DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT
			);

			for (SuspiciousStewEffectsComponent.StewEffect stewEffect : suspiciousStewEffectsComponent.effects()) {
				list.add(stewEffect.createStatusEffectInstance());
			}

			PotionContentsComponent.buildTooltip(list, tooltip::add, 1.0F, world == null ? 20.0F : world.getTickManager().getTickRate());
		}
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
		ItemStack itemStack = super.finishUsing(stack, world, user);
		SuspiciousStewEffectsComponent suspiciousStewEffectsComponent = stack.getOrDefault(
			DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffectsComponent.DEFAULT
		);

		for (SuspiciousStewEffectsComponent.StewEffect stewEffect : suspiciousStewEffectsComponent.effects()) {
			user.addStatusEffect(stewEffect.createStatusEffectInstance());
		}

		return user.isInCreativeMode() ? itemStack : new ItemStack(Items.BOWL);
	}
}
