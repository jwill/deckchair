package jwill.deckchair

import org.json.JSONObject
import java.util.UUID
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

    def serialize = {obj ->
        new JSONObject(obj).toString()
    }

    def deserialize = {json ->
        (new JSONObject(json)).map
    }
}
