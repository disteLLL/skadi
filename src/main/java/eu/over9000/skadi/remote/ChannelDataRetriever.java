/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.remote;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.remote.data.ChannelMetadata;
import eu.over9000.skadi.remote.data.ChannelMetadataBuilder;
import eu.over9000.skadi.util.HttpUtil;
import eu.over9000.skadi.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class provides static methods to retrieve channel metadata from the twitch API.
 *
 * @author Jan Strauß
 */
public class ChannelDataRetriever {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelDataRetriever.class);
	
	private static final JsonParser JSON_PARSER = new JsonParser();
	
	private static long getChannelUptime(final JsonObject channelObject) throws ParseException {
		
		final String start = channelObject.get("created_at").getAsString();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		final Date start_date = sdf.parse(start);
		final Date now_date = new Date();
		
		return now_date.getTime() - start_date.getTime();
		
	}
	
	public static ChannelMetadata getChannelMetadata(final Channel channel) {
		
		try {
			ImageUtil.getPreviewAsyncFromTwitch(channel);

			final JsonObject streamResponse = getStreamData(channel.getName());
			final ChannelMetadataBuilder builder = new ChannelMetadataBuilder();
			
			final JsonObject streamObject;
			final JsonObject channelObject;
			
			final boolean isOnline = !streamResponse.get("stream").isJsonNull();
			builder.setOnline(isOnline);
			
			if (isOnline) {
				
				streamObject = streamResponse.getAsJsonObject("stream");
				channelObject = streamObject.getAsJsonObject("channel");
				
				builder.setUptime(getChannelUptime(streamObject));
				builder.setViewer(streamObject.get("viewers").getAsInt());
				
			} else {
				channelObject = getChannelDataForOfflineStream(channel.getName());
				
				builder.setUptime(0L);
				builder.setViewer(0);
			}
			
			builder.setTitle(getStringIfPresent("status", channelObject));
			builder.setGame(getStringIfPresent("game", channelObject));
			builder.setLogoURL(getStringIfPresent("logo", channelObject));
			builder.setViews(getIntIfPresent("views", channelObject));
			builder.setFollowers(getIntIfPresent("followers", channelObject));
			builder.setPartner(getBoolIfPresent("partner", channelObject));

			return builder.build();
		} catch (final Exception e) {
			LOGGER.error("Exception getting metadata for channel " + channel + ": " + e.getMessage());
			return null;
		}
	}
	
	private static Boolean getBoolIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsBoolean();
		}
		return null;
	}
	
	private static String getStringIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsString();
		}
		return null;
	}
	
	private static Integer getIntIfPresent(final String name, final JsonObject jsonObject) {
		if (jsonObject.has(name) && !jsonObject.get(name).isJsonNull()) {
			return jsonObject.get(name).getAsInt();
		}
		return null;
	}
	
	private static JsonObject getChannelDataForOfflineStream(final String channel) throws URISyntaxException, IOException {
		final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
		return JSON_PARSER.parse(response).getAsJsonObject();
	}
	
	private static JsonObject getStreamData(final String channel) throws URISyntaxException, IOException {
		final String response = HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/streams/" + channel);
		return JSON_PARSER.parse(response).getAsJsonObject();
	}
	
	public static boolean checkIfChannelExists(final String channel) {
		try {
			HttpUtil.getAPIResponse("https://api.twitch.tv/kraken/channels/" + channel);
			return true;
		} catch (URISyntaxException | IOException e) {
			return false;
		}
	}
	
}
