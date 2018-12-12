package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity extends BlockEntity implements CommandOutput {
	public final TextComponent[] text = new TextComponent[]{
		new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")
	};
	private boolean caretVisible = false;
	private int currentRow = -1;
	private int selectionStart = -1;
	private int selectionEnd = -1;
	private boolean editable = true;
	private PlayerEntity editor;
	private final String[] textBeingEdited = new String[4];
	private DyeColor textColor = DyeColor.BLACK;

	public SignBlockEntity() {
		super(BlockEntityType.SIGN);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);

		for (int i = 0; i < 4; i++) {
			String string = TextComponent.Serializer.toJsonString(this.text[i]);
			compoundTag.putString("Text" + (i + 1), string);
		}

		compoundTag.putString("Color", this.textColor.getName());
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.editable = false;
		super.fromTag(compoundTag);
		this.textColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; i++) {
			String string = compoundTag.getString("Text" + (i + 1));
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(string);
			if (this.world instanceof ServerWorld) {
				try {
					this.text[i] = TextFormatter.method_10881(this.method_11304(null), textComponent, null);
				} catch (CommandSyntaxException var6) {
					this.text[i] = textComponent;
				}
			} else {
				this.text[i] = textComponent;
			}

			this.textBeingEdited[i] = null;
		}
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getTextOnRow(int i) {
		return this.text[i];
	}

	public void setTextOnRow(int i, TextComponent textComponent) {
		this.text[i] = textComponent;
		this.textBeingEdited[i] = null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String method_11300(int i, Function<TextComponent, String> function) {
		if (this.textBeingEdited[i] == null && this.text[i] != null) {
			this.textBeingEdited[i] = (String)function.apply(this.text[i]);
		}

		return this.textBeingEdited[i];
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 9, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	@Override
	public boolean method_11011() {
		return true;
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Environment(EnvType.CLIENT)
	public void setEditable(boolean bl) {
		this.editable = bl;
		if (!bl) {
			this.editor = null;
		}
	}

	public void setEditor(PlayerEntity playerEntity) {
		this.editor = playerEntity;
	}

	public PlayerEntity getEditor() {
		return this.editor;
	}

	public boolean onActivate(PlayerEntity playerEntity) {
		for (TextComponent textComponent : this.text) {
			Style style = textComponent == null ? null : textComponent.getStyle();
			if (style != null && style.getClickEvent() != null) {
				ClickEvent clickEvent = style.getClickEvent();
				if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
					playerEntity.getServer().getCommandManager().execute(this.method_11304((ServerPlayerEntity)playerEntity), clickEvent.getValue());
				}
			}
		}

		return true;
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
	}

	public ServerCommandSource method_11304(@Nullable ServerPlayerEntity serverPlayerEntity) {
		String string = serverPlayerEntity == null ? "Sign" : serverPlayerEntity.getName().getString();
		TextComponent textComponent = (TextComponent)(serverPlayerEntity == null ? new StringTextComponent("Sign") : serverPlayerEntity.getDisplayName());
		return new ServerCommandSource(
			this,
			new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5),
			Vec2f.ZERO,
			(ServerWorld)this.world,
			2,
			string,
			textComponent,
			this.world.getServer(),
			serverPlayerEntity
		);
	}

	@Override
	public boolean sendCommandFeedback() {
		return false;
	}

	@Override
	public boolean shouldTrackOutput() {
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return false;
	}

	public DyeColor getTextColor() {
		return this.textColor;
	}

	public boolean setTextColor(DyeColor dyeColor) {
		if (dyeColor != this.getTextColor()) {
			this.textColor = dyeColor;
			this.markDirty();
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			return true;
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public void setSelectionState(int i, int j, int k, boolean bl) {
		this.currentRow = i;
		this.selectionStart = j;
		this.selectionEnd = k;
		this.caretVisible = bl;
	}

	@Environment(EnvType.CLIENT)
	public void resetSelectionState() {
		this.currentRow = -1;
		this.selectionStart = -1;
		this.selectionEnd = -1;
		this.caretVisible = false;
	}

	@Environment(EnvType.CLIENT)
	public boolean isCaretVisible() {
		return this.caretVisible;
	}

	@Environment(EnvType.CLIENT)
	public int getCurrentRow() {
		return this.currentRow;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionStart() {
		return this.selectionStart;
	}

	@Environment(EnvType.CLIENT)
	public int getSelectionEnd() {
		return this.selectionEnd;
	}
}
