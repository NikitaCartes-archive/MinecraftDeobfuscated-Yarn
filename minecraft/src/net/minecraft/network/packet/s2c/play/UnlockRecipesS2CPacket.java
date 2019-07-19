package net.minecraft.network.packet.s2c.play;

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
		Collection<Identifier> recipeIdsToChange,
		Collection<Identifier> recipeIdsToInit,
		boolean guiOpen,
		boolean filteringCraftable,
		boolean furnaceGuiOpen,
		boolean furnaceFilteringCraftable
	) {
		this.action = action;
		this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
		this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
		this.guiOpen = guiOpen;
		this.filteringCraftable = filteringCraftable;
		this.furnaceGuiOpen = furnaceGuiOpen;
		this.furnaceFilteringCraftable = furnaceFilteringCraftable;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnlockRecipes(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.action = buf.readEnumConstant(UnlockRecipesS2CPacket.Action.class);
		this.guiOpen = buf.readBoolean();
		this.filteringCraftable = buf.readBoolean();
		this.furnaceGuiOpen = buf.readBoolean();
		this.furnaceFilteringCraftable = buf.readBoolean();
		int i = buf.readVarInt();
		this.recipeIdsToChange = Lists.<Identifier>newArrayList();

		for (int j = 0; j < i; j++) {
			this.recipeIdsToChange.add(buf.readIdentifier());
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			i = buf.readVarInt();
			this.recipeIdsToInit = Lists.<Identifier>newArrayList();

			for (int j = 0; j < i; j++) {
				this.recipeIdsToInit.add(buf.readIdentifier());
			}
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.action);
		buf.writeBoolean(this.guiOpen);
		buf.writeBoolean(this.filteringCraftable);
		buf.writeBoolean(this.furnaceGuiOpen);
		buf.writeBoolean(this.furnaceFilteringCraftable);
		buf.writeVarInt(this.recipeIdsToChange.size());

		for (Identifier identifier : this.recipeIdsToChange) {
			buf.writeIdentifier(identifier);
		}

		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			buf.writeVarInt(this.recipeIdsToInit.size());

			for (Identifier identifier : this.recipeIdsToInit) {
				buf.writeIdentifier(identifier);
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
