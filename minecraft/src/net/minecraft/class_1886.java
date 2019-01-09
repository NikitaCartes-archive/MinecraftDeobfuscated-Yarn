package net.minecraft;

public enum class_1886 {
	field_9075 {
		@Override
		public boolean method_8177(class_1792 arg) {
			for (class_1886 lv : class_1886.values()) {
				if (lv != class_1886.field_9075 && lv.method_8177(arg)) {
					return true;
				}
			}

			return false;
		}
	},
	field_9068 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1738;
		}
	},
	field_9079 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1738 && ((class_1738)arg).method_7685() == class_1304.field_6166;
		}
	},
	field_9076 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1738 && ((class_1738)arg).method_7685() == class_1304.field_6172;
		}
	},
	field_9071 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1738 && ((class_1738)arg).method_7685() == class_1304.field_6174;
		}
	},
	field_9080 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1738 && ((class_1738)arg).method_7685() == class_1304.field_6169;
		}
	},
	field_9074 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1829;
		}
	},
	field_9069 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1766;
		}
	},
	field_9072 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1787;
		}
	},
	field_9073 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1835;
		}
	},
	field_9082 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg.method_7846();
		}
	},
	field_9070 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1753;
		}
	},
	field_9078 {
		@Override
		public boolean method_8177(class_1792 arg) {
			class_2248 lv = class_2248.method_9503(arg);
			return arg instanceof class_1738 || arg instanceof class_1770 || lv instanceof class_2190 || lv instanceof class_2445;
		}
	},
	field_9081 {
		@Override
		public boolean method_8177(class_1792 arg) {
			return arg instanceof class_1764;
		}
	};

	private class_1886() {
	}

	public abstract boolean method_8177(class_1792 arg);
}
