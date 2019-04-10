package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.util.PacketByteBuf;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener> {
	private String teamName = "";
	private TextComponent field_12603 = new StringTextComponent("");
	private TextComponent field_12601 = new StringTextComponent("");
	private TextComponent field_12597 = new StringTextComponent("");
	private String nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
	private String collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
	private TextFormat field_12598 = TextFormat.color;
	private final Collection<String> playerList = Lists.<String>newArrayList();
	private int mode;
	private int flags;

	public TeamS2CPacket() {
	}

	public TeamS2CPacket(Team team, int i) {
		this.teamName = team.getName();
		this.mode = i;
		if (i == 0 || i == 2) {
			this.field_12603 = team.getDisplayName();
			this.flags = team.getFriendlyFlagsBitwise();
			this.nameTagVisibilityRule = team.getNameTagVisibilityRule().name;
			this.collisionRule = team.getCollisionRule().name;
			this.field_12598 = team.getColor();
			this.field_12601 = team.getPrefix();
			this.field_12597 = team.getSuffix();
		}

		if (i == 0) {
			this.playerList.addAll(team.getPlayerList());
		}
	}

	public TeamS2CPacket(Team team, Collection<String> collection, int i) {
		if (i != 3 && i != 4) {
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
		} else if (collection != null && !collection.isEmpty()) {
			this.mode = i;
			this.teamName = team.getName();
			this.playerList.addAll(collection);
		} else {
			throw new IllegalArgumentException("Players cannot be null/empty");
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.teamName = packetByteBuf.readString(16);
		this.mode = packetByteBuf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.field_12603 = packetByteBuf.readTextComponent();
			this.flags = packetByteBuf.readByte();
			this.nameTagVisibilityRule = packetByteBuf.readString(40);
			this.collisionRule = packetByteBuf.readString(40);
			this.field_12598 = packetByteBuf.readEnumConstant(TextFormat.class);
			this.field_12601 = packetByteBuf.readTextComponent();
			this.field_12597 = packetByteBuf.readTextComponent();
		}

		if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
			int i = packetByteBuf.readVarInt();

			for (int j = 0; j < i; j++) {
				this.playerList.add(packetByteBuf.readString(40));
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.teamName);
		packetByteBuf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			packetByteBuf.writeTextComponent(this.field_12603);
			packetByteBuf.writeByte(this.flags);
			packetByteBuf.writeString(this.nameTagVisibilityRule);
			packetByteBuf.writeString(this.collisionRule);
			packetByteBuf.writeEnumConstant(this.field_12598);
			packetByteBuf.writeTextComponent(this.field_12601);
			packetByteBuf.writeTextComponent(this.field_12597);
		}

		if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
			packetByteBuf.writeVarInt(this.playerList.size());

			for (String string : this.playerList) {
				packetByteBuf.writeString(string);
			}
		}
	}

	public void method_11860(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTeam(this);
	}

	@Environment(EnvType.CLIENT)
	public String getTeamName() {
		return this.teamName;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getDisplayName() {
		return this.field_12603;
	}

	@Environment(EnvType.CLIENT)
	public Collection<String> getPlayerList() {
		return this.playerList;
	}

	@Environment(EnvType.CLIENT)
	public int getMode() {
		return this.mode;
	}

	@Environment(EnvType.CLIENT)
	public int getFlags() {
		return this.flags;
	}

	@Environment(EnvType.CLIENT)
	public TextFormat getPlayerPrefix() {
		return this.field_12598;
	}

	@Environment(EnvType.CLIENT)
	public String getNameTagVisibilityRule() {
		return this.nameTagVisibilityRule;
	}

	@Environment(EnvType.CLIENT)
	public String getCollisionRule() {
		return this.collisionRule;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_11856() {
		return this.field_12601;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent method_11854() {
		return this.field_12597;
	}
}
