package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

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
				Identifier identifier = buf2.readIdentifier();
				EntityAttribute entityAttribute = Registries.ATTRIBUTE.get(identifier);
				double d = buf2.readDouble();
				List<EntityAttributeModifier> list = buf2.readList(
					modifiers -> new EntityAttributeModifier(
							modifiers.readUuid(), "Unknown synced attribute modifier", modifiers.readDouble(), EntityAttributeModifier.Operation.fromId(modifiers.readByte())
						)
				);
				return new EntityAttributesS2CPacket.Entry(entityAttribute, d, list);
			}
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeCollection(this.entries, (buf2, attribute) -> {
			buf2.writeIdentifier(Registries.ATTRIBUTE.getId(attribute.getId()));
			buf2.writeDouble(attribute.getBaseValue());
			buf2.writeCollection(attribute.getModifiers(), (buf3, modifier) -> {
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
