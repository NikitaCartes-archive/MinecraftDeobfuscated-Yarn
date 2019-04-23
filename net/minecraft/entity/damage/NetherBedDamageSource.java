/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class NetherBedDamageSource
extends DamageSource {
    protected NetherBedDamageSource() {
        super("netherBed");
        this.setScaledWithDifficulty();
        this.setExplosive();
    }

    @Override
    public Component getDeathMessage(LivingEntity livingEntity) {
        Component component = Components.bracketed(new TranslatableComponent("death.attack.netherBed.link", new Object[0])).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("MCPE-28723"))));
        return new TranslatableComponent("death.attack.netherBed.message", livingEntity.getDisplayName(), component);
    }
}

