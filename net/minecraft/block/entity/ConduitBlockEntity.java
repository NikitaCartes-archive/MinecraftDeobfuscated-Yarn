/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class ConduitBlockEntity
extends BlockEntity
implements Tickable {
    private static final Block[] ACTIVATING_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int ticks;
    private float ticksActive;
    private boolean active;
    private boolean eyeOpen;
    private final List<BlockPos> activatingBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity targetEntity;
    @Nullable
    private UUID targetUuid;
    private long nextAmbientSoundTime;

    public ConduitBlockEntity() {
        this(BlockEntityType.CONDUIT);
    }

    public ConduitBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.targetUuid = tag.containsUuidNew("Target") ? tag.getUuidNew("Target") : null;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.targetEntity != null) {
            tag.putUuidNew("Target", this.targetEntity.getUuid());
        }
        return tag;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 5, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    public void tick() {
        ++this.ticks;
        long l = this.world.getTime();
        if (l % 40L == 0L) {
            this.setActive(this.updateActivatingBlocks());
            if (!this.world.isClient && this.isActive()) {
                this.givePlayersEffects();
                this.attackHostileEntity();
            }
        }
        if (l % 80L == 0L && this.isActive()) {
            this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT);
        }
        if (l > this.nextAmbientSoundTime && this.isActive()) {
            this.nextAmbientSoundTime = l + 60L + (long)this.world.getRandom().nextInt(40);
            this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT);
        }
        if (this.world.isClient) {
            this.updateTargetEntity();
            this.spawnNautilusParticles();
            if (this.isActive()) {
                this.ticksActive += 1.0f;
            }
        }
    }

    private boolean updateActivatingBlocks() {
        int k;
        int j;
        int i;
        this.activatingBlocks.clear();
        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPos blockPos = this.pos.add(i, j, k);
                    if (this.world.isWater(blockPos)) continue;
                    return false;
                }
            }
        }
        for (i = -2; i <= 2; ++i) {
            for (j = -2; j <= 2; ++j) {
                for (k = -2; k <= 2; ++k) {
                    int l = Math.abs(i);
                    int m = Math.abs(j);
                    int n = Math.abs(k);
                    if (l <= 1 && m <= 1 && n <= 1 || (i != 0 || m != 2 && n != 2) && (j != 0 || l != 2 && n != 2) && (k != 0 || l != 2 && m != 2)) continue;
                    BlockPos blockPos2 = this.pos.add(i, j, k);
                    BlockState blockState = this.world.getBlockState(blockPos2);
                    for (Block block : ACTIVATING_BLOCKS) {
                        if (!blockState.isOf(block)) continue;
                        this.activatingBlocks.add(blockPos2);
                    }
                }
            }
        }
        this.setEyeOpen(this.activatingBlocks.size() >= 42);
        return this.activatingBlocks.size() >= 16;
    }

    private void givePlayersEffects() {
        int m;
        int l;
        int i = this.activatingBlocks.size();
        int j = i / 7 * 16;
        int k = this.pos.getX();
        Box box = new Box(k, l = this.pos.getY(), m = this.pos.getZ(), k + 1, l + 1, m + 1).expand(j).stretch(0.0, this.world.getHeight(), 0.0);
        List<PlayerEntity> list = this.world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (list.isEmpty()) {
            return;
        }
        for (PlayerEntity playerEntity : list) {
            if (!this.pos.isWithinDistance(playerEntity.getBlockPos(), (double)j) || !playerEntity.isTouchingWaterOrRain()) continue;
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private void attackHostileEntity() {
        LivingEntity livingEntity2 = this.targetEntity;
        int i = this.activatingBlocks.size();
        if (i < 42) {
            this.targetEntity = null;
        } else if (this.targetEntity == null && this.targetUuid != null) {
            this.targetEntity = this.findTargetEntity();
            this.targetUuid = null;
        } else if (this.targetEntity == null) {
            List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getAttackZone(), livingEntity -> livingEntity instanceof Monster && livingEntity.isTouchingWaterOrRain());
            if (!list.isEmpty()) {
                this.targetEntity = list.get(this.world.random.nextInt(list.size()));
            }
        } else if (!this.targetEntity.isAlive() || !this.pos.isWithinDistance(this.targetEntity.getBlockPos(), 8.0)) {
            this.targetEntity = null;
        }
        if (this.targetEntity != null) {
            this.world.playSound(null, this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            this.targetEntity.damage(DamageSource.MAGIC, 4.0f);
        }
        if (livingEntity2 != this.targetEntity) {
            BlockState blockState = this.getCachedState();
            this.world.updateListeners(this.pos, blockState, blockState, 2);
        }
    }

    private void updateTargetEntity() {
        if (this.targetUuid == null) {
            this.targetEntity = null;
        } else if (this.targetEntity == null || !this.targetEntity.getUuid().equals(this.targetUuid)) {
            this.targetEntity = this.findTargetEntity();
            if (this.targetEntity == null) {
                this.targetUuid = null;
            }
        }
    }

    private Box getAttackZone() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        return new Box(i, j, k, i + 1, j + 1, k + 1).expand(8.0);
    }

    @Nullable
    private LivingEntity findTargetEntity() {
        List<LivingEntity> list = this.world.getEntities(LivingEntity.class, this.getAttackZone(), livingEntity -> livingEntity.getUuid().equals(this.targetUuid));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private void spawnNautilusParticles() {
        float g;
        float f;
        Random random = this.world.random;
        double d = MathHelper.sin((float)(this.ticks + 35) * 0.1f) / 2.0f + 0.5f;
        d = (d * d + d) * (double)0.3f;
        Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 1.5 + d, (double)this.pos.getZ() + 0.5);
        for (BlockPos blockPos : this.activatingBlocks) {
            if (random.nextInt(50) != 0) continue;
            f = -0.5f + random.nextFloat();
            g = -2.0f + random.nextFloat();
            float h = -0.5f + random.nextFloat();
            BlockPos blockPos2 = blockPos.subtract(this.pos);
            Vec3d vec3d2 = new Vec3d(f, g, h).add(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            this.world.addParticle(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
        }
        if (this.targetEntity != null) {
            Vec3d vec3d3 = new Vec3d(this.targetEntity.getX(), this.targetEntity.getEyeY(), this.targetEntity.getZ());
            float i = (-0.5f + random.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            f = -1.0f + random.nextFloat() * this.targetEntity.getHeight();
            g = (-0.5f + random.nextFloat()) * (3.0f + this.targetEntity.getWidth());
            Vec3d vec3d4 = new Vec3d(i, f, g);
            this.world.addParticle(ParticleTypes.NAUTILUS, vec3d3.x, vec3d3.y, vec3d3.z, vec3d4.x, vec3d4.y, vec3d4.z);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isEyeOpen() {
        return this.eyeOpen;
    }

    private void setActive(boolean active) {
        if (active != this.active) {
            this.playSound(active ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE);
        }
        this.active = active;
    }

    private void setEyeOpen(boolean eyeOpen) {
        this.eyeOpen = eyeOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRotation(float tickDelta) {
        return (this.ticksActive + tickDelta) * -0.0375f;
    }

    public void playSound(SoundEvent soundEvent) {
        this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}

