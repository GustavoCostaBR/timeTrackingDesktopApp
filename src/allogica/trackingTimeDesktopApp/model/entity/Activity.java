package allogica.trackingTimeDesktopApp.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

@Entity
@Table(name = "activity")
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	public Long getId() {
		return id;
	}


	@Column(name = "parent_activity_id") // Mapping for the parent activity ID
    private Long parentActivityId; // Field to store the parent activity ID
	
	public Long getParentActivityId() {
		return parentActivityId;
	}
	public void setParentActivityId(Long parentActivityId) {
		this.parentActivityId = parentActivityId;
	}

	
	private String name;

	public String getName() {
		return name;
	}

	
	public void setName(String name) {
		this.name = name;
	}

	
	@Column(name = "start")
	private LocalDateTime start;
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	
	@Column(name = "end")
	private LocalDateTime end;
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	
	

	@OneToMany(cascade = CascadeType.ALL) // Specify the cascade type
	@JoinColumn(name = "parent_activity_id") // Specify the column linking subactivities to their parent
	private Map<Integer, Activity> subactivities;
	public void addSubactivity(int id, Activity subactivity) {
		subactivities.put(id, subactivity);
	}
	
	
	public Activity(String name) {
		this.name = name;
		this.subactivities = new HashMap<>();
		this.parentActivityId = 0L;
	}

	
	public Duration calcTotalTime() {
		Duration totalTime = Duration.ZERO;
		if ((start != null && end != null) && this.subactivities.isEmpty()) {
			return totalTime = Duration.between(start, end);
		} else {
			for (Activity subatividade : subactivities.values()) {
				totalTime = totalTime.plus(subatividade.calcTotalTime());
			}
			return totalTime;
		}
	}

}
/*
 * CREATE TABLE Atividade ( id INT PRIMARY KEY, nome VARCHAR(100), descricao
 * TEXT, id_atividade_pai INT, -- Referência para a atividade pai FOREIGN KEY
 * (id_atividade_pai) REFERENCES Atividade(id) );
 */