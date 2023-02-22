/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.GameEventTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.Vibration;
import net.minecraft.world.event.listener.VibrationSelector;
import org.jetbrains.annotations.Nullable;

public class VibrationListener
implements GameEventListener {
    @VisibleForTesting
    public static final Object2IntMap<GameEvent> FREQUENCIES = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap(), frequencies -> {
        frequencies.put(GameEvent.STEP, 1);
        frequencies.put(GameEvent.ITEM_INTERACT_FINISH, 2);
        frequencies.put(GameEvent.FLAP, 2);
        frequencies.put(GameEvent.SWIM, 3);
        frequencies.put(GameEvent.ELYTRA_GLIDE, 4);
        frequencies.put(GameEvent.HIT_GROUND, 5);
        frequencies.put(GameEvent.TELEPORT, 5);
        frequencies.put(GameEvent.SPLASH, 6);
        frequencies.put(GameEvent.ENTITY_SHAKE, 6);
        frequencies.put(GameEvent.BLOCK_CHANGE, 6);
        frequencies.put(GameEvent.NOTE_BLOCK_PLAY, 6);
        frequencies.put(GameEvent.ENTITY_DISMOUNT, 6);
        frequencies.put(GameEvent.PROJECTILE_SHOOT, 7);
        frequencies.put(GameEvent.DRINK, 7);
        frequencies.put(GameEvent.PRIME_FUSE, 7);
        frequencies.put(GameEvent.ENTITY_MOUNT, 7);
        frequencies.put(GameEvent.PROJECTILE_LAND, 8);
        frequencies.put(GameEvent.EAT, 8);
        frequencies.put(GameEvent.ENTITY_INTERACT, 8);
        frequencies.put(GameEvent.ENTITY_DAMAGE, 8);
        frequencies.put(GameEvent.EQUIP, 9);
        frequencies.put(GameEvent.SHEAR, 9);
        frequencies.put(GameEvent.ENTITY_ROAR, 9);
        frequencies.put(GameEvent.BLOCK_CLOSE, 10);
        frequencies.put(GameEvent.BLOCK_DEACTIVATE, 10);
        frequencies.put(GameEvent.BLOCK_DETACH, 10);
        frequencies.put(GameEvent.DISPENSE_FAIL, 10);
        frequencies.put(GameEvent.BLOCK_OPEN, 11);
        frequencies.put(GameEvent.BLOCK_ACTIVATE, 11);
        frequencies.put(GameEvent.BLOCK_ATTACH, 11);
        frequencies.put(GameEvent.ENTITY_PLACE, 12);
        frequencies.put(GameEvent.BLOCK_PLACE, 12);
        frequencies.put(GameEvent.FLUID_PLACE, 12);
        frequencies.put(GameEvent.ENTITY_DIE, 13);
        frequencies.put(GameEvent.BLOCK_DESTROY, 13);
        frequencies.put(GameEvent.FLUID_PICKUP, 13);
        frequencies.put(GameEvent.CONTAINER_CLOSE, 14);
        frequencies.put(GameEvent.PISTON_CONTRACT, 14);
        frequencies.put(GameEvent.PISTON_EXTEND, 15);
        frequencies.put(GameEvent.CONTAINER_OPEN, 15);
        frequencies.put(GameEvent.EXPLODE, 15);
        frequencies.put(GameEvent.LIGHTNING_STRIKE, 15);
        frequencies.put(GameEvent.INSTRUMENT_PLAY, 15);
    }));
    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    @Nullable
    protected Vibration vibration;
    protected int delay;
    private final VibrationSelector selector;

    public static Codec<VibrationListener> createCodec(Callback callback) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)PositionSource.CODEC.fieldOf("source")).forGetter(listener -> listener.positionSource), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("range")).forGetter(listener -> listener.range), Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)), ((MapCodec)VibrationSelector.CODEC.fieldOf("selector")).forGetter(listener -> listener.selector), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("event_delay")).orElse(0).forGetter(listener -> listener.delay)).apply((Applicative<VibrationListener, ?>)instance, (positionSource, range, vibration, selector, delay) -> new VibrationListener((PositionSource)positionSource, (int)range, callback, vibration.orElse(null), (VibrationSelector)selector, (int)delay)));
    }

    private VibrationListener(PositionSource positionSource, int range, Callback callback, @Nullable Vibration vibration, VibrationSelector selector, int delay) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.vibration = vibration;
        this.delay = delay;
        this.selector = selector;
    }

    public VibrationListener(PositionSource positionSource, int range, Callback callback) {
        this(positionSource, range, callback, null, new VibrationSelector(), 0);
    }

    public static int getFrequency(GameEvent event) {
        return FREQUENCIES.getOrDefault((Object)event, 0);
    }

    public void tick(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (this.vibration == null) {
                this.selector.getVibrationToTick(serverWorld.getTime()).ifPresent(vibration -> {
                    this.vibration = vibration;
                    Vec3d vec3d = this.vibration.pos();
                    this.delay = MathHelper.floor(this.vibration.distance());
                    serverWorld.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), vec3d.x, vec3d.y, vec3d.z, 1, 0.0, 0.0, 0.0, 0.0);
                    this.callback.onListen();
                    this.selector.clear();
                });
            }
            if (this.vibration != null) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.callback.accept(serverWorld, this, BlockPos.ofFloored(this.vibration.pos()), this.vibration.gameEvent(), this.vibration.getEntity(serverWorld).orElse(null), this.vibration.getOwner(serverWorld).orElse(null), this.vibration.distance());
                    this.vibration = null;
                }
            }
        }
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
        if (this.vibration != null) {
            return false;
        }
        if (!this.callback.canAccept(event, emitter)) {
            return false;
        }
        Optional<Vec3d> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        Vec3d vec3d = optional.get();
        if (!this.callback.accepts(world, this, BlockPos.ofFloored(emitterPos), event, emitter)) {
            return false;
        }
        if (VibrationListener.isOccluded(world, emitterPos, vec3d)) {
            return false;
        }
        this.trySelect(world, event, emitter, emitterPos, vec3d);
        return true;
    }

    public void forceListen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
        this.positionSource.getPos(world).ifPresent(listenerPos -> this.trySelect(world, event, emitter, emitterPos, (Vec3d)listenerPos));
    }

    public void trySelect(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos, Vec3d listenerPos) {
        this.selector.tryAccept(new Vibration(event, (float)emitterPos.distanceTo(listenerPos), emitterPos, emitter.sourceEntity()), world.getTime());
    }

    private static boolean isOccluded(World world, Vec3d start, Vec3d end) {
        Vec3d vec3d = new Vec3d((double)MathHelper.floor(start.x) + 0.5, (double)MathHelper.floor(start.y) + 0.5, (double)MathHelper.floor(start.z) + 0.5);
        Vec3d vec3d2 = new Vec3d((double)MathHelper.floor(end.x) + 0.5, (double)MathHelper.floor(end.y) + 0.5, (double)MathHelper.floor(end.z) + 0.5);
        for (Direction direction : Direction.values()) {
            Vec3d vec3d3 = vec3d.offset(direction, 1.0E-5f);
            if (world.raycast(new BlockStateRaycastContext(vec3d3, vec3d2, state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK) continue;
            return false;
        }
        return true;
    }

    public static interface Callback {
        default public TagKey<GameEvent> getTag() {
            return GameEventTags.VIBRATIONS;
        }

        default public boolean triggersAvoidCriterion() {
            return false;
        }

        default public boolean canAccept(GameEvent gameEvent, GameEvent.Emitter emitter) {
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            }
            Entity entity = emitter.sourceEntity();
            if (entity != null) {
                if (entity.isSpectator()) {
                    return false;
                }
                if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                    if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                        Criteria.AVOID_VIBRATION.trigger(serverPlayerEntity);
                    }
                    return false;
                }
                if (entity.occludeVibrationSignals()) {
                    return false;
                }
            }
            if (emitter.affectedState() != null) {
                return !emitter.affectedState().isIn(BlockTags.DAMPENS_VIBRATIONS);
            }
            return true;
        }

        public boolean accepts(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, GameEvent.Emitter var5);

        public void accept(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, @Nullable Entity var6, float var7);

        default public void onListen() {
        }
    }
}

