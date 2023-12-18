package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import io.netty.handler.codec.DecoderException;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class EntityAttributesS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;
	private final List<EntityAttributesS2CPacket.Entry> entries;

	public EntityAttributesS2CPacket(int entityId, Collection<EntityAttributeInstance> attributes) {
		this.entityId = entityId;
		this.entries = Lists.<EntityAttributesS2CPacket.Entry>newArrayList();

		for (EntityAttributeInstance entityAttributeInstance : attributes) {
			this.entries
				.add(
					new EntityAttributesS2CPacket.Entry(entityAttributeInstance.getAttribute(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers())
				);
		}
	}

	public EntityAttributesS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.entries = buf.readList(
			buf2 -> {
				RegistryEntry<EntityAttribute> registryEntry = buf2.readRegistryValue(Registries.ATTRIBUTE.getIndexedEntries());
				if (registryEntry == null) {
					throw new DecoderException("Received unrecognized attribute id");
				} else {
					double d = buf2.readDouble();
					List<EntityAttributeModifier> list = buf2.readList(
						modifiers -> new EntityAttributeModifier(
								modifiers.readUuid(), "Unknown synced attribute modifier", modifiers.readDouble(), EntityAttributeModifier.Operation.fromId(modifiers.readByte())
							)
					);
					return new EntityAttributesS2CPacket.Entry(registryEntry, d, list);
				}
			}
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeCollection(this.entries, (buf2, attribute) -> {
			buf2.writeRegistryValue(Registries.ATTRIBUTE.getIndexedEntries(), attribute.attribute());
			buf2.writeDouble(attribute.base());
			buf2.writeCollection(attribute.modifiers(), (buf3, modifier) -> {
				buf3.writeUuid(modifier.getId());
				buf3.writeDouble(modifier.getValue());
				buf3.writeByte(modifier.getOperation().getId());
			});
		});
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttributes(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public List<EntityAttributesS2CPacket.Entry> getEntries() {
		return this.entries;
	}

	public static record Entry(RegistryEntry<EntityAttribute> attribute, double base, Collection<EntityAttributeModifier> modifiers) {
	}
}
