package jwill.deckchair

import com.google.gson.Gson
import org.json.JSONObject

/**
 * Created by IntelliJ IDEA.
 * User: jwill
 * Date: Jan 7, 2010
 * Time: 8:23:27 AM
 * To change this template use File | Settings | File Templates.
 */
class AdaptorUtils {

    def now = {
        new Date().getTime()
    }

    def uuid = { len = 16, radix = 0 ->
        // based on Robert Kieffer's randomUUID.js at http://www.broofa.com
        def chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
        def uuid = [ ]
        radix = (radix > chars.length) ? radix : chars.length
       // radix = radix || chars.length

        if (len) {
            for (int i = 0; i < len; i++) {
                def j = (int)(Math.random() * radix)
                uuid[i] = chars[j];
            }
        } else {
            // rfc4122, version 4 form
            def r

            // rfc4122 requires these characters
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-'
            uuid[14] = '4'

            // Fill in random data.  At i==19 set the high bits of clock sequence as
            // per rfc4122, sec. 4.1.5
            for (int i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random() * 16
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8: r]
                }
            }
        }
        uuid.join('')
    }

    def serialize = {obj ->
        new Gson().toJson(obj)
    }

    def deserialize = {json ->
       (new JSONObject(json)).map
    }
}
