package net.minecraft;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class class_3184 extends JList<String> {
	private final MinecraftServer field_13844;
	private int field_13843;

	public class_3184(MinecraftServer minecraftServer) {
		this.field_13844 = minecraftServer;
		minecraftServer.method_3742(this::method_18700);
	}

	public void method_18700() {
		if (this.field_13843++ % 20 == 0) {
			Vector<String> vector = new Vector();

			for (int i = 0; i < this.field_13844.method_3760().method_14571().size(); i++) {
				vector.add(((class_3222)this.field_13844.method_3760().method_14571().get(i)).method_7334().getName());
			}

			this.setListData(vector);
		}
	}
}
