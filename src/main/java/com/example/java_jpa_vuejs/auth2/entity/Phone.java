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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.transaction.Transactional;

@Entity
public class Phone {
	
	@Id
	@Column(name="seq")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int seq;

	@Column(name="no")
	private String no;

	@ManyToOne(optional=false)
	@JoinColumn(name="member_id")
	private Member member;
	
	public Phone() {}
	public Phone(Member member, String no){
		this.no = no;
		this.member = member;
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
	
	public Member getMember() {
		return member;
	}
	
	public void setMember(Member member) {
		this.member = member;
	}	
	
	@Override
	public String toString() {
		String result = "[phone_"+seq+"] " + no;
		return result;
	}	
}