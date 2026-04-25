package com.example.milkman_legacy.variationDisplay;

public class VariationConsoleBean {
	private String name;
	private String date;
	private float cqty;
	private float bqty;

	public VariationConsoleBean(String name, String date, float cqty, float bqty) {
		super();
		this.name = name;
		this.date = date;
		this.cqty = cqty;
		this.bqty = bqty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getCqty() {
		return cqty;
	}

	public void setCqty(float cqty) {
		this.cqty = cqty;
	}

	public float getBqty() {
		return bqty;
	}

	public void setBqty(float bqty) {
		this.bqty = bqty;
	}
}