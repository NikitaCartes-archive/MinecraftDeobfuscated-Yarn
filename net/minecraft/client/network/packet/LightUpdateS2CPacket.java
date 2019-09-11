/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;

public class LightUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int chunkX;
    private int chunkZ;
    private int skyLightMask;
    private int blockLightMask;
    private int filledSkyLightMask;
    private int filledBlockLightMask;
    private List<byte[]> skyLightUpdates;
    private List<byte[]> blockLightUpdates;

    public LightUpdateS2CPacket() {
    }

    public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightingProvider) {
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(chunkPos, -1 + i));
            ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(chunkPos, -1 + i));
            if (chunkNibbleArray != null) {
                if (chunkNibbleArray.isUninitialized()) {
                    this.filledSkyLightMask |= 1 << i;
                } else {
                    this.skyLightMask |= 1 << i;
                    this.skyLightUpdates.add((byte[])chunkNibbleArray.asByteArray().clone());
                }
            }
            if (chunkNibbleArray2 == null) continue;
            if (chunkNibbleArray2.isUninitialized()) {
                this.filledBlockLightMask |= 1 << i;
                continue;
            }
            this.blockLightMask |= 1 << i;
            this.blockLightUpdates.add((byte[])chunkNibbleArray2.asByteArray().clone());
        }
    }

    public LightUpdateS2CPacket(ChunkPos chunkPos, LightingProvider lightingProvider, int i, int j) {
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.skyLightMask = i;
        this.blockLightMask = j;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int k = 0; k < 18; ++k) {
            ChunkNibbleArray chunkNibbleArray;
            if ((this.skyLightMask & 1 << k) != 0) {
                chunkNibbleArray = lightingProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(chunkPos, -1 + k));
                if (chunkNibbleArray == null || chunkNibbleArray.isUninitialized()) {
                    this.skyLightMask &= ~(1 << k);
                    if (chunkNibbleArray != null) {
                        this.filledSkyLightMask |= 1 << k;
                    }
                } else {
                    this.skyLightUpdates.add((byte[])chunkNibbleArray.asByteArray().clone());
                }
            }
            if ((this.blockLightMask & 1 << k) == 0) continue;
            chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(chunkPos, -1 + k));
            if (chunkNibbleArray == null || chunkNibbleArray.isUninitialized()) {
                this.blockLightMask &= ~(1 << k);
                if (chunkNibbleArray == null) continue;
                this.filledBlockLightMask |= 1 << k;
                continue;
            }
            this.blockLightUpdates.add((byte[])chunkNibbleArray.asByteArray().clone());
        }
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        int i;
        this.chunkX = packetByteBuf.readVarInt();
        this.chunkZ = packetByteBuf.readVarInt();
        this.skyLightMask = packetByteBuf.readVarInt();
        this.blockLightMask = packetByteBuf.readVarInt();
        this.filledSkyLightMask = packetByteBuf.readVarInt();
        this.filledBlockLightMask = packetByteBuf.readVarInt();
        this.skyLightUpdates = Lists.newArrayList();
        for (i = 0; i < 18; ++i) {
            if ((this.skyLightMask & 1 << i) == 0) continue;
            this.skyLightUpdates.add(packetByteBuf.readByteArray(2048));
        }
        this.blockLightUpdates = Lists.newArrayList();
        for (i = 0; i < 18; ++i) {
            if ((this.blockLightMask & 1 << i) == 0) continue;
            this.blockLightUpdates.add(packetByteBuf.readByteArray(2048));
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeVarInt(this.chunkX);
        packetByteBuf.writeVarInt(this.chunkZ);
        packetByteBuf.writeVarInt(this.skyLightMask);
        packetByteBuf.writeVarInt(this.blockLightMask);
        packetByteBuf.writeVarInt(this.filledSkyLightMask);
        packetByteBuf.writeVarInt(this.filledBlockLightMask);
        for (byte[] bs : this.skyLightUpdates) {
            packetByteBuf.writeByteArray(bs);
        }
        for (byte[] bs : this.blockLightUpdates) {
            packetByteBuf.writeByteArray(bs);
        }
    }

    public void method_11560(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onLightUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkX() {
        return this.chunkX;
    }

    @Environment(value=EnvType.CLIENT)
    public int getChunkZ() {
        return this.chunkZ;
    }

    @Environment(value=EnvType.CLIENT)
    public int getSkyLightMask() {
        return this.skyLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFilledSkyLightMask() {
        return this.filledSkyLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public List<byte[]> getSkyLightUpdates() {
        return this.skyLightUpdates;
    }

    @Environment(value=EnvType.CLIENT)
    public int getBlockLightMask() {
        return this.blockLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFilledBlockLightMask() {
        return this.filledBlockLightMask;
    }

    @Environment(value=EnvType.CLIENT)
    public List<byte[]> getBlockLightUpdates() {
        return this.blockLightUpdates;
    }
}

