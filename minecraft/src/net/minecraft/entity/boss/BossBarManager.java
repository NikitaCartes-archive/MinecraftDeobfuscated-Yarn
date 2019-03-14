package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;

public class BossBarManager {
	private final MinecraftServer server;
	private final Map<Identifier, CommandBossBar> bossBars = Maps.<Identifier, CommandBossBar>newHashMap();

	public BossBarManager(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Nullable
	public CommandBossBar get(Identifier identifier) {
		return (CommandBossBar)this.bossBars.get(identifier);
	}

	public CommandBossBar add(Identifier identifier, TextComponent textComponent) {
		CommandBossBar commandBossBar = new CommandBossBar(identifier, textComponent);
		this.bossBars.put(identifier, commandBossBar);
		return commandBossBar;
	}

	public void remove(CommandBossBar commandBossBar) {
		this.bossBars.remove(commandBossBar.getId());
	}

	public Collection<Identifier> getIds() {
		return this.bossBars.keySet();
	}

	public Collection<CommandBossBar> getAll() {
		return this.bossBars.values();
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();

		for (CommandBossBar commandBossBar : this.bossBars.values()) {
			compoundTag.put(commandBossBar.getId().toString(), commandBossBar.toTag());
		}

		return compoundTag;
	}

	public void fromTag(CompoundTag compoundTag) {
		for (String string : compoundTag.getKeys()) {
			Identifier identifier = new Identifier(string);
			this.bossBars.put(identifier, CommandBossBar.fromTag(compoundTag.getCompound(string), identifier));
		}
	}

	public void method_12975(ServerPlayerEntity serverPlayerEntity) {
		for (CommandBossBar commandBossBar : this.bossBars.values()) {
			commandBossBar.method_12957(serverPlayerEntity);
		}
	}

	public void method_12976(ServerPlayerEntity serverPlayerEntity) {
		for (CommandBossBar commandBossBar : this.bossBars.values()) {
			commandBossBar.method_12961(serverPlayerEntity);
		}
	}
}
