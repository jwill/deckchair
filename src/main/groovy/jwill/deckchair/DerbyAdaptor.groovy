package jwill.deckchair

import groovy.util.logging.Log
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
          connection = "jdbc:derby:"+props.homeDir +";create=true"
        // Create deckchair instance if it doesn't exist.
        sql = Sql.newInstance([url:connection, driver:classname]);
        this.tableName = props['name']
        
        try {
            def createStmt = "create table "+tableName+" (id VARCHAR(36) not null PRIMARY KEY, value LONG VARCHAR, timestamp bigint)"
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
        data.add(id: id, value: utils.serialize(obj), timestamp:utils.now())
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
            for (def i = 0; i<records.length(); i++) {
                def r = records.get(i)
                closure(r)
            }
        }
    }

    def keys(closure = null) {
       def results = this.all({ JSONArray array ->
           def keyList = []
           for (int i=0; i<array.length(); i++) {
               def r = array.get(i)
               log.info(r.toString())
               keyList.add r.getString("id")
           }
           if(closure)
               closure(keyList)
           else return keyList
       })
    }
    
    def get(key, closure = null) {
        def result = sql.firstRow("SELECT * FROM "+tableName+" WHERE id=\'"+key+"\'")
        if (result) {
            def obj = utils.deserialize(result.value)
            obj.key = result.id
            def r = new JSONObject(obj)
            if (closure)
                closure(r)
            else r
        } else return null
    }

    def exists(key, closure = null) {
        this.get(key, {r ->
            closure(r!=null)
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
    
    def remove(keyOrObj) {
    	def key = (keyOrObj instanceof String ? keyOrObj : keyOrObj?.key)
			if (key) {
					sql.execute("DELETE FROM "+tableName+" WHERE id=\'"+key+"\'")
			}
	}
    
    def nuke() {
        sql.execute("DELETE FROM "+tableName)
        this
    }
    
}
