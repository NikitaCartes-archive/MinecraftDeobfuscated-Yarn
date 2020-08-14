package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.book.RecipeBookCategory;

public class RecipeCategoryOptionsC2SPacket implements Packet<ServerPlayPacketListener> {
	private RecipeBookCategory category;
	private boolean guiOpen;
	private boolean filteringCraftable;

	public RecipeCategoryOptionsC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public RecipeCategoryOptionsC2SPacket(RecipeBookCategory category, boolean guiOpen, boolean filteringCraftable) {
		this.category = category;
		this.guiOpen = guiOpen;
		this.filteringCraftable = filteringCraftable;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.category = buf.readEnumConstant(RecipeBookCategory.class);
		this.guiOpen = buf.readBoolean();
		this.filteringCraftable = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
