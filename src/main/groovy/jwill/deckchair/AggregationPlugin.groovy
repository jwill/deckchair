package jwill.deckchair

import groovy.util.logging.Log

@Log
class AggregationPlugin {
    static deckchair

    public static apply(d) {
        this.deckchair = d

        deckchair.metaClass.sum = sum
    }

    static sum = {propertyName, closure ->
        def vals = this.deckchair.adaptor.all({array ->
            def list = []
            for (int i=0; i<array.length(); i++) {
                def obj = array.get(i)
                list.add obj.get(propertyName)
            }
            list
        })
        def sum = 0
        for (v in vals)
            sum += v
        if (closure)
            closure(sum)
        else sum
    }

    // count, min, max, avg
}
