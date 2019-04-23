package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BossBarManager {
	private final MinecraftServer server;
	private final Map<Identifier, CommandBossBar> commandBossBars = Maps.<Identifier, CommandBossBar>newHashMap();

	public BossBarManager(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Nullable
	public CommandBossBar get(Identifier identifier) {
		return (CommandBossBar)this.commandBossBars.get(identifier);
	}

	public CommandBossBar add(Identifier identifier, Component component) {
		CommandBossBar commandBossBar = new CommandBossBar(identifier, component);
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
