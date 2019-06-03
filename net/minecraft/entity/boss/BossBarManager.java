/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class BossBarManager {
    private final MinecraftServer server;
    private final Map<Identifier, CommandBossBar> commandBossBars = Maps.newHashMap();

    public BossBarManager(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    @Nullable
    public CommandBossBar get(Identifier identifier) {
        return this.commandBossBars.get(identifier);
    }

    public CommandBossBar add(Identifier identifier, Text text) {
        CommandBossBar commandBossBar = new CommandBossBar(identifier, text);
        this.commandBossBars.put(identifier, commandBossBar);
        return commandBossBar;
    }

    public void remove(CommandBossBar commandBossBar) {
        this.commandBossBars.remove(commandBossBar.getId());
    }

    public Collection<Identifier> getIds() {
        return this.commandBossBars.keySet();
    }

    public Collection<CommandBossBar> getAll() {
        return this.commandBossBars.values();
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
            compoundTag.put(commandBossBar.getId().toString(), commandBossBar.toTag());
        }
        return compoundTag;
    }

    public void fromTag(CompoundTag compoundTag) {
        for (String string : compoundTag.getKeys()) {
            Identifier identifier = new Identifier(string);
            this.commandBossBars.put(identifier, CommandBossBar.fromTag(compoundTag.getCompound(string), identifier));
        }
    }

    public void onPlayerConnect(ServerPlayerEntity serverPlayerEntity) {
        for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
            commandBossBar.onPlayerConnect(serverPlayerEntity);
        }
    }

    public void onPlayerDisconnenct(ServerPlayerEntity serverPlayerEntity) {
        for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
            commandBossBar.onPlayerDisconnect(serverPlayerEntity);
        }
    }
}

