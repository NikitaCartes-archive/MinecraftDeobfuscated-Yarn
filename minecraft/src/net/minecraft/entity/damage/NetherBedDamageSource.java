package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.event.HoverEvent;

public class NetherBedDamageSource extends DamageSource {
	protected NetherBedDamageSource() {
		super("netherBed");
		this.setScaledWithDifficulty();
		this.setExplosive();
	}

	@Override
	public TextComponent getDeathMessage(LivingEntity livingEntity) {
		TextComponent textComponent = TextFormatter.bracketed(new TranslatableTextComponent("death.attack.netherBed.link"))
			.modifyStyle(
				style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")))
			);
		return new TranslatableTextComponent("death.attack.netherBed.message", livingEntity.getDisplayName(), textComponent);
	}
}
