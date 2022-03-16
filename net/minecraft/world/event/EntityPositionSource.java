/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;

public class EntityPositionSource
implements PositionSource {
    public static final Codec<EntityPositionSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("source_entity_id")).forGetter(positionSource -> positionSource.entityId)).apply((Applicative<EntityPositionSource, ?>)instance, EntityPositionSource::new));
    final int entityId;
    private Optional<Entity> entity = Optional.empty();

    public EntityPositionSource(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public Optional<BlockPos> getPos(World world) {
        if (!this.entity.isPresent()) {
            this.entity = Optional.ofNullable(world.getEntityById(this.entityId));
        }
        return this.entity.map(Entity::getBlockPos);
    }

    @Override
    public PositionSourceType<?> getType() {
        return PositionSourceType.ENTITY;
    }

    public static class Type
    implements PositionSourceType<EntityPositionSource> {
        @Override
        public EntityPositionSource readFromBuf(PacketByteBuf packetByteBuf) {
            return new EntityPositionSource(packetByteBuf.readVarInt());
        }

        @Override
        public void writeToBuf(PacketByteBuf packetByteBuf, EntityPositionSource entityPositionSource) {
            packetByteBuf.writeVarInt(entityPositionSource.entityId);
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

