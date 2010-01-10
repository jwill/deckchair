package test

import jwill.deckchair.DerbyAdaptor
import jwill.deckchair.Deckchair
/**
 * Created by IntelliJ IDEA.
 * User: jwill
 * Date: Jan 9, 2010
 * Time: 10:40:58 PM
 * To change this template use File | Settings | File Templates.
 */
class DerbyTests extends GroovyTestCase {
    def derby

    void setUp() {
        derby = new Deckchair([name:'tests', adaptor:'derby'])
    }

    void tearDown() {
        derby.nuke()
    }
    void testSave() {
        derby.save([name:'fred',age:15, sex:'M'], null)
        derby.save([name:'john'], null)
        derby.save([name:'kate'], null)
       assertEquals(derby.all().size(),3)
    }

    void testRemove() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       derby.remove(b.key)
       assertEquals(derby.all().size(),2)

    }

    void testGet() {
       def a = derby.save([name:'fred'])
       def b = derby.save([name:'john'])
       def c = derby.save([name:'kate'])

       def d = derby.get(b.key)
       assertEquals(b,d)

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
       assertEquals(list.size(),0)

    }
}
