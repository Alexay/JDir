/*
 The MIT License

 Copyright (c) 2004-2015 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package joptsimple.internal;

import joptsimple.ValueConverter;

import java.lang.reflect.Constructor;

import static joptsimple.internal.Reflection.instantiate;

/**
 * @param <V> constraint on the type of values being converted to
 * @author <a href="mailto:pholser@alumni.rice.edu">Paul Holser</a>
 */
class ConstructorInvokingValueConverter<V> implements ValueConverter<V> {
    private final Constructor<V> ctor;

    ConstructorInvokingValueConverter( Constructor<V> ctor ) {
        this.ctor = ctor;
    }

    public V convert( String value ) {
        return instantiate( ctor, value );
    }

    public Class<V> valueType() {
        return ctor.getDeclaringClass();
    }

    public String valuePattern() {
        return null;
    }
}
