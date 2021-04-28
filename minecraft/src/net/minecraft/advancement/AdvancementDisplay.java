package net.minecraft.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
	private final Text title;
	private final Text description;
	private final ItemStack icon;
	private final Identifier background;
	private final AdvancementFrame frame;
	private final boolean showToast;
	private final boolean announceToChat;
	private final boolean hidden;
	private float x;
	private float y;

	public AdvancementDisplay(
		ItemStack icon,
		Text title,
		Text description,
		@Nullable Identifier background,
		AdvancementFrame frame,
		boolean showToast,
		boolean announceToChat,
		boolean hidden
	) {
		this.title = title;
		this.description = description;
		this.icon = icon;
		this.background = background;
		this.frame = frame;
		this.showToast = showToast;
		this.announceToChat = announceToChat;
		this.hidden = hidden;
	}

	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Text getTitle() {
		return this.title;
	}

	public Text getDescription() {
		return this.description;
	}

	public ItemStack getIcon() {
		return this.icon;
	}

	@Nullable
	public Identifier getBackground() {
		return this.background;
	}

	public AdvancementFrame getFrame() {
		return this.frame;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public boolean shouldShowToast() {
		return this.showToast;
	}

	public boolean shouldAnnounceToChat() {
		return this.announceToChat;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public static AdvancementDisplay fromJson(JsonObject obj) {
		Text text = Text.Serializer.fromJson(obj.get("title"));
		Text text2 = Text.Serializer.fromJson(obj.get("description"));
		if (text != null && text2 != null) {
			ItemStack itemStack = iconFromJson(JsonHelper.getObject(obj, "icon"));
			Identifier identifier = obj.has("background") ? new Identifier(JsonHelper.getString(obj, "background")) : null;
			AdvancementFrame advancementFrame = obj.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(obj, "frame")) : AdvancementFrame.TASK;
			boolean bl = JsonHelper.getBoolean(obj, "show_toast", true);
			boolean bl2 = JsonHelper.getBoolean(obj, "announce_to_chat", true);
			boolean bl3 = JsonHelper.getBoolean(obj, "hidden", false);
			return new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, bl2, bl3);
		} else {
			throw new JsonSyntaxException("Both title and description must be set");
		}
	}

	private static ItemStack iconFromJson(JsonObject json) {
		if (!json.has("item")) {
			throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
		} else {
			Item item = JsonHelper.getItem(json, "item");
			if (json.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				ItemStack itemStack = new ItemStack(item);
				if (json.has("nbt")) {
					try {
						NbtCompound nbtCompound = StringNbtReader.parse(JsonHelper.asString(json.get("nbt"), "nbt"));
						itemStack.setTag(nbtCompound);
					} catch (CommandSyntaxException var4) {
						throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
					}
				}

				return itemStack;
			}
		}
	}

	public void toPacket(PacketByteBuf buf) {
		buf.writeText(this.title);
		buf.writeText(this.description);
		buf.writeItemStack(this.icon);
		buf.writeEnumConstant(this.frame);
		int i = 0;
		if (this.background != null) {
			i |= 1;
		}

		if (this.showToast) {
			i |= 2;
		}

		if (this.hidden) {
			i |= 4;
		}

		buf.writeInt(i);
		if (this.background != null) {
			buf.writeIdentifier(this.background);
		}

		buf.writeFloat(this.x);
		buf.writeFloat(this.y);
	}

	public static AdvancementDisplay fromPacket(PacketByteBuf buf) {
		Text text = buf.readText();
		Text text2 = buf.readText();
		ItemStack itemStack = buf.readItemStack();
		AdvancementFrame advancementFrame = buf.readEnumConstant(AdvancementFrame.class);
		int i = buf.readInt();
		Identifier identifier = (i & 1) != 0 ? buf.readIdentifier() : null;
		boolean bl = (i & 2) != 0;
		boolean bl2 = (i & 4) != 0;
		AdvancementDisplay advancementDisplay = new AdvancementDisplay(itemStack, text, text2, identifier, advancementFrame, bl, false, bl2);
		advancementDisplay.setPos(buf.readFloat(), buf.readFloat());
		return advancementDisplay;
	}

	public JsonElement toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("icon", this.iconToJson());
		jsonObject.add("title", Text.Serializer.toJsonTree(this.title));
		jsonObject.add("description", Text.Serializer.toJsonTree(this.description));
		jsonObject.addProperty("frame", this.frame.getId());
		jsonObject.addProperty("show_toast", this.showToast);
		jsonObject.addProperty("announce_to_chat", this.announceToChat);
		jsonObject.addProperty("hidden", this.hidden);
		if (this.background != null) {
			jsonObject.addProperty("background", this.background.toString());
		}

		return jsonObject;
	}

	private JsonObject iconToJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("item", Registry.ITEM.getId(this.icon.getItem()).toString());
		if (this.icon.hasTag()) {
			jsonObject.addProperty("nbt", this.icon.getTag().toString());
		}

		return jsonObject;
	}
}
