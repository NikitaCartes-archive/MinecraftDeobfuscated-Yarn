/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public class SculkSensorListener
implements GameEventListener {
    protected final PositionSource positionSource;
    protected final int range;
    protected final Callback callback;
    @Nullable
    protected Vibration vibration;
    protected int distance;
    protected int delay;

    public static Codec<SculkSensorListener> createCodec(Callback callback) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)PositionSource.CODEC.fieldOf("source")).forGetter(listener -> listener.positionSource), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("range")).forGetter(listener -> listener.range), Vibration.CODEC.optionalFieldOf("event").forGetter(listener -> Optional.ofNullable(listener.vibration)), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("event_distance")).orElse(0).forGetter(listener -> listener.distance), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("event_delay")).orElse(0).forGetter(listener -> listener.delay)).apply((Applicative<SculkSensorListener, ?>)instance, (positionSource, range, vibration, distance, delay) -> new SculkSensorListener((PositionSource)positionSource, (int)range, callback, vibration.orElse(null), (int)distance, (int)delay)));
    }

    public SculkSensorListener(PositionSource positionSource, int range, Callback callback, @Nullable Vibration vibration, int distance, int delay) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = callback;
        this.vibration = vibration;
        this.distance = distance;
        this.delay = delay;
    }

    public void tick(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (this.vibration != null) {
                --this.delay;
                if (this.delay <= 0) {
                    this.delay = 0;
                    this.callback.accept(serverWorld, this, new BlockPos(this.vibration.pos), this.vibration.gameEvent, this.vibration.getEntity(serverWorld).orElse(null), this.distance);
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
    public boolean listen(ServerWorld world, GameEvent event, @Nullable Entity entity, Vec3d pos) {
        if (this.vibration != null) {
            return false;
        }
        if (!this.callback.canAccept(event, entity)) {
            return false;
        }
        Optional<Vec3d> optional = this.positionSource.getPos(world);
        if (optional.isEmpty()) {
            return false;
        }
        Vec3d vec3d = optional.get();
        if (!this.callback.accepts(world, this, new BlockPos(pos), event, entity)) {
            return false;
        }
        if (SculkSensorListener.isOccluded(world, pos, vec3d)) {
            return false;
        }
        this.listen(world, event, entity, pos, vec3d);
        return true;
    }

    private void listen(ServerWorld world, GameEvent gameEvent, @Nullable Entity entity, Vec3d start, Vec3d end) {
        this.distance = MathHelper.floor(start.distanceTo(end));
        this.vibration = new Vibration(gameEvent, this.distance, start, entity);
        this.delay = this.distance;
        world.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private static boolean isOccluded(World world, Vec3d start, Vec3d end) {
        Vec3d vec3d2;
        Vec3d vec3d = new Vec3d((double)MathHelper.floor(start.x) + 0.5, (double)MathHelper.floor(start.y) + 0.5, (double)MathHelper.floor(start.z) + 0.5);
        return world.raycast(new BlockStateRaycastContext(vec3d, vec3d2 = new Vec3d((double)MathHelper.floor(end.x) + 0.5, (double)MathHelper.floor(end.y) + 0.5, (double)MathHelper.floor(end.z) + 0.5), state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK;
    }

    public static interface Callback {
        default public TagKey<GameEvent> getTag() {
            return GameEventTags.VIBRATIONS;
        }

        default public boolean canAccept(GameEvent gameEvent, @Nullable Entity entity) {
            if (!gameEvent.isIn(this.getTag())) {
                return false;
            }
            if (entity != null) {
                if (entity.isSpectator()) {
                    return false;
                }
                if (entity.bypassesSteppingEffects() && gameEvent.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
                    return false;
                }
                if (entity.occludeVibrationSignals()) {
                    return false;
                }
            }
            return true;
        }

        public boolean accepts(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5);

        public void accept(ServerWorld var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5, int var6);
    }

    public record Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable UUID uuid, @Nullable Entity entity) {
        public static final Codec<Vibration> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Registry.GAME_EVENT.getCodec().fieldOf("game_event")).forGetter(Vibration::gameEvent), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("distance")).forGetter(Vibration::distance), ((MapCodec)Vec3d.CODEC.fieldOf("pos")).forGetter(Vibration::pos), ((MapCodec)Codecs.UUID.fieldOf("source")).orElse(null).forGetter(Vibration::uuid)).apply((Applicative<Vibration, ?>)instance, Vibration::new));

        public Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable UUID uuid) {
            this(gameEvent, distance, pos, uuid, null);
        }

        public Vibration(GameEvent gameEvent, int distance, Vec3d pos, @Nullable Entity entity) {
            this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), entity);
        }

        public Optional<Entity> getEntity(ServerWorld world) {
            return Optional.ofNullable(this.entity).or(() -> Optional.ofNullable(this.uuid).map(world::getEntity));
        }

        @Nullable
        public UUID uuid() {
            return this.uuid;
        }

        @Nullable
        public Entity entity() {
            return this.entity;
        }
    }
}

