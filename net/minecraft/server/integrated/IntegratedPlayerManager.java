/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.WorldSaveHandler;

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    private CompoundTag userData;

    public IntegratedPlayerManager(IntegratedServer integratedServer, RegistryTracker.Modifiable modifiable, WorldSaveHandler worldSaveHandler) {
        super(integratedServer, modifiable, worldSaveHandler, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity player) {
        if (player.getName().getString().equals(this.getServer().getUserName())) {
            this.userData = player.toTag(new CompoundTag());
        }
        super.savePlayerData(player);
    }

    @Override
    public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.getServer().getUserName()) && this.getPlayer(gameProfile.getName()) != null) {
            return new TranslatableText("multiplayer.disconnect.name_taken");
        }
        return super.checkCanJoin(socketAddress, gameProfile);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public CompoundTag getUserData() {
        return this.userData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.getServer();
    }
}

