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
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConduitBlockEntity
extends BlockEntity {
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

    public ConduitBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.CONDUIT, blockPos, blockState);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.targetUuid = tag.containsUuid("Target") ? tag.getUuid("Target") : null;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.targetEntity != null) {
            tag.putUuid("Target", this.targetEntity.getUuid());
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

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, ConduitBlockEntity conduitBlockEntity) {
        ++conduitBlockEntity.ticks;
        long l = world.getTime();
        List<BlockPos> list = conduitBlockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            conduitBlockEntity.active = ConduitBlockEntity.updateActivatingBlocks(world, blockPos, list);
            ConduitBlockEntity.method_31676(conduitBlockEntity, list);
        }
        ConduitBlockEntity.updateTargetEntity(world, blockPos, conduitBlockEntity);
        ConduitBlockEntity.spawnNautilusParticles(world, blockPos, list, conduitBlockEntity.targetEntity, conduitBlockEntity.ticks);
        if (conduitBlockEntity.isActive()) {
            conduitBlockEntity.ticksActive += 1.0f;
        }
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, ConduitBlockEntity conduitBlockEntity) {
        ++conduitBlockEntity.ticks;
        long l = world.getTime();
        List<BlockPos> list = conduitBlockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            boolean bl = ConduitBlockEntity.updateActivatingBlocks(world, blockPos, list);
            if (bl != conduitBlockEntity.active) {
                SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            conduitBlockEntity.active = bl;
            ConduitBlockEntity.method_31676(conduitBlockEntity, list);
            if (bl) {
                ConduitBlockEntity.givePlayersEffects(world, blockPos, list);
                ConduitBlockEntity.attackHostileEntity(world, blockPos, blockState, list, conduitBlockEntity);
            }
        }
        if (conduitBlockEntity.isActive()) {
            if (l % 80L == 0L) {
                world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            if (l > conduitBlockEntity.nextAmbientSoundTime) {
                conduitBlockEntity.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
                world.playSound(null, blockPos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static void method_31676(ConduitBlockEntity conduitBlockEntity, List<BlockPos> list) {
        conduitBlockEntity.setEyeOpen(list.size() >= 42);
    }

    private static boolean updateActivatingBlocks(World world, BlockPos blockPos, List<BlockPos> list) {
        int k;
        int j;
        int i;
        list.clear();
        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPos blockPos2 = blockPos.add(i, j, k);
                    if (world.isWater(blockPos2)) continue;
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
                    BlockPos blockPos3 = blockPos.add(i, j, k);
                    BlockState blockState = world.getBlockState(blockPos3);
                    for (Block block : ACTIVATING_BLOCKS) {
                        if (!blockState.isOf(block)) continue;
                        list.add(blockPos3);
                    }
                }
            }
        }
        return list.size() >= 16;
    }

    private static void givePlayersEffects(World world, BlockPos blockPos, List<BlockPos> list) {
        int m;
        int l;
        int i = list.size();
        int j = i / 7 * 16;
        int k = blockPos.getX();
        Box box = new Box(k, l = blockPos.getY(), m = blockPos.getZ(), k + 1, l + 1, m + 1).expand(j).stretch(0.0, world.getBottomSectionLimit(), 0.0);
        List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (list2.isEmpty()) {
            return;
        }
        for (PlayerEntity playerEntity : list2) {
            if (!blockPos.isWithinDistance(playerEntity.getBlockPos(), (double)j) || !playerEntity.isTouchingWaterOrRain()) continue;
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private static void attackHostileEntity(World world, BlockPos blockPos, BlockState blockState, List<BlockPos> list, ConduitBlockEntity conduitBlockEntity) {
        LivingEntity livingEntity2 = conduitBlockEntity.targetEntity;
        int i = list.size();
        if (i < 42) {
            conduitBlockEntity.targetEntity = null;
        } else if (conduitBlockEntity.targetEntity == null && conduitBlockEntity.targetUuid != null) {
            conduitBlockEntity.targetEntity = ConduitBlockEntity.findTargetEntity(world, blockPos, conduitBlockEntity.targetUuid);
            conduitBlockEntity.targetUuid = null;
        } else if (conduitBlockEntity.targetEntity == null) {
            List<LivingEntity> list2 = world.getEntitiesByClass(LivingEntity.class, ConduitBlockEntity.getAttackZone(blockPos), livingEntity -> livingEntity instanceof Monster && livingEntity.isTouchingWaterOrRain());
            if (!list2.isEmpty()) {
                conduitBlockEntity.targetEntity = list2.get(world.random.nextInt(list2.size()));
            }
        } else if (!conduitBlockEntity.targetEntity.isAlive() || !blockPos.isWithinDistance(conduitBlockEntity.targetEntity.getBlockPos(), 8.0)) {
            conduitBlockEntity.targetEntity = null;
        }
        if (conduitBlockEntity.targetEntity != null) {
            world.playSound(null, conduitBlockEntity.targetEntity.getX(), conduitBlockEntity.targetEntity.getY(), conduitBlockEntity.targetEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            conduitBlockEntity.targetEntity.damage(DamageSource.MAGIC, 4.0f);
        }
        if (livingEntity2 != conduitBlockEntity.targetEntity) {
            world.updateListeners(blockPos, blockState, blockState, 2);
        }
    }

    private static void updateTargetEntity(World world, BlockPos blockPos, ConduitBlockEntity conduitBlockEntity) {
        if (conduitBlockEntity.targetUuid == null) {
            conduitBlockEntity.targetEntity = null;
        } else if (conduitBlockEntity.targetEntity == null || !conduitBlockEntity.targetEntity.getUuid().equals(conduitBlockEntity.targetUuid)) {
            conduitBlockEntity.targetEntity = ConduitBlockEntity.findTargetEntity(world, blockPos, conduitBlockEntity.targetUuid);
            if (conduitBlockEntity.targetEntity == null) {
                conduitBlockEntity.targetUuid = null;
            }
        }
    }

    private static Box getAttackZone(BlockPos blockPos) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        return new Box(i, j, k, i + 1, j + 1, k + 1).expand(8.0);
    }

    @Nullable
    private static LivingEntity findTargetEntity(World world, BlockPos blockPos, UUID uUID) {
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, ConduitBlockEntity.getAttackZone(blockPos), livingEntity -> livingEntity.getUuid().equals(uUID));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private static void spawnNautilusParticles(World world, BlockPos blockPos, List<BlockPos> list, @Nullable Entity entity, int i) {
        float f;
        Random random = world.random;
        double d = MathHelper.sin((float)(i + 35) * 0.1f) / 2.0f + 0.5f;
        d = (d * d + d) * (double)0.3f;
        Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 1.5 + d, (double)blockPos.getZ() + 0.5);
        for (BlockPos blockPos2 : list) {
            if (random.nextInt(50) != 0) continue;
            BlockPos blockPos3 = blockPos2.subtract(blockPos);
            f = -0.5f + random.nextFloat() + (float)blockPos3.getX();
            float g = -2.0f + random.nextFloat() + (float)blockPos3.getY();
            float h = -0.5f + random.nextFloat() + (float)blockPos3.getZ();
            world.addParticle(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, f, g, h);
        }
        if (entity != null) {
            Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
            float j = (-0.5f + random.nextFloat()) * (3.0f + entity.getWidth());
            float k = -1.0f + random.nextFloat() * entity.getHeight();
            f = (-0.5f + random.nextFloat()) * (3.0f + entity.getWidth());
            Vec3d vec3d3 = new Vec3d(j, k, f);
            world.addParticle(ParticleTypes.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
        }
    }

    public boolean isActive() {
        return this.active;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isEyeOpen() {
        return this.eyeOpen;
    }

    private void setEyeOpen(boolean eyeOpen) {
        this.eyeOpen = eyeOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public float getRotation(float tickDelta) {
        return (this.ticksActive + tickDelta) * -0.0375f;
    }
}

