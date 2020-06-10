/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractEntityC2SPacket
implements Packet<ServerPlayPacketListener> {
    private int entityId;
    private InteractionType type;
    private Vec3d hitPos;
    private Hand hand;
    private boolean field_25660;

    public PlayerInteractEntityC2SPacket() {
    }

    public PlayerInteractEntityC2SPacket(Entity entity) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.ATTACK;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, boolean bl) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.INTERACT;
        this.hand = hand;
        this.field_25660 = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public PlayerInteractEntityC2SPacket(Entity entity, Hand hand, Vec3d vec3d, boolean bl) {
        this.entityId = entity.getEntityId();
        this.type = InteractionType.INTERACT_AT;
        this.hand = hand;
        this.hitPos = vec3d;
        this.field_25660 = bl;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.entityId = buf.readVarInt();
        this.type = buf.readEnumConstant(InteractionType.class);
        if (this.type == InteractionType.INTERACT_AT) {
            this.hitPos = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
        if (this.type == InteractionType.INTERACT || this.type == InteractionType.INTERACT_AT) {
            this.hand = buf.readEnumConstant(Hand.class);
            this.field_25660 = buf.readBoolean();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.entityId);
        buf.writeEnumConstant(this.type);
        if (this.type == InteractionType.INTERACT_AT) {
            buf.writeFloat((float)this.hitPos.x);
            buf.writeFloat((float)this.hitPos.y);
            buf.writeFloat((float)this.hitPos.z);
        }
        if (this.type == InteractionType.INTERACT || this.type == InteractionType.INTERACT_AT) {
            buf.writeEnumConstant(this.hand);
            buf.writeBoolean(this.field_25660);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerInteractEntity(this);
    }

    @Nullable
    public Entity getEntity(World world) {
        return world.getEntityById(this.entityId);
    }

    public InteractionType getType() {
        return this.type;
    }

    public Hand getHand() {
        return this.hand;
    }

    public Vec3d getHitPosition() {
        return this.hitPos;
    }

    public boolean method_30007() {
        return this.field_25660;
    }

    public static enum InteractionType {
        INTERACT,
        ATTACK,
        INTERACT_AT;

    }
}

