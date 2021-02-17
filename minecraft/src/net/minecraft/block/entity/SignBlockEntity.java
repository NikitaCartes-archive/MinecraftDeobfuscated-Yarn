package net.minecraft.block.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class SignBlockEntity extends BlockEntity {
	private static final String[] field_28905 = new String[]{"Text1", "Text2", "Text3", "Text4"};
	private static final String[] field_28906 = new String[]{"FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4"};
	private final Text[] text = new Text[]{LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY};
	private final Text[] field_28907 = new Text[]{LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY};
	private boolean editable = true;
	private PlayerEntity editor;
	@Nullable
	private OrderedText[] textBeingEdited;
	@Environment(EnvType.CLIENT)
	private boolean field_28908;
	private DyeColor textColor = DyeColor.BLACK;

	public SignBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SIGN, pos, state);
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);

		for (int i = 0; i < 4; i++) {
			Text text = this.text[i];
			String string = Text.Serializer.toJson(text);
			tag.putString(field_28905[i], string);
			Text text2 = this.field_28907[i];
			if (!text2.equals(text)) {
				tag.putString(field_28906[i], Text.Serializer.toJson(text2));
			}
		}

		tag.putString("Color", this.textColor.getName());
		return tag;
	}

	@Override
	public void readNbt(CompoundTag tag) {
		this.editable = false;
		super.readNbt(tag);
		this.textColor = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);

		for (int i = 0; i < 4; i++) {
			String string = tag.getString(field_28905[i]);
			Text text = this.method_33828(string);
			this.text[i] = text;
			String string2 = field_28906[i];
			if (tag.contains(string2, 8)) {
				this.field_28907[i] = this.method_33828(tag.getString(string2));
			} else {
				this.field_28907[i] = text;
			}
		}

		this.textBeingEdited = null;
	}

	private Text method_33828(String string) {
		Text text = this.method_33384(string);
		if (this.world instanceof ServerWorld) {
			try {
				return Texts.parse(this.getCommandSource(null), text, null, 0);
			} catch (CommandSyntaxException var4) {
			}
		}

		return text;
	}

	private Text method_33384(String string) {
		try {
			Text text = Text.Serializer.fromJson(string);
			if (text != null) {
				return text;
			}
		} catch (Exception var3) {
		}

		return LiteralText.EMPTY;
	}

	@Environment(EnvType.CLIENT)
	public Text getTextOnRow(int row, boolean bl) {
		return this.method_33830(bl)[row];
	}

	public void setTextOnRow(int row, Text text) {
		this.method_33827(row, text, text);
	}

	public void method_33827(int i, Text text, Text text2) {
		this.text[i] = text;
		this.field_28907[i] = text2;
		this.textBeingEdited = null;
	}

	@Environment(EnvType.CLIENT)
	public OrderedText[] method_33829(boolean bl, Function<Text, OrderedText> function) {
		if (this.textBeingEdited == null || this.field_28908 != bl) {
			this.field_28908 = bl;
			this.textBeingEdited = new OrderedText[4];

			for (int i = 0; i < 4; i++) {
				this.textBeingEdited[i] = (OrderedText)function.apply(this.getTextOnRow(i, bl));
			}
		}

		return this.textBeingEdited;
	}

	private Text[] method_33830(boolean bl) {
		return bl ? this.field_28907 : this.text;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataNbt());
	}

	@Override
	public CompoundTag toInitialChunkDataNbt() {
		return this.writeNbt(new CompoundTag());
	}

	@Override
	public boolean copyItemDataRequiresOperator() {
		return true;
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Environment(EnvType.CLIENT)
	public void setEditable(boolean editable) {
		this.editable = editable;
		if (!editable) {
			this.editor = null;
		}
	}

	public void setEditor(PlayerEntity player) {
		this.editor = player;
	}

	public PlayerEntity getEditor() {
		return this.editor;
	}

	public boolean onActivate(ServerPlayerEntity serverPlayerEntity) {
		for (Text text : this.method_33830(serverPlayerEntity.method_33793())) {
			Style style = text.getStyle();
			ClickEvent clickEvent = style.getClickEvent();
			if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
				serverPlayerEntity.getServer().getCommandManager().execute(this.getCommandSource(serverPlayerEntity), clickEvent.getValue());
			}
		}

		return true;
	}

	public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity player) {
		String string = player == null ? "Sign" : player.getName().getString();
		Text text = (Text)(player == null ? new LiteralText("Sign") : player.getDisplayName());
		return new ServerCommandSource(
			CommandOutput.DUMMY, Vec3d.ofCenter(this.pos), Vec2f.ZERO, (ServerWorld)this.world, 2, string, text, this.world.getServer(), player
		);
	}

	public DyeColor getTextColor() {
		return this.textColor;
	}

	public boolean setTextColor(DyeColor value) {
		if (value != this.getTextColor()) {
			this.textColor = value;
			this.markDirty();
			this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
			return true;
		} else {
			return false;
		}
	}
}
