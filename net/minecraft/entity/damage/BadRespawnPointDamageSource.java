/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.PositionedDamageSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.Vec3d;

public class BadRespawnPointDamageSource
extends PositionedDamageSource {
    protected BadRespawnPointDamageSource(Vec3d pos) {
        super("badRespawnPoint", pos);
        this.setScaledWithDifficulty();
        this.setExplosive();
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        MutableText text = Texts.bracketed(Text.translatable("death.attack.badRespawnPoint.link")).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("MCPE-28723"))));
        return Text.translatable("death.attack.badRespawnPoint.message", entity.getDisplayName(), text);
    }
}

