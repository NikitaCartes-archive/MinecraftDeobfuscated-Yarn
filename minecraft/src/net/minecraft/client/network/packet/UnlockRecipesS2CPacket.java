package net.minecraft.client.network.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class UnlockRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private UnlockRecipesS2CPacket.Action action;
	private List<Identifier> recipeIdsToChange;
	private List<Identifier> recipeIdsToInit;
	private boolean guiOpen;
	private boolean filteringCraftable;
	private boolean furnaceGuiOpen;
	private boolean furnaceFilteringCraftable;

	public UnlockRecipesS2CPacket() {
	}

	public UnlockRecipesS2CPacket(
		UnlockRecipesS2CPacket.Action action,
		Collection<Identifier> collection,
		Collection<Identifier> collection2,
		boolean bl,
		boolean bl2,
		boolean bl3,
		boolean bl4
	) {
		this.action = action;
		this.recipeIdsToChange = ImmutableList.copyOf(collection);
		this.recipeIdsToInit = ImmutableList.copyOf(collection2);
		this.guiOpen = bl;
		this.filteringCraftable = bl2;
		this.furnaceGuiOpen = bl3;
		this.furnaceFilteringCraftable = bl4;
	}

	public void method_11753(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnlockRecipes(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.action = packetByteBuf.readEnumConstant(UnlockRecipesS2CPacket.Action.class);
		this.guiOpen = packetByteBuf.readBoolean();
		this.filteringCraftable = packetByteBuf.readBoolean();
		this.furnaceGuiOpen = packetByteBuf.readBoolean();
		this.furnaceFilteringCraftable = packetByteBuf.readBoolean();
		int i = packetByteBuf.readVarInt();
		this.recipeIdsToChange = Lists.<Identifier>newArrayList();

		for (int j = 0; j < i; j++) {
			this.recipeIdsToChange.add(packetByteBuf.readIdentifier());
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			i = packetByteBuf.readVarInt();
			this.recipeIdsToInit = Lists.<Identifier>newArrayList();

			for (int j = 0; j < i; j++) {
				this.recipeIdsToInit.add(packetByteBuf.readIdentifier());
			}
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.action);
		packetByteBuf.writeBoolean(this.guiOpen);
		packetByteBuf.writeBoolean(this.filteringCraftable);
		packetByteBuf.writeBoolean(this.furnaceGuiOpen);
		packetByteBuf.writeBoolean(this.furnaceFilteringCraftable);
		packetByteBuf.writeVarInt(this.recipeIdsToChange.size());

		for (Identifier identifier : this.recipeIdsToChange) {
			packetByteBuf.writeIdentifier(identifier);
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			packetByteBuf.writeVarInt(this.recipeIdsToInit.size());

			for (Identifier identifier : this.recipeIdsToInit) {
				packetByteBuf.writeIdentifier(identifier);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<Identifier> getRecipeIdsToChange() {
		return this.recipeIdsToChange;
	}

	@Environment(EnvType.CLIENT)
	public List<Identifier> getRecipeIdsToInit() {
		return this.recipeIdsToInit;
	}

	@Environment(EnvType.CLIENT)
	public boolean isGuiOpen() {
		return this.guiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFilteringCraftable() {
		return this.filteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceGuiOpen() {
		return this.furnaceGuiOpen;
	}

	@Environment(EnvType.CLIENT)
	public boolean isFurnaceFilteringCraftable() {
		return this.furnaceFilteringCraftable;
	}

	@Environment(EnvType.CLIENT)
	public UnlockRecipesS2CPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		INIT,
		ADD,
		REMOVE;
	}
}
