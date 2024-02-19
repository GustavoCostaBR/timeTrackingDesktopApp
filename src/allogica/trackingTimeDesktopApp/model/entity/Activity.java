package allogica.trackingTimeDesktopApp.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<SubactivityStart> subactivityStarts;
	public List<SubactivityStart> getStart() {
		return subactivityStarts;
	}
	public SubactivityStart getLastStart() {
		int siize = subactivityStarts.size();
		SubactivityStart lastItem = subactivityStarts.get(siize - 1);
		return (lastItem);
	}
	public void deleteSubActivityStart(SubactivityStart subactivityStart) {
		subactivityStarts.remove(subactivityStart);
	}
	public void addStart(LocalDateTime start) {
		subactivityStarts.add(new SubactivityStart(this, start));
	}

	
	
	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<SubactivityEnd> subactivityEnds;
	public List<SubactivityEnd> getEnd() {
		return subactivityEnds;
	}
	public SubactivityEnd getLastEnd() {
		int siize = subactivityEnds.size();
		SubactivityEnd lastItem = subactivityEnds.get(siize - 1);
		return (lastItem);
	}
	public void deleteSubActivityEnd(SubactivityEnd subactivityEnd) {
		subactivityEnds.remove(subactivityEnd);
	}
	public void addEnd(LocalDateTime end) {
		subactivityEnds.add(new SubactivityEnd(this, end));
	}

	@Column(name = "total_time")
	private Duration totalTime;

	public Duration getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Duration totalTime) {
		this.totalTime = totalTime;
	}

	@Column(name = "useful_time")
	private Duration usefulTime;

	public Duration getUsefulTime() {
		return usefulTime;
	}

	public void setUsefulTime(Duration usefulTime) {
		this.usefulTime = usefulTime;
	}

	@OneToMany(cascade = CascadeType.ALL) // Specify the cascade type
	@JoinColumn(name = "parent_activity_id") // Specify the column linking subactivities to their parent
	private Map<Long, Activity> subactivities;

	public Map<Long, Activity> getSubactivities() {
		return subactivities;
	}

	public void addSubactivity(Long id, Activity subactivity) {
		subactivities.put(id, subactivity);
	}

	public void setSubActivities(Map<Long, Activity> activities) {
		for (Map.Entry<Long, Activity> activity : activities.entrySet()) {
			this.addSubactivity(activity.getKey(), activity.getValue());
		}
	}

	public Activity(String name) {
		this.name = name;
		this.subactivities = new HashMap<>();
		this.parentActivityId = 0L;
		this.subactivityStarts = new ArrayList<>();
        this.subactivityEnds = new ArrayList<>();
	}

//	public Activity(Long parentActivityId, String name, LocalDateTime start) {
//		this.name = name;
//		this.starts = new ArrayList<>();
//        this.ends = new ArrayList<>();
//	}

}
/*
 * CREATE TABLE Atividade ( id INT PRIMARY KEY, nome VARCHAR(100), descricao
 * TEXT, id_atividade_pai INT, -- Referência para a atividade pai FOREIGN KEY
 * (id_atividade_pai) REFERENCES Atividade(id) );
 */