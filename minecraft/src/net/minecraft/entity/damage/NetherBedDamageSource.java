package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class NetherBedDamageSource extends DamageSource {
	protected NetherBedDamageSource() {
		super("netherBed");
		this.setScaledWithDifficulty();
		this.setExplosive();
	}

	@Override
	public Component getDeathMessage(LivingEntity livingEntity) {
		Component component = Components.bracketed(new TranslatableComponent("death.attack.netherBed.link"))
			.modifyStyle(
				style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.field_11749, "https://bugs.mojang.com/browse/MCPE-28723"))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new TextComponent("MCPE-28723")))
			);
		return new TranslatableComponent("death.attack.netherBed.message", livingEntity.getDisplayName(), component);
	}
}
