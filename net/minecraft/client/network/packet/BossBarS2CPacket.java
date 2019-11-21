/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class BossBarS2CPacket
implements Packet<ClientPlayPacketListener> {
    private UUID uuid;
    private Type type;
    private Text name;
    private float percent;
    private BossBar.Color color;
    private BossBar.Style overlay;
    private boolean darkenSky;
    private boolean dragonMusic;
    private boolean thickenFog;

    public BossBarS2CPacket() {
    }

    public BossBarS2CPacket(Type type, BossBar bossBar) {
        this.type = type;
        this.uuid = bossBar.getUuid();
        this.name = bossBar.getName();
        this.percent = bossBar.getPercent();
        this.color = bossBar.getColor();
        this.overlay = bossBar.getOverlay();
        this.darkenSky = bossBar.getDarkenSky();
        this.dragonMusic = bossBar.hasDragonMusic();
        this.thickenFog = bossBar.getThickenFog();
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.uuid = packetByteBuf.readUuid();
        this.type = packetByteBuf.readEnumConstant(Type.class);
        switch (this.type) {
            case ADD: {
                this.name = packetByteBuf.readText();
                this.percent = packetByteBuf.readFloat();
                this.color = packetByteBuf.readEnumConstant(BossBar.Color.class);
                this.overlay = packetByteBuf.readEnumConstant(BossBar.Style.class);
                this.setFlagBitfield(packetByteBuf.readUnsignedByte());
                break;
            }
            case REMOVE: {
                break;
            }
            case UPDATE_PCT: {
                this.percent = packetByteBuf.readFloat();
                break;
            }
            case UPDATE_NAME: {
                this.name = packetByteBuf.readText();
                break;
            }
            case UPDATE_STYLE: {
                this.color = packetByteBuf.readEnumConstant(BossBar.Color.class);
                this.overlay = packetByteBuf.readEnumConstant(BossBar.Style.class);
                break;
            }
            case UPDATE_PROPERTIES: {
                this.setFlagBitfield(packetByteBuf.readUnsignedByte());
            }
        }
    }

    private void setFlagBitfield(int i) {
        this.darkenSky = (i & 1) > 0;
        this.dragonMusic = (i & 2) > 0;
        this.thickenFog = (i & 4) > 0;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeUuid(this.uuid);
        packetByteBuf.writeEnumConstant(this.type);
        switch (this.type) {
            case ADD: {
                packetByteBuf.writeText(this.name);
                packetByteBuf.writeFloat(this.percent);
                packetByteBuf.writeEnumConstant(this.color);
                packetByteBuf.writeEnumConstant(this.overlay);
                packetByteBuf.writeByte(this.getFlagBitfield());
                break;
            }
            case REMOVE: {
                break;
            }
            case UPDATE_PCT: {
                packetByteBuf.writeFloat(this.percent);
                break;
            }
            case UPDATE_NAME: {
                packetByteBuf.writeText(this.name);
                break;
            }
            case UPDATE_STYLE: {
                packetByteBuf.writeEnumConstant(this.color);
                packetByteBuf.writeEnumConstant(this.overlay);
                break;
            }
            case UPDATE_PROPERTIES: {
                packetByteBuf.writeByte(this.getFlagBitfield());
            }
        }
    }

    private int getFlagBitfield() {
        int i = 0;
        if (this.darkenSky) {
            i |= 1;
        }
        if (this.dragonMusic) {
            i |= 2;
        }
        if (this.thickenFog) {
            i |= 4;
        }
        return i;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onBossBar(this);
    }

    @Environment(value=EnvType.CLIENT)
    public UUID getUuid() {
        return this.uuid;
    }

    @Environment(value=EnvType.CLIENT)
    public Type getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPercent() {
        return this.percent;
    }

    @Environment(value=EnvType.CLIENT)
    public BossBar.Color getColor() {
        return this.color;
    }

    @Environment(value=EnvType.CLIENT)
    public BossBar.Style getOverlay() {
        return this.overlay;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDarkenSky() {
        return this.darkenSky;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasDragonMusic() {
        return this.dragonMusic;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldThickenFog() {
        return this.thickenFog;
    }

    public static enum Type {
        ADD,
        REMOVE,
        UPDATE_PCT,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES;

    }
}

