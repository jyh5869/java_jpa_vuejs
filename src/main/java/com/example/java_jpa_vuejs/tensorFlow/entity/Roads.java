package com.example.java_jpa_vuejs.tensorFlow.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import com.example.java_jpa_vuejs.geomBoard.entity.GeometryBoard;
import com.example.java_jpa_vuejs.tensorFlow.model.RoadDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "TN_SPRD_RDNM")
public class Roads {
    
	@Id	
	@Column(name="SIG_CD")
	private String sigCd;

	@Column(name="RN_CD")
	private String rnCd;

    @Column(name="EMD_NO")
	private String emdNo;

	@Column(name="RN")
	private String rn;

    @Column(name="ENG_NM")
	private String engNm;

	@Column(name="SIDO_NM")
	private String sidoNm;

    @Column(name="SGG_NM")
	private String sggNm;

	@Column(name="EMD_SE")
	private String emdSe;

    @Column(name="EMD_CD")
	private String emdCd;

	@Column(name="EMD_NM")
	private String emdNm;

    @Column(name="USE_YN")
	private String useYn;

	@Column(name="ALWNC_RESN")
	private String alwncResn;

    @Column(name="CHGHY")
	@ColumnDefault("0")
	private String chghy;

	@Column(name="AFTCH_INFO", length = 1000)
	private String aftchInfo;

    @Column(name="CTP_ENG_NM")
	private String ctpEngNm;

	@Column(name="SIG_ENG_NM")
	private String sigEngNm;

    @Column(name="EMD_ENG_NM")
	private String emdEngNm;

	@Column(name="BEGIN_BSIS")
	private String beginBsis;

    @Column(name="END_BSIS")
	private String endBsis;

	@Column(name="EFFECT_DE")
	private String effectDe;

    @Column(name="ERSR_DE", length = 240)
	private String ersrDe;


	public RoadDTO toDto(Roads roadEntity) {

		RoadDTO roadDto = new RoadDTO();

		roadDto.setSigCd( roadEntity.sigCd);
		roadDto.setRnCd( roadEntity.rnCd);
		roadDto.setEmdNo( roadEntity.emdNo);
		roadDto.setRn( roadEntity.rn);
		roadDto.setEngNm( roadEntity.engNm);
		roadDto.setSidoNm( roadEntity.sidoNm);
		roadDto.setSggNm( roadEntity.sggNm);
		roadDto.setEmdSe( roadEntity.emdSe);
		roadDto.setEmdCd( roadEntity.emdCd);
		roadDto.setEmdNm( roadEntity.emdNm);
		roadDto.setUseYn( roadEntity.useYn);
		roadDto.setAlwncResn( roadEntity.alwncResn);
		roadDto.setAftchInfo( roadEntity.aftchInfo);
		roadDto.setCtpEngNm( roadEntity.ctpEngNm);
		roadDto.setSigEngNm( roadEntity.sigEngNm);
		roadDto.setEmdEngNm( roadEntity.emdEngNm);
		roadDto.setBeginBsis( roadEntity.beginBsis);
		roadDto.setEndBsis( roadEntity.endBsis);
		roadDto.setEffectDe( roadEntity.effectDe);
		roadDto.setErsrDe( roadEntity.ersrDe);
		
		return roadDto;
	}
}
