package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.util.Identifier;

public class UnlockRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	private final UnlockRecipesS2CPacket.Action action;
	private final List<Identifier> recipeIdsToChange;
	private final List<Identifier> recipeIdsToInit;
	private final RecipeBookOptions options;

	public UnlockRecipesS2CPacket(
		UnlockRecipesS2CPacket.Action action, Collection<Identifier> recipeIdsToChange, Collection<Identifier> recipeIdsToInit, RecipeBookOptions options
	) {
		this.action = action;
		this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
		this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
		this.options = options;
	}

	public UnlockRecipesS2CPacket(PacketByteBuf buf) {
		this.action = buf.readEnumConstant(UnlockRecipesS2CPacket.Action.class);
		this.options = RecipeBookOptions.fromPacket(buf);
		this.recipeIdsToChange = buf.readList(PacketByteBuf::readIdentifier);
		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			this.recipeIdsToInit = buf.readList(PacketByteBuf::readIdentifier);
		} else {
			this.recipeIdsToInit = ImmutableList.of();
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		this.options.toPacket(buf);
		buf.writeCollection(this.recipeIdsToChange, PacketByteBuf::writeIdentifier);
		if (this.action == UnlockRecipesS2CPacket.Action.INIT) {
			buf.writeCollection(this.recipeIdsToInit, PacketByteBuf::writeIdentifier);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onUnlockRecipes(this);
	}

	public List<Identifier> getRecipeIdsToChange() {
		return this.recipeIdsToChange;
	}

	public List<Identifier> getRecipeIdsToInit() {
		return this.recipeIdsToInit;
	}

	public RecipeBookOptions getOptions() {
		return this.options;
	}

	public UnlockRecipesS2CPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		INIT,
		ADD,
		REMOVE;
	}
}
