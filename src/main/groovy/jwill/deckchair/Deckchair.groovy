package jwill.deckchair

import org.json.JSONArray

public class Deckchair {
    def adaptors = [
        'derby':DerbyAdaptor.class,
        'flatfile': FlatFileAdaptor.class
    ]
    def adaptor

    public Deckchair(Map props) {
        def a = props['adaptor'] ? props['adaptor'] : 'derby'

        adaptor = adaptors[a].newInstance(props)

        applyPlugins()
    }

    def save(obj, closure = null) {
        this.adaptor.save(obj, closure)
    }

    def batch(array, closure = null) {
        def r = []
        for (a in array) {
            r.add this.adaptor.insert(a)
        }
        if (closure) {
            closure(r)
        } else r
    }

    def exists(key, closure = null) {
        this.get(key, { r ->
            closure(r != null)
        })
    }

    def get(key, closure = null) {
        this.adaptor.get(key, closure)
    }

    def each(Closure closure = null) {
        this.adaptor.each(closure)
    }

    def keys(Closure closure = null) {
        this.adaptor.keys(closure)
    }
    
    def all(closure = null) {
        this.adaptor.all(closure)
    }
    
    def remove(keyOrObj) {
        this.adaptor.remove(keyOrObj)
    }

    def find(condition, closure = null) {
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
        this.adaptor.nuke()
        this
    }

    def applyPlugins() {
        AggregationPlugin.apply(this)
    }
}
