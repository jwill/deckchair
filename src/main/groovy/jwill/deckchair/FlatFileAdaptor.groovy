package jwill.deckchair

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log
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

    def batch(array, closure) {
        def r = []
        for (a in array) {
            r.add insert(a)
        }
        if (closure) {
            closure(r)
        } else r
    }

    def remove(keyObjOrArray) {

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

    def find(condition, closure) {
        def all = this.all()
        def found = new JSONArray()
        all.eachWithIndex { obj, i ->
            if (condition(obj))
                found.put(obj)
        }
        if (closure)
            closure(found)
        else found
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
}
