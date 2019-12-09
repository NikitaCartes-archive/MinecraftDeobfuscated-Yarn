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

    public LightUpdateS2CPacket(ChunkPos pos, LightingProvider lightProvider, int skyLightMask, int blockLightMask) {
        this.chunkX = pos.x;
        this.chunkZ = pos.z;
        this.skyLightMask = skyLightMask;
        this.blockLightMask = blockLightMask;
        this.skyLightUpdates = Lists.newArrayList();
        this.blockLightUpdates = Lists.newArrayList();
        for (int i = 0; i < 18; ++i) {
            ChunkNibbleArray chunkNibbleArray;
            if ((this.skyLightMask & 1 << i) != 0) {
                chunkNibbleArray = lightProvider.get(LightType.SKY).getLightArray(ChunkSectionPos.from(pos, -1 + i));
                if (chunkNibbleArray == null || chunkNibbleArray.isUninitialized()) {
                    this.skyLightMask &= ~(1 << i);
                    if (chunkNibbleArray != null) {
                        this.filledSkyLightMask |= 1 << i;
                    }
                } else {
                    this.skyLightUpdates.add((byte[])chunkNibbleArray.asByteArray().clone());
                }
            }
            if ((this.blockLightMask & 1 << i) == 0) continue;
            chunkNibbleArray = lightProvider.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(pos, -1 + i));
            if (chunkNibbleArray == null || chunkNibbleArray.isUninitialized()) {
                this.blockLightMask &= ~(1 << i);
                if (chunkNibbleArray == null) continue;
                this.filledBlockLightMask |= 1 << i;
                continue;
            }
            this.blockLightUpdates.add((byte[])chunkNibbleArray.asByteArray().clone());
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        int i;
        this.chunkX = buf.readVarInt();
        this.chunkZ = buf.readVarInt();
        this.skyLightMask = buf.readVarInt();
        this.blockLightMask = buf.readVarInt();
        this.filledSkyLightMask = buf.readVarInt();
        this.filledBlockLightMask = buf.readVarInt();
        this.skyLightUpdates = Lists.newArrayList();
        for (i = 0; i < 18; ++i) {
            if ((this.skyLightMask & 1 << i) == 0) continue;
            this.skyLightUpdates.add(buf.readByteArray(2048));
        }
        this.blockLightUpdates = Lists.newArrayList();
        for (i = 0; i < 18; ++i) {
            if ((this.blockLightMask & 1 << i) == 0) continue;
            this.blockLightUpdates.add(buf.readByteArray(2048));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.chunkX);
        buf.writeVarInt(this.chunkZ);
        buf.writeVarInt(this.skyLightMask);
        buf.writeVarInt(this.blockLightMask);
        buf.writeVarInt(this.filledSkyLightMask);
        buf.writeVarInt(this.filledBlockLightMask);
        for (byte[] bs : this.skyLightUpdates) {
            buf.writeByteArray(bs);
        }
        for (byte[] bs : this.blockLightUpdates) {
            buf.writeByteArray(bs);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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

