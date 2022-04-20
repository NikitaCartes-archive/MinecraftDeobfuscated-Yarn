package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class BadRespawnPointDamageSource extends DamageSource {
	protected BadRespawnPointDamageSource() {
		super("badRespawnPoint");
		this.setScaledWithDifficulty();
		this.setExplosive();
	}

	@Override
	public Text getDeathMessage(LivingEntity entity) {
		Text text = Texts.bracketed(Text.translatable("death.attack.badRespawnPoint.link"))
			.styled(
				style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723"))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("MCPE-28723")))
			);
		return Text.translatable("death.attack.badRespawnPoint.message", entity.getDisplayName(), text);
	}
}
