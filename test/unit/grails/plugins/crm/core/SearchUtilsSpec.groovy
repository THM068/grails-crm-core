/*
 * Copyright 2013 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.core

import spock.lang.Specification

/**
 * Test the SearchUtils class.
 */
class SearchUtilsSpec extends Specification {

    def "test wildcard"() {
        expect:
        SearchUtils.wildcard("ABC") == 'abc%'
        SearchUtils.wildcard("ABC*") == 'abc%'
        SearchUtils.wildcard("*ABC") == '%abc'
        SearchUtils.wildcard("*ABC*") == '%abc%'
        SearchUtils.wildcard("=ABC") == 'abc'

        when:
        SearchUtils.wildcard(null)

        then:
        thrown NullPointerException

        when:
        SearchUtils.wildcard('')

        then:
        thrown IndexOutOfBoundsException
    }

    def "double query"() {
        given:
        def swedish = new Locale("sv", "SE")
        def crit = [success: false]
        crit.eq = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Number)) {
                throw new IllegalArgumentException("expected java.lang.Number, found " + q.class.name)
            }
            if (q != 42.11) {
                throw new IllegalArgumentException("expected 42, found " + q)
            }
            crit.success = true
        }
        crit.lt = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Number)) {
                throw new IllegalArgumentException("expected java.lang.Number, found " + q.class.name)
            }
            if (q != 42) {
                throw new IllegalArgumentException("expected 42, found " + q)
            }
            crit.success = true
        }
        crit.gt = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Number)) {
                throw new IllegalArgumentException("expected java.lang.Number, found " + q.class.name)
            }
            if (q != 42) {
                throw new IllegalArgumentException("expected 42, found " + q)
            }
            crit.success = true
        }
        crit.between = { p, a, b ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(a instanceof Number)) {
                throw new IllegalArgumentException("expected java.lang.Number, found " + a.class.name)
            }
            if (!(b instanceof Number)) {
                throw new IllegalArgumentException("expected java.lang.Number, found " + b.class.name)
            }
            if (a != 24) {
                throw new IllegalArgumentException("expected 24, found " + a)
            }
            if (b != 42) {
                throw new IllegalArgumentException("expected 42, found " + b)
            }
            crit.success = true
        }

        when:
        SearchUtils.doubleQuery(crit, 'test', '', swedish)

        then:
        crit.success == false

        when:
        SearchUtils.doubleQuery(crit, 'test', '42,11', swedish)

        then:
        crit.success

        when:
        SearchUtils.doubleQuery(crit, 'test', '>42', swedish)

        then:
        crit.success

        when:
        SearchUtils.doubleQuery(crit, 'test', '<42', swedish)

        then:
        crit.success

        when:
        SearchUtils.doubleQuery(crit, 'test', '24-42', swedish)

        then:
        crit.success

        when:
        SearchUtils.doubleQuery(crit, 'test', '24 42', swedish)

        then:
        crit.success
    }


    def "date query"() {
        given:
        final Locale swedish = new Locale("sv", "SE")
        final TimeZone timezone = TimeZone.getTimeZone("MET")
        final Date referenceDate1 = Date.parse("yyyy-MM-dd", "2014-08-01").clearTime()
        final Date referenceDate2 = Date.parse("yyyy-MM-dd", "2014-08-31").clearTime()
        final Map crit = [success: false]
        crit.eq = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Date)) {
                throw new IllegalArgumentException("expected java.util.Date, found " + q.class.name)
            }
            if (q != referenceDate1) {
                throw new IllegalArgumentException("expected $referenceDate1, found " + q)
            }
            crit.success = true
        }
        crit.lt = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Date)) {
                throw new IllegalArgumentException("expected java.util.Date, found " + q.class.name)
            }
            if (q != referenceDate1) {
                throw new IllegalArgumentException("expected $referenceDate1, found " + q)
            }
            crit.success = true
        }
        crit.gt = { p, q ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(q instanceof Date)) {
                throw new IllegalArgumentException("expected java.util.Date, found " + q.class.name)
            }
            if (q != referenceDate1) {
                throw new IllegalArgumentException("expected $referenceDate1, found " + q)
            }
            crit.success = true
        }
        crit.between = { p, a, b ->
            crit.success = false
            if (p != 'test') {
                throw new IllegalArgumentException("expected test, found " + p)
            }
            if (!(a instanceof Date)) {
                throw new IllegalArgumentException("expected java.util.Date, found " + a.class.name)
            }
            if (!(b instanceof Date)) {
                throw new IllegalArgumentException("expected java.util.Date, found " + b.class.name)
            }
            if (a != referenceDate1) {
                throw new IllegalArgumentException("expected $referenceDate1, found " + a)
            }
            if (b != referenceDate2) {
                throw new IllegalArgumentException("expected $referenceDate2, found " + b)
            }
            crit.success = true
        }

        when:
        SearchUtils.sqlDateQuery(crit, 'test', '', swedish, timezone)

        then:
        crit.success == false

        when:
        SearchUtils.sqlDateQuery(crit, 'test', '2014-08-01', swedish, timezone)

        then:
        crit.success

        when:
        SearchUtils.sqlDateQuery(crit, 'test', '>2014-08-01', swedish, timezone)

        then:
        crit.success

        when:
        SearchUtils.sqlDateQuery(crit, 'test', '<2014-08-01', swedish, timezone)

        then:
        crit.success

        when:
        SearchUtils.sqlDateQuery(crit, 'test', '2014-08-01 2014-08-31', swedish, timezone)

        then:
        crit.success
    }
}
