/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BellBlockEntity
extends BlockEntity
implements Tickable {
    private long field_19155;
    public int ringTicks;
    public boolean isRinging;
    public Direction lastSideHit;
    private List<LivingEntity> field_19156;
    private boolean field_19157;
    private int field_19158;

    public BellBlockEntity() {
        super(BlockEntityType.BELL);
    }

    @Override
    public boolean onBlockAction(int i, int j) {
        if (i == 1) {
            this.method_20219();
            this.field_19158 = 0;
            this.lastSideHit = Direction.byId(j);
            this.ringTicks = 0;
            this.isRinging = true;
            return true;
        }
        return super.onBlockAction(i, j);
    }

    @Override
    public void tick() {
        if (this.isRinging) {
            ++this.ringTicks;
        }
        BlockPos blockPos = this.getPos();
        if (this.ringTicks >= 50) {
            this.isRinging = false;
            this.ringTicks = 0;
        }
        if (this.ringTicks >= 5 && this.field_19158 == 0) {
            this.method_20216(this.world, blockPos);
        }
        if (this.field_19157) {
            if (this.field_19158 < 40) {
                ++this.field_19158;
            } else {
                this.field_19157 = false;
                this.method_20218(this.world, blockPos);
            }
        }
    }

    public void activate(Direction direction) {
        BlockPos blockPos = this.getPos();
        this.lastSideHit = direction;
        if (this.isRinging) {
            this.ringTicks = 0;
        } else {
            this.isRinging = true;
        }
        this.world.addBlockAction(blockPos, this.getCachedState().getBlock(), 1, direction.getId());
    }

    private void method_20219() {
        BlockPos blockPos = this.getPos();
        if (this.world.getTime() > this.field_19155 + 60L || this.field_19156 == null) {
            this.field_19155 = this.world.getTime();
            BoundingBox boundingBox = new BoundingBox(blockPos).expand(48.0);
            this.field_19156 = this.world.getEntities(LivingEntity.class, boundingBox);
        }
        if (!this.world.isClient) {
            for (LivingEntity livingEntity : this.field_19156) {
                if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.isWithinDistance(livingEntity.getPos(), 32.0)) continue;
                livingEntity.getBrain().putMemory(MemoryModuleType.HEARD_BELL_TIME, this.world.getTime());
            }
        }
    }

    private void method_20216(World world, BlockPos blockPos) {
        for (LivingEntity livingEntity : this.field_19156) {
            if (!livingEntity.isAlive() || livingEntity.removed || !blockPos.isWithinDistance(livingEntity.getPos(), 32.0) || !livingEntity.getType().isTaggedWith(EntityTypeTags.RAIDERS)) continue;
            this.field_19157 = true;
        }
        if (this.field_19157) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private void method_20218(World world, BlockPos blockPos) {
        int i = 16700985;
        int j = (int)this.field_19156.stream().filter(livingEntity -> blockPos.isWithinDistance(livingEntity.getPos(), 32.0)).count();
        for (LivingEntity livingEntity2 : this.field_19156) {
            if (!livingEntity2.isAlive() || livingEntity2.removed || !blockPos.isWithinDistance(livingEntity2.getPos(), 32.0) || !livingEntity2.getType().isTaggedWith(EntityTypeTags.RAIDERS)) continue;
            if (!world.isClient) {
                livingEntity2.addPotionEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));
                continue;
            }
            float f = 1.0f;
            float g = MathHelper.sqrt((livingEntity2.x - (double)blockPos.getX()) * (livingEntity2.x - (double)blockPos.getX()) + (livingEntity2.z - (double)blockPos.getZ()) * (livingEntity2.z - (double)blockPos.getZ()));
            double d = (double)((float)blockPos.getX() + 0.5f) + (double)(1.0f / g) * (livingEntity2.x - (double)blockPos.getX());
            double e = (double)((float)blockPos.getZ() + 0.5f) + (double)(1.0f / g) * (livingEntity2.z - (double)blockPos.getZ());
            int k = MathHelper.clamp((j - 21) / -2, 3, 15);
            for (int l = 0; l < k; ++l) {
                double h = (double)((i += 5) >> 16 & 0xFF) / 255.0;
                double m = (double)(i >> 8 & 0xFF) / 255.0;
                double n = (double)(i & 0xFF) / 255.0;
                world.addParticle(ParticleTypes.ENTITY_EFFECT, d, (float)blockPos.getY() + 0.5f, e, h, m, n);
            }
        }
    }
}

