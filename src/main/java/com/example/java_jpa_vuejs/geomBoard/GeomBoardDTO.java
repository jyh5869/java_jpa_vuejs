package com.example.java_jpa_vuejs.geomBoard;


import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.geo.Circle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GeomBoardDTO {

	private long id;
	private Point point;
	private Polygon polygon;
	private Circle circle;

	/* 
	public Members toEntity() {
		Members build = Members.builder()
				.id(id)
				.point(point)
				.polygon(polygon)
				.circle(circle)
				.build();
		
		return build;
	}
	*/
}