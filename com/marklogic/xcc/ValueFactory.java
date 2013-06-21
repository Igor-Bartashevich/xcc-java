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
package com.marklogic.xcc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.marklogic.xcc.types.AtomicType;
import com.marklogic.xcc.types.CtsBox;
import com.marklogic.xcc.types.CtsCircle;
import com.marklogic.xcc.types.CtsPoint;
import com.marklogic.xcc.types.CtsPolygon;
import com.marklogic.xcc.types.Duration;
import com.marklogic.xcc.types.NodeType;
import com.marklogic.xcc.types.SequenceType;
import com.marklogic.xcc.types.ValueType;
import com.marklogic.xcc.types.XName;
import com.marklogic.xcc.types.XSBoolean;
import com.marklogic.xcc.types.XSDate;
import com.marklogic.xcc.types.XSDateTime;
import com.marklogic.xcc.types.XSDuration;
import com.marklogic.xcc.types.XSInteger;
import com.marklogic.xcc.types.XSString;
import com.marklogic.xcc.types.XSTime;
import com.marklogic.xcc.types.XdmAtomic;
import com.marklogic.xcc.types.XdmBinary;
import com.marklogic.xcc.types.XdmComment;
import com.marklogic.xcc.types.XdmDocument;
import com.marklogic.xcc.types.XdmDuration;
import com.marklogic.xcc.types.XdmElement;
import com.marklogic.xcc.types.XdmItem;
import com.marklogic.xcc.types.XdmNode;
import com.marklogic.xcc.types.XdmProcessingInstruction;
import com.marklogic.xcc.types.XdmSequence;
import com.marklogic.xcc.types.XdmText;
import com.marklogic.xcc.types.XdmValue;
import com.marklogic.xcc.types.XdmVariable;
import com.marklogic.xcc.types.impl.BinaryImpl;
import com.marklogic.xcc.types.impl.CommentImpl;
import com.marklogic.xcc.types.impl.CtsBoxImpl;
import com.marklogic.xcc.types.impl.CtsCircleImpl;
import com.marklogic.xcc.types.impl.CtsPointImpl;
import com.marklogic.xcc.types.impl.CtsPolygonImpl;
import com.marklogic.xcc.types.impl.DocumentImpl;
import com.marklogic.xcc.types.impl.ElementImpl;
import com.marklogic.xcc.types.impl.ProcessingInstructionImpl;
import com.marklogic.xcc.types.impl.SequenceImpl;
import com.marklogic.xcc.types.impl.TextImpl;
import com.marklogic.xcc.types.impl.XsAnyUriImpl;
import com.marklogic.xcc.types.impl.XsBase64BinaryImpl;
import com.marklogic.xcc.types.impl.XsBooleanImpl;
import com.marklogic.xcc.types.impl.XsDateImpl;
import com.marklogic.xcc.types.impl.XsDateTimeImpl;
import com.marklogic.xcc.types.impl.XsDayTimeDurationImpl;
import com.marklogic.xcc.types.impl.XsDecimalImpl;
import com.marklogic.xcc.types.impl.XsDoubleImpl;
import com.marklogic.xcc.types.impl.XsDurationImpl;
import com.marklogic.xcc.types.impl.XsFloatImpl;
import com.marklogic.xcc.types.impl.XsGDayImpl;
import com.marklogic.xcc.types.impl.XsGMonthDayImpl;
import com.marklogic.xcc.types.impl.XsGMonthImpl;
import com.marklogic.xcc.types.impl.XsGYearImpl;
import com.marklogic.xcc.types.impl.XsGYearMonthImpl;
import com.marklogic.xcc.types.impl.XsHexBinaryImpl;
import com.marklogic.xcc.types.impl.XsIntegerImpl;
import com.marklogic.xcc.types.impl.XsQNameImpl;
import com.marklogic.xcc.types.impl.XsStringImpl;
import com.marklogic.xcc.types.impl.XsTimeImpl;
import com.marklogic.xcc.types.impl.XsUntypedAtomicImpl;
import com.marklogic.xcc.types.impl.XsYearMonthDurationImpl;

/**
 * <p>
 * This class contains various static factory methods that return instances of {@link XdmValue} and
 * {@link XdmVariable}.
 * </p>
 */
public final class ValueFactory {
    private ValueFactory() {
        // Cannot be instantiated
    }

    /**
     * <p>
     * Generic {@link XdmValue} creation factory method. Value types are enumerated in
     * {@link ValueType}. Examples are {@link ValueType#XS_INTEGER}, {@link ValueType#XS_BOOLEAN},
     * {@link ValueType#SEQUENCE}, {@link ValueType#XS_STRING}, etc.
     * </p>
     * <p>
     * NOTE: If you pass a valueType of {@link ValueType#NODE}, it will be treated as
     * {@link ValueType#ELEMENT}. Using {@link ValueType#NODE} directly is discouraged, it is
     * defined as the common super-type for all node types. Other node types that may be constructed
     * are {@link ValueType#TEXT} and {@link ValueType#BINARY}. In future releases, creation of
     * additional node value types will be supported.
     * </p>
     * 
     * @param valueType
     *            An concrete subclass of {@link ValueType} which indicates the type of value to
     *            create.
     * @param value
     *            An {@link Object} containing the actual value to construct the object with. The
     *            specific class of this object is be dependent on the valueType argument. If the
     *            provided value is not consistent with the valueType then a
     *            {@link IllegalArgumentException} may be thrown.
     * @return An instance of {@link XdmValue}.
     * @throws IllegalArgumentException
     *             If the provided value is not consistent with the valueType.
     */
    public static XdmValue newValue(ValueType valueType, Object value) {
        if (valueType instanceof SequenceType) {
            return newSequenceValue(value);
        }

        if (valueType instanceof NodeType) {
            return newNodeValue(valueType, value);
        }

        if (valueType instanceof AtomicType) {
            return (newAtomicValue(valueType, value));
        }

        throw new IllegalArgumentException("Unrecognized ValueType: " + valueType);
    }

//	public static XdmValue newValue (Object value)
//	{
//		if (value == null) {
//			throw new IllegalArgumentException ("null parameter");
//		}
//
//		if (value instanceof Boolean) {
//			return newValue (ValueType.XS_BOOLEAN, value);
//		}
//
//		if ((value instanceof Double) || (value instanceof Float)) {
//			return newValue (ValueType.XS_DOUBLE, value);
//		}
//
//		if (value instanceof BigInteger) {
//			return newValue (ValueType.XS_DOUBLE, value);
//		}
//
//		if ((value instanceof Integer) || (value instanceof Long)
//			|| (value instanceof BigInteger)) {
//			return newValue (ValueType.XS_INTEGER, value);
//		}
//
//		// TODO: finish this and add tests
//
//		throw new IllegalArgumentException ("Unrecognized object type: " + value.getClass().getName());
//	}

    /**
     * <p>
     * A convenience method to construct an {@link XdmElement} value. {@link XdmElement} objects can
     * be constructed from an XML {@link String}, a W3C DOM {@link Element} or an
     * {@link InputStream}.
     * </p>
     * 
     * @param value
     *            An instance of {@link String}, {@link Element} or {@link InputStream}.
     * @return An instance of {@link XdmElement}.
     * @throws IllegalArgumentException
     *             If value is not a {@link String}, {@link Element} or {@link InputStream}.
     */
    public static XdmElement newElement(Object value) {
        if (value instanceof String) {
            return new ElementImpl((String)value);
        }

        if (value instanceof InputStream) {
            return new ElementImpl((InputStream)value);
        }

        if (value instanceof Element) {
            byte[] bytes = ContentFactory.bytesFromW3cNode((Element)value);

            return new ElementImpl(new ByteArrayInputStream(bytes));
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Element or InputStream value required to construct "
                + ValueType.ELEMENT);
    }

    /**
     * <p>
     * A convenience method to construct an {@link XdmDocument} value. {@link XdmDocument} objects can
     * be constructed from an XML {@link String}, a W3C DOM {@link Document} or an
     * {@link InputStream}.
     * </p>
     * 
     * @param value
     *            An instance of {@link String}, {@link Document} or {@link InputStream}.
     * @return An instance of {@link XdmDocument}.
     * @throws IllegalArgumentException
     *             If value is not a {@link String}, {@link Document} or {@link InputStream}.
     */
    public static XdmDocument newDocumentNode(Object value) {
        if (value instanceof String) {
            return new DocumentImpl((String)value);
        }

        if (value instanceof InputStream) {
            return new DocumentImpl((InputStream)value);
        }

        if (value instanceof Document) {
            byte[] bytes = ContentFactory.bytesFromW3cNode((Document)value);

            return new DocumentImpl(new ByteArrayInputStream(bytes));
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Document or InputStream value required to construct "
                + ValueType.DOCUMENT);
    }

    /**
     * A convenience method to construct an {@link XdmText} value. {@link XdmText} objects can be
     * constructed from an XML {@link String}, a W3C DOM {@link Text} node or an {@link InputStream}
     * .
     * 
     * @param value
     *            An instance of {@link String}, {@link Text} or {@link InputStream}.
     * @return An instance of {@link XdmText}.
     * @throws IllegalArgumentException
     *             If value is not a {@link String}, {@link Text} or {@link InputStream}.
     */
    public static XdmText newTextNode(Object value) {
        if (value instanceof String) {
            return new TextImpl((String)value);
        }

        if (value instanceof InputStream) {
            return new TextImpl((InputStream)value);
        }

        if (value instanceof Text) {
            Text text = (Text)value;

            return new TextImpl(text.getNodeValue());
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Text or InputStream value required to construct "
                + ValueType.TEXT);
    }

    /**
     * A convenience method to construct an {@link XdmComment} value. {@link XdmComment} objects can be
     * constructed from an XML {@link String}, a W3C DOM {@link Comment} node or an {@link InputStream}
     * .
     * 
     * @param value
     *            An instance of {@link String}, {@link Comment} or {@link InputStream}.
     * @return An instance of {@link XdmComment}.
     * @throws IllegalArgumentException
     *             If value is not a {@link String}, {@link Comment} or {@link InputStream}.
     */
    public static XdmComment newCommentNode(Object value) {
        if (value instanceof String) {
            return new CommentImpl((String)value);
        }

        if (value instanceof InputStream) {
            return new CommentImpl((InputStream)value);
        }

        if (value instanceof Comment) {
            Comment c = (Comment)value;

            return new CommentImpl(c.getNodeValue());
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Text or InputStream value required to construct "
                + ValueType.TEXT);
    }

    /**
     * A convenience method to construct an {@link XdmProcessingInstruction} value. {@link XdmProcessingInstruction} objects can be
     * constructed from an XML {@link String}, a W3C DOM {@link ProcessingInstruction} node or an {@link InputStream}
     * .
     * 
     * @param value
     *            An instance of {@link String}, {@link ProcessingInstruction} or {@link InputStream}.
     * @return An instance of {@link XdmProcessingInstruction}.
     * @throws IllegalArgumentException
     *             If value is not a {@link String}, {@link ProcessingInstruction} or {@link InputStream}.
     */
    public static XdmProcessingInstruction newProcessingInstructionNode(Object value) {
        if (value instanceof String) {
            return new ProcessingInstructionImpl((String)value);
        }

        if (value instanceof InputStream) {
            return new ProcessingInstructionImpl((InputStream)value);
        }

        if (value instanceof ProcessingInstruction) {
            ProcessingInstruction c = (ProcessingInstruction)value;

            return new ProcessingInstructionImpl(c.getNodeValue());
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Text or InputStream value required to construct "
                + ValueType.TEXT);
    }

    public static XdmBinary newBinaryNode(Object value) {
        if (value instanceof String) {
            byte[] bytes = null;

            try {
                bytes = ((String)value).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                // unlikely to happen
                bytes = ((String)value).getBytes();
            }

            return new BinaryImpl(new ByteArrayInputStream(bytes), true);
        }

        if (value instanceof byte[]) {
            return new BinaryImpl(new ByteArrayInputStream((byte[])value), true);
        }

        if (value instanceof InputStream) {
            return new BinaryImpl((InputStream)value, true);
        }

        throw new IllegalArgumentException("String, org.w3c.dom.Text or InputStream value required to construct "
                + ValueType.BINARY);
    }

    /**
     * A convenience method to construct an {@link XSString} value.
     * 
     * @param value
     *            A String to construct the {@link XSString} object with.
     * @return An instance of {@link XSString}.
     */
    public static XSString newXSString(String value) {
        return (XSString)newValue(ValueType.XS_STRING, value);
    }

    /**
     * A convenience method to construct an {@link XSInteger} value. Note that an XQuery xs:integer
     * can hold values larger than a Java int or long.
     * 
     * @param value
     *            A long to construct the {@link XSInteger} object with.
     * @return An instance of {@link XSInteger}.
     */
    public static XSInteger newXSInteger(long value) {
        return (XSInteger)newValue(ValueType.XS_INTEGER, new Long(value));
    }

    /**
     * A convenience method to construct an {@link XSInteger} value. Note that an XQuery xs:integer
     * can hold values larger than a Java int or long, but these large value may be represented with
     * a {@link BigInteger} object.
     * 
     * @param value
     *            A {@link BigInteger} to construct the {@link XSInteger} object with.
     * @return An instance of {@link XSInteger}.
     */
    public static XSInteger newXSInteger(BigInteger value) {
        return (XSInteger)newValue(ValueType.XS_INTEGER, value);
    }

    /**
     * A convenience method to construct an {@link com.marklogic.xcc.types.XSBoolean} value.
     * 
     * @param value
     *            A boolean to construct the {@link com.marklogic.xcc.types.XSBoolean} object with.
     * @return An instance of {@link com.marklogic.xcc.types.XSBoolean}.
     */
    public static XSBoolean newXSBoolean(boolean value) {
        return new XsBooleanImpl(Boolean.valueOf(value));
    }

    /**
     * Convenience method to construct a {@link CtsBox} value.
     * 
     * @param south
     *            The southern boundary of the box.
     * @param west
     *            The western boundary of the box.
     * @param north
     *            The northern boundary of the box.
     * @param east
     *            The eastern boundary of the box.
     * @return An instance of {@link CtsBox}.
     */
    public static CtsBox newCtsBox(String south, String west, String north, String east) {
        return new CtsBoxImpl(south, west, north, east);
    }

    /**
     * Convenience method to construct a {@link CtsCircle} value.
     * 
     * @param radius
     *            The radius of the circle.
     * @param center
     *            A point representing the center of the circle.
     * @return An instance of {@link CtsCircle}.
     */
    public static CtsCircle newCtsCircle(String radius, CtsPoint center) {
        return new CtsCircleImpl(radius, center);
    }

    /**
     * Convenience method to construct a {@link CtsPoint} value.
     * 
     * @param latitude
     *            The latitude of the point.
     * @param longitude
     *            The longitude of the point.
     * @return An instance of {@link CtsPoint}.
     */
    public static CtsPoint newCtsPoint(String latitude, String longitude) {
        return new CtsPointImpl(latitude, longitude);
    }

    /**
     * Convenience method to construct a {@link CtsPolygon} value.
     * 
     * @param vertices
     *            The vertices of the polygon, given in order.
     * @return An instance of {@link CtsPolygon}.
     */
    public static CtsPolygon newCtsPolygon(List<CtsPoint> vertices) {
        return new CtsPolygonImpl(vertices);
    }

    /**
     * A convenience method to construct an {@link XSDateTime} value.
     * 
     * @param value
     *            A {@link String} representation of the date/time in standard XQuery form (ie
     *            2006-04-23T11:32:46).
     * @param timeZone
     *            A {@link TimeZone} object to apply to value, null for default.
     * @param locale
     *            A {@link Locale} object to apply to the value, null for default.
     * @return An instance of {@link XSDateTime}.
     */
    public static XSDateTime newXSDateTime(String value, TimeZone timeZone, Locale locale) {
        return new XsDateTimeImpl(value, timeZone, locale);
    }

    /**
     * A convenience method to construct an {@link XSDate} value.
     * 
     * @param value
     *            A {@link String} representation of the date/time in standard XQuery form (ie
     *            2006-04-23).
     * @param timeZone
     *            A {@link TimeZone} object to apply to value, null for default.
     * @param locale
     *            A {@link Locale} object to apply to the value, null for default.
     * @return An instance of {@link XSDate}.
     */
    public static XSDate newXSDate(String value, TimeZone timeZone, Locale locale) {
        return new XsDateImpl(value, timeZone, locale);
    }

    /**
     * A convenience method to construct an {@link XSTime} value.
     * 
     * @param value
     *            A {@link String} representation of the date/time in standard XQuery form (ie
     *            11:32:46).
     * @param timeZone
     *            A {@link TimeZone} object to apply to value, null for default.
     * @param locale
     *            A {@link Locale} object to apply to the value, null for default.
     * @return An instance of {@link XSDate}.
     */
    public static XSTime newXSTime(String value, TimeZone timeZone, Locale locale) {
        return new XsTimeImpl(value, timeZone, locale);
    }

    /**
     * A convenience method to construct an {@link XSDuration} value.
     * 
     * @param value
     *            A {@link String} representation of the duration (ie P2Y3M141DT12H46M12.34S).
     * @return An instance of {@link XSDuration}.
     */
    public static XSDuration newXSDuration(String value) {
        return new XsDurationImpl(value);
    }

    /**
     * A convenience method to construct an {@link XSDuration} value from an {@link XdmDuration}
     * object.
     * 
     * @param duration
     *            An instance XdmDuration
     * @return An instance of {@link XSDuration}.
     */
    public static XSDuration newXSDuration(XdmDuration duration) {
        return newXSDuration(duration.toString());
    }

    /**
     * Convenience method to construct an {@link XdmDuration} value.
     * 
     * @param serializedString
     *            A {@link String} representation of the duration (ie P2Y3M141DT12H46M12.34S).
     * @return An instance of {@link XdmDuration}.
     */
    public static XdmDuration newDuration(String serializedString) {
        return new Duration(serializedString);
    }

    // ------------------------------------------------------------

    /**
     * Factory method to construct an {@link XdmSequence} from an array of {@link XdmValue} objects.
     * Note XdmSequence is not a supported type for external variables.
     * @param values
     *            An array of {@link XdmValue} instances.
     * @return A new {@link XdmSequence} object.
     */
    public static XdmSequence<XdmItem> newSequence(XdmValue[] values) {
        return (new SequenceImpl(values));
    }

    // ------------------------------------------------------------

    /**
     * Factory method to create a variable (named value) from the given {@link XName} and
     * {@link XdmValue} objects.
     * 
     * @param name
     *            An {@link XName} that defines the name and (optional) namespace of the
     *            {@link XdmVariable}.
     * @param value
     *            An instance of {@link XdmValue} which is the value of the variable.
     * @return An instance of {@link XdmVariable} that encapsulates the name and value parameters.
     */
    public static XdmVariable newVariable(XName name, XdmValue value) {
        return new XdmVar(name, value);
    }

    // ------------------------------------------------------------

    private static XdmSequence<XdmItem> newSequenceValue(Object values) {
        if (!(values instanceof XdmValue[])) {
            throw new IllegalArgumentException("Value must be array of XdmValue");
        }

        return newSequence((XdmValue[])values);
    }

    private static XdmNode newNodeValue(ValueType valueType, Object value) {
        if (valueType == ValueType.ELEMENT) {
            return newElement(value);
        }

        if (valueType == ValueType.NODE) {
            return newElement(value);
        }

        if (valueType == ValueType.TEXT) {
            return newTextNode(value);
        }

        if (valueType == ValueType.BINARY) {
            return newBinaryNode(value);
        }

        /* TODO: ?
        if (valueType == ValueType.ATTRIBUTE) {
            return newAttributeNode(value);
        }
        */

        if (valueType == ValueType.COMMENT) {
            return newCommentNode(value);
        }

        if (valueType == ValueType.DOCUMENT) {
            return newDocumentNode(value);
        }

        if (valueType == ValueType.PROCESSING_INSTRUCTION) {
            return newProcessingInstructionNode(value);
        }

        throw new InternalError("Unrecognized valueType: " + valueType);
    }

    private static XdmAtomic newAtomicValue(ValueType valueType, Object value) {
        if (valueType == ValueType.XS_STRING) {
            assertStringArg(value, valueType);

            return new XsStringImpl((String)value);
        }

        if (valueType == ValueType.XS_INTEGER) {
            return new XsIntegerImpl(value);
        }

        if (valueType == ValueType.XS_DECIMAL) {
            return new XsDecimalImpl(value);
        }

        if (valueType == ValueType.XS_DOUBLE) {
            return new XsDoubleImpl(value);
        }

        if (valueType == ValueType.XS_FLOAT) {
            return new XsFloatImpl(value);
        }

        if (valueType == ValueType.XS_BOOLEAN) {
            if (value instanceof Boolean) {
                return new XsBooleanImpl((Boolean)value);
            } else if (value instanceof String) {
                return new XsBooleanImpl((String)value);
            }

            throw new IllegalArgumentException("Illegal value type (" + value.getClass()
                    + "), must be Boolean or String");
        }

        if (valueType == ValueType.XS_ANY_URI) {
            assertStringArg(value, valueType);

            return new XsAnyUriImpl((String)value);
        }

        if (valueType == ValueType.XS_QNAME) {
            assertStringArg(value, valueType);

            return new XsQNameImpl((String)value);
        }

        if (valueType == ValueType.XS_UNTYPED_ATOMIC) {
            assertStringArg(value, valueType);

            return new XsUntypedAtomicImpl((String)value);
        }

        if (valueType == ValueType.XS_DURATION) {
            if (value instanceof XdmDuration) {
                return new XsDurationImpl(value.toString());
            }

            assertStringArg(value, valueType);

            return new XsDurationImpl((String)value);
        }

        if (valueType == ValueType.XS_DAY_TIME_DURATION) {
            if (value instanceof XdmDuration) {
                return new XsDayTimeDurationImpl(value.toString());
            }

            assertStringArg(value, valueType);

            return new XsDayTimeDurationImpl((String)value);
        }

        if (valueType == ValueType.XS_YEAR_MONTH_DURATION) {
            if (value instanceof XdmDuration) {
                return new XsYearMonthDurationImpl(value.toString());
            }

            assertStringArg(value, valueType);

            return new XsYearMonthDurationImpl((String)value);
        }

        if (valueType == ValueType.XS_DATE_TIME) {
            // TODO: make constructor that takes a Date
//			if (value instanceof Date) {
//				return new XsDateTimeImpl ((Date) value);
//			}

            assertStringArg(value, valueType);

            return new XsDateTimeImpl((String)value, TimeZone.getDefault(), Locale.getDefault());
        }

        if (valueType == ValueType.XS_DATE) {
            // TODO: make constructor that takes a Date
//			if (value instanceof Date) {
//				return new XsDateImpl ((Date) value);
//			}

            assertStringArg(value, valueType);

            return new XsDateImpl((String)value, TimeZone.getDefault(), Locale.getDefault());
        }

        if (valueType == ValueType.XS_TIME) {
            // TODO: make constructor that takes a Date
//			if (value instanceof Date) {
//				return new XsTimeImpl ((Date) value);
//			}

            assertStringArg(value, valueType);

            return new XsTimeImpl((String)value, TimeZone.getDefault(), Locale.getDefault());
        }

        if (valueType == ValueType.XS_GDAY) {
            assertStringArg(value, valueType);

            return new XsGDayImpl((String)value, null, null);
        }

        if (valueType == ValueType.XS_GMONTH) {
            assertStringArg(value, valueType);

            return new XsGMonthImpl((String)value, null, null);
        }

        if (valueType == ValueType.XS_GMONTH_DAY) {
            assertStringArg(value, valueType);

            return new XsGMonthDayImpl((String)value, null, null);
        }

        if (valueType == ValueType.XS_GYEAR) {
            assertStringArg(value, valueType);

            return new XsGYearImpl((String)value, null, null);
        }

        if (valueType == ValueType.XS_GYEAR_MONTH) {
            assertStringArg(value, valueType);

            return new XsGYearMonthImpl((String)value, null, null);
        }

        if (valueType == ValueType.XS_HEX_BINARY) {
			if (value instanceof byte[]) {
				return new XsHexBinaryImpl((byte[])value);
			}

            assertStringArg(value, valueType);

            return new XsHexBinaryImpl((String)value);
        }

        if (valueType == ValueType.XS_BASE64_BINARY) {
            // TODO: add byte array constructor
//			if (value instanceof byte []) {
//				return new XsHexBinaryImpl ((byte []) value);
//			}

            assertStringArg(value, valueType);

            return new XsBase64BinaryImpl((String)value);
        }
        
        if (valueType == ValueType.CTS_BOX) {
            assertStringArg(value, valueType);
            
            return new CtsBoxImpl((String)value);
        }

        if (valueType == ValueType.CTS_CIRCLE) {
            assertStringArg(value, valueType);
            
            return new CtsCircleImpl((String)value);
        }

        if (valueType == ValueType.CTS_POINT) {
            assertStringArg(value, valueType);
            
            return new CtsPointImpl((String)value);
        }

        if (valueType == ValueType.CTS_POLYGON) {
            assertStringArg(value, valueType);
            
            return new CtsPolygonImpl((String)value);
        }

        throw new IllegalStateException("Unhandled type: " + valueType);
    }

    private static void assertStringArg(Object value, ValueType valueType) {
        if (value instanceof String) {
            return;
        }

        throw new IllegalArgumentException("String value required to construct " + valueType);
    }

    // ------------------------------------------------------------

    private static class XdmVar implements XdmVariable {
        private final XName name;
        private final XdmValue value;

        public XdmVar(XName name, XdmValue value) {
            this.name = name;
            this.value = value;
        }

        public XName getName() {
            return name;
        }

        public XdmValue getValue() {
            return value;
        }

        // --------------------------------------------

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof XdmVariable) {
                XdmVariable var = (XdmVariable)obj;

                return name.equals(var.getName());
            }

            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
