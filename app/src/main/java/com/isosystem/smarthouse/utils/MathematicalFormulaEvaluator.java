package com.isosystem.smarthouse.utils;

import android.text.TextUtils;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathematicalFormulaEvaluator {

	// ����� �� ��������� ��������� �������
	Boolean mRoundFractionDigits;
	// ���������� ������ ����� �������
	String mFractionDigits;
	// �������� �������
	String mFormula;
	// �������� ����������
	String mVariable;

	public MathematicalFormulaEvaluator(String formula, String var,
			String fdigits, Boolean round) {

		this.mFormula = formula;
		this.mVariable = var;
		this.mFractionDigits = fdigits;
		this.mRoundFractionDigits = round;
	}

	private String checkFormulaString(String formula) throws Exception {
		String resultFormula = formula;

		if (TextUtils.isEmpty(resultFormula.trim())) {
			resultFormula = "x";
		}

		return resultFormula;
	}

	private int checkFractionDigits(String digits) throws Exception {
		int result = 0;

		if (TextUtils.isEmpty(digits.trim())) {
			// ���� ������������ ������� ���� ������, ������ 0 ������ �����
			// �������
			return result;
		}

		// ���� �������� ������ ����, ���������� 0
		result = Integer.parseInt(digits);
		if (result < 0) {
			result = 0;
		}

		return result;
	}

	public EvaluatorResult eval() {

		EvaluatorResult result = new EvaluatorResult();
		Evaluator evaluator = new Evaluator();

		// ��������� ���� "���������� ������ ����� �������"
		int mDigits = 0;
		try {
			mDigits = checkFractionDigits(mFractionDigits);
		} catch (Exception e) {
			result.isCorrect = false;
			result.errorMessage = "�������� ���������� ������ ����� ������� �����������, ��������� ��������������� ����";
			e.printStackTrace();
			return result;
		}
		
		// ��������� �������, � ���������, ������ �� ����
		String replacedFormula = "";
		try {
			replacedFormula = checkFormulaString(mFormula);
		} catch (Exception e) {
			result.isCorrect = false;
			result.errorMessage = "������ ��� �������� �������, ��������� ������������ �������";
			e.printStackTrace();
			return result;
		}

		// �������� �������� ����������
		try {
			evaluator.putVariable("x", mVariable);
		} catch (Exception e) {
			result.isCorrect = false;
			result.errorMessage = "������ ��� ������� �������� �������� ���������� � �������. ��������� � ���������� �������� ���������� x";
			e.printStackTrace();
			return result;
		}

		// ������ 'x' � '�' �� #{x}
		try {
			replacedFormula = replacedFormula.replace('X', 'x');
			replacedFormula = replacedFormula.replace("x", "#{x}");
		} catch (Exception e) {
			result.isCorrect = false;
			result.errorMessage = "������ ��� ������� �������� ���������� � �������. ��������� � ������������ ��������� �������";
			e.printStackTrace();
			return result;
		}

		String evalResult = "";

		// ��������� ��������. ����������� ���������� replacedFormula
		try {
			evalResult = evaluator.evaluate(replacedFormula);
		} catch (EvaluationException e) {
			result.isCorrect = false;
			result.errorMessage = "������ ��� ���������� �������. ��������� � ������������ ��������� �������";
			e.printStackTrace();
			return result;
		}
		
		if (evalResult.equals("Infinity")) {
			result.isCorrect = false;
			result.errorMessage = "��� ������ � ���������� ������� �� ����, �������������� �������";
			return result;
		}
		
		result.numericRawResult = evalResult;
		result.numericRoundedResult = evalResult;

		// ���� ����� ��������� ��������
		if (mRoundFractionDigits) {
			try {
				BigDecimal roundedResult = new BigDecimal(evalResult).setScale(
						mDigits, RoundingMode.HALF_EVEN);
				result.numericRoundedResult = roundedResult.toPlainString();
			} catch (Exception e) {
				result.isCorrect = false;
				result.errorMessage = "������ ��� ���������� ���������� �������. ��������� � ���������� ����� ���������� ������ ����� ������� � � ������������ �������";
				e.printStackTrace();
				return result;
			}
		}
		return result;
	}

}
