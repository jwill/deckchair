import junit.framework.TestCase
import jwill.deckchair.AggregationPlugin
import jwill.deckchair.Deckchair

/**
 * Created with IntelliJ IDEA.
 * User: parallels
 * Date: 10/4/13
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
class AggregationTests extends TestCase {
    def derby

    void setUp() {
        derby = new Deckchair([name:'tests', homeDir:'build/tmp/testing-deckchair', adaptor:'derby'])
        AggregationPlugin.apply(derby)
    }

    void tearDown() {
        derby.nuke()
    }

    void testSum() {
        derby.batch([[cost:50], [cost:100], [cost:125]])
        def sum = derby.sum("cost", null)
        assertEquals(sum, 275)
    }

    void testAvg() {
        derby.batch([[cost:50], [cost:100], [cost:125]])
        def avg = derby.avg("cost", null)
        assertEquals(avg, 91.6666666667)
    }

    void testMin() {
        derby.batch([[cost:100], [cost:50], [cost:125]])
        def min = derby.min("cost", null)
        assertEquals(min, 50)
    }

    void testMax() {
        derby.batch([[cost:125], [cost:50], [cost:100]])
        def max = derby.max("cost", null)
        assertEquals(max, 125)
    }
}
