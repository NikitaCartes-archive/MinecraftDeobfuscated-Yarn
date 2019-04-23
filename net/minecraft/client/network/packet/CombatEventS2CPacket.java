/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CombatEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    public Type type;
    public int entityId;
    public int attackerEntityId;
    public int timeSinceLastAttack;
    public Component deathMessage;

    public CombatEventS2CPacket() {
    }

    public CombatEventS2CPacket(DamageTracker damageTracker, Type type) {
        this(damageTracker, type, new TextComponent(""));
    }

    public CombatEventS2CPacket(DamageTracker damageTracker, Type type, Component component) {
        this.type = type;
        LivingEntity livingEntity = damageTracker.getBiggestAttacker();
        switch (type) {
            case END_COMBAT: {
                this.timeSinceLastAttack = damageTracker.getTimeSinceLastAttack();
                this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
                break;
            }
            case ENTITY_DIED: {
                this.entityId = damageTracker.getEntity().getEntityId();
                this.attackerEntityId = livingEntity == null ? -1 : livingEntity.getEntityId();
                this.deathMessage = component;
            }
        }
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.type = packetByteBuf.readEnumConstant(Type.class);
        if (this.type == Type.END_COMBAT) {
            this.timeSinceLastAttack = packetByteBuf.readVarInt();
            this.attackerEntityId = packetByteBuf.readInt();
        } else if (this.type == Type.ENTITY_DIED) {
            this.entityId = packetByteBuf.readVarInt();
            this.attackerEntityId = packetByteBuf.readInt();
            this.deathMessage = packetByteBuf.readTextComponent();
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.type);
        if (this.type == Type.END_COMBAT) {
            packetByteBuf.writeVarInt(this.timeSinceLastAttack);
            packetByteBuf.writeInt(this.attackerEntityId);
        } else if (this.type == Type.ENTITY_DIED) {
            packetByteBuf.writeVarInt(this.entityId);
            packetByteBuf.writeInt(this.attackerEntityId);
            packetByteBuf.writeTextComponent(this.deathMessage);
        }
    }

    public void method_11706(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCombatEvent(this);
    }

    @Override
    public boolean isErrorFatal() {
        return this.type == Type.ENTITY_DIED;
    }

    public static enum Type {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED;

    }
}

