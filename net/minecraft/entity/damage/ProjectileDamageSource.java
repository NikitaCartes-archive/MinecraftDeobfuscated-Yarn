/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.Nullable;

public class ProjectileDamageSource
extends EntityDamageSource {
    private final Entity attacker;

    public ProjectileDamageSource(String string, Entity entity, @Nullable Entity entity2) {
        super(string, entity);
        this.attacker = entity2;
    }

    @Override
    @Nullable
    public Entity getSource() {
        return this.source;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.attacker;
    }

    @Override
    public Component getDeathMessage(LivingEntity livingEntity) {
        Component component = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
        ItemStack itemStack = this.attacker instanceof LivingEntity ? ((LivingEntity)this.attacker).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        String string2 = string + ".item";
        if (!itemStack.isEmpty() && itemStack.hasDisplayName()) {
            return new TranslatableComponent(string2, livingEntity.getDisplayName(), component, itemStack.toTextComponent());
        }
        return new TranslatableComponent(string, livingEntity.getDisplayName(), component);
    }
}

