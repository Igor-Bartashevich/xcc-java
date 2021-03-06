/*
 * Copyright 2003-2013 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.xcc.types.impl;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XSGYearMonth;

public class XsGYearMonthImpl extends AbstractDateItem implements XSGYearMonth {
    public XsGYearMonthImpl(String bodyString, TimeZone timezone, Locale locale) {
        super(ValueType.XS_GYEAR_MONTH, bodyString, timezone, locale);

        gCalFromGYearMonthString(bodyString);
    }

    public GregorianCalendar asGregorianCalendar() {
        return gCalFromGYearMonthString(asString());
    }
}
