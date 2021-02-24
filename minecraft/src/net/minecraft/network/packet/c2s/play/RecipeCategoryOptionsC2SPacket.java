package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.book.RecipeBookCategory;

public class RecipeCategoryOptionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private final RecipeBookCategory category;
	private final boolean guiOpen;
	private final boolean filteringCraftable;

	@Environment(EnvType.CLIENT)
	public RecipeCategoryOptionsC2SPacket(RecipeBookCategory category, boolean guiOpen, boolean filteringCraftable) {
		this.category = category;
		this.guiOpen = guiOpen;
		this.filteringCraftable = filteringCraftable;
	}

	public RecipeCategoryOptionsC2SPacket(PacketByteBuf packetByteBuf) {
		this.category = packetByteBuf.readEnumConstant(RecipeBookCategory.class);
		this.guiOpen = packetByteBuf.readBoolean();
		this.filteringCraftable = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.category);
		buf.writeBoolean(this.guiOpen);
		buf.writeBoolean(this.filteringCraftable);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onRecipeCategoryOptions(this);
	}

	public RecipeBookCategory getCategory() {
		return this.category;
	}

	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}
}
