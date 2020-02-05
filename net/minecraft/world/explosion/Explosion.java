/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Explosion {
    private final boolean createFire;
    private final DestructionType blockDestructionType;
    private final Random random = new Random();
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    private final float power;
    private DamageSource damageSource;
    private final List<BlockPos> affectedBlocks = Lists.newArrayList();
    private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

    @Environment(value=EnvType.CLIENT)
    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, false, DestructionType.DESTROY, affectedBlocks);
    }

    @Environment(value=EnvType.CLIENT)
    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, createFire, destructionType);
        this.affectedBlocks.addAll(affectedBlocks);
    }

    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType blockDestructionType) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
        this.blockDestructionType = blockDestructionType;
        this.damageSource = DamageSource.explosion(this);
    }

    public static float getExposure(Vec3d source, Entity entity) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.x2 - box.x1) * 2.0 + 1.0);
        double e = 1.0 / ((box.y2 - box.y1) * 2.0 + 1.0);
        double f = 1.0 / ((box.z2 - box.z1) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (d < 0.0 || e < 0.0 || f < 0.0) {
            return 0.0f;
        }
        int i = 0;
        int j = 0;
        float k = 0.0f;
        while (k <= 1.0f) {
            float l = 0.0f;
            while (l <= 1.0f) {
                float m = 0.0f;
                while (m <= 1.0f) {
                    double p;
                    double o;
                    double n = MathHelper.lerp((double)k, box.x1, box.x2);
                    Vec3d vec3d = new Vec3d(n + g, o = MathHelper.lerp((double)l, box.y1, box.y2), (p = MathHelper.lerp((double)m, box.z1, box.z2)) + h);
                    if (entity.world.rayTrace(new RayTraceContext(vec3d, source, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                    m = (float)((double)m + f);
                }
                l = (float)((double)l + e);
            }
            k = (float)((double)k + d);
        }
        return (float)i / (float)j;
    }

    public void collectBlocksAndDamageEntities() {
        int l;
        int k;
        HashSet<BlockPos> set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                for (l = 0; l < 16; ++l) {
                    if (j != 0 && j != 15 && k != 0 && k != 15 && l != 0 && l != 15) continue;
                    double d = (float)j / 15.0f * 2.0f - 1.0f;
                    double e = (float)k / 15.0f * 2.0f - 1.0f;
                    double f = (float)l / 15.0f * 2.0f - 1.0f;
                    double g = Math.sqrt(d * d + e * e + f * f);
                    d /= g;
                    e /= g;
                    f /= g;
                    double m = this.x;
                    double n = this.y;
                    double o = this.z;
                    float p = 0.3f;
                    for (float h = this.power * (0.7f + this.world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = new BlockPos(m, n, o);
                        BlockState blockState = this.world.getBlockState(blockPos);
                        FluidState fluidState = this.world.getFluidState(blockPos);
                        if (!blockState.isAir() || !fluidState.isEmpty()) {
                            float q = Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
                            if (this.entity != null) {
                                q = this.entity.getEffectiveExplosionResistance(this, this.world, blockPos, blockState, fluidState, q);
                            }
                            h -= (q + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, blockPos, blockState, h))) {
                            set.add(blockPos);
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlocks.addAll(set);
        float r = this.power * 2.0f;
        k = MathHelper.floor(this.x - (double)r - 1.0);
        l = MathHelper.floor(this.x + (double)r + 1.0);
        int s = MathHelper.floor(this.y - (double)r - 1.0);
        int t = MathHelper.floor(this.y + (double)r + 1.0);
        int u = MathHelper.floor(this.z - (double)r - 1.0);
        int v = MathHelper.floor(this.z + (double)r + 1.0);
        List<Entity> list = this.world.getEntities(this.entity, new Box(k, s, u, l, t, v));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
        for (int w = 0; w < list.size(); ++w) {
            PlayerEntity playerEntity;
            double aa;
            double z;
            double y;
            double ab;
            double x;
            Entity entity = list.get(w);
            if (entity.isImmuneToExplosion() || !((x = (double)(MathHelper.sqrt(entity.squaredDistanceTo(vec3d)) / r)) <= 1.0) || (ab = (double)MathHelper.sqrt((y = entity.getX() - this.x) * y + (z = entity.getEyeY() - this.y) * z + (aa = entity.getZ() - this.z) * aa)) == 0.0) continue;
            y /= ab;
            z /= ab;
            aa /= ab;
            double ac = Explosion.getExposure(vec3d, entity);
            double ad = (1.0 - x) * ac;
            entity.damage(this.getDamageSource(), (int)((ad * ad + ad) / 2.0 * 7.0 * (double)r + 1.0));
            double ae = ad;
            if (entity instanceof LivingEntity) {
                ae = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity, ad);
            }
            entity.setVelocity(entity.getVelocity().add(y * ae, z * ae, aa * ae));
            if (!(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity)entity).isSpectator() || playerEntity.isCreative() && playerEntity.abilities.flying) continue;
            this.affectedPlayers.put(playerEntity, new Vec3d(y * ad, z * ad, aa * ad));
        }
    }

    public void affectWorld(boolean bl) {
        boolean bl2;
        if (this.world.isClient) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
        }
        boolean bl3 = bl2 = this.blockDestructionType != DestructionType.NONE;
        if (bl) {
            if (this.power < 2.0f || !bl2) {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }
        if (bl2) {
            ObjectArrayList objectArrayList = new ObjectArrayList();
            Collections.shuffle(this.affectedBlocks, this.world.random);
            for (BlockPos blockPos : this.affectedBlocks) {
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (blockState.isAir()) continue;
                BlockPos blockPos2 = blockPos.toImmutable();
                this.world.getProfiler().push("explosion_blocks");
                if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
                    BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                    LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).setRandom(this.world.random).put(LootContextParameters.POSITION, blockPos).put(LootContextParameters.TOOL, ItemStack.EMPTY).putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity).putNullable(LootContextParameters.THIS_ENTITY, this.entity);
                    if (this.blockDestructionType == DestructionType.DESTROY) {
                        builder.put(LootContextParameters.EXPLOSION_RADIUS, Float.valueOf(this.power));
                    }
                    blockState.getDroppedStacks(builder).forEach(itemStack -> Explosion.method_24023(objectArrayList, itemStack, blockPos2));
                }
                this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
                block.onDestroyedByExplosion(this.world, blockPos, this);
                this.world.getProfiler().pop();
            }
            for (Pair pair : objectArrayList) {
                Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
            }
        }
        if (this.createFire) {
            for (BlockPos blockPos3 : this.affectedBlocks) {
                if (this.random.nextInt(3) != 0 || !this.world.getBlockState(blockPos3).isAir() || !this.world.getBlockState(blockPos3.down()).isFullOpaque(this.world, blockPos3.down())) continue;
                this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
            }
        }
    }

    private static void method_24023(ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList, ItemStack itemStack, BlockPos blockPos) {
        int i = objectArrayList.size();
        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = objectArrayList.get(j);
            ItemStack itemStack2 = pair.getFirst();
            if (!ItemEntity.canMerge(itemStack2, itemStack)) continue;
            ItemStack itemStack3 = ItemEntity.merge(itemStack2, itemStack, 16);
            objectArrayList.set(j, Pair.of(itemStack3, pair.getSecond()));
            if (!itemStack.isEmpty()) continue;
            return;
        }
        objectArrayList.add(Pair.of(itemStack, blockPos));
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public void setDamageSource(DamageSource damageSource) {
        this.damageSource = damageSource;
    }

    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        if (this.entity == null) {
            return null;
        }
        if (this.entity instanceof TntEntity) {
            return ((TntEntity)this.entity).getCausingEntity();
        }
        if (this.entity instanceof LivingEntity) {
            return (LivingEntity)this.entity;
        }
        if (this.entity instanceof ExplosiveProjectileEntity) {
            return ((ExplosiveProjectileEntity)this.entity).owner;
        }
        return null;
    }

    public void clearAffectedBlocks() {
        this.affectedBlocks.clear();
    }

    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    public static enum DestructionType {
        NONE,
        BREAK,
        DESTROY;

    }
}

