package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class class_2853 implements Packet<ServerPlayPacketListener> {
	private class_2853.class_2854 field_13009;
	private Identifier field_13004;
	private boolean guiOpen;
	private boolean filteringCraftable;
	private boolean furnaceGuiOpen;
	private boolean furnaceFilteringCraftable;

	public class_2853() {
	}

	public class_2853(Recipe recipe) {
		this.field_13009 = class_2853.class_2854.field_13011;
		this.field_13004 = recipe.getId();
	}

	@Environment(EnvType.CLIENT)
	public class_2853(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.field_13009 = class_2853.class_2854.field_13010;
		this.guiOpen = bl;
		this.filteringCraftable = bl2;
		this.furnaceGuiOpen = bl3;
		this.furnaceFilteringCraftable = bl4;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13009 = packetByteBuf.readEnumConstant(class_2853.class_2854.class);
		if (this.field_13009 == class_2853.class_2854.field_13011) {
			this.field_13004 = packetByteBuf.readIdentifier();
		} else if (this.field_13009 == class_2853.class_2854.field_13010) {
			this.guiOpen = packetByteBuf.readBoolean();
			this.filteringCraftable = packetByteBuf.readBoolean();
			this.furnaceGuiOpen = packetByteBuf.readBoolean();
			this.furnaceFilteringCraftable = packetByteBuf.readBoolean();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_13009);
		if (this.field_13009 == class_2853.class_2854.field_13011) {
			packetByteBuf.writeIdentifier(this.field_13004);
		} else if (this.field_13009 == class_2853.class_2854.field_13010) {
			packetByteBuf.writeBoolean(this.guiOpen);
			packetByteBuf.writeBoolean(this.filteringCraftable);
			packetByteBuf.writeBoolean(this.furnaceGuiOpen);
			packetByteBuf.writeBoolean(this.furnaceFilteringCraftable);
		}
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12047(this);
	}

	public class_2853.class_2854 method_12402() {
		return this.field_13009;
	}

	public Identifier method_12406() {
		return this.field_13004;
	}

	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	public static enum class_2854 {
		field_13011,
		field_13010;
	}
}
