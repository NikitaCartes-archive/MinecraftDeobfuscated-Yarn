/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class BossBarS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final UUID uuid;
    private final class_5882 type;
    private static final class_5882 field_29099 = new class_5882(){

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29108;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34099(uUID);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
        }
    };

    private BossBarS2CPacket(UUID uUID, class_5882 arg) {
        this.uuid = uUID;
        this.type = arg;
    }

    public BossBarS2CPacket(PacketByteBuf packetByteBuf) {
        this.uuid = packetByteBuf.readUuid();
        class_5883 lv = packetByteBuf.readEnumConstant(class_5883.class);
        this.type = (class_5882)lv.field_29113.apply(packetByteBuf);
    }

    public static BossBarS2CPacket method_34089(BossBar bossBar) {
        return new BossBarS2CPacket(bossBar.getUuid(), new class_5880(bossBar));
    }

    public static BossBarS2CPacket method_34090(UUID uUID) {
        return new BossBarS2CPacket(uUID, field_29099);
    }

    public static BossBarS2CPacket method_34094(BossBar bossBar) {
        return new BossBarS2CPacket(bossBar.getUuid(), new class_5885(bossBar.getPercent()));
    }

    public static BossBarS2CPacket method_34096(BossBar bossBar) {
        return new BossBarS2CPacket(bossBar.getUuid(), new class_5884(bossBar.getName()));
    }

    public static BossBarS2CPacket method_34097(BossBar bossBar) {
        return new BossBarS2CPacket(bossBar.getUuid(), new class_5887(bossBar.getColor(), bossBar.getOverlay()));
    }

    public static BossBarS2CPacket method_34098(BossBar bossBar) {
        return new BossBarS2CPacket(bossBar.getUuid(), new class_5886(bossBar.shouldDarkenSky(), bossBar.hasDragonMusic(), bossBar.shouldThickenFog()));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
        buf.writeEnumConstant(this.type.method_34105());
        this.type.method_34107(buf);
    }

    private static int method_34095(boolean bl, boolean bl2, boolean bl3) {
        int i = 0;
        if (bl) {
            i |= 1;
        }
        if (bl2) {
            i |= 2;
        }
        if (bl3) {
            i |= 4;
        }
        return i;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBossBar(this);
    }

    @Environment(value=EnvType.CLIENT)
    public void method_34091(class_5881 arg) {
        this.type.method_34106(this.uuid, arg);
    }

    static class class_5886
    implements class_5882 {
        private final boolean field_29117;
        private final boolean field_29118;
        private final boolean field_29119;

        private class_5886(boolean bl, boolean bl2, boolean bl3) {
            this.field_29117 = bl;
            this.field_29118 = bl2;
            this.field_29119 = bl3;
        }

        private class_5886(PacketByteBuf packetByteBuf) {
            short i = packetByteBuf.readUnsignedByte();
            this.field_29117 = (i & 1) > 0;
            this.field_29118 = (i & 2) > 0;
            this.field_29119 = (i & 4) > 0;
        }

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29112;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34104(uUID, this.field_29117, this.field_29118, this.field_29119);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeByte(BossBarS2CPacket.method_34095(this.field_29117, this.field_29118, this.field_29119));
        }
    }

    static class class_5887
    implements class_5882 {
        private final BossBar.Color field_29120;
        private final BossBar.Style field_29121;

        private class_5887(BossBar.Color color, BossBar.Style style) {
            this.field_29120 = color;
            this.field_29121 = style;
        }

        private class_5887(PacketByteBuf packetByteBuf) {
            this.field_29120 = packetByteBuf.readEnumConstant(BossBar.Color.class);
            this.field_29121 = packetByteBuf.readEnumConstant(BossBar.Style.class);
        }

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29111;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34101(uUID, this.field_29120, this.field_29121);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeEnumConstant(this.field_29120);
            packetByteBuf.writeEnumConstant(this.field_29121);
        }
    }

    static class class_5884
    implements class_5882 {
        private final Text field_29115;

        private class_5884(Text text) {
            this.field_29115 = text;
        }

        private class_5884(PacketByteBuf packetByteBuf) {
            this.field_29115 = packetByteBuf.readText();
        }

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29110;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34102(uUID, this.field_29115);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeText(this.field_29115);
        }
    }

    static class class_5885
    implements class_5882 {
        private final float field_29116;

        private class_5885(float f) {
            this.field_29116 = f;
        }

        private class_5885(PacketByteBuf packetByteBuf) {
            this.field_29116 = packetByteBuf.readFloat();
        }

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29109;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34100(uUID, this.field_29116);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeFloat(this.field_29116);
        }
    }

    static class class_5880
    implements class_5882 {
        private final Text field_29100;
        private final float field_29101;
        private final BossBar.Color field_29102;
        private final BossBar.Style field_29103;
        private final boolean field_29104;
        private final boolean field_29105;
        private final boolean field_29106;

        private class_5880(BossBar bossBar) {
            this.field_29100 = bossBar.getName();
            this.field_29101 = bossBar.getPercent();
            this.field_29102 = bossBar.getColor();
            this.field_29103 = bossBar.getOverlay();
            this.field_29104 = bossBar.shouldDarkenSky();
            this.field_29105 = bossBar.hasDragonMusic();
            this.field_29106 = bossBar.shouldThickenFog();
        }

        private class_5880(PacketByteBuf packetByteBuf) {
            this.field_29100 = packetByteBuf.readText();
            this.field_29101 = packetByteBuf.readFloat();
            this.field_29102 = packetByteBuf.readEnumConstant(BossBar.Color.class);
            this.field_29103 = packetByteBuf.readEnumConstant(BossBar.Style.class);
            short i = packetByteBuf.readUnsignedByte();
            this.field_29104 = (i & 1) > 0;
            this.field_29105 = (i & 2) > 0;
            this.field_29106 = (i & 4) > 0;
        }

        @Override
        public class_5883 method_34105() {
            return class_5883.field_29107;
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID uUID, class_5881 arg) {
            arg.method_34103(uUID, this.field_29100, this.field_29101, this.field_29102, this.field_29103, this.field_29104, this.field_29105, this.field_29106);
        }

        @Override
        public void method_34107(PacketByteBuf packetByteBuf) {
            packetByteBuf.writeText(this.field_29100);
            packetByteBuf.writeFloat(this.field_29101);
            packetByteBuf.writeEnumConstant(this.field_29102);
            packetByteBuf.writeEnumConstant(this.field_29103);
            packetByteBuf.writeByte(BossBarS2CPacket.method_34095(this.field_29104, this.field_29105, this.field_29106));
        }
    }

    static interface class_5882 {
        public class_5883 method_34105();

        @Environment(value=EnvType.CLIENT)
        public void method_34106(UUID var1, class_5881 var2);

        public void method_34107(PacketByteBuf var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface class_5881 {
        default public void method_34103(UUID uUID, Text text, float f, BossBar.Color color, BossBar.Style style, boolean bl, boolean bl2, boolean bl3) {
        }

        default public void method_34099(UUID uUID) {
        }

        default public void method_34100(UUID uUID, float f) {
        }

        default public void method_34102(UUID uUID, Text text) {
        }

        default public void method_34101(UUID uUID, BossBar.Color color, BossBar.Style style) {
        }

        default public void method_34104(UUID uUID, boolean bl, boolean bl2, boolean bl3) {
        }
    }

    static enum class_5883 {
        field_29107(packetByteBuf -> new class_5880((PacketByteBuf)packetByteBuf)),
        field_29108(packetByteBuf -> BossBarS2CPacket.method_34093()),
        field_29109(packetByteBuf -> new class_5885((PacketByteBuf)packetByteBuf)),
        field_29110(packetByteBuf -> new class_5884((PacketByteBuf)packetByteBuf)),
        field_29111(packetByteBuf -> new class_5887((PacketByteBuf)packetByteBuf)),
        field_29112(packetByteBuf -> new class_5886((PacketByteBuf)packetByteBuf));

        private final Function<PacketByteBuf, class_5882> field_29113;

        private class_5883(Function<PacketByteBuf, class_5882> function) {
            this.field_29113 = function;
        }
    }
}

