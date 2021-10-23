package name.soy.notex;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.chars.CharSets;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.io.Charsets;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;

public class Lang {
	public static final HashMap<String, JsonObject> LANG_LIST = new HashMap<>();

	public static String get(String lang, String name) {
		if (LANG_LIST.containsKey(lang)) {
			return LANG_LIST.get(lang).get(name).getAsString();
		}
		try {
			loadLang(lang);
		} catch (Exception e) {
			get("ZH_CN", name);
		}
		return get(lang, name);
	}

	public static String get(ServerPlayerEntity player, String name) {
		String lang = NXVisiualize.PLAYER_LANG.get(player.getUuid());
		if (lang == null) lang = "ZH_CN";
		return get(lang.toUpperCase(Locale.ROOT), name);
	}

	private static void loadLang(String lang) {
		LANG_LIST.put(lang, (JsonObject) new JsonParser().parse(new InputStreamReader(Lang.class.getResourceAsStream("/assests/lang/" + lang + ".json"), StandardCharsets.UTF_8)));
	}
}
