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

@Environment(value=EnvType.CLIENT)
public class IntegratedPlayerManager
extends PlayerManager {
    private CompoundTag userData;

    public IntegratedPlayerManager(IntegratedServer integratedServer) {
        super(integratedServer, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void savePlayerData(ServerPlayerEntity serverPlayerEntity) {
        if (serverPlayerEntity.getName().getString().equals(this.method_4811().getUserName())) {
            this.userData = serverPlayerEntity.toTag(new CompoundTag());
        }
        super.savePlayerData(serverPlayerEntity);
    }

    @Override
    public Text checkCanJoin(SocketAddress socketAddress, GameProfile gameProfile) {
        if (gameProfile.getName().equalsIgnoreCase(this.method_4811().getUserName()) && this.getPlayer(gameProfile.getName()) != null) {
            return new TranslatableText("multiplayer.disconnect.name_taken", new Object[0]);
        }
        return super.checkCanJoin(socketAddress, gameProfile);
    }

    public IntegratedServer method_4811() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public CompoundTag getUserData() {
        return this.userData;
    }

    @Override
    public /* synthetic */ MinecraftServer getServer() {
        return this.method_4811();
    }
}

