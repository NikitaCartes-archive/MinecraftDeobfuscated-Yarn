/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityEquipmentUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private final List<Pair<EquipmentSlot, ItemStack>> field_25721;

    public EntityEquipmentUpdateS2CPacket() {
        this.field_25721 = Lists.newArrayList();
    }

    public EntityEquipmentUpdateS2CPacket(int i, List<Pair<EquipmentSlot, ItemStack>> list) {
        this.id = i;
        this.field_25721 = list;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        byte i;
        this.id = buf.readVarInt();
        EquipmentSlot[] equipmentSlots = EquipmentSlot.values();
        do {
            i = buf.readByte();
            EquipmentSlot equipmentSlot = equipmentSlots[i & 0x7F];
            ItemStack itemStack = buf.readItemStack();
            this.field_25721.add(Pair.of(equipmentSlot, itemStack));
        } while ((i & 0xFFFFFF80) != 0);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.id);
        int i = this.field_25721.size();
        for (int j = 0; j < i; ++j) {
            Pair<EquipmentSlot, ItemStack> pair = this.field_25721.get(j);
            EquipmentSlot equipmentSlot = pair.getFirst();
            boolean bl = j != i - 1;
            int k = equipmentSlot.ordinal();
            buf.writeByte(bl ? k | 0xFFFFFF80 : k);
            buf.writeItemStack(pair.getSecond());
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEquipmentUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Pair<EquipmentSlot, ItemStack>> method_30145() {
        return this.field_25721;
    }
}

