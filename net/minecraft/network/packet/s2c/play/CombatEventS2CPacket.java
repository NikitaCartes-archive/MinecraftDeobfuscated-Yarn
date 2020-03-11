/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CombatEventS2CPacket
implements Packet<ClientPlayPacketListener> {
    public Type type;
    public int entityId;
    public int attackerEntityId;
    public int timeSinceLastAttack;
    public Text deathMessage;

    public CombatEventS2CPacket() {
    }

    public CombatEventS2CPacket(DamageTracker damageTracker, Type type) {
        this(damageTracker, type, new LiteralText(""));
    }

    public CombatEventS2CPacket(DamageTracker damageTracker, Type type, Text deathMessage) {
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
                this.deathMessage = deathMessage;
            }
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.type = buf.readEnumConstant(Type.class);
        if (this.type == Type.END_COMBAT) {
            this.timeSinceLastAttack = buf.readVarInt();
            this.attackerEntityId = buf.readInt();
        } else if (this.type == Type.ENTITY_DIED) {
            this.entityId = buf.readVarInt();
            this.attackerEntityId = buf.readInt();
            this.deathMessage = buf.readText();
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.type);
        if (this.type == Type.END_COMBAT) {
            buf.writeVarInt(this.timeSinceLastAttack);
            buf.writeInt(this.attackerEntityId);
        } else if (this.type == Type.ENTITY_DIED) {
            buf.writeVarInt(this.entityId);
            buf.writeInt(this.attackerEntityId);
            buf.writeText(this.deathMessage);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCombatEvent(this);
    }

    @Override
    public boolean isWritingErrorSkippable() {
        return this.type == Type.ENTITY_DIED;
    }

    public static enum Type {
        ENTER_COMBAT,
        END_COMBAT,
        ENTITY_DIED;

    }
}

