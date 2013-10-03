package test

import groovy.util.logging.Log
import org.junit.*
import org.json.*
import junit.framework.TestCase
import jwill.deckchair.DerbyAdaptor
import jwill.deckchair.Deckchair
/**
 * Created by IntelliJ IDEA.
 * User: jwill
 * Date: Jan 9, 2010
 * Time: 10:40:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Log
class DerbyTests extends TestCase {
    def derby

    void setUp() {
        derby = new Deckchair([name:'tests', homeDir:'build/tmp/testing-deckchair', adaptor:'derby'])
    }

    void tearDown() {
        derby.nuke()
    }
    void testSave() {
        derby.save([name:'fred',age:15, sex:'M'], null)
        derby.save([name:'john'], null)
        derby.save([name:'kate'], null)
       assertEquals(derby.all().length(),3)
    }

    void testRemove() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       derby.remove(b.key)
       assertEquals(derby.all().length(),2)

    }

    void testBatch() {
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        derby.batch(array)
        assertEquals(derby.all().length(), 3)
    }

    void testEach() {
        def count = 0
        def array = [[name:'fred'], [name:'john'], [name:'kate']]
        derby.batch(array)
        derby.each({obj, i ->
            count++
            log.info(""+i)
            log.info(obj.toString())
        })
        assertEquals(array.size(), count)
    }

    void testKeys() {
        def a = derby.save([name:'fred'])
        def b = derby.save([name:'john'])
        def c = derby.save([name:'kate'])

        def expectedList = [a.key, b.key, c.key]
        def keys = derby.keys()
        assertEquals(expectedList, keys)
    }

    void testExists() {
        def c = derby.save([name:'kate'])
        derby.exists(c.key, {b ->
            assertEquals(true, b)
        })
    }

    void testBogusExists() {
        derby.exists('x_x', {b ->
            assertEquals(false, b)
        })
    }

    void testGet() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       def d = derby.get(b.key)
       assertEquals(new JSONObject(b).toString(),d.toString())

    }

    void testMultipleGet() {
        def a = derby.save([name:'fred'])
        def b = derby.save([name:'john'])
        def c = derby.save([name:'kate'])

        def d = derby.get([a.key, b.key])
        assertEquals([a.key,b.key],d.collect{it.key})

    }

    void testChange() {
    	def a = derby.save([name:'fred'])
    	a['name'] ='john'
    	derby.save(a)
    	def b = derby.get(a.key)
    	assertEquals(a.name, b.name)
    	
    }

     void testFind() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       def d = derby.find({array ->
         	 array.getJSONObject(0).getString("name").equals("fred")
         
       }, null)
       assertEquals(d.length(),1)
    }

    void testBogusGet() {
       def a = derby.get("1234")
       assertNull(a)
    }

    void testNuke() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       derby.nuke()
       def list = derby.all()
       assertEquals(list.length(),0)

    }
}
