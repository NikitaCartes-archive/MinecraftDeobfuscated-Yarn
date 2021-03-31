package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
			bufx -> {
				Identifier identifier = bufx.readIdentifier();
				EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
				double d = bufx.readDouble();
				List<EntityAttributeModifier> list = bufx.readList(
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
		buf.writeCollection(this.entries, (bufx, attribute) -> {
			bufx.writeIdentifier(Registry.ATTRIBUTE.getId(attribute.getId()));
			bufx.writeDouble(attribute.getBaseValue());
			bufx.writeCollection(attribute.getModifiers(), (bufxx, modifier) -> {
				bufxx.writeUuid(modifier.getId());
				bufxx.writeDouble(modifier.getValue());
				bufxx.writeByte(modifier.getOperation().getId());
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
