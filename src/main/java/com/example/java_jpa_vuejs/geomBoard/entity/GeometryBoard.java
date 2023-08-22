package com.example.java_jpa_vuejs.geomBoard.entity;

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
@Table(name = "geometry_board")
public class GeometryBoard{

	@Id
	@Column(length = 100, nullable = false)
	private Integer boardSq;

	@Column(length = 100, nullable = false)
	private String title;

	@Column(length = 200, nullable = false)
	private String contents1;

	@Column(length = 200, nullable = false)
	private String contents2;

	@Column(length = 100, nullable = false)
	private String userAdress;

	@Column(length = 50)
	private String userEmail;

	@Column(length = 50)
	private String userName;
	
	@Column(length = 50)
	private String zipCd;

	@Column(length = 50, nullable = false, columnDefinition = "varchar(50) default 'Y'")
	private String state;

	@Column(length = 1, nullable = false, columnDefinition = "char(1) default 'N'")
	private String useYn;

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

	public JoinDto toDto(GeometryBoard geometryBoardEntity) {

		JoinDto joinDto = new JoinDto();

		joinDto.setId( geometryBoardEntity.boardSq);
		joinDto.setEmail(geometryBoardEntity.title);
		joinDto.setPassword(geometryBoardEntity.contents1);
		joinDto.setName(geometryBoardEntity.contents2);
		joinDto.setNickname(geometryBoardEntity.userName);
		joinDto.setMobile(geometryBoardEntity.userEmail);
		joinDto.setProfile(geometryBoardEntity.userAdress);
		joinDto.setDeleteYn(geometryBoardEntity.userName);
		joinDto.setAuthType(geometryBoardEntity.zipCd);
		joinDto.setAuthType(geometryBoardEntity.state);
		joinDto.setAuthType(geometryBoardEntity.useYn);
		joinDto.setCreatedDate(geometryBoardEntity.createdDate);
		joinDto.setModifiedDate(geometryBoardEntity.modifiedDate);

		return joinDto;
	}


	@Builder
	public GeometryBoard(Integer boardSq, String title, String contents1, String contents2, String userAdress, String userEmail, String userName,
					String state, ZonedDateTime createdDate, ZonedDateTime modifiedDate, String zipCd, String useYn) {
		this.boardSq = boardSq;
		this.title = title;
		this.contents1 = contents1;
		this.contents2 = contents2;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.state = state;
		this.title = title;
		this.useYn = useYn;
		this.userAdress = userAdress;
		this.userEmail = userEmail;
		this.userName = userName;
		this.zipCd = zipCd;
	}

	public GeometryBoard(GeometryBoard GeometryBoardEntity) {}

}