/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityAttributesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int entityId;
    private final List<Entry> entries;

    public EntityAttributesS2CPacket(int entityId, Collection<EntityAttributeInstance> attributes) {
        this.entityId = entityId;
        this.entries = Lists.newArrayList();
        for (EntityAttributeInstance entityAttributeInstance : attributes) {
            this.entries.add(new Entry(entityAttributeInstance.getAttribute(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers()));
        }
    }

    public EntityAttributesS2CPacket(PacketByteBuf packetByteBuf) {
        this.entityId = packetByteBuf.readVarInt();
        this.entries = packetByteBuf.method_34066(packetByteBuf2 -> {
            Identifier identifier = packetByteBuf2.readIdentifier();
            EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
            double d = packetByteBuf2.readDouble();
            List<EntityAttributeModifier> list = packetByteBuf2.method_34066(packetByteBuf -> new EntityAttributeModifier(packetByteBuf.readUuid(), "Unknown synced attribute modifier", packetByteBuf.readDouble(), EntityAttributeModifier.Operation.fromId(packetByteBuf.readByte())));
            return new Entry(entityAttribute, d, list);
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.method_34062(this.entries, (packetByteBuf2, entry) -> {
            packetByteBuf2.writeIdentifier(Registry.ATTRIBUTE.getId(entry.getId()));
            packetByteBuf2.writeDouble(entry.getBaseValue());
            packetByteBuf2.method_34062(entry.getModifiers(), (packetByteBuf, entityAttributeModifier) -> {
                packetByteBuf.writeUuid(entityAttributeModifier.getId());
                packetByteBuf.writeDouble(entityAttributeModifier.getValue());
                packetByteBuf.writeByte(entityAttributeModifier.getOperation().getId());
            });
        });
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onEntityAttributes(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }

    public static class Entry {
        private final EntityAttribute id;
        private final double baseValue;
        private final Collection<EntityAttributeModifier> modifiers;

        public Entry(EntityAttribute entityAttribute, double d, Collection<EntityAttributeModifier> collection) {
            this.id = entityAttribute;
            this.baseValue = d;
            this.modifiers = collection;
        }

        public EntityAttribute getId() {
            return this.id;
        }

        public double getBaseValue() {
            return this.baseValue;
        }

        public Collection<EntityAttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

