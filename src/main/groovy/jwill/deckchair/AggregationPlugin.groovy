package jwill.deckchair

import groovy.util.logging.Log

@Log
class AggregationPlugin {
    static deckchair

    public static apply(d) {
        this.deckchair = d

        deckchair.metaClass.sum = sum
        deckchair.metaClass.count = count
        deckchair.metaClass.avg = avg
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

    static avg = {propertyName, closure ->
        def s = this.deckchair.sum(propertyName, null)
        def c = this.deckchair.count(propertyName, null)
        def avg = s / c
        if (closure)
            closure(avg)
        else avg
    }

    static count = {propertyName, closure ->
        def c = 0
        def vals = this.deckchair.adaptor.all({array ->
            for (int i=0; i<array.length(); i++) {
                def obj = array.get(i)
                if (obj.get(propertyName) != null) {
                    c++
                }
            }
        })
        if (closure)
            closure(c)
        else c
    }

    // count, min, max, avg
}
