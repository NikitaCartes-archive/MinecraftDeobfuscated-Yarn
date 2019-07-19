package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BossBarManager {
	private final MinecraftServer server;
	private final Map<Identifier, CommandBossBar> commandBossBars = Maps.<Identifier, CommandBossBar>newHashMap();

	public BossBarManager(MinecraftServer server) {
		this.server = server;
	}

	@Nullable
	public CommandBossBar get(Identifier id) {
		return (CommandBossBar)this.commandBossBars.get(id);
	}

	public CommandBossBar add(Identifier id, Text displayName) {
		CommandBossBar commandBossBar = new CommandBossBar(id, displayName);
		this.commandBossBars.put(id, commandBossBar);
		return commandBossBar;
	}

	public void remove(CommandBossBar bossBar) {
		this.commandBossBars.remove(bossBar.getId());
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

	public void fromTag(CompoundTag tag) {
		for (String string : tag.getKeys()) {
			Identifier identifier = new Identifier(string);
			this.commandBossBars.put(identifier, CommandBossBar.fromTag(tag.getCompound(string), identifier));
		}
	}

	public void onPlayerConnect(ServerPlayerEntity player) {
		for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
			commandBossBar.onPlayerConnect(player);
		}
	}

	public void onPlayerDisconnenct(ServerPlayerEntity player) {
		for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
			commandBossBar.onPlayerDisconnect(player);
		}
	}
}
