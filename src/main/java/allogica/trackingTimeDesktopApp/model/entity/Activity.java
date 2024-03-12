package allogica.trackingTimeDesktopApp.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoEndException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoStartException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity")
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public Long getId() {
		return id;
	}

	
//	@Column(name = "parent_activity_id") // Mapping for the parent activity ID
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "parent_activity_id")
//	private Long parentActivityId; // Field to store the parent activity ID
	
	@Column(name = "parent_activity_id")
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

	
	@Column(name = "current")
    private boolean current;
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}

	
	@ManyToMany
    private Set<ActivityCategory> activityCategories;
	
	
	
	public Set<ActivityCategory> getCategories() {
		return activityCategories;
	}

	public void setCategories(Set<ActivityCategory> categories) {
		this.activityCategories = categories;
	}
	public void addCategories(ActivityCategory category) {
		this.activityCategories.add(category);
	}
	


	@Column(name = "description")
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", parentActivityId=" + parentActivityId + ", name=" + name
				+ ", activityCategories=" + activityCategories + ", description=" + description + ", activityStarts="
				+ activityStarts + ", activityEnds=" + activityEnds + ", totalTime=" + totalTime + ", usefulTime="
				+ usefulTime + "]";
	}
	
	public List<Long> getSubActivitiesIDs(){
		List<Long> subactivitiesIDs = new ArrayList<>();
		for (Activity subactivity : getSubactivities()) {
			subactivitiesIDs.add(subactivity.getId());
		}
		return subactivitiesIDs;
	}
	
	public String toString1() {
		return "Activity [id=" + id + ", parentActivityId=" + parentActivityId + ", name=" + name
				+ ", activityCategories=" + activityCategories + ", description=" + description + ", activityStarts="
				+ activityStarts + ", activityEnds=" + activityEnds + ", totalTime=" + totalTime + ", usefulTime="
				+ usefulTime + ", subactivities=" + getSubActivitiesIDs() + "]";
	}


	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityStart> activityStarts;
	public List<ActivityStart> getStart() {
		return activityStarts;
	}
	public List<LocalDateTime> getStartTime(){
		List <LocalDateTime> startTime = new ArrayList<LocalDateTime>();
		for (ActivityStart start :  getStart()) {
			startTime.add(start.getTime());
		}
		return startTime;
	}
	public ActivityStart getLastStart() throws ThereIsNoStartException {
		int siize = activityStarts.size();
		if (siize == 0) {
			throw new ThereIsNoStartException("There is no start for activity " + this.getName() + "with ID = " + this.getId() + ".");
		}
		ActivityStart lastItem = activityStarts.get(siize - 1);
		return (lastItem);
	}
	public ActivityStart getFirstStart() throws ThereIsNoStartException {
		int siize = activityStarts.size();
		if (siize == 0) {
			throw new ThereIsNoStartException("There is no start for activity " + this.getName() + "with ID = " + this.getId() + ".");
		}
		ActivityStart firstItem = activityStarts.get(0);
		return (firstItem);
	}
	public int getActivityStartCount () {
		return activityStarts.size();
	}
	public void deleteActivityStart(ActivityStart activityStart) {
		activityStarts.remove(activityStart);
	}
	public void addStart(LocalDateTime start) {
		activityStarts.add(new ActivityStart(this, start));
	}
	public void addStart(ActivityStart start) {
		activityStarts.add(start);
	}

	
	
	@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityEnd> activityEnds;
	public List<ActivityEnd> getEnd() {
		return activityEnds;
	}
	public List<LocalDateTime> getEndTime(){
		List <LocalDateTime> endTime = new ArrayList<LocalDateTime>();
		for (ActivityEnd end :  getEnd()) {
			endTime.add(end.getTime());
		}
		return endTime;
	}
	public ActivityEnd getLastEnd() throws ThereIsNoEndException {
		int siize = activityEnds.size();
		if (siize == 0) {
			throw new ThereIsNoEndException("There is no end for activity " + this.getName() + "with ID = " + this.getId() + ".");
		}
		ActivityEnd lastItem = activityEnds.get(siize - 1);
		return (lastItem);
	}
	public ActivityEnd getFirsEnd() throws ThereIsNoEndException {
		int siize = activityEnds.size();
		if (siize == 0) {
			throw new ThereIsNoEndException("There is no end for activity " + this.getName() + "with ID = " + this.getId() + ".");
		}
		ActivityEnd firstItem = activityEnds.get(0);
		return (firstItem);
	}
	public int getActivityEndCount () {
		return activityEnds.size();
	}
	public void deleteActivityEnd(ActivityEnd activityEnd) {
		activityEnds.remove(activityEnd);
	}
	public void addEnd(LocalDateTime end) {
		activityEnds.add(new ActivityEnd(this, end));
	}
	public void addEnd(ActivityEnd end) {
		activityEnds.add(end);
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
	public Duration sumUsefulTime(List<LocalDateTime> starts, List<LocalDateTime> ends) {
		Duration usefulTime = Duration.ZERO;
		Long counter1 = 0L;
		Long counter2;
		for (LocalDateTime end : ends) {
			counter1++;
			counter2 = 0L;
			for (LocalDateTime start : starts) {
				counter2++;
				if (counter1 == counter2) {
					usefulTime = usefulTime.plus(Duration.between(end, start));
				}
			}
		}
		this.usefulTime = usefulTime;
		return usefulTime;
	}

	
	
	@OneToMany(cascade = CascadeType.ALL) // Specify the cascade type
	@JoinColumn(name = "parent_activity_id") // Specify the column linking subactivities to their parent
	private List<Activity> subactivities;
	public List<Activity> getSubactivities() {
		return subactivities;
	}
	
	public void deleteAllSubactitivies() {
		subactivities = new ArrayList<Activity>();
	}
	
	public void addSubactivity(Activity subactivity) {
		subactivities.add(subactivity);
	}
	public void setSubActivities(List<Activity> activities) {
		for (Activity activity : activities) {
			this.addSubactivity(activity);
		}
	}

	public Activity(String name) {
		this.name = name;
		this.subactivities = new ArrayList<>();
		this.activityStarts = new ArrayList<>();
        this.activityEnds = new ArrayList<>();
        this.activityCategories = new HashSet<>();
	}
	
	public Activity() {
		this("Standard Name");
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