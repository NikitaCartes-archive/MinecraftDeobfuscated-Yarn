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

    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.CONDUIT, pos, state);
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

    public static void clientTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
        ++blockEntity.ticks;
        long l = world.getTime();
        List<BlockPos> list = blockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            blockEntity.active = ConduitBlockEntity.updateActivatingBlocks(world, pos, list);
            ConduitBlockEntity.openEye(blockEntity, list);
        }
        ConduitBlockEntity.updateTargetEntity(world, pos, blockEntity);
        ConduitBlockEntity.spawnNautilusParticles(world, pos, list, blockEntity.targetEntity, blockEntity.ticks);
        if (blockEntity.isActive()) {
            blockEntity.ticksActive += 1.0f;
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ConduitBlockEntity blockEntity) {
        ++blockEntity.ticks;
        long l = world.getTime();
        List<BlockPos> list = blockEntity.activatingBlocks;
        if (l % 40L == 0L) {
            boolean bl = ConduitBlockEntity.updateActivatingBlocks(world, pos, list);
            if (bl != blockEntity.active) {
                SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            blockEntity.active = bl;
            ConduitBlockEntity.openEye(blockEntity, list);
            if (bl) {
                ConduitBlockEntity.givePlayersEffects(world, pos, list);
                ConduitBlockEntity.attackHostileEntity(world, pos, state, list, blockEntity);
            }
        }
        if (blockEntity.isActive()) {
            if (l % 80L == 0L) {
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            if (l > blockEntity.nextAmbientSoundTime) {
                blockEntity.nextAmbientSoundTime = l + 60L + (long)world.getRandom().nextInt(40);
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static void openEye(ConduitBlockEntity blockEntity, List<BlockPos> activatingBlocks) {
        blockEntity.setEyeOpen(activatingBlocks.size() >= 42);
    }

    private static boolean updateActivatingBlocks(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        int k;
        int j;
        int i;
        activatingBlocks.clear();
        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPos blockPos = pos.add(i, j, k);
                    if (world.isWater(blockPos)) continue;
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
                    BlockPos blockPos2 = pos.add(i, j, k);
                    BlockState blockState = world.getBlockState(blockPos2);
                    for (Block block : ACTIVATING_BLOCKS) {
                        if (!blockState.isOf(block)) continue;
                        activatingBlocks.add(blockPos2);
                    }
                }
            }
        }
        return activatingBlocks.size() >= 16;
    }

    private static void givePlayersEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        int m;
        int l;
        int i = activatingBlocks.size();
        int j = i / 7 * 16;
        int k = pos.getX();
        Box box = new Box(k, l = pos.getY(), m = pos.getZ(), k + 1, l + 1, m + 1).expand(j).stretch(0.0, world.getSectionCount(), 0.0);
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (list.isEmpty()) {
            return;
        }
        for (PlayerEntity playerEntity : list) {
            if (!pos.isWithinDistance(playerEntity.getBlockPos(), (double)j) || !playerEntity.isTouchingWaterOrRain()) continue;
            playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private static void attackHostileEntity(World world, BlockPos pos, BlockState state, List<BlockPos> activatingBlocks, ConduitBlockEntity blockEntity) {
        LivingEntity livingEntity2 = blockEntity.targetEntity;
        int i = activatingBlocks.size();
        if (i < 42) {
            blockEntity.targetEntity = null;
        } else if (blockEntity.targetEntity == null && blockEntity.targetUuid != null) {
            blockEntity.targetEntity = ConduitBlockEntity.findTargetEntity(world, pos, blockEntity.targetUuid);
            blockEntity.targetUuid = null;
        } else if (blockEntity.targetEntity == null) {
            List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, ConduitBlockEntity.getAttackZone(pos), livingEntity -> livingEntity instanceof Monster && livingEntity.isTouchingWaterOrRain());
            if (!list.isEmpty()) {
                blockEntity.targetEntity = list.get(world.random.nextInt(list.size()));
            }
        } else if (!blockEntity.targetEntity.isAlive() || !pos.isWithinDistance(blockEntity.targetEntity.getBlockPos(), 8.0)) {
            blockEntity.targetEntity = null;
        }
        if (blockEntity.targetEntity != null) {
            world.playSound(null, blockEntity.targetEntity.getX(), blockEntity.targetEntity.getY(), blockEntity.targetEntity.getZ(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0f, 1.0f);
            blockEntity.targetEntity.damage(DamageSource.MAGIC, 4.0f);
        }
        if (livingEntity2 != blockEntity.targetEntity) {
            world.updateListeners(pos, state, state, 2);
        }
    }

    private static void updateTargetEntity(World world, BlockPos pos, ConduitBlockEntity blockEntity) {
        if (blockEntity.targetUuid == null) {
            blockEntity.targetEntity = null;
        } else if (blockEntity.targetEntity == null || !blockEntity.targetEntity.getUuid().equals(blockEntity.targetUuid)) {
            blockEntity.targetEntity = ConduitBlockEntity.findTargetEntity(world, pos, blockEntity.targetUuid);
            if (blockEntity.targetEntity == null) {
                blockEntity.targetUuid = null;
            }
        }
    }

    private static Box getAttackZone(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return new Box(i, j, k, i + 1, j + 1, k + 1).expand(8.0);
    }

    @Nullable
    private static LivingEntity findTargetEntity(World world, BlockPos pos, UUID uuid) {
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, ConduitBlockEntity.getAttackZone(pos), livingEntity -> livingEntity.getUuid().equals(uuid));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private static void spawnNautilusParticles(World world, BlockPos pos, List<BlockPos> activatingBlocks, @Nullable Entity entity, int i) {
        float f;
        Random random = world.random;
        double d = MathHelper.sin((float)(i + 35) * 0.1f) / 2.0f + 0.5f;
        d = (d * d + d) * (double)0.3f;
        Vec3d vec3d = new Vec3d((double)pos.getX() + 0.5, (double)pos.getY() + 1.5 + d, (double)pos.getZ() + 0.5);
        for (BlockPos blockPos : activatingBlocks) {
            if (random.nextInt(50) != 0) continue;
            BlockPos blockPos2 = blockPos.subtract(pos);
            f = -0.5f + random.nextFloat() + (float)blockPos2.getX();
            float g = -2.0f + random.nextFloat() + (float)blockPos2.getY();
            float h = -0.5f + random.nextFloat() + (float)blockPos2.getZ();
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

