package allogica.trackingTimeDesktopApp.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "subactivity_start")
public class SubactivityStart {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	public Long getId() {
		return id;
	}

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "start_time")
    private LocalDateTime startTime;
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
	
	public SubactivityStart() {
        // Default constructor required by JPA
    }

    public SubactivityStart(Activity activity, LocalDateTime startTime) {
        this.activity = activity;
        this.startTime = startTime;
    }
	
	
}
