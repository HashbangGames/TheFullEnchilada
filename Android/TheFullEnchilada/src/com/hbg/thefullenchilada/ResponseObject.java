package com.hbg.thefullenchilada;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseObject extends JSONObject {
	ResponseObject(){
		try {
			this.put("Error", "");
			this.put("Success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
