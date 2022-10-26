/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityDamageSource
extends DamageSource {
    protected final Entity source;
    private boolean thorns;

    public EntityDamageSource(String name, Entity source) {
        super(name);
        this.source = source;
    }

    public EntityDamageSource setThorns() {
        this.thorns = true;
        return this;
    }

    public boolean isThorns() {
        return this.thorns;
    }

    @Override
    public Entity getAttacker() {
        return this.source;
    }

    @Override
    public Text getDeathMessage(LivingEntity entity) {
        ItemStack itemStack;
        Entity entity2 = this.source;
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            itemStack = livingEntity.getMainHandStack();
        } else {
            itemStack = ItemStack.EMPTY;
        }
        ItemStack itemStack2 = itemStack;
        String string = "death.attack." + this.name;
        if (!itemStack2.isEmpty() && itemStack2.hasCustomName()) {
            return Text.translatable(string + ".item", entity.getDisplayName(), this.source.getDisplayName(), itemStack2.toHoverableText());
        }
        return Text.translatable(string, entity.getDisplayName(), this.source.getDisplayName());
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d getPosition() {
        return this.source.getPos();
    }

    @Override
    public String toString() {
        return "EntityDamageSource (" + this.source + ")";
    }
}

