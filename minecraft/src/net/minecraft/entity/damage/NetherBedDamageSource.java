package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class NetherBedDamageSource extends DamageSource {
	protected NetherBedDamageSource() {
		super("netherBed");
		this.setScaledWithDifficulty();
		this.setExplosive();
	}

	@Override
	public Text getDeathMessage(LivingEntity livingEntity) {
		Text text = Texts.bracketed(new TranslatableText("death.attack.netherBed.link"))
			.styled(
				style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.field_11749, "https://bugs.mojang.com/browse/MCPE-28723"))
						.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText("MCPE-28723")))
			);
		return new TranslatableText("death.attack.netherBed.message", livingEntity.getDisplayName(), text);
	}
}
