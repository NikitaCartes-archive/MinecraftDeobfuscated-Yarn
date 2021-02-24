/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlayerInteractEntityC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final int entityId;
    private final class_5906 type;
    private final boolean playerSneaking;
    private static final class_5906 field_29170 = new class_5906(){

        @Override
        public class_5907 method_34211() {
            return class_5907.field_29172;
        }

        @Override
        public void method_34213(class_5908 arg) {
            arg.method_34218();
        }

        @Override
        public void method_34212(PacketByteBuf packetByteBuf) {
        }
    };

    @Environment(value=EnvType.CLIENT)
    private PlayerInteractEntityC2SPacket(int i, boolean bl, class_5906 arg) {
        this.entityId = i;
        this.type = arg;
        this.playerSneaking = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public static PlayerInteractEntityC2SPacket method_34206(Entity entity, boolean bl) {
        return new PlayerInteractEntityC2SPacket(entity.getId(), bl, field_29170);
    }

    @Environment(value=EnvType.CLIENT)
    public static PlayerInteractEntityC2SPacket method_34207(Entity entity, boolean bl, Hand hand) {
        return new PlayerInteractEntityC2SPacket(entity.getId(), bl, new class_5909(hand));
    }

    @Environment(value=EnvType.CLIENT)
    public static PlayerInteractEntityC2SPacket method_34208(Entity entity, boolean bl, Hand hand, Vec3d vec3d) {
        return new PlayerInteractEntityC2SPacket(entity.getId(), bl, new class_5910(hand, vec3d));
    }

    public PlayerInteractEntityC2SPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
        class_5907 lv = packetByteBuf.readEnumConstant(class_5907.class);
        this.type = (class_5906)lv.field_29174.apply(packetByteBuf);
        this.playerSneaking = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeEnumConstant(this.type.method_34211());
        this.type.method_34212(buf);
        buf.writeBoolean(this.playerSneaking);
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onPlayerInteractEntity(this);
    }

    @Nullable
    public Entity getEntity(ServerWorld serverWorld) {
        return serverWorld.method_31424(this.entityId);
    }

    public boolean isPlayerSneaking() {
        return this.playerSneaking;
    }

    public void method_34209(class_5908 arg) {
        this.type.method_34213(arg);
    }

    static class class_5910
    implements class_5906 {
        private final Hand field_29177;
        private final Vec3d field_29178;

        @Environment(value=EnvType.CLIENT)
        private class_5910(Hand hand, Vec3d vec3d) {
            this.field_29177 = hand;
            this.field_29178 = vec3d;
        }

        private class_5910(PacketByteBuf packetByteBuf) {
            this.field_29178 = new Vec3d(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
            this.field_29177 = packetByteBuf.readEnumConstant(Hand.class);
        }

        @Override
        public class_5907 method_34211() {
            return class_5907.field_29173;
        }

        @Override
        public void method_34213(class_5908 arg) {
            arg.method_34220(this.field_29177, this.field_29178);
        }

        @Override
        public void method_34212(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeFloat((float)this.field_29178.x);
            packetByteBuf.writeFloat((float)this.field_29178.y);
            packetByteBuf.writeFloat((float)this.field_29178.z);
            packetByteBuf.writeEnumConstant(this.field_29177);
        }
    }

    static class class_5909
    implements class_5906 {
        private final Hand field_29176;

        @Environment(value=EnvType.CLIENT)
        private class_5909(Hand hand) {
            this.field_29176 = hand;
        }

        private class_5909(PacketByteBuf packetByteBuf) {
            this.field_29176 = packetByteBuf.readEnumConstant(Hand.class);
        }

        @Override
        public class_5907 method_34211() {
            return class_5907.field_29171;
        }

        @Override
        public void method_34213(class_5908 arg) {
            arg.method_34219(this.field_29176);
        }

        @Override
        public void method_34212(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeEnumConstant(this.field_29176);
        }
    }

    static interface class_5906 {
        public class_5907 method_34211();

        public void method_34213(class_5908 var1);

        public void method_34212(PacketByteBuf var1);
    }

    public static interface class_5908 {
        public void method_34219(Hand var1);

        public void method_34220(Hand var1, Vec3d var2);

        public void method_34218();
    }

    static enum class_5907 {
        field_29171(packetByteBuf -> new class_5909((PacketByteBuf)packetByteBuf)),
        field_29172(packetByteBuf -> PlayerInteractEntityC2SPacket.method_34210()),
        field_29173(packetByteBuf -> new class_5910((PacketByteBuf)packetByteBuf));

        private final Function<PacketByteBuf, class_5906> field_29174;

        private class_5907(Function<PacketByteBuf, class_5906> function) {
            this.field_29174 = function;
        }
    }
}

