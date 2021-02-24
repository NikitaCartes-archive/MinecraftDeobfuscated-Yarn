/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.Nullable;

public class MapUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int id;
    private final byte scale;
    private final boolean locked;
    @Nullable
    private final List<MapIcon> icons;
    @Nullable
    private final MapState.UpdateData updateData;

    public MapUpdateS2CPacket(int id, byte scale, boolean showIcons, @Nullable Collection<MapIcon> icons, @Nullable MapState.UpdateData updateData) {
        this.id = id;
        this.scale = scale;
        this.locked = showIcons;
        this.icons = icons != null ? Lists.newArrayList(icons) : null;
        this.updateData = updateData;
    }

    public MapUpdateS2CPacket(PacketByteBuf packetByteBuf2) {
        this.id = packetByteBuf2.readVarInt();
        this.scale = packetByteBuf2.readByte();
        this.locked = packetByteBuf2.readBoolean();
        this.icons = packetByteBuf2.readBoolean() ? packetByteBuf2.method_34066(packetByteBuf -> {
            MapIcon.Type type = packetByteBuf.readEnumConstant(MapIcon.Type.class);
            return new MapIcon(type, packetByteBuf.readByte(), packetByteBuf.readByte(), (byte)(packetByteBuf.readByte() & 0xF), packetByteBuf.readBoolean() ? packetByteBuf.readText() : null);
        }) : null;
        short i = packetByteBuf2.readUnsignedByte();
        if (i > 0) {
            short j = packetByteBuf2.readUnsignedByte();
            short k = packetByteBuf2.readUnsignedByte();
            short l = packetByteBuf2.readUnsignedByte();
            byte[] bs = packetByteBuf2.readByteArray();
            this.updateData = new MapState.UpdateData(k, l, i, j, bs);
        } else {
            this.updateData = null;
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.id);
        buf.writeByte(this.scale);
        buf.writeBoolean(this.locked);
        if (this.icons != null) {
            buf.writeBoolean(true);
            buf.method_34062(this.icons, (packetByteBuf, mapIcon) -> {
                packetByteBuf.writeEnumConstant(mapIcon.getType());
                packetByteBuf.writeByte(mapIcon.getX());
                packetByteBuf.writeByte(mapIcon.getZ());
                packetByteBuf.writeByte(mapIcon.getRotation() & 0xF);
                if (mapIcon.getText() != null) {
                    packetByteBuf.writeBoolean(true);
                    packetByteBuf.writeText(mapIcon.getText());
                } else {
                    packetByteBuf.writeBoolean(false);
                }
            });
        } else {
            buf.writeBoolean(false);
        }
        if (this.updateData != null) {
            buf.writeByte(this.updateData.width);
            buf.writeByte(this.updateData.height);
            buf.writeByte(this.updateData.startX);
            buf.writeByte(this.updateData.startZ);
            buf.writeByteArray(this.updateData.colors);
        } else {
            buf.writeByte(0);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onMapUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void apply(MapState mapState) {
        if (this.icons != null) {
            mapState.replaceIcons(this.icons);
        }
        if (this.updateData != null) {
            this.updateData.setColorsTo(mapState);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public byte method_32701() {
        return this.scale;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_32702() {
        return this.locked;
    }
}

