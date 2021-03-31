/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
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

    public MapUpdateS2CPacket(PacketByteBuf buf) {
        this.id = buf.readVarInt();
        this.scale = buf.readByte();
        this.locked = buf.readBoolean();
        this.icons = buf.readBoolean() ? buf.readList(b -> {
            MapIcon.Type type = b.readEnumConstant(MapIcon.Type.class);
            return new MapIcon(type, b.readByte(), b.readByte(), (byte)(b.readByte() & 0xF), b.readBoolean() ? b.readText() : null);
        }) : null;
        short i = buf.readUnsignedByte();
        if (i > 0) {
            short j = buf.readUnsignedByte();
            short k = buf.readUnsignedByte();
            short l = buf.readUnsignedByte();
            byte[] bs = buf.readByteArray();
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
            buf.writeCollection(this.icons, (b, icon) -> {
                b.writeEnumConstant(icon.getType());
                b.writeByte(icon.getX());
                b.writeByte(icon.getZ());
                b.writeByte(icon.getRotation() & 0xF);
                if (icon.getText() != null) {
                    b.writeBoolean(true);
                    b.writeText(icon.getText());
                } else {
                    b.writeBoolean(false);
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

    public int getId() {
        return this.id;
    }

    @Override
    public void apply(MapState mapState) {
        if (this.icons != null) {
            mapState.replaceIcons(this.icons);
        }
        if (this.updateData != null) {
            this.updateData.setColorsTo(mapState);
        }
    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }
}

