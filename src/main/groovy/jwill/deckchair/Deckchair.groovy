package jwill.deckchair

public class Deckchair {
    def adaptors = [
        'derby':DerbyAdaptor.class
    
    ]
    def adaptor

    public Deckchair(Map props) {
        def a = props['adaptor'] ? props['adaptor'] : 'derby'

        adaptor = adaptors[a].newInstance(props)
    }
    
    def save(obj, closure = null) {
        this.adaptor.save(obj, closure)
    }
    
    def get(key, closure = null) {
        this.adaptor.get(key, closure)
    }
    
    def all(closure = null) {
        this.adaptor.all(closure)
    }
    
    def remove(keyOrObj) {
        this.adaptor.remove(keyOrObj)
    }
    
    def find(condition, closure) {
		this.adaptor.find(condition, closure)
    }
    
    def nuke() {
        this.adaptor.nuke()
        this
    }
}
