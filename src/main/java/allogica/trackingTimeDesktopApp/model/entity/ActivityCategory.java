package allogica.trackingTimeDesktopApp.model.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity(name = "ActivityCategory")
public class ActivityCategory {

	public ActivityCategory() {
	}
	
	public ActivityCategory(String name) {
		setName(name);
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated ID
    private Long id;

    private String name;


    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	@ManyToMany(mappedBy = "activityCategories", cascade = CascadeType.ALL) // One-to-Many relationship with Activity
    private List<Activity> activities;

    // Getter and setter method for activities
    // ...
}