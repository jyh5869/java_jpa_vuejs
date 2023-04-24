package com.example.java_jpa_vuejs.auth.entity;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

@Entity
@Table(name = "phone")
public class Phones {
	
	@Id
	@Column(name="seq")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int seq;

	@Column(name="no")
	private String no;

	@ManyToOne(optional=false)
	@JoinColumn(name="id")
	private Members members;
	
	public Phones() {}
	public Phones(Members members, String no){
		this.no = no;
		this.members = members;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	public Members getMember() {
		return members;
	}
	
	public void setMember(Members member) {
		this.members = members;
	}	
	
	@Override
	public String toString() {
		String result = "[phone_"+seq+"] " + no;
		return result;
	}	
}