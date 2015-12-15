package com.tchip.contact;

public class CallLog {
	Long id;
	String number;
	String name;
	String type; //呼叫类型              
	String time; //呼叫时间        
	String duration; //通话时间


	public CallLog() {

	}
	public CallLog(String id) {
		this.id = Long.parseLong(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long contactid) {
		this.id = contactid;
	}
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
