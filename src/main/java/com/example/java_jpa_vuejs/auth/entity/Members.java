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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/* 
import kr.co.platform.api.posts.domain.entity.Posts;
import kr.co.platform.api.posts.domain.entity.PostsComment;
import javax.persistence.*;
*/
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
	@Column(length = 100, nullable = false)
	//@GeneratedValue(strategy = GenerationType.IDENTITY) // MYSQL 자동증가 시퀀스를 사용할 경우
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
	private String address;

	@Column(length = 200)
	private String profile;

	@Column(length = 10, nullable = false, columnDefinition = "varchar(10) default 'user'")
	private String authType;

	@Column(length = 1, nullable = false, columnDefinition = "char(1) default 'N'")
	private String deleteYn;

	/* 
	
	■ 해당 Repository가 생성, 변경 될 때 일자를 자동으로 업데이트 시켜주는 코드 ■
	1. @CreatedDate - 생성일자
	2. @LastModifiedDate - 변경일자
	※ 메인 클레스에 @EnableJpaAuditing 어노테이션을 추가해 주어야 인식가능하다.
	
	EX> 생성일 컬럼과 수정일 컬럼 사용 예제

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@Column(updatable = true)
	private LocalDateTime modifiedDate;
	
	*/

	@Column(name="created_date", updatable = false)
	private ZonedDateTime createdDate;

	@Column(name="modified_date", updatable = true)
	private ZonedDateTime modifiedDate;

	@PrePersist
	public void prePersist() {
		this.createdDate = ZonedDateTime.now();
		this.modifiedDate = ZonedDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedDate = ZonedDateTime.now();
	}

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
		joinDto.setAddress(MemberEntity.address);
		joinDto.setProfile(MemberEntity.profile);
		joinDto.setCreatedDate(MemberEntity.createdDate);
		joinDto.setModifiedDate(MemberEntity.modifiedDate);
		joinDto.setDeleteYn(MemberEntity.deleteYn);
		joinDto.setAuthType(MemberEntity.authType);

		return joinDto;
	}


	@Builder
	public Members(Long id, String email, String password, String name, String mobile, String nickname, String address,
					String profile, ZonedDateTime createdDate, ZonedDateTime modifiedDate, String deleteYn, String authType) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.mobile = mobile;
		this.nickname = nickname;
		this.address = address;
		this.profile = profile;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.deleteYn = deleteYn;
		this.authType = authType;
	}

	public Members(Members loginEntity) {}

}