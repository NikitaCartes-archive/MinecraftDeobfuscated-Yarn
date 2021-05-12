/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import java.util.function.Function;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class BossBarS2CPacket
implements Packet<ClientPlayPacketListener> {
    private static final int DARKEN_SKY_MASK = 1;
    private static final int DRAGON_MUSIC_MASK = 2;
    private static final int THICKEN_FOG_MASK = 4;
    private final UUID uuid;
    private final Action action;
    static final Action REMOVE_ACTION = new Action(){

        @Override
        public Type getType() {
            return Type.REMOVE;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.remove(uuid);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
        }
    };

    private BossBarS2CPacket(UUID uuid, Action action) {
        this.uuid = uuid;
        this.action = action;
    }

    public BossBarS2CPacket(PacketByteBuf buf) {
        this.uuid = buf.readUuid();
        Type type = buf.readEnumConstant(Type.class);
        this.action = type.parser.apply(buf);
    }

    public static BossBarS2CPacket add(BossBar bar) {
        return new BossBarS2CPacket(bar.getUuid(), new AddAction(bar));
    }

    public static BossBarS2CPacket remove(UUID uuid) {
        return new BossBarS2CPacket(uuid, REMOVE_ACTION);
    }

    public static BossBarS2CPacket updateProgress(BossBar bar) {
        return new BossBarS2CPacket(bar.getUuid(), new UpdateProgressAction(bar.getPercent()));
    }

    public static BossBarS2CPacket updateName(BossBar bar) {
        return new BossBarS2CPacket(bar.getUuid(), new UpdateNameAction(bar.getName()));
    }

    public static BossBarS2CPacket updateStyle(BossBar bar) {
        return new BossBarS2CPacket(bar.getUuid(), new UpdateStyleAction(bar.getColor(), bar.getStyle()));
    }

    public static BossBarS2CPacket updateProperties(BossBar bar) {
        return new BossBarS2CPacket(bar.getUuid(), new UpdatePropertiesAction(bar.shouldDarkenSky(), bar.hasDragonMusic(), bar.shouldThickenFog()));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
        buf.writeEnumConstant(this.action.getType());
        this.action.toPacket(buf);
    }

    static int maskProperties(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        int i = 0;
        if (darkenSky) {
            i |= 1;
        }
        if (dragonMusic) {
            i |= 2;
        }
        if (thickenFog) {
            i |= 4;
        }
        return i;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBossBar(this);
    }

    public void accept(Consumer consumer) {
        this.action.accept(this.uuid, consumer);
    }

    static interface Action {
        public Type getType();

        public void accept(UUID var1, Consumer var2);

        public void toPacket(PacketByteBuf var1);
    }

    static enum Type {
        ADD(AddAction::new),
        REMOVE(buf -> REMOVE_ACTION),
        UPDATE_PROGRESS(UpdateProgressAction::new),
        UPDATE_NAME(UpdateNameAction::new),
        UPDATE_STYLE(UpdateStyleAction::new),
        UPDATE_PROPERTIES(UpdatePropertiesAction::new);

        final Function<PacketByteBuf, Action> parser;

        private Type(Function<PacketByteBuf, Action> parser) {
            this.parser = parser;
        }
    }

    static class AddAction
    implements Action {
        private final Text name;
        private final float percent;
        private final BossBar.Color color;
        private final BossBar.Style style;
        private final boolean darkenSky;
        private final boolean dragonMusic;
        private final boolean thickenFog;

        AddAction(BossBar bar) {
            this.name = bar.getName();
            this.percent = bar.getPercent();
            this.color = bar.getColor();
            this.style = bar.getStyle();
            this.darkenSky = bar.shouldDarkenSky();
            this.dragonMusic = bar.hasDragonMusic();
            this.thickenFog = bar.shouldThickenFog();
        }

        private AddAction(PacketByteBuf buf) {
            this.name = buf.readText();
            this.percent = buf.readFloat();
            this.color = buf.readEnumConstant(BossBar.Color.class);
            this.style = buf.readEnumConstant(BossBar.Style.class);
            short i = buf.readUnsignedByte();
            this.darkenSky = (i & 1) > 0;
            this.dragonMusic = (i & 2) > 0;
            this.thickenFog = (i & 4) > 0;
        }

        @Override
        public Type getType() {
            return Type.ADD;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.add(uuid, this.name, this.percent, this.color, this.style, this.darkenSky, this.dragonMusic, this.thickenFog);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
            buf.writeText(this.name);
            buf.writeFloat(this.percent);
            buf.writeEnumConstant(this.color);
            buf.writeEnumConstant(this.style);
            buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
        }
    }

    static class UpdateProgressAction
    implements Action {
        private final float percent;

        UpdateProgressAction(float percent) {
            this.percent = percent;
        }

        private UpdateProgressAction(PacketByteBuf buf) {
            this.percent = buf.readFloat();
        }

        @Override
        public Type getType() {
            return Type.UPDATE_PROGRESS;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.updateProgress(uuid, this.percent);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
            buf.writeFloat(this.percent);
        }
    }

    static class UpdateNameAction
    implements Action {
        private final Text name;

        UpdateNameAction(Text name) {
            this.name = name;
        }

        private UpdateNameAction(PacketByteBuf buf) {
            this.name = buf.readText();
        }

        @Override
        public Type getType() {
            return Type.UPDATE_NAME;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.updateName(uuid, this.name);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
            buf.writeText(this.name);
        }
    }

    static class UpdateStyleAction
    implements Action {
        private final BossBar.Color color;
        private final BossBar.Style style;

        UpdateStyleAction(BossBar.Color color, BossBar.Style style) {
            this.color = color;
            this.style = style;
        }

        private UpdateStyleAction(PacketByteBuf buf) {
            this.color = buf.readEnumConstant(BossBar.Color.class);
            this.style = buf.readEnumConstant(BossBar.Style.class);
        }

        @Override
        public Type getType() {
            return Type.UPDATE_STYLE;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.updateStyle(uuid, this.color, this.style);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
            buf.writeEnumConstant(this.color);
            buf.writeEnumConstant(this.style);
        }
    }

    static class UpdatePropertiesAction
    implements Action {
        private final boolean darkenSky;
        private final boolean dragonMusic;
        private final boolean thickenFog;

        UpdatePropertiesAction(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
            this.darkenSky = darkenSky;
            this.dragonMusic = dragonMusic;
            this.thickenFog = thickenFog;
        }

        private UpdatePropertiesAction(PacketByteBuf buf) {
            short i = buf.readUnsignedByte();
            this.darkenSky = (i & 1) > 0;
            this.dragonMusic = (i & 2) > 0;
            this.thickenFog = (i & 4) > 0;
        }

        @Override
        public Type getType() {
            return Type.UPDATE_PROPERTIES;
        }

        @Override
        public void accept(UUID uuid, Consumer consumer) {
            consumer.updateProperties(uuid, this.darkenSky, this.dragonMusic, this.thickenFog);
        }

        @Override
        public void toPacket(PacketByteBuf buf) {
            buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
        }
    }

    public static interface Consumer {
        default public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        }

        default public void remove(UUID uuid) {
        }

        default public void updateProgress(UUID uuid, float percent) {
        }

        default public void updateName(UUID uuid, Text name) {
        }

        default public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
        }

        default public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        }
    }
}

