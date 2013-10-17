import groovy.util.logging.Log
import junit.framework.TestCase
import jwill.deckchair.Deckchair
import org.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/12/13
 * Time: 11:11 AM
 * To change this template use File | Settings | File Templates.
 */
@Log
class FlatFileTests extends TestCase {
    def db

    void setUp() {
        db = new Deckchair([name:'tests', homeDir:'build/tmp/testing-deckchair', adaptor:'flatfile'])
    }

    void tearDown() {
        db.nuke()
    }

    void testFind() {
        db.save([name:'fred'])
        db.save([name:'john'])
        db.save([name:'kate'])

        def d = db.find({o ->
            o.name.equals("fred")
        }, null)
        assertEquals(d.length(),1)
    }

    void testKeys() {
        def a = db.save([name:'fred'])
        def b = db.save([name:'john'])
        def c = db.save([name:'kate'])

        def expectedList = [a.key, b.key, c.key]
        def keys = db.keys()
        assertEquals(expectedList, keys)
    }

    void testEach() {
        def count = 0
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        db.batch(array)
        db.each({obj, i ->
            count++
            log.info(""+i)
            log.info(obj.toString())
        })
        assertEquals(array.size(), count)
    }

    void testExists() {
        def c = db.save([name:'kate'])
        db.exists(c.key, {b ->
            assertEquals(true, b)
        })
    }

    void testBogusExists() {
        db.exists('x_x', {b ->
            assertEquals(false, b)
        })
    }

    void testMultipleGet() {
        def a = db.save([name:'fred'])
        def b = db.save([name:'john'])
        db.save([name:'kate'])

        def d = db.get([a.key, b.key])
        assertEquals([a.key,b.key],d.collect{it.key})

    }

    void testChange() {
        def a = db.save([name:'fred'])
        a['name'] ='john'
        db.save(a)
        def b = db.get(a.key)
        assertEquals(a.name, b.name)

    }

    void testBogusGet() {
        def a = db.get("1234")
        assertNull(a)
    }

    void testBatch() {
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        db.batch(array)
        assertEquals(db.all().size(), 3)
    }

    void testGet() {
        db.save([name:'fred'])
        def b = db.save([name:'john'])
        db.save([name:'kate'])

        def d = db.get(b.key)
        assertEquals(b.toString(),d.toString())
    }

    void testSave() {
        db.save([name:'fred',age:15, sex:'M'], null)
        db.save([name:'john'], null)
        db.save([name:'kate'], null)
        assertEquals(db.all().size(),3)
    }

    void testNuke() {
        db.save([name:'fred'])
        db.save([name:'john'])
        db.save([name:'kate'])
        log.info(""+db.all().size())
        db.nuke()
        def list = db.all()
        assertEquals(list.size(),0)
    }

    void testRemove() {
        db.save([name:'fred'])
        def b = db.save([name:'john'])
        db.save([name:'kate'])

        db.remove(b.key)
        log.info("testRemove:"+db.all().toString())
        assertEquals(db.all().size(),2)

    }

    void testRemoveArray() {
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        array = db.batch(array)
        db.remove(array)
        assertEquals(db.all().size(), 0)
    }
}
