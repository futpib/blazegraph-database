package com.bigdata.rdf.internal;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.datatypes.XMLDatatypeUtil;
import org.openrdf.model.util.Literals;
import org.openrdf.model.vocabulary.XMLSchema;

import com.bigdata.rdf.internal.impl.AbstractIV;
import com.bigdata.rdf.internal.impl.TermId;
import com.bigdata.rdf.model.BigdataLiteralImpl;
import com.bigdata.rdf.model.BigdataValue;
import com.bigdata.rdf.model.BigdataValueImpl;

import org.openrdf.query.algebra.Compare.CompareOp;
import org.openrdf.query.algebra.evaluation.ValueExprEvaluationException;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConstants;

/**
 * Overrides of buggy methods in {@link org.openrdf.query.algebra.evaluation.util.QueryEvaluationUtil}.
 */
public class QueryEvaluationUtil extends org.openrdf.query.algebra.evaluation.util.QueryEvaluationUtil {
	/**
	 * Checks whether the supplied literal is a "simple literal".
	 *
	 * A "simple literal" is a literal with no language tag or datatype.
	 *
	 * @see <a href="https://www.w3.org/TR/sparql11-query/#simple_literal">Definition of Simple Literal</a>
	 */
	public static boolean isSimpleLiteral(Literal literal) {
		return (
			literal.getLanguage() == null
			&& literal.getDatatype() == null
		);
	}

	/**
	 * Checks whether the supplied value is a "plain literal".
	 *
	 * A "plain literal" is a literal with no datatype and optionally a language tag.
	 *
	 * @see <a href="http://www.w3.org/TR/2004/REC-rdf-concepts-20040210/#dfn-plain-literal">RDF Literal Documentation</a>
	 */
	public static boolean isPlainLiteral(Literal literal) {
		return literal.getDatatype() == null;
	}

	/**
	 * Checks whether the supplied literal is a "string literal".
	 *
	 * A "string literal" is either a simple literal, a plain literal with language tag, or a literal with datatype xsd:string.
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-query/#func-string">Strings in SPARQL Functions</a>
	 */
	public static boolean isStringLiteral(Literal literal) {
		return (
			isSimpleLiteral(literal)
			|| (
				isPlainLiteral(literal)
				&& literal.getLanguage() != null
			)
			|| literal.getDatatype().equals(XMLSchema.STRING)
		);
	}

	public static boolean compareLiterals(Literal leftLiteral, Literal rightLiteral, CompareOp operator) throws ValueExprEvaluationException {
		// type precendence:
		// - simple literal
		// - numeric
		// - xsd:boolean
		// - xsd:dateTime
		// - xsd:string
		// - RDF term (equal and unequal only)

		URI leftDatatype = leftLiteral.getDatatype();
		URI rightDatatype = rightLiteral.getDatatype();

		boolean leftLangLiteral = Literals.isLanguageLiteral(leftLiteral);
		boolean rightLangLiteral = Literals.isLanguageLiteral(rightLiteral);
		
		// for purposes of query evaluation in SPARQL, simple literals and
		// string-typed literals with the same
		// lexical value are considered equal.
		URI commonDatatype = null;
		if (isPlainLiteral(leftLiteral) && isPlainLiteral(rightLiteral)) {
			commonDatatype = XMLSchema.STRING;
		}

		Integer compareResult = null;

		if (isPlainLiteral(leftLiteral) && isPlainLiteral(rightLiteral)) {
			compareResult = leftLiteral.getLabel().compareTo(rightLiteral.getLabel());
		}
		else if ((!leftLangLiteral && !rightLangLiteral) || commonDatatype != null) {
			if (commonDatatype == null) {
				if (leftDatatype == null || rightDatatype == null) {
					commonDatatype = null;
				} else if (leftDatatype.equals(rightDatatype)) {
					commonDatatype = leftDatatype;
				} else if (
					XMLDatatypeUtil.isNumericDatatype(leftDatatype)
						&& XMLDatatypeUtil.isNumericDatatype(rightDatatype)
				) {
					// left and right arguments have different datatypes, try to find
					// a
					// more general, shared datatype
					if (leftDatatype.equals(XMLSchema.DOUBLE) || rightDatatype.equals(XMLSchema.DOUBLE)) {
						commonDatatype = XMLSchema.DOUBLE;
					}
					else if (leftDatatype.equals(XMLSchema.FLOAT) || rightDatatype.equals(XMLSchema.FLOAT)) {
						commonDatatype = XMLSchema.FLOAT;
					}
					else if (leftDatatype.equals(XMLSchema.DECIMAL) || rightDatatype.equals(XMLSchema.DECIMAL)) {
						commonDatatype = XMLSchema.DECIMAL;
					}
					else {
						commonDatatype = XMLSchema.INTEGER;
					}
				}
			}

			if (commonDatatype != null) {
				try {
					if (commonDatatype.equals(XMLSchema.DOUBLE)) {
						compareResult = Double.compare(leftLiteral.doubleValue(), rightLiteral.doubleValue());
					}
					else if (commonDatatype.equals(XMLSchema.FLOAT)) {
						compareResult = Float.compare(leftLiteral.floatValue(), rightLiteral.floatValue());
					}
					else if (commonDatatype.equals(XMLSchema.DECIMAL)) {
						compareResult = leftLiteral.decimalValue().compareTo(rightLiteral.decimalValue());
					}
					else if (XMLDatatypeUtil.isIntegerDatatype(commonDatatype)) {
						compareResult = leftLiteral.integerValue().compareTo(rightLiteral.integerValue());
					}
					else if (commonDatatype.equals(XMLSchema.BOOLEAN)) {
						Boolean leftBool = Boolean.valueOf(leftLiteral.booleanValue());
						Boolean rightBool = Boolean.valueOf(rightLiteral.booleanValue());
						compareResult = leftBool.compareTo(rightBool);
					}
					else if (XMLDatatypeUtil.isCalendarDatatype(commonDatatype)) {
						XMLGregorianCalendar left = leftLiteral.calendarValue();
						XMLGregorianCalendar right = rightLiteral.calendarValue();

						compareResult = left.compare(right);

						// Note: XMLGregorianCalendar.compare() returns compatible
						// values
						// (-1, 0, 1) but INDETERMINATE needs special treatment
						if (compareResult == DatatypeConstants.INDETERMINATE) {
							throw new ValueExprEvaluationException("Indeterminate result for date/time comparison");
						}
					}
					else if (commonDatatype.equals(XMLSchema.STRING)) {
						compareResult = leftLiteral.getLabel().compareTo(rightLiteral.getLabel());
					}
				}
				catch (IllegalArgumentException e) {
					// One of the basic-type method calls failed, try syntactic match
					// before throwing an error
					if (leftLiteral.equals(rightLiteral)) {
						switch (operator) {
							case EQ:
								return true;
							case NE:
								return false;
						}
					}

					throw new ValueExprEvaluationException(e);
				}
			}
		}

		if (compareResult != null) {
			// Literals have compatible ordered datatypes
			switch (operator) {
				case LT:
					return compareResult.intValue() < 0;
				case LE:
					return compareResult.intValue() <= 0;
				case EQ:
					return compareResult.intValue() == 0;
				case NE:
					return compareResult.intValue() != 0;
				case GE:
					return compareResult.intValue() >= 0;
				case GT:
					return compareResult.intValue() > 0;
				default:
					throw new IllegalArgumentException("Unknown operator: " + operator);
			}
		}
		else {
			// All other cases, e.g. literals with languages, unequal or
			// unordered datatypes, etc. These arguments can only be compared
			// using the operators 'EQ' and 'NE'. See SPARQL's RDFterm-equal
			// operator

			boolean literalsEqual = leftLiteral.equals(rightLiteral);

			if (!literalsEqual) {
				if (!leftLangLiteral && !rightLangLiteral && isSupportedDatatype(leftDatatype)
						&& isSupportedDatatype(rightDatatype))
				{
					// left and right arguments have incompatible but supported
					// datatypes

					// we need to check that the lexical-to-value mapping for both
					// datatypes succeeds
					if (!XMLDatatypeUtil.isValidValue(leftLiteral.getLabel(), leftDatatype)) {
						throw new ValueExprEvaluationException("not a valid datatype value: " + leftLiteral);
					}

					if (!XMLDatatypeUtil.isValidValue(rightLiteral.getLabel(), rightDatatype)) {
						throw new ValueExprEvaluationException("not a valid datatype value: " + rightLiteral);
					}
					boolean leftString = leftDatatype.equals(XMLSchema.STRING);
					boolean rightString = rightDatatype.equals(XMLSchema.STRING);
					boolean leftNumeric = XMLDatatypeUtil.isNumericDatatype(leftDatatype);
					boolean rightNumeric = XMLDatatypeUtil.isNumericDatatype(rightDatatype);
					boolean leftDate = XMLDatatypeUtil.isCalendarDatatype(leftDatatype);
					boolean rightDate = XMLDatatypeUtil.isCalendarDatatype(rightDatatype);
					
					if(leftString != rightString) {
						throw new ValueExprEvaluationException("Unable to compare strings with other supported types");
					}
					if(leftNumeric != rightNumeric) {
						throw new ValueExprEvaluationException("Unable to compare numeric types with other supported types");
					}
					if(leftDate != rightDate) {
						throw new ValueExprEvaluationException("Unable to compare date types with other supported types");
					}
				}
				else if (!leftLangLiteral && !rightLangLiteral)
				{
					// For literals with unsupported datatypes we don't know if their
					// values are equal
					throw new ValueExprEvaluationException("Unable to compare literals with unsupported types");
				}
			}

			switch (operator) {
				case EQ:
					return literalsEqual;
				case NE:
					return !literalsEqual;
				case LT:
				case LE:
				case GE:
				case GT:
					throw new ValueExprEvaluationException(
							"Only literals with compatible, ordered datatypes can be compared using <, <=, > and >= operators");
				default:
					throw new IllegalArgumentException("Unknown operator: " + operator);
			}
		}
	}

	private static boolean isSupportedDatatype(URI datatype) {
		return datatype != null && (
			XMLSchema.STRING.equals(datatype)
			|| XMLDatatypeUtil.isNumericDatatype(datatype)
			|| XMLDatatypeUtil.isCalendarDatatype(datatype)
		);
	}
}
