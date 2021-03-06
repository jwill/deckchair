package jwill.deckchair

import groovy.util.logging.Log
import org.codehaus.groovy.runtime.NullObject
import org.json.*
import java.sql.*
import groovy.sql.*

@Log
class DerbyAdaptor {
    Sql sql
    def tableName
    def classname = 'org.apache.derby.jdbc.EmbeddedDriver'
    def connection = "jdbc:derby:deckchair;create=true"
    def utils = new AdaptorUtils()

    public DerbyAdaptor(props) {
        if (props.homeDir != null)
            connection = "jdbc:derby:" + props.homeDir + ";create=true"
        // Create deckchair instance if it doesn't exist.
        sql = Sql.newInstance([url: connection, driver: classname]);
        this.tableName = props['name']

        try {
            def createStmt = "create table " + tableName + " (id VARCHAR(36) not null PRIMARY KEY, value LONG VARCHAR, timestamp bigint)"
            sql.execute(createStmt)
        } catch (SQLException ex) {
            // Table already exists
        }
    }

    def save(obj, closure = null) {
        def object = insert(obj)
        if (closure) {
            closure(object)
        } else object
    }

    private insert(obj) {
        def id = (obj.key == null) ? UUID.randomUUID().toString() : obj.key
        remove(obj.key)
        def data = sql.dataSet(tableName)
        data.add(id: id, value: utils.serialize(obj), timestamp: utils.now())
        obj.key = id
        obj
    }

    def all(closure = null) {
        def data = sql.dataSet(tableName)
        def results = new JSONArray()
        data.each {
            def raw = it.value
            def obj = utils.deserialize(raw)
            obj.put('id', it.id)
            results.put(obj)
        }

        if (closure)
            closure(results)
        else results
    }

    def each(Closure closure = null) {
        def records = this.all(null)
        if (closure) {
            for (def i = 0; i < records.length(); i++) {
                def r = records.get(i)
                closure(r, i)
            }
        }
    }

    def keys(closure = null) {
        def results = this.all({ JSONArray array ->
            def keyList = []
            for (int i = 0; i < array.length(); i++) {
                def r = array.get(i)
                log.info(r.toString())
                keyList.add r.getString("id")
            }
            if (closure)
                closure(keyList)
            else return keyList
        })
    }

    def get(keyOrArray, closure = null) {
        def result
        if (keyOrArray instanceof String)
            result = sql.firstRow("SELECT * FROM " + tableName + " WHERE id=\'" + keyOrArray + "\'")
        else if (keyOrArray instanceof ArrayList) {
            result = []
            for (key in keyOrArray) {
                result.add(sql.firstRow("SELECT * FROM " + tableName + " WHERE id=\'" + key + "\'"))
            }
        }
        if (result instanceof GroovyRowResult) {
            def obj = utils.deserialize(result.value)
            obj.key = result.id
            def r = new JSONObject(obj)
            if (closure)
                closure(r)
            else r
        } else if (result instanceof ArrayList) {
            def objs = []
            for (row in result) {
                def o = utils.deserialize(row.value)
                o.key = row.id
                def r = new JSONObject(o)
                objs.add(r)
            }
            if (closure)
                for (o in objs)
                    closure(o)
            else return objs
        }
    }

    def remove(keyObjOrArray) {
        def deleteString = "DELETE FROM "+tableName + " WHERE id = ?"
        def type = keyObjOrArray.getClass()
        switch (type) {
            case NullObject:
                //noop
                break
            case String:
                sql.execute(deleteString, [keyObjOrArray])
                break
            case ArrayList:
                for (obj in keyObjOrArray) {
                    sql.execute(deleteString, obj.key)
                }
                break
            case Object:
                sql.execute(deleteString, [keyObjOrArray.id])
                break

        }
    }

    def nuke() {
        sql.execute("DELETE FROM " + tableName)
        this
    }

}
