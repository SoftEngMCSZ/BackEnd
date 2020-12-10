package me.whatdo.app.handlers.admin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.whatdo.app.db.ChoiceDAO;
import me.whatdo.app.model.ApiResponse;
import me.whatdo.app.model.request.AdminRequest;

import java.time.Instant;
import java.util.Date;

public class AdminDeleteHandler implements RequestHandler<AdminRequest, ApiResponse> {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public ApiResponse handleRequest(final AdminRequest input, final Context context) {
		JsonObject body = new JsonObject();
		ChoiceDAO dao = new ChoiceDAO();
		try {
			if (!validateRequest(input.toJsonObject())) {
				body.addProperty("Message", "400 malformed AdminRequest");
				body.addProperty("Input", input.toJson());
				return new ApiResponse(400, body.toString());
			}

			Date d = Date.from(Instant.now().minusSeconds(daysToSeconds(input.getDaysAgo())));
			if (dao.deleteChoicesOlderThan(d) == 0) {
				body.addProperty("Message", "400 unable to delete choices older than " + d.toString());
				return new ApiResponse(400, body.toString());
			}

			body.add("choices", gson.toJsonTree(dao.getAllChoices()));
			return new ApiResponse(200, body.toString());

		} catch (Exception e) {
			body.addProperty("Message", "500 server error");
			body.addProperty("Error", e.getMessage());
			return new ApiResponse(500, body.toString());
		}
	}

	private static boolean validateRequest(JsonObject req) {
		return req.has("daysAgo");
	}

	private static long daysToSeconds(double days) {
		return (long) Math.floor(days * 24 * 60 * 60);
	}
}
