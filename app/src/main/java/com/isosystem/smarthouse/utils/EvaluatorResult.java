package com.isosystem.smarthouse.utils;

public class EvaluatorResult {

	//���������� �� ���������
	public Boolean isCorrect;
	// ��������� �� ������, ���� ��������� ��� �� ����������
	public String errorMessage;
	// �������� ��������� �� ����������
	public String numericRawResult;
	//�������� ��������� ����� ����������
	public String numericRoundedResult;
	//������� ���������, ���� ������� �������
	public Boolean booleanResult;
	
	public EvaluatorResult() {
		this.isCorrect = true;
		this.errorMessage = "";
		this.numericRawResult = "";
		this.numericRoundedResult = "";
		this.booleanResult = false;
	}
}
