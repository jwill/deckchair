package jwill.deckchair

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log
import org.codehaus.groovy.runtime.NullObject
import org.json.JSONArray

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/12/13
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
@Log
class FlatFileAdaptor {
    def db = []
    def file
    def filename
    def slurper
    def utils = new AdaptorUtils()

    public FlatFileAdaptor(props) {
        if (props.name != null) {
            filename = "${props.name}.json"
        } else filename = "db.json"

        if (props.homeDir != null) {
            new File(props.homeDir).mkdirs()
            file = new File(props.homeDir + File.separator + filename)
        } else {
            file = new File(filename)
        }
        if (!file.exists()) {
            file.createNewFile()
            file.write("[]")
        }
        slurper = new JsonSlurper()
        db = slurper.parseText(file.getText())
    }

    def saveToFile(){
        file.write(new JsonBuilder(db).toString())
    }

    def save(obj, closure = null) {
        def object = insert(obj)
        saveToFile()
        if (closure) {
            closure(object)
        } else object
    }

    private insert(obj) {
        def id = (obj.key == null) ? UUID.randomUUID().toString() : obj.key
        remove(obj.key)
        db.add([id: id, value:obj, timestamp:utils.now()])
        obj.key = id
        obj
    }

    def each(Closure closure = null) {
        def records = this.all(null)
        if (closure) {
            for (def i = 0; i < records.size(); i++) {
                def r = records.get(i)
                closure(r, i)
            }
        }
    }

    def keys(closure = null) {
        def results = this.all({ array ->
            def keyList = []
            for (int i = 0; i < array.size(); i++) {
                def r = array.get(i)
                log.info(r.toString())
                keyList.add r.("id")
            }
            if (closure)
                closure(keyList)
            else return keyList
        })
    }

    def nuke() {
        db = []
        saveToFile()
        this
    }

    def all(closure = null) {
        def data = db
        def results = []
        data.each {
            def raw = it.value
            raw.id =  it.id
            results.add(raw)
        }

        if (closure)
            closure(results)
        else results
    }

    def get(keyOrArray, closure = null) {
        def result
        if (keyOrArray instanceof String)
            result = db.find {it.id == keyOrArray}
        else if (keyOrArray instanceof ArrayList) {
            result = []
            for (key in keyOrArray) {
                result.add(db.find{it.id == key})
            }
        }
        if (result instanceof HashMap) {
            def obj = utils.deserialize(result.value)
            obj.key = result.id
            if (closure)
                closure(obj)
            else obj
        } else if (result instanceof ArrayList) {
            def objs = []
            for (row in result) {
                def o = utils.deserialize(row.value)
                o.key = row.id
                objs.add(o)
            }
            if (closure)
                for (o in objs)
                    closure(o)
            else return objs
        }
    }

    def remove(keyObjOrArray) {
        def type = keyObjOrArray.getClass()
        switch (type) {
            case NullObject:
                //noop
                break
            case String:
                def obj = db.find{it.id == keyObjOrArray}
                db -= obj
                break
            case ArrayList:
                for (obj in keyObjOrArray) {
                    db -= db.find{it.id == obj.key}
                }
                break
            case Object:
                db -= keyObjOrArray
                break

        }
    }
}
