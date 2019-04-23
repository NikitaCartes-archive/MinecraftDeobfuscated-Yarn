/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.sound.SoundEvents;

public class SittingAttackingPhase
extends AbstractSittingPhase {
    private int field_7049;

    public SittingAttackingPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void method_6853() {
        this.dragon.world.playSound(this.dragon.x, this.dragon.y, this.dragon.z, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.dragon.getSoundCategory(), 2.5f, 0.8f + this.dragon.getRand().nextFloat() * 0.3f, false);
    }

    @Override
    public void method_6855() {
        if (this.field_7049++ >= 40) {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
        }
    }

    @Override
    public void beginPhase() {
        this.field_7049 = 0;
    }

    public PhaseType<SittingAttackingPhase> getType() {
        return PhaseType.SITTING_ATTACKING;
    }
}

