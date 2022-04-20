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
        ItemStack itemStack = this.source instanceof LivingEntity ? ((LivingEntity)this.source).getMainHandStack() : ItemStack.EMPTY;
        String string = "death.attack." + this.name;
        if (!itemStack.isEmpty() && itemStack.hasCustomName()) {
            return Text.method_43469(string + ".item", entity.getDisplayName(), this.source.getDisplayName(), itemStack.toHoverableText());
        }
        return Text.method_43469(string, entity.getDisplayName(), this.source.getDisplayName());
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

