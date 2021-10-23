package name.soy.notex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class NoteMap {
	public static ArrayList<String> ONE_NOTENAMES;
	public static ArrayList<String> ALL_NOTENAMES;
	public static HashMap<String, Integer> NOTENAME_MAP;

	static {
		ONE_NOTENAMES = new ArrayList<>();
		ONE_NOTENAMES.add("C%/B♯$");
		ONE_NOTENAMES.add("C♯%/D♭%");
		ONE_NOTENAMES.add("D%");
		ONE_NOTENAMES.add("D♯%/E♭%");
		ONE_NOTENAMES.add("E%/F♭%");
		ONE_NOTENAMES.add("F%/E♯%");
		ONE_NOTENAMES.add("F♯%/G♭%");
		ONE_NOTENAMES.add("G%");
		ONE_NOTENAMES.add("G♯%/A♭%");
		ONE_NOTENAMES.add("A%");
		ONE_NOTENAMES.add("A♯%/B♭%");
		ONE_NOTENAMES.add("B%/C♭^");
		ALL_NOTENAMES = new ArrayList<>();
		NOTENAME_MAP = new LinkedHashMap<>();
		int pitch = 0;
		for (int i = -1; i < 16; i++) {
			for (String oneNotename : ONE_NOTENAMES) {
				String noteName = oneNotename
						.replace("%", String.valueOf(i))
						.replace("^", String.valueOf(i + 1))
						.replace("$", String.valueOf(i - 1));
				ALL_NOTENAMES.add(noteName);
				for (String s : noteName.split("/")) {
					NOTENAME_MAP.put(s, pitch);
				}
				pitch++;
			}
		}
	}
}
