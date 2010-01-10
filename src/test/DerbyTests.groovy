package test

import jwill.deckchair.DerbyAdaptor
/**
 * Created by IntelliJ IDEA.
 * User: jwill
 * Date: Jan 9, 2010
 * Time: 10:40:58 PM
 * To change this template use File | Settings | File Templates.
 */
class DerbyTests extends GroovyTestCase {
    def adaptor

    void setUp() {
        adaptor = new DerbyAdaptor([name:'tests'])
    }

    void tearDown() {
        adaptor.sql.execute("DROP TABLE tests")
    }
    void testSave() {
        adaptor.save([name:'fred',age:15, sex:'M'], null)
        adaptor.save([name:'john'], null)
        adaptor.save([name:'kate'], null)
       assertEquals(adaptor.all().size(),3)
    }

    void testRemove() {
       def a = adaptor.save([name:'fred'])
       def b = adaptor.save([name:'john'])
       def c = adaptor.save([name:'kate'])

       adaptor.remove(b.key)
       assertEquals(adaptor.all().size(),2)

    }

    void testGet() {
       def a = adaptor.save([name:'fred'])
       def b = adaptor.save([name:'john'])
       def c = adaptor.save([name:'kate'])

       def d = adaptor.get(b.key)
       assertEquals(b,d)

    }

    void testBogusGet() {
       def a = adaptor.get("1234")
       assertNull(a)
    }

    void testNuke() {
       def a = adaptor.save([name:'fred'])
       def b = adaptor.save([name:'john'])
       def c = adaptor.save([name:'kate'])

       adaptor.nuke()
       def list = adaptor.all()
       assertEquals(list.size(),0)

    }
}
