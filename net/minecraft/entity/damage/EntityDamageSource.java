/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class EntityDamageSource
extends DamageSource {
    @Nullable
    protected final Entity source;
    private boolean field_5880;

    public EntityDamageSource(String string, @Nullable Entity entity) {
        super(string);
        this.source = entity;
    }

    public EntityDamageSource method_5550() {
        this.field_5880 = true;
        return this;
    }

    public boolean method_5549() {
        return this.field_5880;
    }

    @Override
    @Nullable
    public Entity getAttacker() {
        return this.source;
    }

    @Override
    public Component getDeathMessage(LivingEntity livingEntity) {
        ItemStack itemStack = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        if (!itemStack.isEmpty() && itemStack.hasDisplayName()) {
            return new TranslatableComponent(string + ".item", livingEntity.getDisplayName(), this.source.getDisplayName(), itemStack.toTextComponent());
        }
        return new TranslatableComponent(string, livingEntity.getDisplayName(), this.source.getDisplayName());
    }

    @Override
    public boolean isScaledWithDifficulty() {
        return this.source != null && this.source instanceof LivingEntity && !(this.source instanceof PlayerEntity);
    }

    @Override
    @Nullable
    public Vec3d method_5510() {
        return new Vec3d(this.source.x, this.source.y, this.source.z);
    }
}

