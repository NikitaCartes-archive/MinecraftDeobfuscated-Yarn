package net.minecraft.entity.boss;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CommandBossBar extends ServerBossBar {
	private final Identifier id;
	private final Set<UUID> playerUuids = Sets.<UUID>newHashSet();
	private int value;
	private int maxValue = 100;

	public CommandBossBar(Identifier id, Text displayName) {
		super(displayName, BossBar.Color.WHITE, BossBar.Style.PROGRESS);
		this.id = id;
		this.setPercent(0.0F);
	}

	public Identifier getId() {
		return this.id;
	}

	@Override
	public void addPlayer(ServerPlayerEntity player) {
		super.addPlayer(player);
		this.playerUuids.add(player.getUuid());
	}

	public void addPlayer(UUID uuid) {
		this.playerUuids.add(uuid);
	}

	@Override
	public void removePlayer(ServerPlayerEntity player) {
		super.removePlayer(player);
		this.playerUuids.remove(player.getUuid());
	}

	@Override
	public void clearPlayers() {
		super.clearPlayers();
		this.playerUuids.clear();
	}

	public int getValue() {
		return this.value;
	}

	public int getMaxValue() {
		return this.maxValue;
	}

	public void setValue(int value) {
		this.value = value;
		this.setPercent(MathHelper.clamp((float)value / (float)this.maxValue, 0.0F, 1.0F));
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		this.setPercent(MathHelper.clamp((float)this.value / (float)maxValue, 0.0F, 1.0F));
	}

	public final Text toHoverableText() {
		return Texts.bracketed(this.getName())
			.styled(
				style -> style.withColor(this.getColor().getTextFormat())
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(this.getId().toString())))
						.withInsertion(this.getId().toString())
			);
	}

	public boolean addPlayers(Collection<ServerPlayerEntity> players) {
		Set<UUID> set = Sets.<UUID>newHashSet();
		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet();

		for (UUID uUID : this.playerUuids) {
			boolean bl = false;

			for (ServerPlayerEntity serverPlayerEntity : players) {
				if (serverPlayerEntity.getUuid().equals(uUID)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set.add(uUID);
			}
		}

		for (ServerPlayerEntity serverPlayerEntity2 : players) {
			boolean bl = false;

			for (UUID uUID2 : this.playerUuids) {
				if (serverPlayerEntity2.getUuid().equals(uUID2)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set2.add(serverPlayerEntity2);
			}
		}

		for (UUID uUID : set) {
			for (ServerPlayerEntity serverPlayerEntity3 : this.getPlayers()) {
				if (serverPlayerEntity3.getUuid().equals(uUID)) {
					this.removePlayer(serverPlayerEntity3);
					break;
				}
			}

			this.playerUuids.remove(uUID);
		}

		for (ServerPlayerEntity serverPlayerEntity2 : set2) {
			this.addPlayer(serverPlayerEntity2);
		}

		return !set.isEmpty() || !set2.isEmpty();
	}

	public NbtCompound toNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("Name", Text.Serialization.toJsonString(this.name, wrapperLookup));
		nbtCompound.putBoolean("Visible", this.isVisible());
		nbtCompound.putInt("Value", this.value);
		nbtCompound.putInt("Max", this.maxValue);
		nbtCompound.putString("Color", this.getColor().getName());
		nbtCompound.putString("Overlay", this.getStyle().getName());
		nbtCompound.putBoolean("DarkenScreen", this.shouldDarkenSky());
		nbtCompound.putBoolean("PlayBossMusic", this.hasDragonMusic());
		nbtCompound.putBoolean("CreateWorldFog", this.shouldThickenFog());
		NbtList nbtList = new NbtList();

		for (UUID uUID : this.playerUuids) {
			nbtList.add(NbtHelper.fromUuid(uUID));
		}

		nbtCompound.put("Players", nbtList);
		return nbtCompound;
	}

	public static CommandBossBar fromNbt(NbtCompound nbt, Identifier id, RegistryWrapper.WrapperLookup wrapperLookup) {
		CommandBossBar commandBossBar = new CommandBossBar(id, Text.Serialization.fromJson(nbt.getString("Name"), wrapperLookup));
		commandBossBar.setVisible(nbt.getBoolean("Visible"));
		commandBossBar.setValue(nbt.getInt("Value"));
		commandBossBar.setMaxValue(nbt.getInt("Max"));
		commandBossBar.setColor(BossBar.Color.byName(nbt.getString("Color")));
		commandBossBar.setStyle(BossBar.Style.byName(nbt.getString("Overlay")));
		commandBossBar.setDarkenSky(nbt.getBoolean("DarkenScreen"));
		commandBossBar.setDragonMusic(nbt.getBoolean("PlayBossMusic"));
		commandBossBar.setThickenFog(nbt.getBoolean("CreateWorldFog"));

		for (NbtElement nbtElement : nbt.getList("Players", NbtElement.INT_ARRAY_TYPE)) {
			commandBossBar.addPlayer(NbtHelper.toUuid(nbtElement));
		}

		return commandBossBar;
	}

	public void onPlayerConnect(ServerPlayerEntity player) {
		if (this.playerUuids.contains(player.getUuid())) {
			this.addPlayer(player);
		}
	}

	public void onPlayerDisconnect(ServerPlayerEntity player) {
		super.removePlayer(player);
	}
}
