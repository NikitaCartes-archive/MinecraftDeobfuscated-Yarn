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

    public EntityAttributesS2CPacket(PacketByteBuf buf2) {
        this.entityId = buf2.readVarInt();
        this.entries = buf2.readList(buf -> {
            Identifier identifier = buf.readIdentifier();
            EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
            double d = buf.readDouble();
            List<EntityAttributeModifier> list = buf.readList(modifiers -> new EntityAttributeModifier(modifiers.readUuid(), "Unknown synced attribute modifier", modifiers.readDouble(), EntityAttributeModifier.Operation.fromId(modifiers.readByte())));
            return new Entry(entityAttribute, d, list);
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeCollection(this.entries, (buf2, attribute) -> {
            buf2.writeIdentifier(Registry.ATTRIBUTE.getId(attribute.getId()));
            buf2.writeDouble(attribute.getBaseValue());
            buf2.writeCollection(attribute.getModifiers(), (buf, modifier) -> {
                buf.writeUuid(modifier.getId());
                buf.writeDouble(modifier.getValue());
                buf.writeByte(modifier.getOperation().getId());
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
        private final EntityAttribute attribute;
        private final double baseValue;
        private final Collection<EntityAttributeModifier> modifiers;

        public Entry(EntityAttribute attribute, double baseValue, Collection<EntityAttributeModifier> modifiers) {
            this.attribute = attribute;
            this.baseValue = baseValue;
            this.modifiers = modifiers;
        }

        public EntityAttribute getId() {
            return this.attribute;
        }

        public double getBaseValue() {
            return this.baseValue;
        }

        public Collection<EntityAttributeModifier> getModifiers() {
            return this.modifiers;
        }
    }
}

