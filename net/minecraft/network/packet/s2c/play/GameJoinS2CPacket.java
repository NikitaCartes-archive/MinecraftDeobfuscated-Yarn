/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class GameJoinS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int playerEntityId;
    private final long sha256Seed;
    private final boolean hardcore;
    private final GameMode gameMode;
    @Nullable
    private final GameMode previousGameMode;
    private final Set<RegistryKey<World>> dimensionIds;
    private final DynamicRegistryManager.Impl registryManager;
    private final DimensionType dimensionType;
    private final RegistryKey<World> dimensionId;
    private final int maxPlayers;
    private final int viewDistance;
    private final boolean reducedDebugInfo;
    private final boolean showDeathScreen;
    private final boolean debugWorld;
    private final boolean flatWorld;

    public GameJoinS2CPacket(int playerEntityId, GameMode gameMode, @Nullable GameMode previousGameMode, long sha256Seed, boolean hardcore, Set<RegistryKey<World>> dimensionIds, DynamicRegistryManager.Impl registryManager, DimensionType dimensionType, RegistryKey<World> dimensionId, int maxPlayers, int chunkLoadDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean debugWorld, boolean flatWorld) {
        this.playerEntityId = playerEntityId;
        this.dimensionIds = dimensionIds;
        this.registryManager = registryManager;
        this.dimensionType = dimensionType;
        this.dimensionId = dimensionId;
        this.sha256Seed = sha256Seed;
        this.gameMode = gameMode;
        this.previousGameMode = previousGameMode;
        this.maxPlayers = maxPlayers;
        this.hardcore = hardcore;
        this.viewDistance = chunkLoadDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.showDeathScreen = showDeathScreen;
        this.debugWorld = debugWorld;
        this.flatWorld = flatWorld;
    }

    public GameJoinS2CPacket(PacketByteBuf packetByteBuf2) {
        this.playerEntityId = packetByteBuf2.readInt();
        this.hardcore = packetByteBuf2.readBoolean();
        this.gameMode = GameMode.byId(packetByteBuf2.readByte());
        this.previousGameMode = GameMode.getOrNull(packetByteBuf2.readByte());
        this.dimensionIds = packetByteBuf2.method_34068(Sets::newHashSetWithExpectedSize, packetByteBuf -> RegistryKey.of(Registry.DIMENSION, packetByteBuf.readIdentifier()));
        this.registryManager = packetByteBuf2.decode(DynamicRegistryManager.Impl.CODEC);
        this.dimensionType = packetByteBuf2.decode(DimensionType.REGISTRY_CODEC).get();
        this.dimensionId = RegistryKey.of(Registry.DIMENSION, packetByteBuf2.readIdentifier());
        this.sha256Seed = packetByteBuf2.readLong();
        this.maxPlayers = packetByteBuf2.readVarInt();
        this.viewDistance = packetByteBuf2.readVarInt();
        this.reducedDebugInfo = packetByteBuf2.readBoolean();
        this.showDeathScreen = packetByteBuf2.readBoolean();
        this.debugWorld = packetByteBuf2.readBoolean();
        this.flatWorld = packetByteBuf2.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(GameMode.getId(this.previousGameMode));
        buf.method_34062(this.dimensionIds, (packetByteBuf, registryKey) -> packetByteBuf.writeIdentifier(registryKey.getValue()));
        buf.encode(DynamicRegistryManager.Impl.CODEC, this.registryManager);
        buf.encode(DimensionType.REGISTRY_CODEC, () -> this.dimensionType);
        buf.writeIdentifier(this.dimensionId.getValue());
        buf.writeLong(this.sha256Seed);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.debugWorld);
        buf.writeBoolean(this.flatWorld);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameJoin(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getEntityId() {
        return this.playerEntityId;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSha256Seed() {
        return this.sha256Seed;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isHardcore() {
        return this.hardcore;
    }

    @Environment(value=EnvType.CLIENT)
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public GameMode getPreviousGameMode() {
        return this.previousGameMode;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<RegistryKey<World>> getDimensionIds() {
        return this.dimensionIds;
    }

    @Environment(value=EnvType.CLIENT)
    public DynamicRegistryManager getRegistryManager() {
        return this.registryManager;
    }

    @Environment(value=EnvType.CLIENT)
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Environment(value=EnvType.CLIENT)
    public RegistryKey<World> getDimensionId() {
        return this.dimensionId;
    }

    @Environment(value=EnvType.CLIENT)
    public int getViewDistance() {
        return this.viewDistance;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean hasReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean showsDeathScreen() {
        return this.showDeathScreen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isDebugWorld() {
        return this.debugWorld;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFlatWorld() {
        return this.flatWorld;
    }
}

