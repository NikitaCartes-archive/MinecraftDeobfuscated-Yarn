/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class EntityPositionSource
implements PositionSource {
    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codecs.UUID.fieldOf("source_entity")).forGetter(EntityPositionSource::getUuid), ((MapCodec)Codec.FLOAT.fieldOf("y_offset")).orElse(Float.valueOf(0.0f)).forGetter(entityPositionSource -> Float.valueOf(entityPositionSource.yOffset))).apply((Applicative<EntityPositionSource, ?>)instance, (uUID, float_) -> new EntityPositionSource(Either.right(Either.left(uUID)), float_.floatValue())));
    private Either<Entity, Either<UUID, Integer>> source;
    final float yOffset;

    public EntityPositionSource(Entity entity, float yOffset) {
        this(Either.left(entity), yOffset);
    }

    EntityPositionSource(Either<Entity, Either<UUID, Integer>> source, float yOffset) {
        this.source = source;
        this.yOffset = yOffset;
    }

    @Override
    public Optional<Vec3d> getPos(World world) {
        if (this.source.left().isEmpty()) {
            this.findEntityInWorld(world);
        }
        return this.source.left().map(entity -> entity.getPos().add(0.0, this.yOffset, 0.0));
    }

    private void findEntityInWorld(World world) {
        this.source.map(Optional::of, either -> Optional.ofNullable(either.map(uuid -> {
            Entity entity;
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                entity = serverWorld.getEntity((UUID)uuid);
            } else {
                entity = null;
            }
            return entity;
        }, world::getEntityById))).ifPresent(entity -> {
            this.source = Either.left(entity);
        });
    }

    private UUID getUuid() {
        return this.source.map(Entity::getUuid, either -> either.map(Function.identity(), integer -> {
            throw new RuntimeException("Unable to get entityId from uuid");
        }));
    }

    int getEntityId() {
        return this.source.map(Entity::getId, either -> either.map(uUID -> {
            throw new IllegalStateException("Unable to get entityId from uuid");
        }, Function.identity()));
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type
    implements PositionSourceType<EntityPositionSource> {
        @Override
        public EntityPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new EntityPositionSource(Either.right(Either.right(packetByteBuf.readVarInt())), packetByteBuf.readFloat());
        }

        @Override
        public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
            packetByteBuf.writeVarInt(entityPositionSource.getEntityId());
            packetByteBuf.writeFloat(entityPositionSource.yOffset);
        }

        @Override
        public Codec<EntityPositionSource> getCodec() {
            return CODEC;
        }

        @Override
        public /* synthetic */ PositionSource readFromBuf(PacketByteBuf buf) {
            return this.readFromBuf(buf);
        }
    }
}

