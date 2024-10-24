package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BossBarManager {
	private final Map<Identifier, CommandBossBar> commandBossBars = Maps.<Identifier, CommandBossBar>newHashMap();

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

	public NbtCompound toNbt(RegistryWrapper.WrapperLookup registries) {
		NbtCompound nbtCompound = new NbtCompound();

		for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
			nbtCompound.put(commandBossBar.getId().toString(), commandBossBar.toNbt(registries));
		}

		return nbtCompound;
	}

	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		for (String string : nbt.getKeys()) {
			Identifier identifier = Identifier.of(string);
			this.commandBossBars.put(identifier, CommandBossBar.fromNbt(nbt.getCompound(string), identifier, registries));
		}
	}

	public void onPlayerConnect(ServerPlayerEntity player) {
		for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
			commandBossBar.onPlayerConnect(player);
		}
	}

	public void onPlayerDisconnect(ServerPlayerEntity player) {
		for (CommandBossBar commandBossBar : this.commandBossBars.values()) {
			commandBossBar.onPlayerDisconnect(player);
		}
	}
}
