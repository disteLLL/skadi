/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
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

package eu.over9000.skadi.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Future;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.over9000.skadi.model.Channel;
import eu.over9000.skadi.util.helper.AsyncImageRequest;

public class ImageUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

	private static final String BASE_URL_GAME_BOX = "http://static-cdn.jtvnw.net/ttv-boxart/%s-52x72.jpg";
	private static final String BASE_URL_PREVIEW = "http://static-cdn.jtvnw.net/previews-ttv/live_user_%s-320x180.jpg";

	public static ImageView getGameLogoFromTwitch(final String game) {

		try {
			final String url = String.format(BASE_URL_GAME_BOX, URLEncoder.encode(game, "UTF-8"));

			return new ImageView(url);
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error("exception getting game logo for " + game, e);
			return null;
		}
	}

	public static Image getPreviewFromTwitch(final Channel channel) {
		try {
			final String url = String.format(BASE_URL_PREVIEW, URLEncoder.encode(channel.getName().toLowerCase(), "UTF-8"));
			return new Image(url);
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error("exception getting channel preview for " + channel, e);
			return null;
		}

	}

	public static ImageView getChannelLogo(final String logoURL) {
		final ImageView iv = new ImageView(logoURL);
		iv.setFitHeight(72);
		iv.setFitWidth(72);
		iv.setSmooth(true);
		iv.setCache(true);
		return iv;
	}

	public static Future<Image> getPreviewAsyncFromTwitch(final Channel channel) {
		try {
			final String url = String.format(BASE_URL_PREVIEW, URLEncoder.encode(channel.getName().toLowerCase(), "UTF-8"));
			return ExecutorServiceAccess.getExecutorService().submit(new AsyncImageRequest(url));
		} catch (final UnsupportedEncodingException e) {
			LOGGER.error("exception getting channel preview for " + channel, e);
			return null;
		}
	}
}
