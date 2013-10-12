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
