package name.soy.notex;

import net.minecraft.nbt.NbtCompound;

public class CustomIns {

	public enum InsMode {
		SEPARATE,
		STRETCH,
	}

	public String name;
	public int min;
	public int max;
	public String soundSpace;
	public InsMode sType;
	public int basepitch;

	public CustomIns(String name, int min, int max, String soundSpace, InsMode sType, int basepitch) {
		this.name = name;
		this.min = min;
		this.max = max;
		this.soundSpace = soundSpace;
		this.sType = sType;
		this.basepitch = basepitch;
	}

	@Override
	public String toString() {
		return "CustomIns{" +
				"name='" + name + '\'' +
				", min=" + min +
				", max=" + max +
				", soundSpace='" + soundSpace + '\'' +
				", sType=" + sType +
				", basepitch=" + basepitch +
				'}';
	}

	public static CustomIns read(NbtCompound nbt) {
		return new CustomIns(
				nbt.getString("name"),
				nbt.getInt("min"),
				nbt.getInt("max"),
				nbt.getString("soundSpace"),
				InsMode.values()[nbt.getInt("type")],
				nbt.getInt("basepitch")
		);
	}

	public NbtCompound write() {
		NbtCompound nbt = new NbtCompound();
		nbt.putString("name", name);
		nbt.putInt("min", min);
		nbt.putInt("max", max);
		nbt.putString("soundSpace", soundSpace);
		nbt.putInt("type", sType.ordinal());
		nbt.putInt("basepitch", basepitch);
		return nbt;
	}


	public static final CustomIns HARP = new CustomIns("harp", 0, 24, "minecraft:block.note_block.harp", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯3"));
	public static final CustomIns BASEDRUM = new CustomIns("basedrum", 0, 24, "minecraft:block.note_block.basedrum", InsMode.STRETCH, -1);
	public static final CustomIns SNARE = new CustomIns("snare", 0, 24, "minecraft:block.note_block.snare", InsMode.STRETCH, -1);
	public static final CustomIns HAT = new CustomIns("hat", 0, 24, "minecraft:block.note_block.hat", InsMode.STRETCH, -1);
	public static final CustomIns BASS = new CustomIns("bass", 0, 24, "minecraft:block.note_block.bass", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯1"));
	public static final CustomIns FLUTE = new CustomIns("flute", 0, 24, "minecraft:block.note_block.flute", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯4"));
	public static final CustomIns BELL = new CustomIns("bell", 0, 24, "minecraft:block.note_block.bell", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯5"));
	public static final CustomIns GUITAR = new CustomIns("guitar", 0, 24, "minecraft:block.note_block.guitar", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯2"));
	public static final CustomIns CHIME = new CustomIns("chime", 0, 24, "minecraft:block.note_block.chime", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯5"));
	public static final CustomIns XYLOPHONE = new CustomIns("xylophone", 0, 24, "minecraft:block.note_block.xylophone", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯5"));
	public static final CustomIns IRON_XYLOPHONE = new CustomIns("iron_xylophone", 0, 24, "minecraft:block.note_block.iron_xylophone", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯3"));
	public static final CustomIns COW_BELL = new CustomIns("cow_bell", 0, 24, "minecraft:block.note_block.cow_bell", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯4"));
	public static final CustomIns DIDGERIDOO = new CustomIns("digeridoo", 0, 24, "minecraft:block.note_block.digeridoo", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯1"));
	public static final CustomIns BIT = new CustomIns("bit", 0, 24, "minecraft:block.note_block.bit", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯3"));
	public static final CustomIns BANJO = new CustomIns("banjo", 0, 24, "minecraft:block.note_block.banjo", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯3"));
	public static final CustomIns PLING = new CustomIns("pling", 0, 24, "minecraft:block.note_block.pling", InsMode.STRETCH, NoteMap.NOTENAME_MAP.get("F♯3"));


}
