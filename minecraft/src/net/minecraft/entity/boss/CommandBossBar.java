package net.minecraft.entity.boss;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.MathHelper;

public class CommandBossBar extends ServerBossBar {
	private final Identifier id;
	private final Set<UUID> playerUuids = Sets.<UUID>newHashSet();
	private int value;
	private int maxValue = 100;

	public CommandBossBar(Identifier identifier, Text text) {
		super(text, BossBar.Color.field_5786, BossBar.Style.field_5795);
		this.id = identifier;
		this.setPercent(0.0F);
	}

	public Identifier getId() {
		return this.id;
	}

	@Override
	public void addPlayer(ServerPlayerEntity serverPlayerEntity) {
		super.addPlayer(serverPlayerEntity);
		this.playerUuids.add(serverPlayerEntity.getUuid());
	}

	public void addPlayer(UUID uUID) {
		this.playerUuids.add(uUID);
	}

	@Override
	public void removePlayer(ServerPlayerEntity serverPlayerEntity) {
		super.removePlayer(serverPlayerEntity);
		this.playerUuids.remove(serverPlayerEntity.getUuid());
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

	public void setValue(int i) {
		this.value = i;
		this.setPercent(MathHelper.clamp((float)i / (float)this.maxValue, 0.0F, 1.0F));
	}

	public void setMaxValue(int i) {
		this.maxValue = i;
		this.setPercent(MathHelper.clamp((float)this.value / (float)i, 0.0F, 1.0F));
	}

	public final Text toHoverableText() {
		return Texts.bracketed(this.getName())
			.styled(
				style -> style.setColor(this.getColor().getTextFormat())
						.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, new LiteralText(this.getId().toString())))
						.setInsertion(this.getId().toString())
			);
	}

	public boolean addPlayers(Collection<ServerPlayerEntity> collection) {
		Set<UUID> set = Sets.<UUID>newHashSet();
		Set<ServerPlayerEntity> set2 = Sets.<ServerPlayerEntity>newHashSet();

		for (UUID uUID : this.playerUuids) {
			boolean bl = false;

			for (ServerPlayerEntity serverPlayerEntity : collection) {
				if (serverPlayerEntity.getUuid().equals(uUID)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				set.add(uUID);
			}
		}

		for (ServerPlayerEntity serverPlayerEntity2 : collection) {
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

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("Name", Text.Serializer.toJson(this.name));
		compoundTag.putBoolean("Visible", this.isVisible());
		compoundTag.putInt("Value", this.value);
		compoundTag.putInt("Max", this.maxValue);
		compoundTag.putString("Color", this.getColor().getName());
		compoundTag.putString("Overlay", this.getOverlay().getName());
		compoundTag.putBoolean("DarkenScreen", this.getDarkenSky());
		compoundTag.putBoolean("PlayBossMusic", this.hasDragonMusic());
		compoundTag.putBoolean("CreateWorldFog", this.getThickenFog());
		ListTag listTag = new ListTag();

		for (UUID uUID : this.playerUuids) {
			listTag.add(TagHelper.serializeUuid(uUID));
		}

		compoundTag.put("Players", listTag);
		return compoundTag;
	}

	public static CommandBossBar fromTag(CompoundTag compoundTag, Identifier identifier) {
		CommandBossBar commandBossBar = new CommandBossBar(identifier, Text.Serializer.fromJson(compoundTag.getString("Name")));
		commandBossBar.setVisible(compoundTag.getBoolean("Visible"));
		commandBossBar.setValue(compoundTag.getInt("Value"));
		commandBossBar.setMaxValue(compoundTag.getInt("Max"));
		commandBossBar.setColor(BossBar.Color.byName(compoundTag.getString("Color")));
		commandBossBar.setOverlay(BossBar.Style.byName(compoundTag.getString("Overlay")));
		commandBossBar.setDarkenSky(compoundTag.getBoolean("DarkenScreen"));
		commandBossBar.setDragonMusic(compoundTag.getBoolean("PlayBossMusic"));
		commandBossBar.setThickenFog(compoundTag.getBoolean("CreateWorldFog"));
		ListTag listTag = compoundTag.getList("Players", 10);

		for (int i = 0; i < listTag.size(); i++) {
			commandBossBar.addPlayer(TagHelper.deserializeUuid(listTag.getCompoundTag(i)));
		}

		return commandBossBar;
	}

	public void onPlayerConnect(ServerPlayerEntity serverPlayerEntity) {
		if (this.playerUuids.contains(serverPlayerEntity.getUuid())) {
			this.addPlayer(serverPlayerEntity);
		}
	}

	public void onPlayerDisconnect(ServerPlayerEntity serverPlayerEntity) {
		super.removePlayer(serverPlayerEntity);
	}
}
