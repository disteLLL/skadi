/*
 * Copyright (c) 2014-2016 Jan Strauß <jan[at]over9000.eu>
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

import eu.over9000.cathode.Twitch;

import java.net.URI;

public class TwitchUtil {

	private static final String SKADI_CLIENT_ID = "i2uu9j43ure9x7n4ojpgg4hvcnw6y91";
	private static final String AUTH_REDIRECT = "https://janstrauss.github.io/skadi/auth/";
	private static final String AUTH_SCOPE = "user_follows_edit";

	private static Twitch twitch;

	public static void init() {
		init(null);
	}

	public static void init(final String authToken) {
		twitch = new Twitch(SKADI_CLIENT_ID, authToken);
	}

	public static Twitch getTwitch() {
		return twitch;
	}

	public static URI buildAuthUrl() {
		return Twitch.buildTokenAuthURI(SKADI_CLIENT_ID, AUTH_REDIRECT, AUTH_SCOPE);
	}
}
