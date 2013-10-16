import groovy.util.logging.Log
import junit.framework.TestCase
import jwill.deckchair.Deckchair

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

    void testBatch() {
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        db.batch(array)
        assertEquals(db.all().size(), 3)
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
}
