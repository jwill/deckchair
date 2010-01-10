package jwill.deckchair

import java.sql.*
import groovy.sql.*


class DerbyAdaptor {
    Sql sql
    def tableName
    def connection = "jdbc:derby:deckchair;create=true"
    def utils = new AdaptorUtils()
    
	public DerbyAdaptor(props) {
        // Create deckchair instance if it doesn't exist.
        sql = Sql.newInstance(connection);
        this.tableName = props['name']
        
        try {
            def createStmt = "create table "+tableName+" (id VARCHAR(32) not null PRIMARY KEY, value VARCHAR(100), timestamp bigint)"
            sql.execute(createStmt)
        } catch (SQLException ex) {
            // Table already exists
            ex.printStackTrace()
        }
	}

    def save(obj, closure = null) {
        if (obj?.key == null) {
            insert(obj, closure)
        } else {
            this.get(obj, { results ->
               if (results != null) {
                   def id = obj.key
                   remove(obj.key)
                   update(id, obj, closure)

               }
            })
        }
    }

    private update(id, obj, closure = null) {
        sql.executeUpdate("UPDATE ${tableName} SET value=${utils.serialize(obj)}, timestamp=${utils.now()} WHERE id=${id}")
        if (closure) {
            obj['key'] = id;
            closure(obj)
        }
    }

    private insert(obj, closure) {
        def id = (obj.key == null) ? utils.uuid() : obj.key
        remove(obj.key)
        def data = sql.dataSet(tableName)
        data.add(id: id, value: utils.serialize(obj), timestamp:utils.now())
        obj.key = id
        if (closure) {
           closure(obj)
        } else obj
    }

    def all(closure = null) {
        def data = sql.dataSet(tableName)
        def results = [ ]
        data.each {
            def raw = it.value
            def obj = utils.deserialize(raw)
            obj.put('id', it.id)
            results.add(obj)
        }

        if (closure)
            closure(results)
        else results
    }
    
    def get(key, closure = null) {
        def result = sql.firstRow("SELECT * FROM "+tableName+" WHERE id=\'"+key+"\'")
        if (result) {
            def obj = utils.deserialize(result.value)
            obj.key = result.id
            if (closure)
                closure(obj)
            else obj
        } else return null
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