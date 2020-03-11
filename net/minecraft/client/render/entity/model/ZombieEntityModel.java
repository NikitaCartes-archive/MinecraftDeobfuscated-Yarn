/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(value=EnvType.CLIENT)
public class ZombieEntityModel<T extends ZombieEntity>
extends AbstractZombieModel<T> {
    public ZombieEntityModel(float scale, boolean bl) {
        this(scale, 0.0f, 64, bl ? 32 : 64);
    }

    protected ZombieEntityModel(float f, float g, int i, int j) {
        super(f, g, i, j);
    }

    @Override
    public boolean isAttacking(T zombieEntity) {
        return ((MobEntity)zombieEntity).isAttacking();
    }
}

