package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.util.Identifier;

public class ChangeUnlockedRecipesS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChangeUnlockedRecipesS2CPacket> CODEC = Packet.createCodec(
		ChangeUnlockedRecipesS2CPacket::write, ChangeUnlockedRecipesS2CPacket::new
	);
	private final ChangeUnlockedRecipesS2CPacket.Action action;
	private final List<Identifier> recipeIdsToChange;
	private final List<Identifier> recipeIdsToInit;
	private final RecipeBookOptions options;

	public ChangeUnlockedRecipesS2CPacket(
		ChangeUnlockedRecipesS2CPacket.Action action, Collection<Identifier> recipeIdsToChange, Collection<Identifier> recipeIdsToInit, RecipeBookOptions options
	) {
		this.action = action;
		this.recipeIdsToChange = ImmutableList.copyOf(recipeIdsToChange);
		this.recipeIdsToInit = ImmutableList.copyOf(recipeIdsToInit);
		this.options = options;
	}

	private ChangeUnlockedRecipesS2CPacket(PacketByteBuf buf) {
		this.action = buf.readEnumConstant(ChangeUnlockedRecipesS2CPacket.Action.class);
		this.options = RecipeBookOptions.fromPacket(buf);
		this.recipeIdsToChange = buf.readList(PacketByteBuf::readIdentifier);
		if (this.action == ChangeUnlockedRecipesS2CPacket.Action.INIT) {
			this.recipeIdsToInit = buf.readList(PacketByteBuf::readIdentifier);
		} else {
			this.recipeIdsToInit = ImmutableList.of();
		}
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		this.options.toPacket(buf);
		buf.writeCollection(this.recipeIdsToChange, PacketByteBuf::writeIdentifier);
		if (this.action == ChangeUnlockedRecipesS2CPacket.Action.INIT) {
			buf.writeCollection(this.recipeIdsToInit, PacketByteBuf::writeIdentifier);
		}
	}

	@Override
	public PacketType<ChangeUnlockedRecipesS2CPacket> getPacketId() {
		return PlayPackets.RECIPE;
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

	public ChangeUnlockedRecipesS2CPacket.Action getAction() {
		return this.action;
	}

	public static enum Action {
		INIT,
		ADD,
		REMOVE;
	}
}
