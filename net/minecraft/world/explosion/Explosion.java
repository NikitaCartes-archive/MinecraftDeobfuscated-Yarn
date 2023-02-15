/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.explosion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class Explosion {
    private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    private static final int field_30960 = 16;
    private final boolean createFire;
    private final DestructionType destructionType;
    private final Random random = Random.create();
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    private final float power;
    private final DamageSource damageSource;
    private final ExplosionBehavior behavior;
    private final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList();
    private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, false, DestructionType.DESTROY_WITH_DECAY, affectedBlocks);
    }

    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType, List<BlockPos> affectedBlocks) {
        this(world, entity, x, y, z, power, createFire, destructionType);
        this.affectedBlocks.addAll((Collection<BlockPos>)affectedBlocks);
    }

    public Explosion(World world, @Nullable Entity entity, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        this(world, entity, null, null, x, y, z, power, createFire, destructionType);
    }

    public Explosion(World world, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, DestructionType destructionType) {
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.createFire = createFire;
        this.destructionType = destructionType;
        this.damageSource = damageSource == null ? world.getDamageSources().explosion(this) : damageSource;
        this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity);
    }

    public static float getExposure(Vec3d source, Entity entity) {
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
        for (double k = 0.0; k <= 1.0; k += d) {
            for (double l = 0.0; l <= 1.0; l += e) {
                for (double m = 0.0; m <= 1.0; m += f) {
                    double p;
                    double o;
                    double n = MathHelper.lerp(k, box.minX, box.maxX);
                    Vec3d vec3d = new Vec3d(n + g, o = MathHelper.lerp(l, box.minY, box.maxY), (p = MathHelper.lerp(m, box.minZ, box.maxZ)) + h);
                    if (entity.world.raycast(new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.MISS) {
                        ++i;
                    }
                    ++j;
                }
            }
        }
        return (float)i / (float)j;
    }

    public void collectBlocksAndDamageEntities() {
        int l;
        int k;
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        HashSet<BlockPos> set = Sets.newHashSet();
        int i = 16;
        for (int j = 0; j < 16; ++j) {
            for (k = 0; k < 16; ++k) {
                block2: for (l = 0; l < 16; ++l) {
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
                        if (!this.world.isInBuildLimit(blockPos)) continue block2;
                        Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                        if (optional.isPresent()) {
                            h -= (optional.get().floatValue() + 0.3f) * 0.3f;
                        }
                        if (h > 0.0f && this.behavior.canDestroyBlock(this, this.world, blockPos, blockState, h)) {
                            set.add(blockPos);
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                    }
                }
            }
        }
        this.affectedBlocks.addAll((Collection<BlockPos>)set);
        float q = this.power * 2.0f;
        k = MathHelper.floor(this.x - (double)q - 1.0);
        l = MathHelper.floor(this.x + (double)q + 1.0);
        int r = MathHelper.floor(this.y - (double)q - 1.0);
        int s = MathHelper.floor(this.y + (double)q + 1.0);
        int t = MathHelper.floor(this.z - (double)q - 1.0);
        int u = MathHelper.floor(this.z + (double)q + 1.0);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k, r, t, l, s, u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
        for (int v = 0; v < list.size(); ++v) {
            PlayerEntity playerEntity;
            double ad;
            double z;
            double y;
            double x;
            double aa;
            double w;
            Entity entity = list.get(v);
            if (entity.isImmuneToExplosion() || !((w = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double)q) <= 1.0) || (aa = Math.sqrt((x = entity.getX() - this.x) * x + (y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y) * y + (z = entity.getZ() - this.z) * z)) == 0.0) continue;
            x /= aa;
            y /= aa;
            z /= aa;
            double ab = Explosion.getExposure(vec3d, entity);
            double ac = (1.0 - w) * ab;
            entity.damage(this.getDamageSource(), (int)((ac * ac + ac) / 2.0 * 7.0 * (double)q + 1.0));
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                ad = ProtectionEnchantment.transformExplosionKnockback(livingEntity, ac);
            } else {
                ad = ac;
            }
            Vec3d vec3d2 = new Vec3d(x *= ad, y *= ad, z *= ad);
            entity.setVelocity(entity.getVelocity().add(vec3d2));
            if (!(entity instanceof PlayerEntity) || (playerEntity = (PlayerEntity)entity).isSpectator() || playerEntity.isCreative() && playerEntity.getAbilities().flying) continue;
            this.affectedPlayers.put(playerEntity, vec3d2);
        }
    }

    /**
     * @param particles whether this explosion should emit explosion or explosion emitter particles around the source of the explosion
     */
    public void affectWorld(boolean particles) {
        if (this.world.isClient) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0f, (1.0f + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2f) * 0.7f, false);
        }
        boolean bl = this.shouldDestroy();
        if (particles) {
            if (this.power < 2.0f || !bl) {
                this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }
        if (bl) {
            ObjectArrayList objectArrayList = new ObjectArrayList();
            boolean bl2 = this.getCausingEntity() instanceof PlayerEntity;
            Util.shuffle(this.affectedBlocks, this.world.random);
            for (BlockPos blockPos : this.affectedBlocks) {
                World world;
                BlockState blockState = this.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (blockState.isAir()) continue;
                BlockPos blockPos2 = blockPos.toImmutable();
                this.world.getProfiler().push("explosion_blocks");
                if (block.shouldDropItemsOnExplosion(this) && (world = this.world) instanceof ServerWorld) {
                    ServerWorld serverWorld = (ServerWorld)world;
                    BlockEntity blockEntity = blockState.hasBlockEntity() ? this.world.getBlockEntity(blockPos) : null;
                    LootContext.Builder builder = new LootContext.Builder(serverWorld).random(this.world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, blockEntity).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);
                    if (this.destructionType == DestructionType.DESTROY_WITH_DECAY) {
                        builder.parameter(LootContextParameters.EXPLOSION_RADIUS, Float.valueOf(this.power));
                    }
                    blockState.onStacksDropped(serverWorld, blockPos, ItemStack.EMPTY, bl2);
                    blockState.getDroppedStacks(builder).forEach(stack -> Explosion.tryMergeStack(objectArrayList, stack, blockPos2));
                }
                this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                block.onDestroyedByExplosion(this.world, blockPos, this);
                this.world.getProfiler().pop();
            }
            for (Pair pair : objectArrayList) {
                Block.dropStack(this.world, (BlockPos)pair.getSecond(), (ItemStack)pair.getFirst());
            }
        }
        if (this.createFire) {
            for (BlockPos blockPos3 : this.affectedBlocks) {
                if (this.random.nextInt(3) != 0 || !this.world.getBlockState(blockPos3).isAir() || !this.world.getBlockState(blockPos3.down()).isOpaqueFullCube(this.world, blockPos3.down())) continue;
                this.world.setBlockState(blockPos3, AbstractFireBlock.getState(this.world, blockPos3));
            }
        }
    }

    public boolean shouldDestroy() {
        return this.destructionType != DestructionType.KEEP;
    }

    private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        int i = stacks.size();
        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = stacks.get(j);
            ItemStack itemStack = pair.getFirst();
            if (!ItemEntity.canMerge(itemStack, stack)) continue;
            ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
            stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
            if (!stack.isEmpty()) continue;
            return;
        }
        stacks.add(Pair.of(stack, pos));
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        ProjectileEntity projectileEntity;
        Entity entity;
        if (this.entity == null) {
            return null;
        }
        Entity entity2 = this.entity;
        if (entity2 instanceof TntEntity) {
            TntEntity tntEntity = (TntEntity)entity2;
            return tntEntity.getOwner();
        }
        entity2 = this.entity;
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            return livingEntity;
        }
        entity2 = this.entity;
        if (entity2 instanceof ProjectileEntity && (entity = (projectileEntity = (ProjectileEntity)entity2).getOwner()) instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            return livingEntity2;
        }
        return null;
    }

    @Nullable
    public Entity getEntity() {
        return this.entity;
    }

    public void clearAffectedBlocks() {
        this.affectedBlocks.clear();
    }

    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    public static enum DestructionType {
        KEEP,
        DESTROY,
        DESTROY_WITH_DECAY;

    }
}

