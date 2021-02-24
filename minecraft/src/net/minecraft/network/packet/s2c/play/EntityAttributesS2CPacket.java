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

	public EntityAttributesS2CPacket(PacketByteBuf packetByteBuf) {
		this.entityId = packetByteBuf.readVarInt();
		this.entries = packetByteBuf.method_34066(
			packetByteBufx -> {
				Identifier identifier = packetByteBufx.readIdentifier();
				EntityAttribute entityAttribute = Registry.ATTRIBUTE.get(identifier);
				double d = packetByteBufx.readDouble();
				List<EntityAttributeModifier> list = packetByteBufx.method_34066(
					packetByteBufxx -> new EntityAttributeModifier(
							packetByteBufxx.readUuid(),
							"Unknown synced attribute modifier",
							packetByteBufxx.readDouble(),
							EntityAttributeModifier.Operation.fromId(packetByteBufxx.readByte())
						)
				);
				return new EntityAttributesS2CPacket.Entry(entityAttribute, d, list);
			}
		);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.method_34062(this.entries, (packetByteBuf, entry) -> {
			packetByteBuf.writeIdentifier(Registry.ATTRIBUTE.getId(entry.getId()));
			packetByteBuf.writeDouble(entry.getBaseValue());
			packetByteBuf.method_34062(entry.getModifiers(), (packetByteBufx, entityAttributeModifier) -> {
				packetByteBufx.writeUuid(entityAttributeModifier.getId());
				packetByteBufx.writeDouble(entityAttributeModifier.getValue());
				packetByteBufx.writeByte(entityAttributeModifier.getOperation().getId());
			});
		});
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onEntityAttributes(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	public List<EntityAttributesS2CPacket.Entry> getEntries() {
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
