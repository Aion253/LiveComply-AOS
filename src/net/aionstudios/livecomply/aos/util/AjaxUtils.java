package net.aionstudios.livecomply.aos.util;

import java.util.Map;

import net.aionstudios.api.util.DatabaseUtils;

public class AjaxUtils {
	
	private static String validTokenQuery = "SELECT * FROM `livecomply_ajax`.`ajax_tokens` WHERE `token` = ? AND `sessionID` = ?;";
	
	public static boolean isValidAjaxToken(Map<String, String> postQuery) {
		return !DatabaseUtils.prepareAndExecute(validTokenQuery, true, postQuery.get("ajax_token"), postQuery.get("sessionID")).get(0).getResults().isEmpty();
	}

}
