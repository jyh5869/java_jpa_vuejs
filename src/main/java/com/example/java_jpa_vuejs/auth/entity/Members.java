package com.example.java_jpa_vuejs.auth.entity;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.java_jpa_vuejs.auth.JoinDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/* 
import kr.co.platform.api.posts.domain.entity.Posts;
import kr.co.platform.api.posts.domain.entity.PostsComment;
import javax.persistence.*;
*/
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "members")
public class Members{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false)
	private String email;

	@Column(length = 200, nullable = false)
	private String password;

	@Column(length = 100, nullable = false)
	private String name;

	@Column(length = 100, nullable = false)
	private String mobile;

	@Column(length = 100)
	private String nickname;
	
	@Column(length = 200)
	private String profile;

	@Column(length = 1, nullable = false, columnDefinition = "char(1) default 'N'")
	private String isDeleted;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime modifiedDate;
    /* 
	// 게시글 Entity 연관관계 설정(One(회원 Entity) To Many(게시글 Entity)
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Posts> posts = new ArrayList<>();

	// 댓글 Entity 연관관계 설정(One(회원 Entity) To Many(댓글 Entity)
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PostsComment> postsComment = new ArrayList<>();
    */

	@OneToMany(cascade=CascadeType.ALL, mappedBy="members")
	private Collection<Phones> phones;
	
	public Collection<Phones> getPhone() {
		if( phones == null ){
			phones = new ArrayList<Phones>();
		}
		return phones;
	}
	
	public void addPhone(Phones p){
		Collection<Phones> phones = getPhone();
		phones.add(p);
	}

	public void setPhone(Collection<Phones> phones) {
		this.phones = phones;
	}

	public JoinDto toDto(Members MemberEntity) {

		JoinDto joinDto = new JoinDto();

		joinDto.setId( MemberEntity.id);
		joinDto.setEmail(MemberEntity.email);
		joinDto.setPassword(MemberEntity.password);
		joinDto.setName(MemberEntity.name);
		joinDto.setNickname(MemberEntity.nickname);
		joinDto.setMobile(MemberEntity.mobile);
		joinDto.setProfile(MemberEntity.profile);

		return joinDto;
	}


	@Builder
	public Members(Long id, String email, 
			String password, String name, 
			String mobile, String nickname) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.mobile = mobile;
		this.nickname = nickname;
	}

	public Members(Members loginEntity) {
	}


}