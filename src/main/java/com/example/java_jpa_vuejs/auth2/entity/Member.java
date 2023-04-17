package com.example.java_jpa_vuejs.auth2.entity;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
public class Member {
	
	@Id
	@Column(name="seq")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int seq;
	
	@Column(name="name")
	private String name;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="member")
	private Collection<Phone> phone;

	public Member(){}
	
	public Member(String name){
		this.name = name;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int id) {
		this.seq = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Collection<Phone> getPhone() {
		if( phone == null ){
			phone = new ArrayList<Phone>();
		}
		return phone;
	}
	
	public void addPhone(Phone p){
		Collection<Phone> phone = getPhone();
		phone.add(p);
	}

	public void setPhone(Collection<Phone> phone) {
		this.phone = phone;
	}	
	
	@Override
	public String toString() {
		String result = "[member_"+seq+"] " + name;
		return result;
	}
}