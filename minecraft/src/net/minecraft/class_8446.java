package net.minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.vote.PendingVotesScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class class_8446 extends AlwaysSelectedEntryListWidget<class_8445> {
	public static final int field_44333 = 4;
	private static final Tooltip field_44334 = Tooltip.of(Text.translatable("vote.no_more_votes"));

	public class_8446(class_8471 arg, PendingVotesScreen pendingVotesScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
		super(minecraftClient, i, j, k, l, m);
		this.setRenderSelection(false);
		this.setRenderBackground(false);
		this.setRenderHorizontalShadows(false);
		UUID uUID = minecraftClient.player.getUuid();
		List<class_8445> list = new ArrayList();
		arg.method_51072((uUID2, arg2) -> {
			boolean bl = arg2.method_51083(uUID);
			list.add(new class_8445(minecraftClient, pendingVotesScreen, bl, this.getRowWidth(), uUID2, arg, arg2, bl ? null : field_44334));
		});
		list.sort(class_8445.field_44323);
		list.forEach(entry -> this.addEntry(entry));
	}

	public boolean method_50968() {
		Iterator<class_8445> iterator = this.children().iterator();

		while (iterator.hasNext()) {
			class_8445 lv = (class_8445)iterator.next();
			if (!lv.method_50961()) {
				iterator.remove();
			}
		}

		return !this.children().isEmpty();
	}

	@Override
	protected void enableScissor() {
		enableScissor(this.left, this.top + 4, this.right, this.bottom);
	}
}
