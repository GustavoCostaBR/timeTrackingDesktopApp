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
@Table(name = "subactivity_end")
public class SubactivityEnd {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	public Long getId() {
		return id;
	}

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "end_time")
    private LocalDateTime endTime;
	public LocalDateTime getStartTime() {
		return endTime;
	}
	public void setStartTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}  
	
	
	
	public SubactivityEnd() {
        // Default constructor required by JPA
    }

    public SubactivityEnd(Activity activity, LocalDateTime endTime) {
        this.activity = activity;
        this.endTime = endTime;
    }
	
}
