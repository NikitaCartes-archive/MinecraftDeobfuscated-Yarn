/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.Vibration;
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
    protected Optional<GameEvent> event = Optional.empty();
    protected int distance;
    protected int delay = 0;

    public SculkSensorListener(PositionSource positionSource, int range, Callback listener) {
        this.positionSource = positionSource;
        this.range = range;
        this.callback = listener;
    }

    public void tick(World world) {
        if (this.event.isPresent()) {
            --this.delay;
            if (this.delay <= 0) {
                this.delay = 0;
                this.callback.accept(world, this, this.event.get(), this.distance);
                this.event = Optional.empty();
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
    public boolean listen(World world, GameEvent event, @Nullable Entity entity, BlockPos pos) {
        if (!this.shouldActivate(event, entity)) {
            return false;
        }
        Optional<BlockPos> optional = this.positionSource.getPos(world);
        if (!optional.isPresent()) {
            return false;
        }
        BlockPos blockPos = optional.get();
        if (!this.callback.accepts(world, this, pos, event, entity)) {
            return false;
        }
        if (this.isOccluded(world, pos, blockPos)) {
            return false;
        }
        this.listen(world, event, pos, blockPos);
        return true;
    }

    private boolean shouldActivate(GameEvent event, @Nullable Entity entity) {
        if (this.event.isPresent()) {
            return false;
        }
        if (!event.isIn(GameEventTags.VIBRATIONS)) {
            return false;
        }
        if (entity != null) {
            if (event.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING) && entity.bypassesSteppingEffects()) {
                return false;
            }
            if (entity.occludeVibrationSignals()) {
                return false;
            }
        }
        return entity == null || !entity.isSpectator();
    }

    private void listen(World world, GameEvent event, BlockPos pos, BlockPos sourcePos) {
        this.event = Optional.of(event);
        if (world instanceof ServerWorld) {
            this.delay = this.distance = MathHelper.floor(Math.sqrt(pos.getSquaredDistance(sourcePos)));
            ((ServerWorld)world).sendVibrationPacket(new Vibration(pos, this.positionSource, this.delay));
        }
    }

    private boolean isOccluded(World world, BlockPos pos, BlockPos sourcePos) {
        return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() == HitResult.Type.BLOCK;
    }

    public static interface Callback {
        public boolean accepts(World var1, GameEventListener var2, BlockPos var3, GameEvent var4, @Nullable Entity var5);

        public void accept(World var1, GameEventListener var2, GameEvent var3, int var4);
    }
}

