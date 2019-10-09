/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.explosion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
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
    public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, List<BlockPos> list) {
        this(world, entity, d, e, f, g, false, DestructionType.DESTROY, list);
    }

    @Environment(value=EnvType.CLIENT)
    public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, DestructionType destructionType, List<BlockPos> list) {
        this(world, entity, d, e, f, g, bl, destructionType);
        this.affectedBlocks.addAll(list);
    }

    public Explosion(World world, @Nullable Entity entity, double d, double e, double f, float g, boolean bl, DestructionType destructionType) {
        this.world = world;
        this.entity = entity;
        this.power = g;
        this.x = d;
        this.y = e;
        this.z = f;
        this.createFire = bl;
        this.blockDestructionType = destructionType;
        this.damageSource = DamageSource.explosion(this);
    }

    public static float getExposure(Vec3d vec3d, Entity entity) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
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
                    double n = MathHelper.lerp((double)k, box.minX, box.maxX);
                    Vec3d vec3d2 = new Vec3d(n + g, o = MathHelper.lerp((double)l, box.minY, box.maxY), (p = MathHelper.lerp((double)m, box.minZ, box.maxZ)) + h);
                    if (entity.world.rayTrace(new RayTraceContext(vec3d2, vec3d, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
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
            if (entity.isImmuneToExplosion() || !((x = (double)(MathHelper.sqrt(entity.squaredDistanceTo(new Vec3d(this.x, this.y, this.z))) / r)) <= 1.0) || (ab = (double)MathHelper.sqrt((y = entity.getX() - this.x) * y + (z = entity.method_23320() - this.y) * z + (aa = entity.getZ() - this.z) * aa)) == 0.0) continue;
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
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f);
        boolean bl3 = bl2 = this.blockDestructionType != DestructionType.NONE;
        if (this.power < 2.0f || !bl2) {
            this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        } else {
            this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
        }
        if (bl2) {
            for (BlockPos blockPos : this.affectedBlocks) {
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (bl) {
                    double d = (float)blockPos.getX() + this.world.random.nextFloat();
                    double e = (float)blockPos.getY() + this.world.random.nextFloat();
                    double f = (float)blockPos.getZ() + this.world.random.nextFloat();
                    double g = d - this.x;
                    double h = e - this.y;
                    double i = f - this.z;
                    double j = MathHelper.sqrt(g * g + h * h + i * i);
                    g /= j;
                    h /= j;
                    i /= j;
                    double k = 0.5 / (j / (double)this.power + 0.1);
                    this.world.addParticle(ParticleTypes.POOF, (d + this.x) / 2.0, (e + this.y) / 2.0, (f + this.z) / 2.0, g *= (k *= (double)(this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3f)), h *= k, i *= k);
                    this.world.addParticle(ParticleTypes.SMOKE, d, e, f, g, h, i);
                }
                if (blockState.isAir()) continue;
                if (block.shouldDropItemsOnExplosion(this) && this.world instanceof ServerWorld) {
                    BlockEntity blockEntity = block.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                    LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).setRandom(this.world.random).put(LootContextParameters.POSITION, blockPos).put(LootContextParameters.TOOL, ItemStack.EMPTY).putNullable(LootContextParameters.BLOCK_ENTITY, blockEntity).putNullable(LootContextParameters.THIS_ENTITY, this.entity);
                    if (this.blockDestructionType == DestructionType.DESTROY) {
                        builder.put(LootContextParameters.EXPLOSION_RADIUS, Float.valueOf(this.power));
                    }
                    Block.dropStacks(blockState, builder);
                }
                this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
                block.onDestroyedByExplosion(this.world, blockPos, this);
            }
        }
        if (this.createFire) {
            for (BlockPos blockPos : this.affectedBlocks) {
                if (!this.world.getBlockState(blockPos).isAir() || !this.world.getBlockState(blockPos.method_10074()).isFullOpaque(this.world, blockPos.method_10074()) || this.random.nextInt(3) != 0) continue;
                this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
            }
        }
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

