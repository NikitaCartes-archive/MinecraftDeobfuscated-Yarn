/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ProjectileDamageSource
extends EntityDamageSource {
    @Nullable
    private final Entity attacker;

    public ProjectileDamageSource(String name, Entity projectile, @Nullable Entity attacker) {
        super(name, projectile);
        this.attacker = attacker;
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
    public Text getDeathMessage(LivingEntity entity) {
        ItemStack itemStack;
        Text text = this.attacker == null ? this.source.getDisplayName() : this.attacker.getDisplayName();
        Entity entity2 = this.attacker;
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        ItemStack itemStack2 = itemStack;
        String string = "death.attack." + this.name;
        if (!itemStack2.isEmpty() && itemStack2.hasCustomName()) {
            String string2 = string + ".item";
            return Text.translatable(string2, entity.getDisplayName(), text, itemStack2.toHoverableText());
        }
        return Text.translatable(string, entity.getDisplayName(), text);
    }
}

