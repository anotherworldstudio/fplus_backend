package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SKINDOCTOR")
@Data
@EqualsAndHashCode(callSuper=false)
public class SkinDoctor extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "skinid")  private String skinId;	
	@Column(name = "doctorid")	private String doctorId;
	@Column(name = "forehead")  private int forehead;
	@Column(name = "redness")  private int redness;
	@Column(name = "middle")  private int middle;
	@Column(name = "rightzone")  private int rightZone;
	@Column(name = "leftzone")  private int leftZone;
	@Column(name = "trouble")  private int trouble;
	@Column(name = "wrinkle")  private int wrinkle;
	@Column(name = "topzone")  private int topZone;
	@Column(name = "bottomzone")  private int bottomZone;
	@Column(name = "pigment")  private int pigment;
	@Column(name = "mouth")  private int mouth;
	@Column(name = "eye")  private int eye;
	
	public boolean isEmpty() {
		return StringUtil.isEmpty(skinId) || StringUtil.isEmpty(doctorId);
	}

	public boolean isValidScore() {
		return isValid(forehead) && isValid(redness) && isValid(middle) && isValid(rightZone) && isValid(leftZone)
				&& isValid(trouble) && isValid(wrinkle) && isValid(topZone) && isValid(bottomZone) && isValid(pigment)
				&& isValid(mouth) && isValid(eye);
	}

	/**
	 * true val fale not val
	 * 
	 * @param score
	 * @return
	 */
	private boolean isValid(int score) {
		return !(score < 0 || score > 100);
	}
	
	public SkinDoctor( ) {
	}

}
